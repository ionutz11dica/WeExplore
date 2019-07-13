package licenta.books.androidmobile.database.DaoMethods;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        Completable.fromAction(() -> bookmarkDao.insertBookmark(bookmark))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Bookmark Insert", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Bookmark Insert", e.getMessage());
                    }
                });
    }

    @Override
    public void deleteBookmark(final Double bookmarkId, final String bookId) {
        Completable.fromAction(() -> bookmarkDao.deleteBookmark(bookmarkId,bookId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Bookmark Delete", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Bookmark Delete", e.getMessage());
                    }
                });
    }

    @Override
    public Single<List<Bookmark>> getAllBookmark(String bookId, Integer userId) {
        return bookmarkDao.getAllBookmark(bookId,userId);
    }
}
