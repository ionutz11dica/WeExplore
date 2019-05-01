package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.nashapp.epublibdroid.EpubReaderView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
//import licenta.books.androidmobile.activities.PopupWindow.NotifyingSelectionWebView;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.interfaces.Constants;
import nl.siegmann.epublib.domain.Book;
//import nl.siegmann.epublib.domain.Book;
//import nl.siegmann.epublib.domain.Resource;
//import nl.siegmann.epublib.domain.Spine;
//import nl.siegmann.epublib.domain.TOCReference;

public class ReaderActivity extends AppCompatActivity  {
    EpubReaderView epubReaderView;
    Toolbar toolbar;
    Toolbar toolbarBottom;
    Intent intent;

    BookE bookE1;

    UserBookJoinDao userBookJoinDao;
    UserBookMethods userBookMethods;
    BookStateDao bookStateDao;
    BookStateMethods bookStateMethods;
    boolean show = true;

    CoordinatorLayout coordinatorLayout;
    TextView tvPage;
    SeekBar seekBarPager;
    ImageButton imgBtnContent;
    ImageButton imgBtnDayNightMode;
//    Magnifier magnifier;

//    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);




        openDb();
        initComp();
        initBookRead();

//        magnifier.show(epubReaderView.getWidth() / 2, epubReaderView.getHeight() / 2);
//        test();

        imgBtnDayNightMode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(epubReaderView.GetTheme() == 1){
                    imgBtnDayNightMode.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                    epubReaderView.SetTheme(0);
                    coordinatorLayout.setBackgroundColor(Color.parseColor("#000000"));
                }else{
                    imgBtnDayNightMode.setImageResource(R.drawable.ic_highlight_black_24dp);
                    epubReaderView.SetTheme(1);
                    coordinatorLayout.setBackgroundColor(Color.parseColor("#ffffff"));

                }
            }
        });

        imgBtnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),AnnotationBookActivity.class), Constants.RESULT_CODE_CHAPTER);
//                startActivity(new Intent(getApplicationContext(),AnnotationBookActivity.class));
            }
        });

//        final Magnifier magnifier = new Magnifier(epubReaderView);
//
//        epubReaderView.post(new Runnable() {
//            @Override
//            public void run() {
//                magnifier.update();
//            }
//        });



    }

    private void initComp(){
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        seekBarPager = findViewById(R.id.reader_seekBar);
        tvPage = findViewById(R.id.reader_tv_page);

        imgBtnContent = findViewById(R.id.image_button_content);
        imgBtnDayNightMode = findViewById(R.id.image_button_dayNight);

    }


    private void initToolbar(BookE bookE){

        toolbar = findViewById(R.id.reader_toolbar_top);
        toolbar.setTitle(bookE.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarBottom = findViewById(R.id.reader_toolbar_bottom);
    }

    private void initBookRead(){
        epubReaderView = findViewById(R.id.epub_reader);
        Disposable disposable2 = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                if(bookE!=null){
                    bookE1 = bookE;
                }
            }
        });
        disposable2.dispose();

        Single<String> stringSingle = userBookMethods.getPathBookFromDatabase(bookE1.get_id());
        stringSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        epubReaderView.OpenEpubFile(s);
                        ArrayList<String> chapters = new ArrayList<>();
                        for(int i = 0 ; i < epubReaderView.ChapterList.size();i++){
                            chapters.add(epubReaderView.ChapterList.get(i).getName());
                        }
                        RxBus.publishsChapterList(chapters);
                        setBookState();
