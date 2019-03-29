package licenta.books.androidmobile.classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.util.ArrayList;

import licenta.books.androidmobile.classes.Converters.ArrayStringConverter;

@Entity(tableName = "book")
public class BookE implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private Integer bookId;
    private String title;
    @TypeConverters({ArrayStringConverter.class})
    private ArrayList<String> authors;
    @TypeConverters({ArrayStringConverter.class})
    private ArrayList<String> categories;

    private Integer pageCount;
    private String description;
    private String publisher;
    private String publishedDate;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private  byte[] image;
    private String pathFile;
    private String isbn;

    @Ignore
    private ArrayList<Review> reviews;
    @Ignore
    private String fileID;
    @Ignore
    private String imageLink;
    @Ignore
    private String _id;
    @Ignore
    private Boolean isEbook; //---??
    @Ignore
    private Boolean publicDomain; //---??
    @Ignore
    private Boolean isAvailableEpub; //---??
    @Ignore
    private String downloadLink; //---??




    @Ignore
    public BookE(Integer bookId, String title, ArrayList<String> authors, ArrayList<String> categories, ArrayList<Review> reviews,
                 Integer pageCount, String description, String publisher, String publishedDate, String imageLink, String _id, String fileID,String pathFile,String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.categories = categories;
        this.reviews = reviews;
        this.pageCount = pageCount;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.imageLink = imageLink;
        this._id = _id;
        this.fileID = fileID;
        this.pathFile = pathFile;
        this.isbn = isbn;
    }
    //Database constructor
    public BookE( String title, ArrayList<String> authors, ArrayList<String> categories, Integer pageCount, String description, String publisher,
                 String publishedDate, byte[] image, String pathFile, String isbn) {

        this.title = title;
        this.authors = authors;
        this.categories = categories;
        this.pageCount = pageCount;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
        this.pathFile = pathFile;
        this.isbn = isbn;
    }

    protected BookE(Parcel in) {
        if (in.readByte() == 0) {
            bookId = null;
        } else {
            bookId = in.readInt();
        }
        title = in.readString();
        authors = in.createStringArrayList();
        categories = in.createStringArrayList();
        reviews = in.createTypedArrayList(Review.CREATOR);
        if (in.readByte() == 0) {
            pageCount = null;
        } else {
            pageCount = in.readInt();
        }
        description = in.readString();
        publisher = in.readString();
        publishedDate = in.readString();
        imageLink = in.readString();
        image = in.createByteArray();
        fileID = in.readString();
        pathFile = in.readString();
        isbn = in.readString();
        _id = in.readString();
        byte tmpIsEbook = in.readByte();
        isEbook = tmpIsEbook == 0 ? null : tmpIsEbook == 1;
        byte tmpPublicDomain = in.readByte();
        publicDomain = tmpPublicDomain == 0 ? null : tmpPublicDomain == 1;
        byte tmpIsAvailableEpub = in.readByte();
        isAvailableEpub = tmpIsAvailableEpub == 0 ? null : tmpIsAvailableEpub == 1;
        downloadLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (bookId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookId);
        }
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeStringList(categories);
        dest.writeTypedList(reviews);
        if (pageCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageCount);
        }
        dest.writeString(description);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(imageLink);
        dest.writeByteArray(image);
        dest.writeString(fileID);
        dest.writeString(pathFile);
        dest.writeString(isbn);
        dest.writeString(_id);
        dest.writeByte((byte) (isEbook == null ? 0 : isEbook ? 1 : 2));
        dest.writeByte((byte) (publicDomain == null ? 0 : publicDomain ? 1 : 2));
        dest.writeByte((byte) (isAvailableEpub == null ? 0 : isAvailableEpub ? 1 : 2));
        dest.writeString(downloadLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookE> CREATOR = new Creator<BookE>() {
        @Override
        public BookE createFromParcel(Parcel in) {
            return new BookE(in);
        }

        @Override
        public BookE[] newArray(int size) {
            return new BookE[size];
        }
    };

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
