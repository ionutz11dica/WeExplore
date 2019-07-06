package licenta.books.androidmobile.patterns.StrategySortBooks;

import android.os.Parcel;
import android.os.Parcelable;

import com.skytree.epub.Book;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import licenta.books.androidmobile.classes.BookE;

public class TitleSort implements StrategySort, Parcelable {
    public String title = "Title";

    private Comparator<BookE> comparator = (BookE o1, BookE o2)-> o1.getTitle().compareTo(o2.getTitle());

    public TitleSort() {
    }

    protected TitleSort(Parcel in) {
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TitleSort> CREATOR = new Creator<TitleSort>() {
        @Override
        public TitleSort createFromParcel(Parcel in) {
            return new TitleSort(in);
        }

        @Override
        public TitleSort[] newArray(int size) {
            return new TitleSort[size];
        }
    };

    @Override
    public List<BookE> strategySort(List<BookE> books) {
        Collections.sort(books,comparator);
        return books;
    }
}
