package licenta.books.androidmobile.activities.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurImageGenre(Context context, Bitmap bitmap){
        int width = Math.round(bitmap.getWidth()* Constants.BITMAP_SCALE_GENRE);
        int height = Math.round(bitmap.getHeight()*Constants.BITMAP_SCALE_GENRE);

        Bitmap inputBitamp = Bitmap.createScaledBitmap(bitmap,width,height,false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitamp);

        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation tempIn = Allocation.createFromBitmap(renderScript,inputBitamp);
        Allocation tempOut = Allocation.createFromBitmap(renderScript,outputBitmap);

        intrinsicBlur.setRadius(Constants.BLUR_RADIUS_GENRE);
        intrinsicBlur.setInput(tempIn);
        intrinsicBlur.forEach(tempOut);
        tempOut.copyTo(outputBitmap);

        return  outputBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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
