package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import io.reactivex.Single;
import licenta.books.androidmobile.classes.BookState;

@Dao
public interface BookStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookState(BookState... bookState);

    @Query("SELECT * FROM bookstate WHERE bookId = :bookId")
    Single<BookState> getBookStateFromDatabase(String bookId);

}
