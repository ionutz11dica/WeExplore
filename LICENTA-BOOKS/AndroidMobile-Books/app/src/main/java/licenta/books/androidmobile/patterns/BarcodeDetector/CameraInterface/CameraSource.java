package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class CameraSource {
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
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

    private Camera camera;
    private int facing = CAMERA_FACING_BACK;
    private  int rotation;
    private Size previewSize;

    private float requestFps = 30.0f;
    private int requestPreviewWidth = 1024;
    private int requestPrevireHeight = 768;

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
    public static class Builder{
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
            cameraSource.requestPrevireHeight = height;
            return this;
        }

        //setam camera de spate si verificam daca este sau nu setata corect
        public Builder setFacing(int facing){
            if((facing!= CAMERA_FACING_BACK)){
                throw new IllegalArgumentException("Invalid camera: "+facing);
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
