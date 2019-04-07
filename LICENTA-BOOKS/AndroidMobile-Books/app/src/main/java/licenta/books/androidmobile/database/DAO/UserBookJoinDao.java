package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.UserBookJoin;

@Dao
public interface UserBookJoinDao  {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserBook(UserBookJoin... userBookJoin);


}
