package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Highlight;

@Dao
public interface HighlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHighlight(Highlight... highlights);

    @Query("DELETE from highlight_join WHERE pagePosInBoo=:pagePos and selectedText =:text and bookId=:bookId")
    void deleteHighlight(Double pagePos,String text,String bookId);

    @Query("SELECT * FROM highlight_join WHERE bookId =:bookId AND userId = :userId ORDER BY chapterIndex")
    Single<List<Highlight>> getAllHighlights(String bookId,Integer userId);

    @Query("UPDATE highlight_join SET startIndex =:startIndex, startOffset = :startOffset, endIndex =:endIndex, endOffset =:endOffset,color= :color," +
            "selectedText = :text, noteContent = :note, isNote =:isNote,style= :style WHERE bookId =:bookId and chapterIndex =:chapterIndex and startIndex =:startI and startOffset =:startO " +
            "and endIndex =:endI and endOffset =:endO")
    void updateHighlight(Integer startIndex, Integer startOffset,Integer endIndex,Integer endOffset,Integer color, String text, String note,Boolean isNote,Integer style,
                         String bookId,Integer chapterIndex,Integer startI, Integer startO, Integer endI,Integer endO);

    @Update
    void updateHighlight2(Highlight highlight);

}
