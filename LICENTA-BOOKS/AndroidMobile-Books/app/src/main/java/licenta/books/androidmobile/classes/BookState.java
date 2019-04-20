package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import licenta.books.androidmobile.classes.Converters.TimestampConverter;

@Entity(tableName = "bookstate",
        foreignKeys = @ForeignKey(entity = BookE.class,
        parentColumns = "bookId",
        childColumns = "bookId"),
    indices = {@Index(value = {"bookId"},unique = true)})
public class BookState implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer stateId;
    private Float pagePosition;
    private Integer noChapter;
    @TypeConverters({TimestampConverter.class})
    private Date readDate;
    private String bookId;


    public BookState(Float pagePosition, Integer noChapter,String bookId,Date readDate) {
        this.pagePosition = pagePosition;
        this.noChapter = noChapter;
        this.bookId = bookId;
        this.readDate = readDate;
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
            pagePosition = in.readFloat();
        }
        if (in.readByte() == 0) {
            noChapter = null;
        } else {
            noChapter = in.readInt();
        }
        bookId = in.readString();
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
            dest.writeFloat(pagePosition);
        }
        if (noChapter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(noChapter);
        }
        dest.writeString(bookId);
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

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Float getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(Float pagePosition) {
        this.pagePosition = pagePosition;
    }

    public Integer getNoChapter() {
        return noChapter;
    }

    public void setNoChapter(Integer noChapter) {
        this.noChapter = noChapter;
    }
}
