package licenta.books.androidmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Highlight implements Parcelable {
    private Integer highlightId;
    private Integer pagePosition;
    private Integer noChapter;
    private String date;
    private Integer colorCode;
    private String content;

    public Highlight(Integer highlightId, Integer pagePosition, Integer noChapter, String date, Integer colorCode, String content) {
        this.highlightId = highlightId;
        this.pagePosition = pagePosition;
        this.noChapter = noChapter;
        this.date = date;
        this.colorCode = colorCode;
        this.content = content;
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
        date = in.readString();
        if (in.readByte() == 0) {
            colorCode = null;
        } else {
            colorCode = in.readInt();
        }
        content = in.readString();
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
        dest.writeString(date);
        if (colorCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(colorCode);
        }
        dest.writeString(content);
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
