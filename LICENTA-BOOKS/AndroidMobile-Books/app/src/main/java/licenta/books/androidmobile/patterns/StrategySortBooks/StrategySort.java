package licenta.books.androidmobile.patterns.StrategySortBooks;

import java.util.List;

import licenta.books.androidmobile.classes.BookE;

public interface StrategySort {
    List<BookE> strategySort( List<BookE> books);
}
