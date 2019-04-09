package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(
        tableName = "collection_join",
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
public class Collections implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer collectionId;
    private String nameCollection;
    private String bookId;
    private Integer userId;


    public Collections(Integer collectionId, String nameCollection, String bookId, Integer userId) {
        this.collectionId = collectionId;
        this.nameCollection = nameCollection;
        this.bookId = bookId;
        this.userId = userId;
    }

    protected Collections(Parcel in) {
        if (in.readByte() == 0) {
            collectionId = null;
        } else {
            collectionId = in.readInt();
        }
        nameCollection = in.readString();
        bookId = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (collectionId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(collectionId);
        }
        dest.writeString(nameCollection);
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

    public static final Creator<Collections> CREATOR = new Creator<Collections>() {
        @Override
        public Collections createFromParcel(Parcel in) {
            return new Collections(in);
        }

        @Override
        public Collections[] newArray(int size) {
            return new Collections[size];
        }
    };

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
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
