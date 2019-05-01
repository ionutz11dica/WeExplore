package licenta.books.androidmobile.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.State;
import com.skytree.epub.StateListener;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import retrofit2.http.PATCH;

//import android.widget.RelativeLayout;

public class ReaderBookActivity extends AppCompatActivity {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;

    LinearLayout toolbarTop;
    LinearLayout toolbarBottom;

    Toolbar topToolbar;
    Toolbar bottomToolbar;

    //database var
    BookStateDao bookStateDao;
    BookStateMethods bookStateMethods;

    boolean show = true;
    BookE book;

    //book information
    static double pagePosition;
    static Integer fontSize;
    static String fontType;
    PageTransition pageTransition;
    BookState disposableBookstate;


    ProgressDialog progressDialog;

    StateHandler stateHandler;
    PageMoveHandler pageMoveHandler;


//    @SuppressLint("SdCardPath")
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

        topToolbar = findViewById(R.id.reader_toolbar_top);
        bottomToolbar = findViewById(R.id.reader_toolbar_bottom);

        renderRelative = findViewById(R.id.readerRelativeLayout);
    }

    private void initPathBook(){
        Disposable d = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                book = bookE;
            }
        });
        d.dispose();
        initBookVariableFromDataBase(fontType,fontSize,pagePosition);

        String fileName = book.getTitle()+".epub";
        String pathFile = Environment.getExternalStorageDirectory().getPath();
        renderRelative.addView(setUpReflowableController(fileName,"/sdcard/Android/data/licenta.books.androidmobile/files"));

    }

    private ReflowableControl setUpReflowableController(String fileName, String baseDirectoryPath) {

        showLog("baseDirectoryPath",baseDirectoryPath);

        reflowableControl = new ReflowableControl(this);

//        fileName = prepareFileNameWithWhiteSpaceReplacement(fileName);

        showLog("fileName", String.valueOf(fileName));

        reflowableControl.setBookName(fileName);

        reflowableControl.setBaseDirectory(baseDirectoryPath);

        reflowableControl.setLicenseKey("04FE-082A-0B15-0CFB");

        reflowableControl.setDoublePagedForLandscape(true);

//        Disposable d = RxBus.subscribeBookState(new Consumer<BookState>() {
//            @Override
//            public void accept(BookState bookState) throws Exception {
////                testBookstate = bookState;
//                showLog("Ceva",testBookstate.getFontType());
//            }
//        });
//        d.dispose();


//        if(fontSize == null && fontType == null){
//            fontSize=15;fontType="TimesRoman";
//        }
//        reflowableControl.setFont(fontType, fontSize);

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

//        reflowableControl.setStartPositionInBook(pagePosition);

        reflowableControl.useDOMForHighlight(false);

        reflowableControl.setNavigationAreaWidthRatio(0.0f); // both left and right side.

        //To get text of page in pageMovedListener in pageDescription
        reflowableControl.setExtractText(true);

//        reflowableControl.setMediaOverlayListener(new MediaOverlayHandler()); //For Audio Book

        showLog("Media Overlay Available : ", String.valueOf(reflowableControl.isMediaOverlayAvailable()));

//        //Listeners
//        selectionHandler = new SelectionHandler();
//        reflowableControl.setSelectionListener(selectionHandler);
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

    private void initBookVariableFromDataBase(String fontT, Integer fontS, Double pageP){
        final Integer[] plm = new Integer[1];
        Single<BookState> stateSingle = bookStateMethods.getBookStateFromDatabase(book.get_id());
        final Integer finalFontS = fontS;
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
        fontS=plm[0];
    }



    private void showOrHideToolbar(){
        if(show){
            toolbarTop.animate().translationY(-toolbarTop.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            toolbarTop.setVisibility(View.GONE);

            toolbarBottom.animate().translationY(-topToolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            toolbarBottom.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


//                    hideSystemUI();

            show = false;
        }else {
            toolbarTop.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            toolbarTop.setVisibility(View.VISIBLE);

            toolbarBottom.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            toolbarBottom.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            pagePosition = pageInformation.pagePositionInBook;
            RxBus.publishBookState(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,book.get_id()));
            showLog("PageInfo",String.valueOf(pagePosition));
        }

        @Override
        public void onChapterLoaded(int i) {

        }

        @Override
        public void onFailedToMove(boolean b) {

        }
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

    private void openDb(){
        bookStateDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookStateDao();
        bookStateMethods = BookStateMethods.getInstance(bookStateDao);
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
