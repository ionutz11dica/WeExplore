package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import licenta.books.androidmobile.classes.Bookmark;

@Dao
public interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(Bookmark... bookmark);

    @Delete
    void deleteBookmark(Bookmark... bookmark);

    @Query("SELECT * FROM bookmark_join WHERE bookId =:bookId AND userId =:userId")
    Flowable<List<Bookmark>> getAllBookmark(String bookId,Integer userId);
}
