package licenta.books.androidmobile.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.commonsware.cwac.saferoom.SQLCipherUtils;

import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.CollectionBookJoin;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.classes.Review;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DAO.EstimatorDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.interfaces.Constants;

@Database(entities = {User.class, BookE.class, Review.class, Highlight.class, Bookmark.class,
        BookState.class, UserBookJoin.class, Collections.class, CollectionBookJoin.class, Estimator.class},version = 46,exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {
    //database object
    public abstract BookEDao getBookEDao();
    public abstract UserDao getUserDao();
    public abstract UserBookJoinDao getUserBookDao();
    public abstract BookStateDao getBookStateDao();
    public abstract BookmarkDao getBookmarkDao();
    public abstract HighlightDao getHighlightDao();
    public abstract EstimatorDao getEstimatorDao();
    public abstract CollectionDao getCollectionDao();




    private static volatile AppRoomDatabase appRoomDatabase=null;

    public static AppRoomDatabase getInstance(final Context context){
        if(appRoomDatabase == null){
            synchronized (AppRoomDatabase.class){
                if(appRoomDatabase == null){
                    appRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, Constants.DATABASE_NAME)
//                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return appRoomDatabase;
    }


}
