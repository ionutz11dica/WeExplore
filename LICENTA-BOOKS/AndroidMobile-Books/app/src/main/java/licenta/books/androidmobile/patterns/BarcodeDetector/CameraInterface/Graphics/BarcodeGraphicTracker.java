package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics;

import android.content.Context;
import android.support.annotation.UiThread;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.GraphicOverlay;

public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> overlay;
    private BarcodeGraphic graphic;

    private BarcodeUpdateListener barcodeUpdateListener;

    public interface BarcodeUpdateListener {
        @UiThread
        void onBarcodeDetected(Barcode barcode);
    }

    public BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic, Context context) {
        this.overlay = overlay;
        this.graphic = graphic;
        if (context instanceof BarcodeUpdateListener) {
            this.barcodeUpdateListener = (BarcodeUpdateListener) context;
        }else{
            throw new RuntimeException("Hosting activity must implement BarcodeUpdateListener");
        }
    }

    @Override
    public void onNewItem(int id, Barcode barcode) {
        graphic.setId(id);
        barcodeUpdateListener.onBarcodeDetected(barcode);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detections, Barcode barcode) {
        overlay.add(graphic);
        graphic.updateItem(barcode);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detections) {
        overlay.remove(graphic);
    }

    @Override
    public void onDone() {
        overlay.remove(graphic);
    }
}
