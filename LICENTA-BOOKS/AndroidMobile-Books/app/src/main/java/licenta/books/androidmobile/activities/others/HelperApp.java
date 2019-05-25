package licenta.books.androidmobile.activities.others;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.ArrayList;


public class HelperApp extends Application {
    public ArrayList<CustomFont> customFonts = new ArrayList<>();
    public HelperSettings setting;

    @Override
    public void onCreate() {
        super.onCreate();
        String appName = this.getApplicationName();
        if (HelperSettings.getStorageDirectory()==null) {
//			 All book related data will be stored /data/data/com....../files/appName/
            HelperSettings.setStorageDirectory(getFilesDir().getAbsolutePath(),appName);
            // All book related data will be stored /sdcard/appName/...
        }
    }

    public String getApplicationName() {
        int stringId = this.getApplicationInfo().labelRes;
        return this.getString(stringId);
    }
}
