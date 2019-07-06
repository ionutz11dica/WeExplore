package licenta.books.androidmobile.database.DAO;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.CollectionBookJoin;
import licenta.books.androidmobile.classes.CollectionPOJO;

@Dao
public interface BookCollectionJoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookCollection(CollectionBookJoin collectionBookJoin);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT b.nameCollection as collectionName, count(a.bookId) as bookIds" +
            " FROM collection b" +
            " LEFT JOIN collectionBook a ON b.collectionId = a.collectionId and b.userId= :userId group by b.nameCollection")
    Single<List<CollectionPOJO>> fetchUserCollection(Integer userId);
    
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM book a INNER JOIN collectionBook b  ON a.bookId = b.bookId INNER JOIN collection c ON " +
            "c.collectionId = b.collectionId and c.nameCollection = :name and c.userId = :userId")
    Single<List<BookE>> fetchCollectionBooks(Integer userId, String name);

}
