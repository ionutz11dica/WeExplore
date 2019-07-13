package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private Context context;
    private SurfaceView surfaceView;
    private boolean startRequested;
    private boolean surfaceAvailable;
    private CameraSource cameraSource;

    private GraphicOverlay overlay;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        startRequested = false;
        surfaceAvailable = false;

        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(surfaceView);
    }



    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException {
        if(cameraSource == null){
            stop();
        }

        this.cameraSource = cameraSource;

        if(this.cameraSource != null){
            startRequested = true;
            startIfReady();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException, SecurityException {
        this.overlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    public void release() {
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        if(startRequested && surfaceAvailable){
            cameraSource.start(surfaceView.getHolder());
            if(overlay !=null){
                Size size = cameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if(isPortraitMode()){
                    overlay.setCameraInfo(min, max, cameraSource.getCameraFacing());
                } else {
                    overlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
                }
                overlay.clear();
            }
            startRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceAvailable = true;
            try{
                startIfReady();
            }catch (SecurityException se) {
                Log.e(TAG,"Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceAvailable = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int lt, int top, int rt, int btm) {
        int width = 400;
        int height = 300;
        if(cameraSource !=null){
            Size size = cameraSource.getPreviewSize();
            if(size !=null){
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        //schimba width si height cand este portret, atunci cand este rotit la 90 de grade
        if(isPortraitMode()){
            int temp = width;
            width = height;
            height = temp;
        }

        final int lyWidth = rt - lt;
        final int lyHeight = btm - top;

        //calcueaza inaltimea si latimea pt o potentiala potrivire de latime
        int childWidth = lyWidth;
        int childHeight = (int)(((float) lyWidth /(float) width ) * height);

        if(childHeight > lyHeight){
            childHeight = lyHeight;
            childWidth = (int)(((float) lyHeight / (float) height) * width);
        }

        for(int i = 0; i < getChildCount();++i){
            getChildAt(i).layout(0,0,childWidth,childHeight);
        }

        try{
            startIfReady();
        }  catch (SecurityException se) {
            Log.e(TAG,"Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
