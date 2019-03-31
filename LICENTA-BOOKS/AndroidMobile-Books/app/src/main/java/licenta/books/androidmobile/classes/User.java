package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

@Entity(tableName = "user")
public class User implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer userId;

    @NonNull
    private String email;
    @Nullable
    private String username;
    @Nullable
    private String password;

    @Ignore
    private String _id;
    @Ignore
    private ArrayList<BookE> books;

    @Ignore
    public User() {
        super();
    }


    @Ignore
    public User(Integer userId,@NonNull String email,@Nullable String username,@Nullable String password, ArrayList<BookE> books) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.books = books;
    }

    //DatabaseConstructor

    public User(@NonNull String email, @Nullable String username,@Nullable String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        _id = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        books = in.createTypedArrayList(BookE.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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
