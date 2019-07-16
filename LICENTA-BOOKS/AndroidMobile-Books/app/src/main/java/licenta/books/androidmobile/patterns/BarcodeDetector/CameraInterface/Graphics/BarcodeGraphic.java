package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.barcode.Barcode;

import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.GraphicOverlay;

public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private int id;

    private static final int COLOR_CHOICES[] = {
            Color.parseColor("#099a97"),
            Color.parseColor("#d8cbbb"),
            Color.parseColor("#f2f4fb"),
            Color.parseColor("#eaec96")
    };

    private static int currentColorIndex = 0;

    private Paint rectPaint;
    private Paint textPaint;
    private volatile Barcode barcode;

    BarcodeGraphic(GraphicOverlay overlay){
        super(overlay);

        currentColorIndex = (currentColorIndex +1 ) % COLOR_CHOICES.length;
        final  int selectedColor = COLOR_CHOICES[currentColorIndex];

        rectPaint = new Paint();
        rectPaint.setColor(selectedColor);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3.0f);

        textPaint = new Paint();
        textPaint.setColor(selectedColor);
        rectPaint.setTextSize(25.0f);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){return id;}

    public Barcode getBarcode() {
        return barcode;
    }

    /** Actualizeaza instanta codului de bare prin detectarea celui mai recent frame*/
    void updateItem(Barcode barcode) {
        this.barcode = barcode;
        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas){
        Barcode barcode2 = barcode;
        if (barcode2 == null) {
            return;
        }

        //desenarea box-ului in care se afla codul de bare
        RectF rectF = new RectF(barcode2.getBoundingBox());
        rectF.left = translateX(rectF.left);
        rectF.top = translateY(rectF.top);
        rectF.right = translateX(rectF.right);
        rectF.bottom = translateY(rectF.bottom);
        canvas.drawRect(rectF, rectPaint);

        //desenarea unui label in partea de jos a barcode-ului pentru indicarea valorii
        canvas.drawText(barcode2.rawValue,rectF.left,rectF.bottom,textPaint);
    }

}
