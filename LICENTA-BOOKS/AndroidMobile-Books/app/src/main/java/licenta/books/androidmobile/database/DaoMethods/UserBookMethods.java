package licenta.books.androidmobile.database.DaoMethods;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
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


    @Override
    public void insertUserBook(final UserBookJoin... userBookJoin) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                userBookJoinDao.insertUserBook(userBookJoin);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Flowable<List<BookE>> getAllUserBooksFromDatabase(int userId) {
        return userBookJoinDao.getAllUserBooksFromDatabase(userId);
    }

    @Override
    public Single<BookE> getBookFromDatabase(Integer userId, String bookId) {
        return userBookJoinDao.getBookFromDatabase(userId,bookId);
    }
}
