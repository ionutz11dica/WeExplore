package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.GraphicOverlay;

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private Context context;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> graphicOverlay, Context context) {
        this.graphicOverlay = graphicOverlay;
        this.context = context;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay);
        return new BarcodeGraphicTracker(graphicOverlay,graphic,context);
    }
}
