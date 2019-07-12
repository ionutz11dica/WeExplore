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

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "bookId")
    private String _id;
    private String title;
    @TypeConverters({ArrayStringConverter.class})
    private ArrayList<String> authors;
    @TypeConverters({ArrayStringConverter.class})
    private ArrayList<String> categories;

    private Integer pageCount;
    private String description;
    private String publisher;
    private String publishedDate;

    @Ignore
    private  byte[] image;
    @ColumnInfo(name = "path_file")
    private String pathBook;
    private String isbn;
    private String imageLink;


    @Ignore
    private ArrayList<Review> reviews;
    @Ignore
    private String fileID;


    @Ignore
    private Boolean isEbook; //---??
    @Ignore
    private Boolean publicDomain; //---??
    @Ignore
    private Boolean isAvailableEpub; //---??
    @Ignore
    private String downloadLink; //---??
    @Ignore
    private int noDownloads;

    //Database constructor
    public BookE(@NonNull String _id, String title, ArrayList<String> authors, ArrayList<String> categories, Integer pageCount, String description, String publisher,
                 String publishedDate, @NonNull String pathBook, String isbn, String imageLink) {
        this._id = _id;
        this.title = title;
        this.authors = authors;
        this.categories = categories;
        this.pageCount = pageCount;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.imageLink = imageLink;

        this.pathBook = pathBook;
        this.isbn = isbn;

    }



    @Ignore
    public BookE() {
        super();
    }


    protected BookE(Parcel in) {
        _id = in.readString();
        title = in.readString();
        authors = in.createStringArrayList();
        categories = in.createStringArrayList();
        if (in.readByte() == 0) {
            pageCount = null;
        } else {
            pageCount = in.readInt();
        }
        description = in.readString();
        publisher = in.readString();
        publishedDate = in.readString();
        image = in.createByteArray();
        pathBook = in.readString();
        isbn = in.readString();
        imageLink = in.readString();
        reviews = in.createTypedArrayList(Review.CREATOR);
        fileID = in.readString();
        byte tmpIsEbook = in.readByte();
        isEbook = tmpIsEbook == 0 ? null : tmpIsEbook == 1;
        byte tmpPublicDomain = in.readByte();
        publicDomain = tmpPublicDomain == 0 ? null : tmpPublicDomain == 1;
        byte tmpIsAvailableEpub = in.readByte();
        isAvailableEpub = tmpIsAvailableEpub == 0 ? null : tmpIsAvailableEpub == 1;
        downloadLink = in.readString();
        noDownloads = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeStringList(categories);
        if (pageCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageCount);
        }
        dest.writeString(description);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeByteArray(image);
        dest.writeString(pathBook);
        dest.writeString(isbn);
        dest.writeString(imageLink);
        dest.writeTypedList(reviews);
        dest.writeString(fileID);
        dest.writeByte((byte) (isEbook == null ? 0 : isEbook ? 1 : 2));
        dest.writeByte((byte) (publicDomain == null ? 0 : publicDomain ? 1 : 2));
        dest.writeByte((byte) (isAvailableEpub == null ? 0 : isAvailableEpub ? 1 : 2));
        dest.writeString(downloadLink);
        dest.writeInt(noDownloads);
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


    public int getNoDownloads() {
        return noDownloads;
    }

    public void setNoDownloads(int noDownloads) {
        this.noDownloads = noDownloads;
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

    public String getPathBook() {
        return pathBook;
    }

    public void setPathBook(String pathBook) {
        this.pathBook = pathBook;
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

    public static String convertFromArray(ArrayList<String> authors){
        StringBuilder sb = new StringBuilder();
        for(String s : authors){
            if(authors.size() == 1){
                sb.append(s);
            }else{
                sb.append(s).append(", ");
            }

        }
        return sb.toString();
    }
}
