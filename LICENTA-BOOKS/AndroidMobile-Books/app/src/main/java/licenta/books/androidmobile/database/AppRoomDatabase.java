package licenta.books.androidmobile.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.classes.Review;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;

@Database(entities = {User.class, BookE.class, Review.class, Highlight.class, Bookmark.class,
        BookState.class, UserBookJoin.class},version = 23,exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {
    //database object
    public abstract BookEDao getBookEDao();
    public abstract UserDao getUserDao();
    public abstract UserBookJoinDao getUserBookDao();
    public abstract BookStateDao getBookStateDao();
    public abstract BookmarkDao getBookmarkDao();
    public abstract HighlightDao getHighlightDao();




    private static volatile AppRoomDatabase appRoomDatabase;

    public static AppRoomDatabase getInstance(final Context context){
        if(appRoomDatabase == null){
            synchronized (AppRoomDatabase.class){
                if(appRoomDatabase == null){
                    appRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class,"AppDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return appRoomDatabase;
    }

}
