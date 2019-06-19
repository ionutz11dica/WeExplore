package licenta.books.androidmobile.patterns.prototypeAnnotation;

import java.util.ArrayList;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public class ListBookAnnotations implements Prototype {
    private ArrayList<BookAnnotations> bookAnnotations;


    public ListBookAnnotations() {
        this.bookAnnotations = new ArrayList<>();
    }

    public ArrayList<BookAnnotations> getBookAnnotations() {
        return bookAnnotations;
    }

    public void setBookAnnotations(ArrayList<BookAnnotations> bookAnnotations) {
        this.bookAnnotations = bookAnnotations;
    }

    @Override
    public Prototype copyBookAnnotations() {
        ListBookAnnotations listBookAnnotations = new ListBookAnnotations();
        ArrayList<BookAnnotations> tempList = new ArrayList<>(this.bookAnnotations);
        listBookAnnotations.setBookAnnotations(tempList);

        return listBookAnnotations;
    }
}
