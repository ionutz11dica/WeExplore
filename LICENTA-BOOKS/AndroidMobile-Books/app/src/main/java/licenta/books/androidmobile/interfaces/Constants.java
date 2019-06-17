package licenta.books.androidmobile.interfaces;
public interface Constants {

      String DATABASE_NAME = "AppDatabase.db";

      int RC_SIGN_IN = 9001;
      int WRITE_REQUEST_CODE = 300;

      String BASE_URL = "http://192.168.1.8:4000/";

      float BITMAP_SCALE = 0.4f;
      float BLUR_RADIUS = 24f;

      String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";


      String KEY_IMAGE_URL = "keyImgUrl";

      String KEY_PREF_USER = "prefUser";
      String KEY_USER_EMAIL = "keyUserEmail";
      String KEY_USER_PASSWORD = "keyUserPassword";
      String KEY_USER_USERNAME = "keyUserUsername";

      String KEY_USER_ID= "keyUserId";

      String KEY_BOOK = "keyBook";
      String KEY_USER = "keyUser";

      String KEY_USER_BOOK = "keyUserBook";

      String KEY_STATUS = "keyStatus";


      int RESULT_CODE_CHAPTER = 101;
      int RESULT_CODE_BOOKMARK = 102;
      String KEY_CHAPTER = "keyChapter";
      String KEY_NOTE_CONTENT = "keyNoteContent";
      int REQUEST_CODE_NOTE = 103;
      String KEY_HIGHLIGHT_EXISTS = "keyHighlightExists";


      String[] TYPEFACE_NAMES =  {"BradleyHand","Cantarell","CrimsonText","Inconsolata","JosefinSans", "Molengo","Simplicity","ReenieBeanie"};
      String KEY_CURRENT_COLOR = "keyCurrentColor";
      String KEY_STATUS_COLOR = "keyStatusColor";
}
