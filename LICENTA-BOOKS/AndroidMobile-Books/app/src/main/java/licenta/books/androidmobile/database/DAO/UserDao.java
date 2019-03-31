package licenta.books.androidmobile.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import licenta.books.androidmobile.classes.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User... user);

    @Query("SELECT * from user where username = :usernameReq and password = :passwordReq")
    User verifyAvailableAccount(String usernameReq,String passwordReq);

    @Query("SELECT * from user where email = :emailReq")
    User verifyExistenceGoogleAcount(String emailReq);

}
