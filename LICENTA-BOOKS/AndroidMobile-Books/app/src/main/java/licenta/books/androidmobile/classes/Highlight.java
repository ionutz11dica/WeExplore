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

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "highlight_join",
        foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId",
                        onDelete = CASCADE
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
public class Highlight implements Parcelable, AnnotationFamily {
    @PrimaryKey(autoGenerate = true)
    private Integer highlightId;
    private Integer code;
    private Integer chapterIndex;
    private String chapterName;
    private Double pagePosInBoo;
    private Double pagePosInChapter;
    private Integer startIndex;
    private Integer endIndex;
    private Integer startOffset;
    private Integer endOffset;
    private Integer color;
    private String selectedText;
    private Integer left;
    private Integer top;
    private String noteContent;
    private Boolean isNote;
    private Boolean isOpen;
    @TypeConverters({TimestampConverter.class})
    private Date highlightedDate;
    private Boolean forSearch;
    private Integer style;
    private Integer pageIndex;
    private String bookId;
    private Integer userId;

    public Highlight(Integer code, Integer chapterIndex,String chapterName, Double pagePosInBoo, Double pagePosInChapter, Integer startIndex, Integer endIndex, Integer startOffset, Integer endOffset, Integer color, String selectedText,
                     Integer left, Integer top, String noteContent, Boolean isNote, Boolean isOpen, Date highlightedDate, Boolean forSearch, Integer style, Integer pageIndex, String bookId, Integer userId) {
        this.code = code;
        this.chapterIndex = chapterIndex;
        this.chapterName = chapterName;
        this.pagePosInBoo = pagePosInBoo;
        this.pagePosInChapter = pagePosInChapter;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.color = color;
        this.selectedText = selectedText;
        this.left = left;
        this.top = top;
        this.noteContent = noteContent;
        this.isNote = isNote;
        this.isOpen = isOpen;
        this.highlightedDate = highlightedDate;
        this.forSearch = forSearch;
        this.style = style;
        this.pageIndex = pageIndex;
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
            code = null;
        } else {
            code = in.readInt();
        }
        if (in.readByte() == 0) {
            chapterIndex = null;
        } else {
            chapterIndex = in.readInt();
        }
        chapterName = in.readString();
        if (in.readByte() == 0) {
            pagePosInBoo = null;
        } else {
            pagePosInBoo = in.readDouble();
        }
        if (in.readByte() == 0) {
            pagePosInChapter = null;
        } else {
            pagePosInChapter = in.readDouble();
        }
        if (in.readByte() == 0) {
            startIndex = null;
        } else {
            startIndex = in.readInt();
        }
        if (in.readByte() == 0) {
            endIndex = null;
        } else {
            endIndex = in.readInt();
        }
        if (in.readByte() == 0) {
            startOffset = null;
        } else {
            startOffset = in.readInt();
        }
        if (in.readByte() == 0) {
            endOffset = null;
        } else {
            endOffset = in.readInt();
        }
        if (in.readByte() == 0) {
            color = null;
        } else {
            color = in.readInt();
        }
        selectedText = in.readString();
        if (in.readByte() == 0) {
            left = null;
        } else {
            left = in.readInt();
        }
        if (in.readByte() == 0) {
            top = null;
        } else {
            top = in.readInt();
        }
        noteContent = in.readString();
        byte tmpIsNote = in.readByte();
        isNote = tmpIsNote == 0 ? null : tmpIsNote == 1;
        byte tmpIsOpen = in.readByte();
        isOpen = tmpIsOpen == 0 ? null : tmpIsOpen == 1;
        byte tmpForSearch = in.readByte();
        forSearch = tmpForSearch == 0 ? null : tmpForSearch == 1;
        if (in.readByte() == 0) {
            style = null;
        } else {
            style = in.readInt();
        }
        if (in.readByte() == 0) {
            pageIndex = null;
        } else {
            pageIndex = in.readInt();
        }
        bookId = in.readString();
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
        if (code == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(code);
        }
        if (chapterIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(chapterIndex);
        }
        dest.writeString(chapterName);
        if (pagePosInBoo == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pagePosInBoo);
        }
        if (pagePosInChapter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pagePosInChapter);
        }
        if (startIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(startIndex);
        }
        if (endIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(endIndex);
        }
        if (startOffset == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(startOffset);
        }
        if (endOffset == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(endOffset);
        }
        if (color == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(color);
        }
        dest.writeString(selectedText);
        if (left == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(left);
        }
        if (top == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(top);
        }
        dest.writeString(noteContent);
        dest.writeByte((byte) (isNote == null ? 0 : isNote ? 1 : 2));
        dest.writeByte((byte) (isOpen == null ? 0 : isOpen ? 1 : 2));
        dest.writeByte((byte) (forSearch == null ? 0 : forSearch ? 1 : 2));
        if (style == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(style);
        }
        if (pageIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageIndex);
        }
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Integer getHighlightId() {
        return highlightId;
    }

    public void setHighlightId(Integer highlightId) {
        this.highlightId = highlightId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public Double getPagePosInBoo() {
        return pagePosInBoo;
    }

    public void setPagePosInBoo(Double pagePosInBoo) {
        this.pagePosInBoo = pagePosInBoo;
    }

    public Double getPagePosInChapter() {
        return pagePosInChapter;
    }

    public void setPagePosInChapter(Double pagePosInChapter) {
        this.pagePosInChapter = pagePosInChapter;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    public Integer getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Integer endOffset) {
        this.endOffset = endOffset;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(Boolean noteContent) {
        isNote = noteContent;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Date getHighlightedDate() {
        return highlightedDate;
    }

    public void setHighlightedDate(Date highlightedDate) {
        this.highlightedDate = highlightedDate;
    }

    public Boolean isForSearch() {
        return forSearch;
    }

    public void setForSearch(Boolean forSearch) {
        this.forSearch = forSearch;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
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

    public void setNote(String note) {
        this.noteContent = note;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Boolean isNote() {
        return isNote;
    }

    public void setNote(Boolean note) {
        isNote = note;
    }
}
