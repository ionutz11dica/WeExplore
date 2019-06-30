package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraSource {
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static final String TAG = "OpenCameraSource";

    private static final int TEXTURE_NAME_VALUE = 100;
   /**
     daca diferenta absoluta dintre preview size aspect ratio si picture size aspect ratio
     este mai mica decat aceasta valoare tolerata, atunci se considera ca acestea sa aiba aceeasi ration
    */

    private static final float ASPECT_RATIO_TOLERANCE = 0.01f;

    @StringDef({
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_INFINITY,
            Camera.Parameters.FOCUS_MODE_MACRO
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface FocusMode{}

    @StringDef({
            Camera.Parameters.FLASH_MODE_ON,
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_AUTO,
            Camera.Parameters.FLASH_MODE_RED_EYE,
            Camera.Parameters.FLASH_MODE_TORCH
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface FlashMode {}

    private Context context;

    private final Object cameraLock = new Object();

    private Camera camera;
    private int facing = CAMERA_FACING_BACK;
    private  int rotation;
    private Size previewSize;

    private float requestFps = 30.0f;
    private int requestPreviewWidth = 1024;
    private int requestPreviewHeight = 768;

    private String focusMode = null;
    private String flashMode = null;

    /**avoid allocation failure GC -> instantele urmatoare trebuiesc tinute (copil parinte)*/
    private SurfaceView surfaceView;
    private SurfaceTexture surfaceTexture;

    /**thread pentru detector cu frame odata ce un frame devine disponibil pt camera*/
    private Thread processingThread;
    private FrameProcessingRunnable frameProcessor;

    /**conversie intre vectorul de octeti, primiti de la camera, si bytebufferul sau asociat tampon.
    folosim buffer ul intern pentru eficienta apelului (evitam o copie potentiala)*/
    private Map<byte[], ByteBuffer> byteToByteBuffer = new HashMap<>();


    //builder pentru configurarea si crearea unei camere sursa asociate
    public static class Builder {

        private final Detector<?> detector;
        private CameraSource cameraSource = new CameraSource();

       /** creem un builder pt clasa CameraSource care primeste un context si un detector.
        CameraPreview -> imaginile or fi transimise catre detectorul asociat la pornirea camerei sursa*/
        public Builder(Context context,Detector<?> detector) {
            if(context == null){
                throw new IllegalArgumentException("no context Supplied");
            }
            if (detector == null){
                throw new IllegalArgumentException("no detector Supplied");
            }
            this.cameraSource.context = context;
            this.detector = detector;
        }

        public Builder setRequestedFps(float fps){
            if (fps <= 0){
                throw new IllegalArgumentException("Invalid fps "+fps);
            }
            cameraSource.requestFps = fps;
            return this;
        }

        public Builder setFocusMode(@FocusMode String mode){
            cameraSource.focusMode = mode;
            return this;
        }

        public Builder setFlashMode(@FlashMode String mode){
            cameraSource.flashMode = mode;
            return this;
        }

        //setam lungimea si inalitmea pentru camera frame

        public Builder setRequestedPreviewSize(int width, int height) {
            /**limitare interval in aria posibilitatilor
             luam o constanta cu valoare aberanta ex: 1.000.000 pentru ca toate device urile sa fie in sfera
             Limitam intervalul pentru a nu face overflow*/
            final int MAX = 1000000;
            if((width <= 0) || (width > MAX) || (height <= 0)||(height> MAX)){
                throw new IllegalArgumentException("Invalid preview size: "+width + "x"+height);
            }
            cameraSource.requestPreviewWidth = width;
            cameraSource.requestPreviewHeight = height;
            return this;
        }

        //setam camera de spate si verificam daca este sau nu setata corect
        public Builder setFacing(int facing) {
            if ((facing != CAMERA_FACING_BACK) && (facing != CAMERA_FACING_FRONT)) {
                throw new IllegalArgumentException("Invalid camera: " + facing);
            }
            cameraSource.facing = facing;
            return this;
        }

        //build
        public CameraSource build(){
            cameraSource.frameProcessor = cameraSource.new FrameProcessingRunnable(detector);
            return cameraSource;
        }
    }

    private CameraSource() {
    }


    //Bridge functionallity pentru Camera API
    /**Callback interface folosit pentru a semnala momentul unei capturi*/

    public interface ShutterCallback{
        /**este apelata in momentul in care o imagine este capturata de catre senzor*/
        void onShutter();
    }

    // Callback interface folosit pentru a alimenta data-ul(bytearray) dintr-o captura
    public interface PictureCallBack {
       /** este apelata in momentul in care bytearray ul este disponibil dupa ce imaginea este luat.
        Formatul este jpeg binary*/
        void onPictureTaken(byte[] data);
    }

    // Callback interface folosit pentru a notifica auto focusarea camerei
    public interface AutoFocusCallback {
       /** este apelata atunci cand focusarea este completata
        in cazul in care camera nu are autofocusare dar este apelata
        se trimite o valoare falsa <code> succes</code> set to <code>true</code>
        @param success true daca focusare a fost cu succes, altfel false
        */
        void onAutoFocus(boolean success);
    }

    /** Callback inteface folosit pentru a notifica cand auto-focusare a pornit si s-a orpit
     * Esti suportata doar de autofocusarea cotinua in modurile --
     * {@Link Camera.Parameters#FOCUS_MODE_CONTINUOUS_VIDEO} si
     * {@Link Camera.Parameters#FOCUS_MODE_CONTINUOUS_PICTURE}
     * Aplicatiile pot arata animatii in timpul autofocusarii bazata pe acest callback*/

    public interface AutoFocusMoveCallback {
        /** folosita cand auto-focusare a pornit sau s-a oprit
         * @param start true daca a inceput, false daca s-a oprit */
        void autoFocusMoving(boolean start);
    }

    //Public
    /** Oprire camera si eliberarea resurselor camerei si al detectorului de baza */

    public void release(){
        synchronized (cameraLock){
            stop();
            frameProcessor.release();
        }
    }

    /**
     * Deschide camera si porneste trimiterea de frames catre detectorul de baza.
     * Surface holder ul furnizat este folosit pentru previzualizare asa ca frame-urile pot fi
     * afisate catre user
     * @param surfaceHolder este suportul suprafetei folosit pentru previzualizarea frameurilor */
    @RequiresPermission(Manifest.permission.CAMERA)
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (cameraLock){
            if (camera != null){
                return this;
            }
            camera = createCamera();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            processingThread = new Thread(frameProcessor);
            frameProcessor.setActive(true);
            processingThread.start();
        }
        return this;
    }


    public void stop() {
        synchronized (cameraLock) {
            frameProcessor.setActive(false);
            if (frameProcessor !=null){
                try{
                    //Astemptam thread ul sa se termine pentru evita sa avem threaduri ce se executa
                    //concomitent(ex: ce s-ar intampla daca apelam start prea repede dupa stop)
                    processingThread.join();
                }catch (InterruptedException e){
                    Log.d(TAG, "Frame processing thread interrupted and release");
                }
                processingThread = null;

                //eliberam bufferul pentru a preveni OutOfMemoryException
                byteToByteBuffer.clear();

                if(camera!=null){
                    camera.stopPreview();
                    camera.setPreviewCallbackWithBuffer(null);
                    try{
                        //Verificam compatibilitatea
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                            camera.setPreviewTexture(null);
                        }else{
                            camera.setPreviewDisplay(null);
                        }
                    }catch (Exception e){
                        Log.d(TAG,"Failde to clear camera preview "+e);
                    }
                    camera.release();
                    camera = null;
                }
            }
        }
    }

    private Camera createCamera(){
        int requestedCameraId = getIdForRequestedCamera(facing);
        if(requestedCameraId == -1 ){
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera camera = Camera.open(requestedCameraId);

        SizePair sizePair = selectSizePair(camera,requestPreviewWidth, requestPreviewHeight);
        if(sizePair ==null){
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size pictureSize = sizePair.pictureSize();
        previewSize = sizePair.previewSize();

        int[] previewFpsRange = selectPreviewFpsRange(camera,requestFps);
        if(previewFpsRange == null){
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }

        Camera.Parameters parameters = camera.getParameters();

        if(pictureSize != null){
            parameters.setPictureSize(pictureSize.getWidth(),pictureSize.getHeight());
        }

        parameters.setPreviewSize(previewSize.getWidth(),previewSize.getHeight());
        parameters.setPreviewFpsRange(previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                                      previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        parameters.setPreviewFormat(ImageFormat.NV21);

        setRotation(camera,parameters, requestedCameraId);

        if(focusMode !=null){
            if (parameters.getSupportedFocusModes().contains(focusMode)){
                parameters.setFocusMode(focusMode);
            }else{
                Log.i(TAG, "Camera focus mode: " + focusMode + " is not supported on this device.");
            }
        }

        //setam modul focus pt cea stabilita in parametrii
        focusMode = parameters.getFocusMode();

        if (flashMode != null) {
            if (parameters.getSupportedFlashModes() != null) {
                if (parameters.getSupportedFlashModes().contains(
                        flashMode)) {
                    parameters.setFlashMode(flashMode);
                } else {
                    Log.i(TAG, "Camera flash mode: " + flashMode + " is not supported on this device.");
                }
            }
        }

        //setam modul flash pt cea stabilita in parametrii
        flashMode = parameters.getFlashMode();

        camera.setParameters(parameters);
        //4 bufferuri de frame uri are nevoie sa mearga cu camera
        //unul este pentru frameul in curs de executie in momentul detectarii
        //unul pentru urmatorul frame care urmeaza sa fie procesat imediat dupa terminarea detectarii
        //doua pentru frame-urile pe care camera le foloseste ca sa populeze imaginiile viitoare previzualizate
        camera.setPreviewCallbackWithBuffer(new CameraPreviewCallback());
        camera.addCallbackBuffer(createPreviewBuffer(previewSize));
        camera.addCallbackBuffer(createPreviewBuffer(previewSize));
        camera.addCallbackBuffer(createPreviewBuffer(previewSize));
        camera.addCallbackBuffer(createPreviewBuffer(previewSize));

        return camera;
    }

    private static int getIdForRequestedCamera(int facing){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for( int i = 0; i < Camera.getNumberOfCameras();++i){
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == facing){
                return i;
            }
        }
        return -1;
    }
    public int getCameraFacing() {
        return facing;
    }
    public Size getPreviewSize() {
        return previewSize;
    }

    /** Creaza un buffer pt apelul camerei. Marimea buffer ului este bazat pe marimea camerei previzualizate si pe formatul camerei
     * */
    private byte[] createPreviewBuffer(Size previewSize) {
        int bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
        long sizeInBits = previewSize.getHeight() * previewSize.getWidth() * bitsPerPixel;
        int bufferSize = (int) Math.ceil(sizeInBits / 8.0d) + 1;

        //
        // NOTICE: This code only works when using play services v. 8.1 or higher.
        //

        byte[] byteArray = new byte[bufferSize];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (!buffer.hasArray() || (buffer.array() != byteArray)) {
            throw new IllegalStateException("Failed to create valid buffer for camera source.");
        }

        byteToByteBuffer.put(byteArray, buffer);
        return byteArray;
    }

    /** Calculeaza rotatia corecta pentru id ul camerei si seteaza rotatia in parametri.*/

    private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int degrees = 0;
        int localRotation = windowManager.getDefaultDisplay().getRotation();
        switch (localRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                Log.e(TAG, "Bad rotation value: " + localRotation);
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int angle;
        int displayAngle;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360;
            displayAngle = (360 - angle) % 360; // compensate for it being mirrored
        } else {  // back-facing
            angle = (cameraInfo.orientation - degrees + 360) % 360;
            displayAngle = angle;
        }


        rotation = angle / 90;

        camera.setDisplayOrientation(displayAngle);
        parameters.setRotation(angle);
    }


    /** Selectam cea mai convenabila previzualizare a frameurilor per secunda*/

    private int[] selectPreviewFpsRange(Camera camera, float previewFps){
        /** API camerei foloseste numere intregi scalate cu un factor de 1000 in loc de cadru cu virgula mobila*/
        int previewFpsScaled = (int) (previewFps * 1000.0f);

        int[] selectedFpsRange = null;
        int minDiff = Integer.MAX_VALUE;
        List<int[]> previewFpsRangeList = camera.getParameters().getSupportedPreviewFpsRange();
        for (int[] range : previewFpsRangeList) {
            int deltaMin = previewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int deltaMax = previewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
            if (diff < minDiff) {
                selectedFpsRange = range;
                minDiff = diff;
            }
        }
        return selectedFpsRange;
    }

    /** Stocheaza un preview size si o corespondenta same-aspect-ratio (un raport cu acelasi aspect).
     * Pentru a evita previzualizarea imaginiolor deformate din anumite device-uri, poza trebuie setata la
     * o marime care este la fel cu aspect-ratio ca preview size-ul sau previzualizarea ar putea fi deformata.
     */

    private static class SizePair {
        private Size preview;
        private Size picture;

        public SizePair(Camera.Size previewSize, Camera.Size pictureSize) {
            preview = new Size(previewSize.width,previewSize.height);
            if (pictureSize!=null) {
                picture = new Size(pictureSize.width,pictureSize.height);
            }
        }

        public Size previewSize(){return preview;}

        public Size pictureSize(){return picture;}
    }

    private static SizePair selectSizePair(Camera camera, int width, int height) {
        List<SizePair> validPreviewSizes = generateValidPreviewSizes(camera);

        SizePair selectedPair = null;
        int minDiff = Integer.MAX_VALUE;
        for(SizePair sizePair : validPreviewSizes) {
            Size size = sizePair.previewSize();
            int diff = Math.abs(size.getWidth() - width) + Math.abs(size.getHeight() - height);
            if(diff < minDiff){
                selectedPair = sizePair;
                minDiff = diff;
            }
        }
        return selectedPair;
    }

    private static List<SizePair> generateValidPreviewSizes(Camera camera){
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

        List<SizePair> validPreviewSizes = new ArrayList<>();
        for(Camera.Size previewSize : supportedPreviewSizes){
            float previewAspectRatio = (float) previewSize.width/(float)previewSize.height;
            //Realizand for in for, favorizam marimea rezolutie
            //Alegem rezolutia cea mai mare pentru a ajuta la extragerea unei rezolutii full
            for(Camera.Size pictureSize : supportedPictureSizes){
                float pictureAspectRatio = (float) pictureSize.width/(float)pictureSize.height;
                if(Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                    validPreviewSizes.add(new SizePair(previewSize,pictureSize));
                    break;
                }
            }
        }
        return validPreviewSizes;
    }

    private class CameraPreviewCallback implements Camera.PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            frameProcessor.setNextFrame(data,camera);
        }
    }

    private class FrameProcessingRunnable implements Runnable {
        private Detector<?> detector;
        private long startTimeMillis = SystemClock.elapsedRealtime();

        private final Object lock = new Object();
        private boolean active = true;

        /**aceste variabile pastreaza starea asociata cu noul frame care asteapta*/
        private long pendingTimeMillis;
        private int pendingFrameId;
        private ByteBuffer pendingFrameDate;

        FrameProcessingRunnable(Detector<?> detector){this.detector = detector;}

        /**eliberarea receptorului subiacent. pentru a asigura deblocarea dupa ce threadul asociat e complet*/
        @SuppressLint("Assert")
        void release(){
            assert (processingThread.getState() == Thread.State.TERMINATED);
            detector.release();
            detector=null;
        }

        //setare stare activa/inactiva
        void setActive(boolean active){
            synchronized (lock){
                this.active = active;
                lock.notifyAll();
            }
        }

        void setNextFrame(byte[] data, Camera camera){
            synchronized (lock){
                if(pendingFrameDate !=null){
                    camera.addCallbackBuffer(pendingFrameDate.array());
                    pendingFrameDate = null;
                }

                if(!byteToByteBuffer.containsKey(data)){
                    /**sarim peste frameul respectiv deoarece valoare(bytebuffer asociata)nu poate fi gasita*/
                    Log.d(TAG,
                            "Skip, frame could not be found in map.");
                    return;
                }

                pendingTimeMillis = SystemClock.elapsedRealtime() - startTimeMillis;
                pendingFrameId++;
                pendingFrameDate = byteToByteBuffer.get(data);

                /** notificam processingThread-ul in caz ca alt frame asteapta*/
                lock.notifyAll();
            }
        }

        @Override
        public void run(){
            Frame outputFrame;
            ByteBuffer data;

            while (true) {
                synchronized (lock){
                    while (active && (pendingFrameDate == null)){
                        try{
                            /** asteptam frame ul urmator sa fie primit de la camera, in caz ca nu avem unul deja*/
                            lock.wait();
                        }catch (InterruptedException e){
                            Log.d(TAG,"Frame processing loop terminated",e);
                            return;
                        }
                    }

                    if(!active){
                        /** iesim din bucla odata ce camera este oprita sau primita.
                        verificam imediat dupa wait() de mai sus, pentru a gestiona cazul in care a fost apelat setActive(false)
                        facem triggering pentru terminarea bucle*/
                        return;
                    }

                    outputFrame = new Frame.Builder()
                            .setImageData(pendingFrameDate,previewSize.getWidth(),
                                    previewSize.getHeight(), ImageFormat.NV21)
                            .setId(pendingFrameId)
                            .setTimestampMillis(pendingTimeMillis)
                            .setRotation(rotation)
                            .build();

                    /** Pastram frame-ul "data" intr o variabila locala pentru detectarea urmatoare.
                    trebuie sa eliberam pendingDataFrame ul sa ne asiguram ca buffer ul nu este
                     reciclat inapoi catre camera inainte de al folosi*/
                    data = pendingFrameDate;
                    pendingFrameDate = null;
                }
                /**aici trebuie sa rulam in afara sincronizarii, deoarece aici ne va lasa
                camera sa adauge frame uri in asteptare cat timp asteptam detectarea in frameul curent*/

                try{
                    detector.receiveFrame(outputFrame);
                }catch (Throwable t){
                    Log.d(TAG,"Exception thrown from reciver",t);
                }finally {
                    camera.addCallbackBuffer(data.array());
                }
            }
        }

    }
}
