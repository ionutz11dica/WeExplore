package licenta.books.androidmobile.classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.sql.Timestamp;

import licenta.books.androidmobile.classes.Converters.TimestampConverter;

@Entity(tableName = "highlight_join",
        foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId"
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId")
        },
        indices = {
                @Index(value = "userId"),
                @Index(value = "bookId")
        })
public class Highlight implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer highlightId;
    private Integer pagePosition;
    private Integer noChapter;
    private Integer colorCode;
    private String content;
    private  Integer bookId;
    private  Integer userId;

    public Highlight( Integer pagePosition, Integer noChapter,Integer colorCode, String content,Integer bookId,Integer userId) {

        this.pagePosition = pagePosition;
        this.noChapter = noChapter;

        this.colorCode = colorCode;
        this.content = content;
        this.bookId = bookId;
        this.userId = userId;
    }


    protected Highlight(Parcel in) {
        if (in.readByte() == 0) {
            highlightId = null;
        } else {
            highlightId = in.readInt();
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
            colorCode = null;
        } else {
            colorCode = in.readInt();
        }
        content = in.readString();
        if (in.readByte() == 0) {
            bookId = null;
        } else {
            bookId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (highlightId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(highlightId);
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
        if (colorCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(colorCode);
        }
        dest.writeString(content);
        if (bookId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Highlight> CREATOR = new Creator<Highlight>() {
        @Override
        public Highlight createFromParcel(Parcel in) {
            return new Highlight(in);
        }

        @Override
        public Highlight[] newArray(int size) {
            return new Highlight[size];
        }
    };

    public Integer getHighlightId() {
        return highlightId;
    }

    public void setHighlightId(Integer highlightId) {
        this.highlightId = highlightId;
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



    public Integer getColorCode() {
        return colorCode;
    }

    public void setColorCode(Integer colorCode) {
        this.colorCode = colorCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
