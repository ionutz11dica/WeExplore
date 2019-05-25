package licenta.books.androidmobile.database.DaoMethods;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserDao;

public class UserMethods implements UserDao {
    private  UserDao userDao;
    private static UserMethods userMethods;

    private UserMethods(UserDao userDao) {
        this.userDao = userDao;
    }


    public static UserMethods getInstance(UserDao userDao){
        if(userMethods == null){
            synchronized (UserMethods.class){
                if(userMethods==null){
                    userMethods = new UserMethods(userDao);
                }
            }
        }
        return userMethods;
    }

    @Override
    public void insertUser(final User... user) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(user);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe().dispose();
    }

    @Override
    public Single<User> verifyAvailableAccount(String usernameReq, String passwordReq) {
        return userDao.verifyAvailableAccount(usernameReq,passwordReq);
    }

    @Override
    public Single<User>  verifyExistenceGoogleAcount(String emailReq) {
        return userDao.verifyExistenceGoogleAcount(emailReq);
    }


}
