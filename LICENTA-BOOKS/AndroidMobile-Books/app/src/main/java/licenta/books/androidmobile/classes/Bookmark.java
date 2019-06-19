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
import licenta.books.androidmobile.interfaces.AnnotationFamily;

@Entity(
        tableName = "bookmark_join",
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
public class Bookmark implements Parcelable, AnnotationFamily {
    @PrimaryKey(autoGenerate = true)
    private Integer bookmarkId;
    private Integer bookmarkCode;
    private Double pagePosition;
    private Integer chapterIndex;
    private Integer pageIndex;
    private String bookmarkPageInfo;
    @TypeConverters({TimestampConverter.class})
    private Date bookmarkDate;
    private String chapterName;
    private String bookId;
    private Integer userId;

    public Bookmark( Integer bookmarkCode, Double pagePosition, Integer chapterIndex, Integer pageIndex, String bookmarkPageInfo,Date bookmarkDate,String chapterName, String bookId, Integer userId) {

        this.bookmarkCode = bookmarkCode;
        this.pagePosition = pagePosition;
        this.chapterIndex = chapterIndex;
        this.pageIndex = pageIndex;
        this.bookmarkPageInfo = bookmarkPageInfo;
        this.bookmarkDate = bookmarkDate;
        this.chapterName = chapterName;
        this.bookId = bookId;
        this.userId = userId;
    }


    protected Bookmark(Parcel in) {
        if (in.readByte() == 0) {
            bookmarkId = null;
        } else {
            bookmarkId = in.readInt();
        }
        if (in.readByte() == 0) {
            bookmarkCode = null;
        } else {
            bookmarkCode = in.readInt();
        }
        if (in.readByte() == 0) {
            pagePosition = null;
        } else {
            pagePosition = in.readDouble();
        }
        if (in.readByte() == 0) {
            chapterIndex = null;
        } else {
            chapterIndex = in.readInt();
        }
        if (in.readByte() == 0) {
            pageIndex = null;
        } else {
            pageIndex = in.readInt();
        }
        bookmarkPageInfo = in.readString();
        chapterName = in.readString();
        bookId = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (bookmarkId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookmarkId);
        }
        if (bookmarkCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookmarkCode);
        }
        if (pagePosition == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pagePosition);
        }
        if (chapterIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(chapterIndex);
        }
        if (pageIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageIndex);
        }
        dest.writeString(bookmarkPageInfo);
        dest.writeString(chapterName);
        dest.writeString(bookId);
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

    public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
        @Override
        public Bookmark createFromParcel(Parcel in) {
            return new Bookmark(in);
        }

        @Override
        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Date getBookmarkDate() {
        return bookmarkDate;
    }

    public void setBookmarkDate(Date bookmarkDate) {
        this.bookmarkDate = bookmarkDate;
    }

    public Integer getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(Integer bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public Integer getBookmarkCode() {
        return bookmarkCode;
    }

    public void setBookmarkCode(Integer bookmarkCode) {
        this.bookmarkCode = bookmarkCode;
    }

    public Double getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(Double pagePosition) {
        this.pagePosition = pagePosition;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getBookmarkPageInfo() {
        return bookmarkPageInfo;
    }

    public void setBookmarkPageInfo(String bookmarkPageInfo) {
        this.bookmarkPageInfo = bookmarkPageInfo;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
