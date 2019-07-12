package licenta.books.androidmobile.patterns.StrategySortBooks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import licenta.books.androidmobile.classes.BookE;

public class NoPagesSort implements StrategySort, Parcelable {
    public String noPage = "Number of Pages";

    private Comparator<BookE> comparator = (BookE o1, BookE o2)-> o2.getPageCount().compareTo(o1.getPageCount());

    public NoPagesSort() {
    }

    protected NoPagesSort(Parcel in) {
        noPage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NoPagesSort> CREATOR = new Creator<NoPagesSort>() {
        @Override
        public NoPagesSort createFromParcel(Parcel in) {
            return new NoPagesSort(in);
        }

        @Override
        public NoPagesSort[] newArray(int size) {
            return new NoPagesSort[size];
        }
    };

    @Override
    public List<BookE> strategySort(List<BookE> books) {
        Collections.sort(books,comparator);
        return books;
    }
}
