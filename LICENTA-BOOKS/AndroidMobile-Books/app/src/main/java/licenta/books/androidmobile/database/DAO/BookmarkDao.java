package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Bookmark;

@Dao
public interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(Bookmark... bookmark);

    @Query("DELETE from bookmark_join WHERE pagePosition=:pagePos")
    void deleteBookmark(Double pagePos);

    @Query("SELECT * FROM bookmark_join WHERE bookId =:bookId AND userId =:userId")
    Single<List<Bookmark>> getAllBookmark(String bookId, Integer userId);
}
