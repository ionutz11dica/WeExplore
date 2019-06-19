package licenta.books.androidmobile.patterns.strategyAnnotationSort;

import java.util.ArrayList;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public class AllSelected implements TypeSelection{
    @Override
    public ArrayList<BookAnnotations> selectAnnotations(int color, ArrayList<BookAnnotations> bookAnnotations) {
        return bookAnnotations;
    }
}
