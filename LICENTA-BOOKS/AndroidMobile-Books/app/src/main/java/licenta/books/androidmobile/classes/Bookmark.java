package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

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
public class Bookmark implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer bookmarkId;
    private Integer pagePosition;
    private Integer noChapter;
    private  Integer bookId;
    private  Integer userId;

    public Bookmark( Integer pagePosition, Integer noChapter, Integer bookId, Integer userId) {
        this.pagePosition = pagePosition;
        this.noChapter = noChapter;
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

    public Integer getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(Integer bookmarkId) {
        this.bookmarkId = bookmarkId;
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
