package licenta.books.androidmobile.database.DaoMethods;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.UserDao;

public class BookMethods implements BookEDao {
    private BookEDao bookEDao;
    private static BookMethods bookMethods;

    public BookMethods(BookEDao bookEDao){
        this.bookEDao = bookEDao;
    }

    public static BookMethods getInstance(BookEDao bookEDao){
        if(bookMethods == null){
            synchronized (UserMethods.class){
                if(bookMethods==null){
                    bookMethods = new BookMethods(bookEDao);
                }
            }
        }
        return bookMethods;
    }

    @Override
    public void insertBook(final BookE... book) {
            Completable.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    bookEDao.insertBook(book);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
    }

}
