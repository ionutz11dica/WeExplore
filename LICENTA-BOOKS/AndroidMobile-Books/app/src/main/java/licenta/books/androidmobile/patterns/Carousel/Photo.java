package licenta.books.androidmobile.patterns.Carousel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public final class Photo implements Parcelable
{

    private static final long serialVersionUID = 1L;

    public final String title;

    public final String imageLink;

    public Photo(String name, String image)
    {
        this.title = name;
        this.imageLink = image;
    }

    protected Photo(Parcel in) {
        title = in.readString();
        imageLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
