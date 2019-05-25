package licenta.books.androidmobile.database.DaoMethods;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;

public class UserBookMethods implements UserBookJoinDao {
    private UserBookJoinDao userBookJoinDao;
    private static UserBookMethods userBookMethods;

    public UserBookMethods(UserBookJoinDao userBookJoinDao){
        this.userBookJoinDao = userBookJoinDao;
    }

    public static UserBookMethods getInstance(UserBookJoinDao userBookJoinDao){
        if(userBookMethods == null){
            synchronized (UserBookMethods.class){
                if(userBookMethods == null){
                    userBookMethods = new UserBookMethods(userBookJoinDao);
                }
            }
        }
        return userBookMethods;
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void insertUserBook(final UserBookJoin... userBookJoin) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                userBookJoinDao.insertUserBook(userBookJoin);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe().dispose();



//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                userBookJoinDao.insertUserBook(userBookJoin);
//                return null;
//            }
//        }.execute();

//        userBookJoinDao.insertUserBook(user);

//        Completable.fromAction(new Action() {
//            @Override
//            public void run() throws Exception {
//                userBookJoinDao.insertUserBook(userBookJoin);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
    }

    @Override
    public Flowable<List<BookE>> getAllUserBooksFromDatabase(Integer userId) {
        return userBookJoinDao.getAllUserBooksFromDatabase(userId);
    }

    @Override
    public Single<BookE> getBookFromDatabase(Integer userId, String bookId) {
        return userBookJoinDao.getBookFromDatabase(userId,bookId);
    }

    @Override
    public Single<String> getPathBookFromDatabase(String bookId) {
        return userBookJoinDao.getPathBookFromDatabase(bookId);
    }

    @Override
    public void deleteUserBook(final UserBookJoin... userBookJoin) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                userBookJoinDao.deleteUserBook(userBookJoin);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }


}
