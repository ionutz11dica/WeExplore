package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import licenta.books.androidmobile.classes.BookE;

@Dao
public interface BookEDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBook(BookE... book);



}
