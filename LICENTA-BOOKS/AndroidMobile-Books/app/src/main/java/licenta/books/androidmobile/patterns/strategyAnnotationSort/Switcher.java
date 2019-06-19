package licenta.books.androidmobile.patterns.strategyAnnotationSort;

import java.util.ArrayList;

import licenta.books.androidmobile.activities.others.BookAnnotations;

public class Switcher {
    private int color;
    private TypeSelection typeSelection;

    public Switcher(int color, TypeSelection typeSelection) {
        this.color = color;
        this.typeSelection = typeSelection;
    }

    public ArrayList<BookAnnotations> switchTypeSelection(int color, ArrayList<BookAnnotations> bookAnnotations){
       return typeSelection.selectAnnotations(color,bookAnnotations);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public TypeSelection getTypeSelection() {
        return typeSelection;
    }

    public void setTypeSelection(TypeSelection typeSelection) {
        this.typeSelection = typeSelection;
    }
}