//                        setChapterFromChapterList();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });



        intent = getIntent();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);




        epubReaderView.setEpubReaderListener(new EpubReaderView.EpubReaderListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {
//                    Log.d("Chapter name: ", epubReaderView.ChapterList.get(ChapterNumber).getName());
//                    String string = epubReaderView.ChapterList.get(0).getHref();
//                    String chapterName = getChapterTitleFromToc(ChapterNumber);
                    tvPage.setText(String.valueOf(PageNumber)+" Page - "+ String.valueOf(ChapterNumber) + " Chapter" );

                    Date date = Calendar.getInstance().getTime();
//                    BookState bookState = new BookState(ProgressStart,ChapterNumber,bookE1.get_id(), date);
//                    RxBus.publishBook(bookState);
//                    bookStateMethods.insertBookState(bookState);
//                    Log.d("Prog page: ->",String.valueOf(PageNumber));
//                    Log.d("Progress: ->",String.valueOf(ProgressStart));
//
//                    ArrayList<String> list = getBookContent(epubReaderView.book);

            }

            @Override
            public void OnChapterChangeListener(int ChapterNumber) {

            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void OnTextSelectionModeChangeListner(Boolean mode) {

//                epubReaderView.setOnTouchListener(new View.OnTouchListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.P)
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//
//                        switch (event.getActionMasked()){
//                            case MotionEvent.ACTION_HOVER_MOVE:{
//                                final int[] viewPosition = new int[2];
//                                v.getLocationOnScreen(viewPosition);
//                                magnifier.show(event.getRawX() - viewPosition[0],
//                                        event.getRawY() - viewPosition[1]);
//                                break;
//                            }
//                            case MotionEvent.ACTION_CANCEL:
//                                // Fall through.
//                            case MotionEvent.ACTION_UP: {
//                                magnifier.dismiss();
//                            }
//                        }
//                        return true;
//                    }
//                });
            }

            @Override
            public void OnLinkClicked(String url) {

            }

            @Override
            public void OnBookStartReached() {

            }

            @Override
            public void OnBookEndReached() {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void OnSingleTap() {

                if(show){
                    toolbar.animate().translationY(-toolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
                    toolbar.setVisibility(View.GONE);

                    toolbarBottom.animate().translationY(-toolbar.getBottom()).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
                    toolbarBottom.setVisibility(View.GONE);

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


//                    hideSystemUI();

                    show = false;
                }else {
                    toolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
                    toolbar.setVisibility(View.VISIBLE);

                    toolbarBottom.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
                    toolbarBottom.setVisibility(View.VISIBLE);

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    showStatusBar(toolbar);
                    show = true;
                }
            }
        });
        initToolbar(bookE1);
    }




    private String formatLine(String line) {
        if (line.contains("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd")) {
            line = line.substring(line.indexOf(">") + 1, line.length());
        }

        // REMOVE STYLES AND COMMENTS IN HTML
        if ((line.contains("{") && line.contains("}"))
                || ((line.contains("/*")) && line.contains("*/"))
                || (line.contains("<!--") && line.contains("-->"))) {
            line = line.substring(line.length());
        }
        return line;
    }



    private void setChapterFromChapterList(){
        intent = getIntent();
        if(intent.hasExtra("keye")){
            Disposable d = RxBus.subscribeChapter(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    epubReaderView.GotoPosition(integer,(float)0);
                }
            });
            d.dispose();
        }else{

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_CODE_CHAPTER && RESULT_OK == resultCode && data!=null){
            epubReaderView.GotoPosition(data.getIntExtra(Constants.KEY_CHAPTER,0),(float)0);
//            setBookState();
        }else if(requestCode == Constants.RESULT_CODE_BOOKMARK && RESULT_OK == resultCode && data!=null);
    }

    private void openDb(){
        userBookJoinDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserBookDao();
        userBookMethods = UserBookMethods.getInstance(userBookJoinDao);

        bookStateDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookStateDao();
        bookStateMethods = BookStateMethods.getInstance(bookStateDao);
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
                insertBookStateDb();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertBookStateDb() {
        Disposable disposable = RxBus.subscribeBookState(new Consumer<BookState>() {
            @Override
            public void accept(BookState bookState) throws Exception {
                bookStateMethods.insertBookState(bookState);
            }
        });
        disposable.dispose();
    }


    private void setBookState(){

        Single<BookState> stateSingle = bookStateMethods.getBookStateFromDatabase(bookE1.get_id());
        stateSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<BookState>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BookState bookState) {
//                        epubReaderView.GotoPosition(bookState.getNoChapter(),bookState.getPagePosition());
//                        RxBus.publishBook(bookState);
                    }

                    @Override
                    public void onError(Throwable e) {
                        epubReaderView.GotoPosition(0,(float)0);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        insertBookStateDb();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        insertBookStateDb();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void hideSystemUI(){
        toolbar.hideOverflowMenu();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


    }

    public void showStatusBar(View view){
        // Show the status bar on Android 4.0 and Lower
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        //Show the status bar on Android 4.1 and higher
        else{
            View decorView = getWindow().getDecorView();
            // Show the status bar.
            int visibility = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(visibility);


        }

    }


}
