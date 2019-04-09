package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.UserBookJoin;

@Dao
public interface UserBookJoinDao  {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserBook(UserBookJoin... userBookJoin);

    @Query("SELECT * FROM book INNER JOIN user_book_join ON book.bookId = user_book_join.bookId WHERE user_book_join.userId = :userId")
    Flowable<List<BookE>> getAllUserBooksFromDatabase(final int userId);

    @Query("SELECT bookId FROM user_book_join where userId = :userId AND bookId = :bookId")
    Single<BookE> getBookFromDatabase(Integer userId, String bookId);
}
