package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
@Entity(
        tableName = "user_book_join",
        primaryKeys = {"userId","bookId"},
        foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId"
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

        public @NonNull  final Integer bookId;
        public @NonNull   final Integer userId;

        public UserBookJoin(@NonNull  final Integer bookId,@NonNull final  Integer userId) {
            this.bookId = bookId;
            this.userId = userId;
        }

}
