package licenta.books.androidmobile.patterns.StrategySortBooks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import licenta.books.androidmobile.classes.BookE;

public class PublishedDateSort implements StrategySort, Parcelable {
    public String publicationDate = "Publication Date";
    private Comparator<BookE> comparator = (BookE o1, BookE o2)-> o2.getPublishedDate().compareTo(o1.getPublishedDate());

    public PublishedDateSort() {
    }

    protected PublishedDateSort(Parcel in) {
        publicationDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(publicationDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PublishedDateSort> CREATOR = new Creator<PublishedDateSort>() {
        @Override
        public PublishedDateSort createFromParcel(Parcel in) {
            return new PublishedDateSort(in);
        }

        @Override
        public PublishedDateSort[] newArray(int size) {
            return new PublishedDateSort[size];
        }
    };

    @Override
    public List<BookE> strategySort(List<BookE> books) {
        Collections.sort(books,comparator);
        return books;
    }
}
