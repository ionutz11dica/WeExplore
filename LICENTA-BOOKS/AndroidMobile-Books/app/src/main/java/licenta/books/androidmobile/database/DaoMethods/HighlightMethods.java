package licenta.books.androidmobile.database.DaoMethods;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;

public class HighlightMethods implements HighlightDao {
    private HighlightDao highlightDao;
    private static HighlightMethods highlightMethods;

    private HighlightMethods(HighlightDao highlightDao){
        this.highlightDao = highlightDao;
    }

    public static HighlightMethods getInstance(HighlightDao highlightDao){
        if(highlightMethods == null){
            synchronized (UserMethods.class){
                if(highlightMethods==null){
                    highlightMethods = new HighlightMethods(highlightDao);
                }
            }
        }
        return highlightMethods;
    }

    @Override
    public void insertHighlight(final Highlight... highlights) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                highlightDao.insertHighlight(highlights);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void deleteHighlight(final Highlight... highlights) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                highlightDao.deleteHighlight(highlights);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Single<List<Highlight>> getAllHighlights(String bookId, Integer userId) {
        return highlightDao.getAllHighlights(bookId,userId);
    }
}
