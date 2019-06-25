package licenta.books.androidmobile.database.DaoMethods;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.database.DAO.EstimatorDao;

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
}
