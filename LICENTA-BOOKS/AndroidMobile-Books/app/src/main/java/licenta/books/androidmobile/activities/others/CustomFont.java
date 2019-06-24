package licenta.books.androidmobile.activities.others;

import android.util.Log;

public class CustomFont {
    public String fontFaceName;
    public String fontFileName;

    public CustomFont(String faceName, String fileName) {
        this.fontFaceName = faceName;
        this.fontFileName = fileName;
    }

    public String getFullName() {
        String fullName = "";
        if (fontFileName==null || fontFileName.isEmpty()) {
            fullName = this.fontFaceName;
        }else {
            fullName = this.fontFaceName+"!!!/fonts/"+this.fontFileName;
        }
        Log.d("font",fullName);
        return fullName;
    }
}
