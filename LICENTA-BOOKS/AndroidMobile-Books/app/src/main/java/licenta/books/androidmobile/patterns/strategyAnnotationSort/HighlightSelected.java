package licenta.books.androidmobile.patterns.strategyAnnotationSort;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.Collectors;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public class HighlightSelected implements TypeSelection {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ArrayList<BookAnnotations> selectAnnotations(int color, ArrayList<BookAnnotations> bookAnnotations) {

        if(bookAnnotations.isEmpty()){
            return bookAnnotations;
        }

        bookAnnotations.removeIf(n -> n.getBookmark()!=null || n.getHighlight().getColor()!=color);

        return bookAnnotations;
    }
}
