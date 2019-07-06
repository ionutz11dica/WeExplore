package licenta.books.androidmobile.patterns.StrategySortBooks;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Converters.ArrayStringConverter;

public class AuthorSort implements StrategySort, Parcelable {
    public String author = "Authors";
    private Comparator<BookE> comparator = (BookE o1, BookE o2)-> ArrayStringConverter.fromArrayList(o1.getAuthors()).compareTo(ArrayStringConverter.fromArrayList(o2.getAuthors()));

    public AuthorSort() {
    }

    protected AuthorSort(Parcel in) {
        author = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthorSort> CREATOR = new Creator<AuthorSort>() {
        @Override
        public AuthorSort createFromParcel(Parcel in) {
            return new AuthorSort(in);
        }

        @Override
        public AuthorSort[] newArray(int size) {
            return new AuthorSort[size];
        }
    };

    @Override
    public List<BookE> strategySort(List<BookE> books) {
        Collections.sort(books,comparator);
        return books;
    }
}
