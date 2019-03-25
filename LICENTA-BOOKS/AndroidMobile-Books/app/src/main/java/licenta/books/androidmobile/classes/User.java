package licenta.books.androidmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String _id;
    private String email;
    private String username;
    private String password;
    private ArrayList<BookE> books;

    public User() {
        super();
    }


    public User(String _id, String email, String username, String password, ArrayList<BookE> books) {
        this._id = _id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.books = books;
    }

    protected User(Parcel in) {
        _id = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        books = in.createTypedArrayList(BookE.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeTypedList(books);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<BookE> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<BookE> books) {
        this.books = books;
    }
}
