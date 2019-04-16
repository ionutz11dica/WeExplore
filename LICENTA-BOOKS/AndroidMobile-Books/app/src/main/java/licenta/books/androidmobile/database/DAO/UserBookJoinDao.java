package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.UserBookJoin;

@Dao
public interface UserBookJoinDao  {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserBook(UserBookJoin... userBookJoin);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM book INNER JOIN user_book_join ON book.bookId = user_book_join.bookId WHERE user_book_join.userId = :userId")
    Flowable<List<BookE>> getAllUserBooksFromDatabase(Integer userId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM book INNER JOIN user_book_join ON book.bookId = user_book_join.bookId where user_book_join.userId = :userId AND user_book_join.bookId = :bookId")
    Single<BookE> getBookFromDatabase(Integer userId, String bookId);

    @Query("SELECT path_file FROM book WHERE  bookId = :bookId")
    Single<String> getPathBookFromDatabase(String bookId);

    @Delete
    void deleteUserBook(UserBookJoin... userBookJoin);

}
