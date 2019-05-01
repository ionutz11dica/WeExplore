package licenta.books.androidmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
    private String chapterName;
    private Integer noPage;

    public Chapter(String chapterName, Integer noPage) {
        this.chapterName = chapterName;
        this.noPage = noPage;
    }

    protected Chapter(Parcel in) {
        chapterName = in.readString();
        if (in.readByte() == 0) {
            noPage = null;
        } else {
            noPage = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chapterName);
        if (noPage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(noPage);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Integer getNoPage() {
        return noPage;
    }

    public void setNoPage(Integer noPage) {
        this.noPage = noPage;
    }
}
