package licenta.books.androidmobile.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.commonsware.cwac.saferoom.SQLCipherUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import licenta.books.androidmobile.database.DAO.BookCollectionJoinDao;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DAO.EstimatorDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.interfaces.Constants;

import static licenta.books.androidmobile.interfaces.Constants.DATABASE_NAME;

@Database(entities = {User.class, BookE.class, Review.class, Highlight.class, Bookmark.class,
        BookState.class, UserBookJoin.class, Collections.class, CollectionBookJoin.class, Estimator.class},version = 49,exportSchema = false)
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
    public abstract BookCollectionJoinDao getBookCollectionJoinDao();


    private static volatile AppRoomDatabase appRoomDatabase=null;

    public static AppRoomDatabase getInstance(final Context context){
        if(appRoomDatabase == null){
            synchronized (AppRoomDatabase.class){
                if(appRoomDatabase == null){
                    appRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, DATABASE_NAME)
//                            .allowMainThreadQueries()
//                            .setJournalMode(JournalMode.TRUNCATE)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return appRoomDatabase;
    }

    public void backup(String outFilename,Context context) {
        final String inFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try{
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream outputStream = new FileOutputStream(outFilename);

            byte[] buffer = new byte[1024];
            int length;
            while ((length= fis.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }

            outputStream.flush();
            outputStream.close();
            fis.close();

            Toast.makeText(context, "Backup Completed", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDB(String inFileName,Context context) {

        final String outFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {
            context.deleteDatabase(outFileName);
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
