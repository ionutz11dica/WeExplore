package licenta.books.androidmobile.database.DaoMethods;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.Highlight;
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
    public void deleteHighlight(final Double pos,final String text, final String bookid) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                highlightDao.deleteHighlight(pos,text,bookid);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Single<List<Highlight>> getAllHighlights(String bookId, Integer userId) {
        return highlightDao.getAllHighlights(bookId,userId);
    }

    @Override
    public void updateHighlight(final Integer startIndex, final Integer startOffset, final Integer endIndex, final Integer endOffset, final Integer color, final String text,
                                final String note, final Boolean isNote, final Integer style, final String bookId, final Integer chapterIndex,
                                final Integer startI, final Integer startO, final Integer endI, final Integer endO) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                highlightDao.updateHighlight(startIndex,startOffset,endIndex,endOffset,color,text,note,isNote,style,bookId,chapterIndex,startI,startO,endI,endO);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void updateHighlight2(final Highlight highlight) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                highlightDao.updateHighlight2(highlight);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }


}
