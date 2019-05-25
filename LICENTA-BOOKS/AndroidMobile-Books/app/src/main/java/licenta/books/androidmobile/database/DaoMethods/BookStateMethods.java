package licenta.books.androidmobile.database.DaoMethods;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.database.DAO.BookStateDao;

public class BookStateMethods implements BookStateDao {
    private BookStateDao bookStateDao;
    private static BookStateMethods bookStateMethods;

    private BookStateMethods(BookStateDao bookStateDao){
        this.bookStateDao = bookStateDao;
    }

    public static BookStateMethods getInstance(BookStateDao bookStateDao){
        if(bookStateMethods==null){
            synchronized (BookStateMethods.class){
                if(bookStateMethods == null){
                    bookStateMethods = new BookStateMethods(bookStateDao);
                }
            }
        }
        return bookStateMethods;
    }

    @Override
    public void insertBookState(final BookState... bookState) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                bookStateDao.insertBookState(bookState);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe().dispose();
    }

    @Override
    public Single<BookState> getBookStateFromDatabase(String bookId) {
        return bookStateDao.getBookStateFromDatabase(bookId);
    }
}
