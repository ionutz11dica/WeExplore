package licenta.books.androidmobile.patterns.StrategySortBooks;

import java.util.List;

import licenta.books.androidmobile.classes.BookE;

public class Sorter {
    private StrategySort strategySort;

    public Sorter(StrategySort strategySort) {
        this.strategySort = strategySort;
    }

    public StrategySort getStrategySort() {
        return strategySort;
    }

    public void setStrategySort(StrategySort strategySort) {
        this.strategySort = strategySort;
    }

    public List<BookE> sorting(List<BookE> bookES){
        return strategySort.strategySort(bookES);
    }
}
