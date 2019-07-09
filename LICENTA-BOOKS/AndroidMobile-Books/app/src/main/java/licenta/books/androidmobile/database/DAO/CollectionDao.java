package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.UserBookJoin;

@Dao
public interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertCollection(Collections collections);

    @Query("SELECT * FROM collection where userId =:userId")
    Single<List<Collections>> getAllUserCollections(Integer userId);

    @Query("UPDATE collection SET collectionId= :id, nameCollection = :name WHERE nameCollection = :nameCol")
    void updateShelf(Integer id, String name, String nameCol);

    @Query("DELETE from collection WHERE nameCollection= :name")
    void deleteCollection(String name);

}
