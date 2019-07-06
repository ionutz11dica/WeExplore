package licenta.books.androidmobile.database.DaoMethods;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.CollectionBookJoin;
import licenta.books.androidmobile.classes.CollectionPOJO;
import licenta.books.androidmobile.database.DAO.BookCollectionJoinDao;

public class BookCollectionJoinMethods implements BookCollectionJoinDao {

    private BookCollectionJoinDao bookCollectionJoinDao;
    private static BookCollectionJoinMethods bookCollectionJoinMethods;
    private Context context;

    private BookCollectionJoinMethods(BookCollectionJoinDao bookCollectionJoinDao){
        this.bookCollectionJoinDao = bookCollectionJoinDao;

    }

    public static BookCollectionJoinMethods getInstance(BookCollectionJoinDao bookCollectionJoinDao){
        if(bookCollectionJoinMethods==null){
            synchronized (BookStateMethods.class){
                if(bookCollectionJoinMethods == null){
                    bookCollectionJoinMethods = new BookCollectionJoinMethods(bookCollectionJoinDao);
                }
            }
        }
        return bookCollectionJoinMethods;
    }

    @Override
    public void insertBookCollection(CollectionBookJoin collectionBookJoin) {
        Completable.fromAction(() -> bookCollectionJoinDao.insertBookCollection(collectionBookJoin))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Collection", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Alert");

                        alertDialog.setMessage("This name is already used");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                    }
                });
    }

    @Override
    public Single<List<CollectionPOJO>> fetchUserCollection(Integer userId) {
        return bookCollectionJoinDao.fetchUserCollection(userId);
    }

    @Override
    public Single<List<BookE>> fetchCollectionBooks(Integer userId,String name) {
        return bookCollectionJoinDao.fetchCollectionBooks(userId,name);
    }
}
