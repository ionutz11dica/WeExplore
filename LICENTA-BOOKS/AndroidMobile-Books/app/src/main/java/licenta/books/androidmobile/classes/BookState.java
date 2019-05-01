package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.skytree.epub.PageTransition;

import java.util.Date;

import licenta.books.androidmobile.classes.Converters.PageTransitionConverter;
import licenta.books.androidmobile.classes.Converters.TimestampConverter;

@Entity(tableName = "bookstate",
        foreignKeys = @ForeignKey(entity = BookE.class,
        parentColumns = "bookId",
        childColumns = "bookId"),
    indices = {@Index(value = {"bookId"},unique = true)})
public class BookState implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer stateId;
    private Double pagePosition;
    private Integer noChapter;
    @TypeConverters({TimestampConverter.class})
    private Date readDate;
    private String fontType;
    private Integer fontSize;
    private Integer backgroundColor;
    private Integer colorText;
    @TypeConverters({PageTransitionConverter.class})
    private PageTransition pageTransition;
    private String bookId;

    public BookState( Double pagePosition, Integer noChapter, Date readDate, String fontType, Integer fontSize, Integer backgroundColor, Integer colorText, PageTransition pageTransition, String bookId) {
        this.pagePosition = pagePosition;
        this.noChapter = noChapter;
        this.readDate = readDate;
        this.fontType = fontType;
        this.fontSize = fontSize;
        this.backgroundColor = backgroundColor;
        this.colorText = colorText;
        this.pageTransition = pageTransition;
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
            pagePosition = in.readDouble();
        }
        if (in.readByte() == 0) {
            noChapter = null;
        } else {
            noChapter = in.readInt();
        }
        fontType = in.readString();
        if (in.readByte() == 0) {
            fontSize = null;
        } else {
            fontSize = in.readInt();
        }
        if (in.readByte() == 0) {
            backgroundColor = null;
        } else {
            backgroundColor = in.readInt();
        }
        if (in.readByte() == 0) {
            colorText = null;
        } else {
            colorText = in.readInt();
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
            dest.writeDouble(pagePosition);
        }
        if (noChapter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(noChapter);
        }
        dest.writeString(fontType);
        if (fontSize == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fontSize);
        }
        if (backgroundColor == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(backgroundColor);
        }
        if (colorText == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(colorText);
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

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Double getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(Double pagePosition) {
        this.pagePosition = pagePosition;
    }

    public Integer getNoChapter() {
        return noChapter;
    }

    public void setNoChapter(Integer noChapter) {
        this.noChapter = noChapter;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getColorText() {
        return colorText;
    }

    public void setColorText(Integer colorText) {
        this.colorText = colorText;
    }

    public PageTransition getPageTransition() {
        return pageTransition;
    }

    public void setPageTransition(PageTransition pageTransition) {
        this.pageTransition = pageTransition;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
