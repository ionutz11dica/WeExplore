package licenta.books.androidmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class BookE implements Parcelable {

    private String _id;
    private String title;
    private ArrayList<String> authors;
    private ArrayList<String> categories;
    private ArrayList<Review> reviews;
    private Integer pageCount;
    private String description;
    private String publisher;
    private String publishedDate;
    private String imageLink;
    private Boolean isEbook; //---??
    private Boolean publicDomain; //---??
    private Boolean isAvailableEpub; //---??
    private String downloadLink; //---??
    private String fileID;


    public BookE(String _id, String title, ArrayList<String> authors, ArrayList<String> categories, ArrayList<Review> reviews, Integer pageCount, String description, String publisher, String publishedDate,
                 String imageLink, Boolean isEbook, Boolean publicDomain, Boolean isAvailableEpub, String downloadLink, String fileID) {
        this._id = _id;
        this.title = title;
        this.authors = authors;
        this.categories = categories;
        this.reviews = reviews;
        this.pageCount = pageCount;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.imageLink = imageLink;
        this.isEbook = isEbook;
        this.publicDomain = publicDomain;
        this.isAvailableEpub = isAvailableEpub;
        this.downloadLink = downloadLink;
        this.fileID = fileID;
    }

    public BookE(){

    }


    protected BookE(Parcel in) {
        _id = in.readString();
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
        byte tmpIsEbook = in.readByte();
        isEbook = tmpIsEbook == 0 ? null : tmpIsEbook == 1;
        byte tmpPublicDomain = in.readByte();
        publicDomain = tmpPublicDomain == 0 ? null : tmpPublicDomain == 1;
        byte tmpIsAvailableEpub = in.readByte();
        isAvailableEpub = tmpIsAvailableEpub == 0 ? null : tmpIsAvailableEpub == 1;
        downloadLink = in.readString();
        fileID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
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
        dest.writeByte((byte) (isEbook == null ? 0 : isEbook ? 1 : 2));
        dest.writeByte((byte) (publicDomain == null ? 0 : publicDomain ? 1 : 2));
        dest.writeByte((byte) (isAvailableEpub == null ? 0 : isAvailableEpub ? 1 : 2));
        dest.writeString(downloadLink);
        dest.writeString(fileID);
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

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public Boolean getEbook() {
        return isEbook;
    }

    public void setEbook(Boolean ebook) {
        isEbook = ebook;
    }

    public Boolean getPublicDomain() {
        return publicDomain;
    }

    public void setPublicDomain(Boolean publicDomain) {
        this.publicDomain = publicDomain;
    }

    public Boolean getAvailableEpub() {
        return isAvailableEpub;
    }

    public void setAvailableEpub(Boolean availableEpub) {
        isAvailableEpub = availableEpub;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
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

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", pageCount=" + pageCount +
                ", description='" + description + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", isEbook=" + isEbook +
                ", publicDomain=" + publicDomain +
                ", isAvailableEpub=" + isAvailableEpub +
                ", downloadLink='" + downloadLink + '\'' +
                ", categories=" + categories +
                ", reviews=" + reviews +
                '}';
    }


}
