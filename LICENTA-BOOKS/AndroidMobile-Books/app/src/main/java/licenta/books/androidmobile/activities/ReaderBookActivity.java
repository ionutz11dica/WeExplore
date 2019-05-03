package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skytree.epub.BookmarkListener;
import com.skytree.epub.Highlight;
import com.skytree.epub.NavPoints;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.State;
import com.skytree.epub.StateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.Chapter;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.database.DaoMethods.BookmarkMethods;
import licenta.books.androidmobile.interfaces.Constants;


public class ReaderBookActivity extends AppCompatActivity {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;

    LinearLayout toolbarTop;
    LinearLayout toolbarBottom;

    RelativeLayout selectionButtonsPopup;

    Toolbar topToolbar;
    Toolbar bottomToolbar;

    //database var
    BookStateDao bookStateDao;
    BookStateMethods bookStateMethods;
    BookmarkDao bookmarkDao;
    BookmarkMethods bookmarkMethods;

    boolean show = true;
    BookE book;
    User user;
    Menu menuItems;

    private Rect bookmarkRect, bookmarkedRect;

    //book information
    static double pagePosition;
    static Integer fontSize;
    static String fontType;
    PageTransition pageTransition;
    BookState disposableBookstate;
    Bookmark disposableBookmark;


    ProgressDialog progressDialog;

    //Handlers
    SelectionHandler selectionHandler;
    StateHandler stateHandler;
    PageMoveHandler pageMoveHandler;
    BookmarkHandler bookmarkHandler;

    //toolbar controllers
    TextView tvPage;
    SeekBar seekBarPager;
    ImageButton imgBtnContent;
    ImageButton imgBtnDayNightMode;

