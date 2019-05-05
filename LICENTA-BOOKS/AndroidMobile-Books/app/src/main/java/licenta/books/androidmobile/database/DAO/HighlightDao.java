package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Highlight;

@Dao
public interface HighlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHighlight(Highlight... highlights);

    @Delete
    void deleteHighlight(Highlight... highlights);

    @Query("SELECT * FROM highlight_join WHERE bookId =:bookId AND userId = :userId ORDER BY chapterIndex")
    Single<List<Highlight>> getAllHighlights(String bookId,Integer userId);

}
