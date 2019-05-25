package licenta.books.androidmobile.activities.others;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelperSettings {
    private static String storageDirectory;
    private Context context;
    public String fontName;

    public HelperSettings(Context context){
        this.context = context;
    }

    public static String getStorageDirectory() {
        return storageDirectory;
    }

    public static void setStorageDirectory(String directory,String appName) {
        storageDirectory = directory+"/"+appName;
    }

    public void copyFontToDevice(String fileName) {
//        try
//        {
//            String path = getStorageDirectory()+ "/font/"+fileName;
//            File file = new File(path);
//            if (file.exists()) return;
//            InputStream localInputStream = context.getAssets().open("font/"+fileName);
//            FileOutputStream localFileOutputStream = new FileOutputStream(getStorageDirectory()+"\font\reeniebeanie.ttf");
//            byte[] arrayOfByte = new byte[1024];
//            int offset;
//            while ((offset = localInputStream.read(arrayOfByte))>0)
//            {
//                localFileOutputStream.write(arrayOfByte, 0, offset);
//            }
//            localFileOutputStream.close();
//            localInputStream.close();
//            Log.d("font ", fileName+" copied to phone");
//        }
//        catch (IOException localIOException)
//        {
//            localIOException.printStackTrace();
//            Log.d("font ", "failed to copy");
//            return;
//        }
    }
}
