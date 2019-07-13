package licenta.books.androidmobile.classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName = "userBook",
        primaryKeys = {"userId","bookId"},
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
public class UserBookJoin {
//        @ColumnInfo(name = "bookId")
    public @NonNull  final String bookId;
    //        @ColumnInfo(name = "userId")
    public @NonNull   final Integer userId;

    public UserBookJoin(@NonNull  final String bookId,@NonNull final  Integer userId) {
        this.bookId = bookId;
        this.userId = userId;
    }



}
