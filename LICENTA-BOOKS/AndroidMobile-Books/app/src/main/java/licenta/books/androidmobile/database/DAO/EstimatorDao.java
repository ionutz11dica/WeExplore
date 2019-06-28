package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.patterns.readingEstimator.AverageIndicators;
import licenta.books.androidmobile.patterns.readingEstimator.ChapterIndexes;

@Dao
public interface EstimatorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEstimation(Estimator estimator);

    @Query("SELECT AVG(gunningFogScore) as avgGFS, AVG(timePerPage) as avgTime, SUM(totalOfWatchedWords) as sumWords, SUM(totalOfWatchedPages) as sumPages FROM estimator WHERE bookId = :bookId AND userId = :userId")
    Single<AverageIndicators> fetchAverageIndicators(String bookId, Integer userId);

    @Query("SELECT chapterIndexes as chapterList FROM estimator WHERE bookId = :bookId AND userId = :userId")
    Single<ChapterIndexes> fetchWatchedChapterIndexes(String bookId, Integer userId);

    @Query("UPDATE estimator SET totalOfWatchedWords = :totalWords , totalOfWatchedPages = :totalPages, gunningFogScore = :GFS, chapterIndexes = :chapterList  WHERE bookId = :bookId AND userId = :userId")
    void updateIndicators(int totalWords, int totalPages, double GFS, String chapterList, String bookId, Integer userId);

}
