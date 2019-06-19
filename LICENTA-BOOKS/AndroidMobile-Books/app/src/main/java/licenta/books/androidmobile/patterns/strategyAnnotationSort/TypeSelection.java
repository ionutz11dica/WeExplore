package licenta.books.androidmobile.patterns.strategyAnnotationSort;

import java.util.ArrayList;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public interface TypeSelection {

    ArrayList<BookAnnotations> selectAnnotations(int color, ArrayList<BookAnnotations> bookAnnotations);
}
