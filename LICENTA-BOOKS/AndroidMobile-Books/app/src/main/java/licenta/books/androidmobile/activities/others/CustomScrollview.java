package licenta.books.androidmobile.activities.others;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollview extends ScrollView {
   private OnScrollListener onScrollListener = null;

    public CustomScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }



//    @Override
////    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
////        super.onScrollChanged(l, t, oldl, oldt);
////        Log.e("Scroll", "new  " + t + "  old  " + oldt);
////        if (t >= oldt) {
////            System.out.println("dwwn");
////        } else {
////            System.out.println("upppp");
////        }
////    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(x, y, oldx, oldy);
        }
    }

    public interface OnScrollListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }
}