    //lists
    List<Bookmark> bookmarksList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readerbook);
        openDb();
        initComp();
        initPathBook();

    }

    private void initComp(){
        progressDialog = new ProgressDialog(this);

        topToolbar=findViewById(R.id.reader_toolbar_top);
        bottomToolbar=findViewById(R.id.reader_toolbar_bottom);
        topToolbar.setVisibility(View.GONE);
        bottomToolbar.setVisibility(View.GONE);
        selectionButtonsPopup = findViewById(R.id.selectionButtonsPopup);


        //bottom toolbar components
        imgBtnContent = findViewById(R.id.image_button_content);

        renderRelative = findViewById(R.id.readerRelativeLayout);

        eventClickContent();
    }

    private void eventClickContent() {
        imgBtnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getApplicationContext(), AnnotationBookActivity.class), Constants.RESULT_CODE_CHAPTER);
            }
        });
    }

    private void initPathBook(){
        Disposable d = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                book = bookE;
            }
        });
        d.dispose();

        Disposable disp = RxBus.subscribeUser(new Consumer<User>() {
            @Override
            public void accept(User userr) throws Exception {
                user = userr;
                loadBookMarkFromDb(userr);
            }
        });
        disp.dispose();

        topToolbar.setTitle(book.getTitle());
        topToolbar.setSubtitle(convertFromArray(book.getAuthors()));
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initBookVariableFromDataBase();

        String fileName = book.getTitle()+".epub";
        renderRelative.addView(setUpReflowableController(fileName,"/sdcard/Android/data/licenta.books.androidmobile/files"));

    }

    private ReflowableControl setUpReflowableController(String fileName, String baseDirectoryPath) {
        showLog("baseDirectoryPath",baseDirectoryPath);

        reflowableControl = new ReflowableControl(this);

        showLog("fileName", String.valueOf(fileName));

        reflowableControl.setBookName(fileName);

        reflowableControl.setBaseDirectory(baseDirectoryPath);

        reflowableControl.setLicenseKey("04FE-082A-0B15-0CFB");

        reflowableControl.setDoublePagedForLandscape(true);

        reflowableControl.setLineSpacing(135); // the value is supposed to be percent(%).

        reflowableControl.setHorizontalGapRatio(0.25);

        reflowableControl.setVerticalGapRatio(0.1);

        reflowableControl.setPageTransition(PageTransition.Curl);


        reflowableControl.setFingerTractionForSlide(true);



        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        reflowableControl.setLayoutParams(params);

        SkyProvider skyProvider = new SkyProvider();

        reflowableControl.setContentProvider(skyProvider);

        reflowableControl.useDOMForHighlight(true);

        reflowableControl.setNavigationAreaWidthRatio(0.0f); // both left and right side.

        //To get text of page in pageMovedListener in pageDescription
        reflowableControl.setExtractText(true);

//        reflowableControl.setMediaOverlayListener(new MediaOverlayHandler()); //For Audio Book

        showLog("Media Overlay Available : ", String.valueOf(reflowableControl.isMediaOverlayAvailable()));

        //Listeners
        selectionHandler = new SelectionHandler();
        reflowableControl.setSelectionListener(selectionHandler);
//
        pageMoveHandler = new PageMoveHandler();
        reflowableControl.setPageMovedListener(pageMoveHandler);
//
//        highlightHandler = new HighlightHandler();
//        reflowableControl.setHighlightListener(highlightHandler);
//
        stateHandler = new StateHandler();
        reflowableControl.setStateListener(stateHandler);
//
//        bookmarkHandler = new BookmarkHandler();
//        reflowableControl.setBookmarkListener(bookmarkHandler);

        return reflowableControl;
    }

    @SuppressLint("CheckResult")
    private void initBookVariableFromDataBase(){

        Single<BookState> stateSingle = bookStateMethods.getBookStateFromDatabase(book.get_id());

        stateSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<BookState>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public  void onSuccess(BookState bookState) {
                        initReflowableControlDisplay(bookState.getFontType(),bookState.getFontSize(),bookState.getPagePosition(),bookState.getPageTransition());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Date date = Calendar.getInstance().getTime();
                        pagePosition = 0;
                        fontSize = 15;
                        fontType = "TimesRoman";
                        pageTransition = PageTransition.Curl;
                        BookState bookState = new BookState(pagePosition,0,date,fontType,fontSize,Color.BLACK,Color.WHITE,PageTransition.Curl,book.get_id());
                        showLog("haide",bookState.toString());
                        insertBookState(bookState);
                    }
                });




    }

    @SuppressLint("CheckResult")
    private void loadBookMarkFromDb(User user) {
        Single<List<Bookmark>> bookMarks = bookmarkMethods.getAllBookmark(book.get_id(),user.getUserId());

        bookMarks.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Bookmark>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Bookmark> arrayList) {
                        loadBookmarkList(arrayList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showLog("Eroare bookmark ", e.getMessage());
                    }
                });
        int y =0;
    }

    private class SelectionHandler implements SelectionListener {

        @Override
        public void selectionStarted(Highlight highlight, Rect rect, Rect rect1) {
            hideSelectionButtonsPopup();
        }

        @Override
        public void selectionChanged(Highlight highlight, Rect rect, Rect rect1) {
            hideSelectionButtonsPopup();
        }

        @Override
        public void selectionEnded(Highlight highlight, Rect startRect, Rect endRect) {
            String selectedText = highlight.text;

            if(startRect.top < (reflowableControl.getHeight()/2) && endRect.bottom < (reflowableControl.getHeight()/2)){
                if((reflowableControl.getWidth() - startRect.left) > selectionButtonsPopup.getWidth()){
                    moveSelectionButtonPopupToBellow(endRect.bottom + 30, startRect.left);
                }else{
                    moveSelectionButtonPopupToBellow(endRect.bottom + 30, reflowableControl.getWidth() - selectionButtonsPopup.getWidth());
                }
            }else if(startRect.top > (reflowableControl.getHeight()/2)&& endRect.bottom > (selectionButtonsPopup.getHeight()/2)){
                if ((reflowableControl.getWidth() - startRect.left) > selectionButtonsPopup.getWidth()) {
                    moveSelectionButtonPopupToUpper(startRect.top - selectionButtonsPopup.getHeight() - 30, startRect.left);
                } else {
                    moveSelectionButtonPopupToUpper(startRect.top - selectionButtonsPopup.getHeight() - 30, reflowableControl.getWidth() - selectionButtonsPopup.getWidth());
                }
            } else {
                //This vertical center is center of selection not reflowable contrller layout center
                int verticalCenter = startRect.top + (int) ((endRect.bottom - startRect.top) / 2);
                //Now we have to check whether left of starRect is > than multiple button width
                if ((reflowableControl.getWidth() - startRect.left) > selectionButtonsPopup.getWidth()) {
                    moveSelectionButtonPopupToMiddle(verticalCenter, startRect.left);
                } else {
                    moveSelectionButtonPopupToMiddle(verticalCenter, reflowableControl.getWidth() - selectionButtonsPopup.getWidth());
                }
            }
        }

        @Override
        public void selectionCancelled() {
            hideSelectionButtonsPopup();
//            showOrHideToolbar();
        }
    }

    private class StateHandler implements StateListener {

        @Override
        public void onStateChanged(State state) {
            if(state.equals(State.NORMAL)){
                showLog("State =>", "NORMAL");
                hideProgressDialog();
            } else if(state.equals(State.LOADING)) {
                showLog("State =>", "LOADING");
                showProgressDialog("Loading","Please wait..",false);
            }else if (state.equals(State.ROTATING)){
                showLog("State =>", "ROTATING");
                showProgressDialog("Loading","Please wait..",false);
            }else if (state.equals(State.BUSY)){
                showLog("State =>", "BUSY");
                showProgressDialog("Loading","Please wait..",false);
            }
        }
    }

    private class PageMoveHandler implements PageMovedListener {

        @Override
        public void onPageMoved(PageInformation pageInformation) {
            Integer bookmarkCode = getBookmarkCode(pageInformation.chapterIndex,pageInformation.pageIndex);
            Double pagePos = pageInformation.pagePositionInBook;
            Integer chapterIndex = pageInformation.chapterIndex;
            Integer pageIndex = pageInformation.pageIndex;
            String bookmarkPageInfo = "Chapter : " + pageInformation.chapterIndex + " Page In Chapter : " + pageInformation.pageIndex;
            String bookId = book.get_id();
            Integer userId = user.getUserId();
            Bookmark bookmark = new Bookmark(bookmarkCode,pagePos,chapterIndex,pageIndex,bookmarkPageInfo,bookId,userId);
            boolean isBookmark = isBookmarkExists(bookmark);
            if(isBookmark){
                menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_black_24dp));
            }else{
                menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_border_black_24dp));
            }
            show=true;
            showOrHideToolbar();
            pagePosition = pageInformation.pagePositionInBook;
            RxBus.publishBookState(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,book.get_id()));
            RxBus.publishBookMark(bookmark);
        }

        @Override
        public void onChapterLoaded(int i) {
            NavPoints tocNavPoints = reflowableControl.getNavPoints();
            ArrayList<Chapter> chapterList = new ArrayList<>();

            for(int index = 0 ;index < tocNavPoints.getSize();index++){
                chapterList.add(new Chapter(tocNavPoints.getNavPoint(index).text,tocNavPoints.getNavPoint(index).chapterIndex));
            }

            RxBus.publishsChapterList(chapterList);
            if(reflowableControl.getChapterTitle(i) !=null){
                RxBus.publishsChapterName(reflowableControl.getChapterTitle(i));
                RxBus.publishsChapter(reflowableControl.getChapterIndex());
            }
        }

        @Override
        public void onFailedToMove(boolean b) {

        }
    }

        private class BookmarkHandler implements BookmarkListener{

        @Override
        public void onBookmarkHit(PageInformation pageInformation, boolean b) {

            Integer bookmarkCode = getBookmarkCode(pageInformation.chapterIndex,pageInformation.pageIndex);
            Double pagePosition = pageInformation.pagePositionInBook;
            Integer chapterIndex = pageInformation.chapterIndex;
            Integer pageIndex = pageInformation.pageIndex;
            String bookmarkPageInfo = "Chapter : " + pageInformation.chapterIndex + " Page In Chapter : " + pageInformation.pageIndex;
            String bookId = book.get_id();
            Integer userId = user.getUserId();
            Bookmark bookmark = new Bookmark(bookmarkCode,pagePosition,chapterIndex,pageIndex,bookmarkPageInfo,bookId,userId);

            verifyBookMark(bookmark);
        }

        @Override
        public Rect getBookmarkRect(boolean b) {
            if (b) return bookmarkedRect;
            return bookmarkRect;
        }

        @Override
        public Bitmap getBookmarkBitmap(boolean b) {
            if(b){
                return getBookmarkedIcon();
            }else {
                return getNonBookmarkIcon();
            }
        }

        @Override
        public boolean isBookmarked(PageInformation pageInformation) {
            Integer bookmarkCode = getBookmarkCode(pageInformation.chapterIndex,pageInformation.pageIndex);
            Double pagePosition = pageInformation.pagePositionInBook;
            Integer chapterIndex = pageInformation.chapterIndex;
            Integer pageIndex = pageInformation.pageIndex;
            String bookmarkPageInfo = "Chapter : " + pageInformation.chapterIndex + " Page In Chapter : " + pageInformation.pageIndex;
            String bookId = book.get_id();
            Integer userId = user.getUserId();
            Bookmark bookmark = new Bookmark(bookmarkCode,pagePosition,chapterIndex,pageIndex,bookmarkPageInfo,bookId,userId);
            return isBookmarkExists(bookmark);
        }
    }

    private Bitmap getNonBookmarkIcon() {
        Bitmap bitmap;


        Drawable markIcon = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_bookmark_border_black_24dp,null);

