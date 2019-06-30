package licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class GraphicOverlay <T extends GraphicOverlay.Graphic> extends View {

    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;
    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int facing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> graphics = new HashSet<>();



    /** Clasa de baza pentru crearea unui obiect grafic customizat pentru a fi randat in cadrul graphicOverlay-ului*/
    public static abstract class Graphic {
        private GraphicOverlay overlay;

        public Graphic(GraphicOverlay graphicOverlay){
            this.overlay = graphicOverlay;
        }


        public abstract void draw(Canvas canvas);

        /** Ajusteaza o valoare orizontala a valori furnizate de la scalarea anterioara catre vizualizarea scalara
         */
        public float scaleX(float horizontal) { return  horizontal * overlay.widthScaleFactor;}

        /** Ajusteaza o valoare verticala a valori furnizate de la scalarea anterioara catre vizualizarea scalara
         */
        public float scaleY(float vertical) { return  vertical * overlay.heightScaleFactor;}

        /** Ajusteaza coordonat x de la systemul de coordonate anterior catre vizualizarea de coordonate
         */

        public float translateX(float x){
            if(overlay.facing == CameraSource.CAMERA_FACING_FRONT){
                return overlay.getWidth() - scaleX(x);
            }else{
                return scaleX(x);
            }
        }

        public float translateY(float y){
            return scaleY(y);
        }

        public void postInvalidate() {overlay.postInvalidate();}
    }

    public GraphicOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /** Sterge toate graficele din overlay */
    public void clear(){
        synchronized (lock){
            graphics.clear();
        }
        postInvalidate();
    }

    /** adauga un grafic overlay-ului*/
    public void add(T graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
        postInvalidate();
    }

    /** sterge un gravic din overlay*/
    public void remove(T graphic){
        synchronized (lock){
            graphics.remove(graphic);
        }
        postInvalidate();
    }

    /**returneaza o copie a setului cu toate graficele actvie*/
    public List<T> getGraphics(){
        synchronized (lock){
            return new Vector(graphics);
        }
    }

    public float getWidthScaleFactor() {
        return widthScaleFactor;
    }

    public float getHeightScaleFactor() {
        return heightScaleFactor;
    }

    /** seteaza atributele camerei pentru marimea si directia camerei, care informeaza cum sa tranforme mai tarziu coordonatele imaginii*/
    public void setCameraInfo(int previewWidth, int previewHeight, int facing){
        synchronized (lock){
            this.previewHeight = previewHeight;
            this.previewWidth = previewWidth;
            this.facing = facing;
        }
        postInvalidate();
    }

    /** deseneaza overlay-ul cu obiectul grafic asociat*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight!= 0)) {
                widthScaleFactor = (float) getWidth() / (float) previewWidth;
                heightScaleFactor = (float) getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }
}
