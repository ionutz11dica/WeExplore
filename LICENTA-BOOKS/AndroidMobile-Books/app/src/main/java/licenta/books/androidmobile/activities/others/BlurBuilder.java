package licenta.books.androidmobile.activities.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;

import licenta.books.androidmobile.interfaces.Constants;

public class BlurBuilder {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurImage(Context context, Bitmap bitmap){
        int width = Math.round(bitmap.getWidth()* Constants.BITMAP_SCALE);
        int height = Math.round(bitmap.getHeight()*Constants.BITMAP_SCALE);

        Bitmap inputBitamp = Bitmap.createScaledBitmap(bitmap,width,height,false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitamp);

        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation tempIn = Allocation.createFromBitmap(renderScript,inputBitamp);
        Allocation tempOut = Allocation.createFromBitmap(renderScript,outputBitmap);

        intrinsicBlur.setRadius(Constants.BLUR_RADIUS);
        intrinsicBlur.setInput(tempIn);
        intrinsicBlur.forEach(tempOut);
        tempOut.copyTo(outputBitmap);

        return  outputBitmap;

    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }
}
