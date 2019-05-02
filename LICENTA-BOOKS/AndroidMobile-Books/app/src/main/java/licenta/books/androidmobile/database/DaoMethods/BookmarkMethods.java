package licenta.books.androidmobile.database.DaoMethods;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.database.DAO.BookmarkDao;

public class BookmarkMethods implements BookmarkDao {
    private BookmarkDao bookmarkDao;
    private static BookmarkMethods bookmarkMethods;

    private BookmarkMethods(BookmarkDao bookmarkDao){
        this.bookmarkDao = bookmarkDao;
    }

    public static BookmarkMethods getInstance(BookmarkDao bookmarkDao){
        if(bookmarkMethods == null){
            synchronized (BookmarkMethods.class){
                if(bookmarkMethods == null){
                    bookmarkMethods = new BookmarkMethods(bookmarkDao);
                }
            }
        }
        return bookmarkMethods;
    }

    @Override
    public void insertBookmark(final Bookmark... bookmark) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                bookmarkDao.insertBookmark(bookmark);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void deleteBookmark(final Bookmark... bookmark) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                bookmarkDao.deleteBookmark(bookmark);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Flowable<List<Bookmark>> getAllBookmark(String bookId, Integer userId) {
        return bookmarkDao.getAllBookmark(bookId,userId);
    }
}
