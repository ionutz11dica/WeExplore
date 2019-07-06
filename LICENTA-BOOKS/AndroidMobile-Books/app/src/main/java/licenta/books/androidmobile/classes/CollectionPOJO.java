package licenta.books.androidmobile.classes;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class CollectionPOJO implements Parcelable {
    public int bookIds;
    public String collectionName;

    public CollectionPOJO(){

    }

    protected CollectionPOJO(Parcel in) {
        bookIds = in.readInt();
        collectionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookIds);
        dest.writeString(collectionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CollectionPOJO> CREATOR = new Creator<CollectionPOJO>() {
        @Override
        public CollectionPOJO createFromParcel(Parcel in) {
            return new CollectionPOJO(in);
        }

        @Override
        public CollectionPOJO[] newArray(int size) {
            return new CollectionPOJO[size];
        }
    };
}
