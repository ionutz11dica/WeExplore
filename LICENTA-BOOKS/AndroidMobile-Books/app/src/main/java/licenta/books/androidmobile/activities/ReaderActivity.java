package licenta.books.androidmobile.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import in.nashapp.epublibdroid.EpubReaderView;
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
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;

public class ReaderActivity extends AppCompatActivity {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        openDb();
        initBookRead();


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
                        setBookState();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });



        intent = getIntent();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        epubReaderView.setEpubReaderListener(new EpubReaderView.EpubReaderListener() {
            @Override
            public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {
                    BookState bookState = new BookState(ProgressStart,ChapterNumber,bookE1.get_id());
                    RxBus.publishBook(bookState);
//                    bookStateMethods.insertBookState(bookState);
                    Log.d("Prog page: ->",String.valueOf(PageNumber));
                    Log.d("Progress: ->",String.valueOf(ProgressStart));

            }

            @Override
            public void OnChapterChangeListener(int ChapterNumber) {

            }

            @Override
            public void OnTextSelectionModeChangeListner(Boolean mode) {

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
                        epubReaderView.GotoPosition(bookState.getNoChapter(),bookState.getPagePosition());
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
