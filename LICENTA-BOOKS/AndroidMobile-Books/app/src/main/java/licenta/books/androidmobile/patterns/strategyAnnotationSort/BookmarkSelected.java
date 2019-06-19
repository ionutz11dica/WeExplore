package licenta.books.androidmobile.patterns.strategyAnnotationSort;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public class BookmarkSelected implements TypeSelection {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ArrayList<BookAnnotations> selectAnnotations(int color, ArrayList<BookAnnotations> bookAnnotations) {

        if(bookAnnotations.isEmpty()){
            return bookAnnotations;
        }

        bookAnnotations.removeIf(n -> n.getHighlight()!=null);

        return bookAnnotations;
    }
}