//        assert markIcon != null;
        return Bitmap.createBitmap(markIcon.getIntrinsicWidth(), markIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }

    private Bitmap getBookmarkedIcon(){
        Drawable markIcon = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_bookmark_black_24dp,null);
//        assert markIcon != null;
        return ((BitmapDrawable)markIcon).getBitmap();
    }

    private Integer getBookmarkCode(Integer chapterIndex,Integer pageIndex){
        return chapterIndex * 1000 + pageIndex;
    }

    private void loadBookmarkList(List<Bookmark> arrayList){
        bookmarksList = arrayList;
    }

    private boolean isBookmarkExists(Bookmark bookmark) {
        if(bookmarksList !=null){
            for(Bookmark bookmarkTemp : bookmarksList){
                if(bookmarkTemp.getBookmarkCode().equals(bookmark.getBookmarkCode())){
//                    menuItems.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_black_24dp));
//                    verifyBookMark(bookmark);
                    return true;
                }
            }
        }
     return false;
    }

    private void verifyBookMark(Bookmark bookmark){
        int bookmarkIndex = -1;
        if(isBookmarkExists(bookmark)){
            for(int i = 0 ;i < bookmarksList.size();i++) {
                if(bookmark.getBookmarkCode().equals(bookmarksList.get(i).getBookmarkCode())){
                    bookmarkIndex = i;
                    break;
                }
            }
        }

        if(bookmarkIndex != -1){
            bookmarkMethods.deleteBookmark(bookmark.getPagePosition());
            bookmarksList.remove(bookmark);
            menuItems.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_border_black_24dp));
        }else{
            bookmarkMethods.insertBookmark(bookmark);
            menuItems.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_black_24dp));
        }
        //refresh list
        loadBookMarkFromDb(user);
    }

    //Highlight

    private void showSelectionButtonsPopup(){
        selectionButtonsPopup.setVisibility(View.VISIBLE);
    }

    private void hideSelectionButtonsPopup(){
        selectionButtonsPopup.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;

        selectionButtonsPopup.setLayoutParams(layoutParams);
    }

    public void moveSelectionButtonPopupToUpper(int topMargin,int leftMargin){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = leftMargin;
        layoutParams.topMargin = topMargin;
        layoutParams.bottomMargin = 30;

        selectionButtonsPopup.setLayoutParams(layoutParams);

        showSelectionButtonsPopup();
    }

    public void moveSelectionButtonPopupToBellow(int topMargin,int leftMargin){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = leftMargin;
        layoutParams.topMargin = topMargin;

        selectionButtonsPopup.setLayoutParams(layoutParams);

        showSelectionButtonsPopup();
    }

    public void moveSelectionButtonPopupToMiddle(int topMargin,int leftMargin){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = leftMargin;
        layoutParams.topMargin = topMargin;

        selectionButtonsPopup.setLayoutParams(layoutParams);

        showSelectionButtonsPopup();
    }

    private int getDensity() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        return density;
    }

    private float getFactor() {
        return (float) getDensity() / 240.f;
    }

    private int getPX(float dp) {
        return (int) (dp * getFactor());
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isPortrait() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            return true;
        else
            return false;
    }

    public int getWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        return width;
    }

    public int getPXFromRight(float dip) {
        int ps = this.getPX(dip);
        int ms = this.getWidth() - ps;
        return ms;
    }

    public int getPYFromTop(float dip) {
        int ps = this.getPX(dip);
        return ps;
    }

    public int pxr(float dp) {
        return this.getPXFromRight(dp);
    }

    public int pyt(float dp) {
        return this.getPYFromTop(dp);
    }


    public void calculateFrames() {

        if (!this.isTablet()) { // for phones
            if (this.isPortrait()) {
                int brx = 36 + (44) * 1;
                int bry = 23;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 40));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 38), pyt(bry + 70));
            } else {

                int brx = 40 + (48 + 12) * 1;
                int bry = 14;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 40));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 38), pyt(bry + 70));
            }
        } else { // for tables
            if (this.isPortrait()) {
                int ox = 50;
                int rx = 100;
                int oy = 30;

                int brx = rx - 10 + (44) * 1;
                int bry = oy + 10;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 50), pyt(bry + 50));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 50), pyt(bry + 90));
            } else {
                int sd = getPX(40);
                int ox = 40;
                int rx = 130;
                int oy = 20;


                int brx = rx - 20 + (48 + 12) * 1;
                int bry = oy + 10;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 40));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 38), pyt(bry + 70));
            }
        }

    }

    private void showOrHideToolbar(){
        if(show){
            topToolbar.animate().translationY(-topToolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            topToolbar.setVisibility(View.GONE);

            bottomToolbar.animate().translationY(-bottomToolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            bottomToolbar.setVisibility(View.GONE);

//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


//                    hideSystemUI();

            show = false;
        }else {
            topToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            topToolbar.setVisibility(View.VISIBLE);

            bottomToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            bottomToolbar.setVisibility(View.VISIBLE);

//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    showStatusBar(toolbar);
            show = true;
        }
    }

    private void showProgressDialog(String title, String message, boolean cancelable) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void showLog(String tag, String value) {
        if(tag.length() > 22) {
            tag = tag.substring(0, 22);
        }
        Log.d(tag, value);
    }

    private void initReflowableControlDisplay(String fontT,Integer fontS,Double pageP,PageTransition pageTran){
        fontType = fontT;
        fontSize = fontS;
        pagePosition = pageP;
        pageTransition = pageTran;
        reflowableControl.setFontSize(fontS);
        reflowableControl.setFontName(fontT);
        reflowableControl.setStartPositionInBook(pageP);
        reflowableControl.setPageTransition(pageTransition);
    }

    private String convertFromArray(ArrayList<String> authors){
        StringBuilder sb = new StringBuilder();
        for(String s : authors){
            if(authors.size() == 1){
                sb.append(s);
            }else{
                sb.append(s).append(", ");
            }

        }
        return sb.toString();
    }

    private void insertBookState(BookState bookState){
        bookStateMethods.insertBookState(bookState);
    }

    private BookState subscribeBookState(){

        Disposable disposable = RxBus.subscribeBookState(new Consumer<BookState>() {
            @Override
            public void accept(BookState bookState) throws Exception {
                disposableBookstate = bookState;
            }
        });
        disposable.dispose();

        return disposableBookstate;
    }

    private Bookmark subscribeBookmark(){
        Disposable disposable = RxBus.subscribeBookMark(new Consumer<Bookmark>() {
            @Override
            public void accept(Bookmark bookmark) throws Exception {
                disposableBookmark = bookmark;
            }
        });
        disposable.dispose();
        return disposableBookmark;
    }

    private void openDb(){
        bookStateDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookStateDao();
        bookStateMethods = BookStateMethods.getInstance(bookStateDao);
        bookmarkDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookmarkDao();
        bookmarkMethods = BookmarkMethods.getInstance(bookmarkDao);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_CODE_CHAPTER && RESULT_OK == resultCode && data!=null){
            if(data.getIntExtra(Constants.KEY_CHAPTER,0)-1 <0){
                reflowableControl.gotoPageByNavPointIndex(0);
            }else{
                reflowableControl.gotoPageByNavPointIndex(data.getIntExtra(Constants.KEY_CHAPTER,0));
            }

            insertBookState(subscribeBookState());
        }else if(requestCode == Constants.RESULT_CODE_BOOKMARK && RESULT_OK == resultCode && data!=null){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItems = menu;
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                insertBookState(subscribeBookState());
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.bookmark_id:
                verifyBookMark(subscribeBookmark());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        insertBookState(subscribeBookState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        insertBookState(subscribeBookState());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        insertBookState(subscribeBookState());
    }
}
