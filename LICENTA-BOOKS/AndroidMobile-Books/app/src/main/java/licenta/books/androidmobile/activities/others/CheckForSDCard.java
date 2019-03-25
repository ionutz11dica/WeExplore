package licenta.books.androidmobile.activities.others;

import android.os.Environment;

public class CheckForSDCard {

    public static boolean isSDCardPresent(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

}
