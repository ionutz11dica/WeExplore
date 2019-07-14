package licenta.books.androidmobile.database.DaoMethods;

import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        Completable.fromAction(() -> userDao.insertUser(user))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("User Insert", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("User Insert", e.getMessage());
                    }
                });
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
