package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(foreignKeys = @ForeignKey(entity = BookE.class,
        parentColumns = "bookId",
        childColumns = "stateId"))
public class BookState implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer stateId;
    private Integer pagePosition;
    private Integer noChapter;
    private Integer bookId;


    public BookState(Integer stateId, Integer pagePosition, Integer noChapter,Integer bookId) {
        this.stateId = stateId;
        this.pagePosition = pagePosition;
        this.noChapter = noChapter;
        this.bookId = bookId;
    }


    protected BookState(Parcel in) {
        if (in.readByte() == 0) {
            stateId = null;
        } else {
            stateId = in.readInt();
        }
        if (in.readByte() == 0) {
            pagePosition = null;
        } else {
            pagePosition = in.readInt();
        }
        if (in.readByte() == 0) {
            noChapter = null;
        } else {
            noChapter = in.readInt();
        }
        if (in.readByte() == 0) {
            bookId = null;
        } else {
            bookId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (stateId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(stateId);
        }
        if (pagePosition == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pagePosition);
        }
        if (noChapter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(noChapter);
        }
        if (bookId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookState> CREATOR = new Creator<BookState>() {
        @Override
        public BookState createFromParcel(Parcel in) {
            return new BookState(in);
        }

        @Override
        public BookState[] newArray(int size) {
            return new BookState[size];
        }
    };

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(Integer pagePosition) {
        this.pagePosition = pagePosition;
    }

    public Integer getNoChapter() {
        return noChapter;
    }

    public void setNoChapter(Integer noChapter) {
        this.noChapter = noChapter;
    }
}
