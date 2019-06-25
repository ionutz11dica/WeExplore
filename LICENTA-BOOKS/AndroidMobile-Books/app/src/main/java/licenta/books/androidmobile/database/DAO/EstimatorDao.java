package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import licenta.books.androidmobile.classes.Estimator;

@Dao
public interface EstimatorDao {

    @Insert
    void insertEstimation(Estimator estimator);
}
