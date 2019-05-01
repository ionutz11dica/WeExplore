package licenta.books.androidmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Magnifier;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skytree.epub.Highlight;
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

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.Chapter;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.interfaces.Constants;
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

    //Handlers
    SelectionHandler selectionHandler;
    StateHandler stateHandler;
    PageMoveHandler pageMoveHandler;
    Magnifier magnifier;

    //toolbar controllers
    TextView tvPage;
    SeekBar seekBarPager;
    ImageButton imgBtnContent;
    ImageButton imgBtnDayNightMode;


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
        topToolbar.setTitle(book.getTitle());
        topToolbar.setSubtitle(convertFromArray(book.getAuthors()));
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initBookVariableFromDataBase(fontType,fontSize,pagePosition);

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

        reflowableControl.useDOMForHighlight(false);

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

    }

    private class SelectionHandler implements SelectionListener {

        @Override
        public void selectionStarted(Highlight highlight, Rect rect, Rect rect1) {

        }

        @Override
        public void selectionChanged(Highlight highlight, Rect rect, Rect rect1) {

        }

        @Override
        public void selectionEnded(Highlight highlight, Rect rect, Rect rect1) {

        }

        @Override
        public void selectionCancelled() {
            showOrHideToolbar();
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
            show=true;
            showOrHideToolbar();
            pagePosition = pageInformation.pagePositionInBook;
            RxBus.publishBookState(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,book.get_id()));
            showLog("PageInfo",String.valueOf(reflowableControl.getNumberOfChaptersInBook()));
//            for(int i =0;i<reflowableControl.getNumberOfPagesInBook();i++){
//                Toast.makeText(getApplicationContext(),reflowableControl.getChapterTitle(15),Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(),reflowableControl.getNumberOfPagesInChapter(4),Toast.LENGTH_LONG).show();
            showLog("PageInfo",String.valueOf(reflowableControl.getNumberOfPagesInChapter()));

        }

        @Override
        public void onChapterLoaded(int i) {
            ArrayList<String> chapterList = new ArrayList<>();
            for(int j = 0 ;j < reflowableControl.getNumberOfChaptersInBook();j++){
                if(reflowableControl.getChapterTitle(j)!=null){
                    chapterList.add(reflowableControl.getChapterTitle(j));
                }
            }
            RxBus.publishsChapterList(chapterList);
            RxBus.publishsChapter(reflowableControl.getNumberOfPagesInChapter());
            int y=0;

        }

        @Override
        public void onFailedToMove(boolean b) {

        }
    }

    private void showOrHideToolbar(){
        if(show){
            topToolbar.animate().translationY(-topToolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            topToolbar.setVisibility(View.GONE);

            bottomToolbar.animate().translationY(-bottomToolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            bottomToolbar.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


//                    hideSystemUI();

            show = false;
        }else {
            topToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            topToolbar.setVisibility(View.VISIBLE);

            bottomToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            bottomToolbar.setVisibility(View.VISIBLE);

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

    private void openDb(){
        bookStateDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookStateDao();
        bookStateMethods = BookStateMethods.getInstance(bookStateDao);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_CODE_CHAPTER && RESULT_OK == resultCode && data!=null){
            reflowableControl.gotoPageByNavPointIndex(data.getIntExtra(Constants.KEY_CHAPTER,0));
        }else if(requestCode == Constants.RESULT_CODE_BOOKMARK && RESULT_OK == resultCode && data!=null){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
