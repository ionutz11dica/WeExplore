package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Collections;

@Dao
public interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertCollection(Collections collections);

    @Query("SELECT * FROM collection where userId =:userId")
    Single<List<Collections>> getAllUserCollections(Integer userId);
}
