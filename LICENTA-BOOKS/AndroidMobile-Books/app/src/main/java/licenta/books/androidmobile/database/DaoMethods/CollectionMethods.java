package licenta.books.androidmobile.database.DaoMethods;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DialogFragments.CreateShelfDialogFragment;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DAO.EstimatorDao;

public class CollectionMethods implements CollectionDao{

    private CollectionDao collectionDao;
    private static CollectionMethods collectionMethods;
    private Context context;

    private CollectionMethods(CollectionDao collectionDao, Context context){
        this.collectionDao = collectionDao;
        this.context = context;
    }

    public static CollectionMethods getInstance(CollectionDao collectionDao,Context context){
        if(collectionMethods==null){
            synchronized (BookStateMethods.class){
                if(collectionMethods == null){
                    collectionMethods = new CollectionMethods(collectionDao,context);
                }
            }
        }
        return collectionMethods;
    }


    @Override
    public void insertCollection(Collections collections) {
        Completable.fromAction(() -> collectionDao.insertCollection(collections))
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
//                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//                        alertDialog.setTitle("Alert");
//
//                        alertDialog.setMessage("This name is already used");
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                (dialog, which) -> dialog.dismiss());
//                        alertDialog.show();

                        Toast.makeText(context,"This name is already used",Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public Single<List<Collections>> getAllUserCollections(Integer userId) {
        return collectionDao.getAllUserCollections(userId);
    }

    @Override
    public void updateShelf(Integer id,String name, String nameCol) {
        Completable.fromAction(() -> collectionDao.updateShelf(id,name,nameCol))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Collection Update", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void deleteCollection(String name) {
        Completable.fromAction(() -> collectionDao.deleteCollection(name))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Collection Delete", "Successful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Collection Delete", e.getMessage());
                    }
                });
    }
}
