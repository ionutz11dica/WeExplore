package licenta.books.androidmobile.activities.others;

import android.os.Parcel;
import android.os.Parcelable;

import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.interfaces.AnnotationFamily;

public class BookAnnotations implements Parcelable {
    private Bookmark bookmark;
    private Highlight highlight;

    public BookAnnotations(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public BookAnnotations(Highlight highlight) {
        this.highlight = highlight;
    }

    public BookAnnotations(Bookmark bookmark, Highlight highlight) {
        this.bookmark = bookmark;
        this.highlight = highlight;
    }

    protected BookAnnotations(Parcel in) {
        bookmark = in.readParcelable(Bookmark.class.getClassLoader());
        highlight = in.readParcelable(Highlight.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bookmark, flags);
        dest.writeParcelable(highlight, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookAnnotations> CREATOR = new Creator<BookAnnotations>() {
        @Override
        public BookAnnotations createFromParcel(Parcel in) {
            return new BookAnnotations(in);
        }

        @Override
        public BookAnnotations[] newArray(int size) {
            return new BookAnnotations[size];
        }
    };

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public Highlight getHighlight() {
        return highlight;
    }

    public void setHighlight(Highlight highlight) {
        this.highlight = highlight;
    }
}
