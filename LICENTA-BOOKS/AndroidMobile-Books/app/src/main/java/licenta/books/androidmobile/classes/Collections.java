package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "collection",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId"),
            indices = {@Index(value = "userId")})
public class Collections implements Parcelable {
    @NonNull
    @PrimaryKey
    private Integer collectionId;
    private String nameCollection;
    private Integer userId;

    public Collections(@NonNull Integer collectionId, String nameCollection, Integer userId) {
        this.collectionId = collectionId;
        this.nameCollection = nameCollection;
        this.userId = userId;
    }

    @NonNull
    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(@NonNull Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    protected Collections(Parcel in) {
        collectionId = in.readInt();
        nameCollection = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(collectionId);
        dest.writeString(nameCollection);
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
}
