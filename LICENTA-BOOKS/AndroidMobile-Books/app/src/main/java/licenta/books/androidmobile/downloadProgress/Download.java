package licenta.books.androidmobile.downloadProgress;

import android.os.Parcel;
import android.os.Parcelable;

public class Download implements Parcelable {

    private int progress;
    private long currentFileSize;
    private long totalFileSize;


    public Download() {
    }

    public Download(Parcel in) {
        progress = in.readInt();
        currentFileSize = in.readLong();
        totalFileSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(progress);
        dest.writeLong(currentFileSize);
        dest.writeLong(totalFileSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Download> CREATOR = new Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel in) {
            return new Download(in);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }
}
