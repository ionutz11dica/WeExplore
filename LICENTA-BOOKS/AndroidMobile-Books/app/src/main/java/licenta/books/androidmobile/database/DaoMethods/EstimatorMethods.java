package licenta.books.androidmobile.database.DaoMethods;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.database.DAO.EstimatorDao;
import licenta.books.androidmobile.patterns.readingEstimator.AverageIndicators;
import licenta.books.androidmobile.patterns.readingEstimator.ChapterIndexes;

public class EstimatorMethods implements EstimatorDao {
    private EstimatorDao estimatorDao;
    private static EstimatorMethods estimatorMethods;

    private EstimatorMethods(EstimatorDao estimatorDao){
        this.estimatorDao = estimatorDao;
    }

    public static EstimatorMethods getInstance(EstimatorDao estimatorDao){
        if(estimatorMethods==null){
            synchronized (BookStateMethods.class){
                if(estimatorMethods == null){
                    estimatorMethods = new EstimatorMethods(estimatorDao);
                }
            }
        }
        return estimatorMethods;
    }

    @Override
    public void insertEstimation(Estimator estimator) {
        Completable.fromRunnable(() -> estimatorDao.insertEstimation(estimator)).subscribeOn(Schedulers.io())
                .subscribe().dispose();
    }

    @Override
    public Single<AverageIndicators> fetchAverageIndicators( String bookId, Integer userId) {
        return estimatorDao.fetchAverageIndicators(bookId,userId);
    }

    @Override
    public Single<ChapterIndexes> fetchWatchedChapterIndexes(String bookId, Integer userId) {
        return estimatorDao.fetchWatchedChapterIndexes(bookId,userId);
    }

    @Override
    public void updateIndicators(int totalWords, int totalPages,double GFS, String chapterList, String bookId, Integer userId) {
        Completable.fromRunnable(() -> estimatorDao.updateIndicators(totalWords,totalPages, GFS,chapterList,bookId,userId)).subscribeOn(Schedulers.io())
                .subscribe().dispose();
    }
}
