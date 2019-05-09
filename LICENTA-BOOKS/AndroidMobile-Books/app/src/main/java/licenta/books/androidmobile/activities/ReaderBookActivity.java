package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skytree.epub.BookmarkListener;
import com.skytree.epub.Caret;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
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
import licenta.books.androidmobile.classes.Converters.TimestampConverter;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.database.DaoMethods.BookmarkMethods;
import licenta.books.androidmobile.database.DaoMethods.HighlightMethods;
import licenta.books.androidmobile.interfaces.Constants;


public class ReaderBookActivity extends AppCompatActivity implements View.OnClickListener, NoteDialogFragment.OnCompleteListener {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;

    LinearLayout toolbarTop;
    LinearLayout toolbarBottom;
    EditText noteEditor;
    boolean isHighlighted=false;
    boolean forDelete = false;

    int noteBoxWidth;
    int noteBoxHeight;
    NoteDialogFragment dialogFragment;
    Bundle bundleG;
//    LinearLayout notePopup;


    RelativeLayout selectionButtonsPopup;

    Toolbar topToolbar;
    Toolbar bottomToolbar;

    //database var
    BookStateDao bookStateDao;
    BookStateMethods bookStateMethods;
    BookmarkDao bookmarkDao;
    BookmarkMethods bookmarkMethods;
    HighlightDao highlightDao;
    HighlightMethods highlightMethods;

    boolean isFullScreenForNexus=true;
    boolean isBoxesShonw = true;
    licenta.books.androidmobile.classes.Highlight currentHighlight;
    Highlight highlightTrue;

    boolean show = true;
    BookE book;
    User user;
    Menu menuItems;

     Rect highlightStartRect, highlightEndRect;
    Rect boxFrame;

    FragmentManager fragmentManager;

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
    PagerMoveHandler pageMoveHandler;
    HighLightHandler highLightHandler;

    //Highlight colors
    Button highlightColorYellow;
    Button highlightColorGreen;
    Button highlightColorPink;
    Button highlightColorBlue;
    Button highlightColorOrange;
    int currentColor;

    //Buttons
    Button noteBtn;


    //toolbar controllers
    TextView tvPage;
    SeekBar seekBarPager;
    ImageButton imgBtnContent;
    ImageButton imgBtnDayNightMode;

    //lists
    List<Bookmark> bookmarksList;
    List<licenta.books.androidmobile.classes.Highlight> highlightsList;
    Highlights chapterHighlightsList;

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
        selectionButtonsPopup.setVisibility(View.GONE);
        renderRelative = findViewById(R.id.readerRelativeLayout);
//        notePopup = findViewById(R.id.noteHighlightPopup);
//        notePopup.setVisibility(View.GONE);
//        setupUI(notePopup);



        //bottom toolbar components
        imgBtnContent = findViewById(R.id.image_button_content);

        //highlight buttons
        highlightColorYellow = findViewById(R.id.highlight_yellow);
        highlightColorGreen = findViewById(R.id.highlight_green);
        highlightColorPink = findViewById(R.id.highlight_pink);
        highlightColorBlue = findViewById(R.id.highlight_blue);
        highlightColorOrange = findViewById(R.id.highlight_orange);

        highlightColorYellow.setOnClickListener(this);
        highlightColorGreen.setOnClickListener(this);
        highlightColorPink.setOnClickListener(this);
        highlightColorBlue.setOnClickListener(this);
        highlightColorOrange.setOnClickListener(this);





        noteBtn = findViewById(R.id.btn_note);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Intra?","ceva?");
                hideSelectionButtonsPopup();
                dialogFragment = new NoteDialogFragment();
                dialogFragment.setArguments(bundleG);
//                fragmentManager = getSupportFragmentManager();
//                dialogFragment.setTargetFragment(this,Constants.REQUEST_CODE_NOTE);
                dialogFragment.show(getSupportFragmentManager(),"myFragment");
                dialogFragment.setCancelable(false);

            }
        });
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if(focusedView!=null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ReaderBookActivity.this);

                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
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
                loadHighLightsFromDb(userr);
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
        pageMoveHandler = new PagerMoveHandler();
        reflowableControl.setPageMovedListener(pageMoveHandler);
//
        highLightHandler = new HighLightHandler();
        reflowableControl.setHighlightListener(highLightHandler);
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

                        RxBus.publishBookState(bookState);
//                        RxBus.publishBookState(bookState);
                        initReflowableControlDisplay(bookState.getFontType(),bookState.getFontSize(),bookState.getPagePosition(),bookState.getPageTransition());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Date date = Calendar.getInstance().getTime();
                        pagePosition = 0;
                        fontSize = 15;
                        fontType = "TimesRoman";
                        pageTransition = PageTransition.Curl;
                        BookState bookState = new BookState(pagePosition,0,date,fontType,fontSize,Color.BLACK,Color.WHITE,PageTransition.Curl,true,book.get_id());
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

    private void loadHighLightsFromDb(User user) {
        Single<List<licenta.books.androidmobile.classes.Highlight>> hiListSingle = highlightMethods.getAllHighlights(book.get_id(),user.getUserId());
        hiListSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<licenta.books.androidmobile.classes.Highlight>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<licenta.books.androidmobile.classes.Highlight> highlights) {
                        loadHighlight(highlights);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showLog("Eroare highlight ", e.getMessage());
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.highlight_yellow:
                currentColor = Color.parseColor("#80f4cc70");
                if(isHighlighted){
                    if(highlightTrue.color == currentColor){
                        Toast.makeText(getApplicationContext(),String.valueOf(forDelete),Toast.LENGTH_LONG).show();
                        reflowableControl.deleteHighlight(highlightTrue);
                    }else{
                        changeColorHighlight(currentColor);

                    }
                }else{
                    mark(currentColor);
                }
                hideSelectionButtonsPopup();
                break;
            case R.id.highlight_green:
                currentColor = Color.parseColor("#8089da59");
                if(isHighlighted){
                    if(highlightTrue.color == currentColor){
                        reflowableControl.deleteHighlight(highlightTrue);
                    }else{
                        changeColorHighlight(currentColor);
                    }
                }else{
                    mark(currentColor);
                }
                hideSelectionButtonsPopup();
                break;
            case R.id.highlight_blue:
                currentColor = Color.parseColor("#80375e97");
                if(isHighlighted){
                    if(highlightTrue.color == currentColor){
                        reflowableControl.deleteHighlight(highlightTrue);
                    }else{
                        changeColorHighlight(currentColor);
                    }
                }else{
                    mark(currentColor);
                }
                hideSelectionButtonsPopup();
                break;
            case R.id.highlight_pink:
                currentColor = Color.parseColor("#80f18d9e");
                if(isHighlighted){
                    if(highlightTrue.color == currentColor){
                        reflowableControl.deleteHighlight(highlightTrue);
                    }else{
                        changeColorHighlight(currentColor);
                    }
                }else{
                    mark(currentColor);
                }
                hideSelectionButtonsPopup();
                break;
            case R.id.highlight_orange:
                currentColor = Color.parseColor("#80f98866");
                if(isHighlighted){
                    if(highlightTrue.color == currentColor){
                        reflowableControl.deleteHighlight(highlightTrue);
                    }else{
                        changeColorHighlight(currentColor);
                    }
                }else{
                    mark(currentColor);
                }
                hideSelectionButtonsPopup();
                break;
        }
    }

    private void mark(int color) {
        reflowableControl.markSelection(color,"");
    }
    private void changeColorHighlight(int color) {
        reflowableControl.changeHighlightColor(highlightTrue,color);
    }

    private void selectionHighlightButtonsDisplay(Rect startRect, Rect endRect) {
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
    public void onComplete(String note,boolean isUpdated) {
        if(note!=null){
            if(isUpdated){
                reflowableControl.changeHighlightNote(highlightTrue,note);
            }else{
                reflowableControl.markSelection(Color.parseColor("#80f4cc70"),note);
            }
        }
    }

    private class SelectionHandler implements SelectionListener {

        @Override
        public void selectionStarted(Highlight highlight, Rect rect, Rect rect1) {
            show=true;
            hideSelectionButtonsPopup();
        }

        @Override
        public void selectionChanged(Highlight highlight, Rect rect, Rect rect1) {
            show=true;

            hideSelectionButtonsPopup();
        }

        @Override
        public void selectionEnded(Highlight highlight, Rect startRect, Rect endRect) {
            currentHighlight = creatorHighlight(highlight);
            highlightTrue = highlight;
            highlightStartRect = startRect;
            highlightEndRect = endRect;
            isHighlighted=false;
            refreshButtons();
            selectionHighlightButtonsDisplay(startRect, endRect);
        }

        @Override
        public void selectionCancelled() {

            showOrHideToolbar();
            hideSelectionButtonsPopup();
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



    private class PagerMoveHandler implements PageMovedListener {

        @Override
        public void onPageMoved(PageInformation pageInformation) {
            showLog("Page Index", String.valueOf(pageInformation.pageIndex));
            show = true;
            showOrHideToolbar();
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
            RxBus.publishBookState(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,true,book.get_id()));
//            intent.putExtra("aoloc",new BookState(book.get_id()+"x",pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,true,book.get_id()));
//            bookstateTransporetr(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,Color.BLACK,Color.WHITE,pageTransition,true,book.get_id()));
            insertBookState(subscribeBookState());
            RxBus.publishBookMark(bookmark);

        }


        @Override
        public void onChapterLoaded(int i) {
            showLog("Chapter Index", String.valueOf(i));
            NavPoints tocNavPoints = reflowableControl.getNavPoints();
            ArrayList<Chapter> chapterList = new ArrayList<>();

            for(int index = 0 ;index < tocNavPoints.getSize();index++){
                chapterList.add(new Chapter(tocNavPoints.getNavPoint(index).text,tocNavPoints.getNavPoint(index).chapterIndex));
            }

            RxBus.publishsChapterList(chapterList);
            if(reflowableControl.getChapterTitle(i) !=null){
                RxBus.publishsChapterName(reflowableControl.getChapterTitle(i));
                RxBus.publishsChapter(reflowableControl.getChapterIndex());
            }else {

                RxBus.publishsChapterName(reflowableControl.getChapterTitle(i+1));
                RxBus.publishsChapter(reflowableControl.getChapterIndex());
            }
        }

        @Override
        public void onFailedToMove(boolean b) {
            showLog("Fail?", String.valueOf(b));
        }
    }


    private licenta.books.androidmobile.classes.Highlight creatorHighlight(Highlight highlight){
        licenta.books.androidmobile.classes.Highlight highlightDb = new licenta.books.androidmobile.classes.Highlight(highlight.code,highlight.chapterIndex,highlight.pagePositionInBook,highlight.pagePositionInChapter,
                highlight.startIndex,highlight.endIndex,highlight.startOffset,highlight.endOffset,highlight.color,highlight.text,highlight.left,highlight.top,highlight.note,highlight.isNote,highlight.isOpen,
                Calendar.getInstance().getTime(),highlight.forSearch,highlight.style,highlight.pageIndex,book.get_id(),user.getUserId());
        return highlightDb;
    }

    private Highlight creatorHighlightReverse(licenta.books.androidmobile.classes.Highlight highlightMine){
       Highlight highlight = new Highlight();
        highlight.bookCode = highlightMine.getCode();
        highlight.chapterIndex = highlightMine.getChapterIndex();
        highlight.pagePositionInBook = highlightMine.getPagePosInBoo();
        highlight.pagePositionInChapter = highlightMine.getPagePosInChapter();
        highlight.startIndex = highlightMine.getStartIndex();
        highlight.endIndex = highlightMine.getEndIndex();
        highlight.startOffset = highlightMine.getStartOffset();
        highlight.endOffset = highlightMine.getEndOffset();
        highlight.color = highlightMine.getColor();
        highlight.text = highlightMine.getSelectedText();
        highlight.left = highlightMine.getLeft();
        highlight.top = highlightMine.getTop();
        highlight.note = highlightMine.getNoteContent();
        highlight.isNote = highlightMine.isNote();
        highlight.isOpen = highlightMine.isOpen();
        highlight.datetime = TimestampConverter.fromDateToString(highlightMine.getHighlightedDate());
        highlight.forSearch = highlightMine.isForSearch();
        highlight.style = highlightMine.getStyle();
        highlight.pageIndex = highlightMine.getPageIndex();

        return highlight;
    }

    private Highlights creatorHighlightSkyEpub(List<licenta.books.androidmobile.classes.Highlight> arrayList,int chapterIndex){
        chapterHighlightsList = new Highlights();
        for(int i = 0 ;i < arrayList.size();i++){
            if(arrayList.get(i).getChapterIndex() == chapterIndex){
                Highlight highlight = new Highlight();
                highlight.bookCode = arrayList.get(i).getCode();
                highlight.chapterIndex = arrayList.get(i).getChapterIndex();
                highlight.pagePositionInBook = arrayList.get(i).getPagePosInBoo();
                highlight.pagePositionInChapter = arrayList.get(i).getPagePosInChapter();
                highlight.startIndex = arrayList.get(i).getStartIndex();
                highlight.endIndex = arrayList.get(i).getEndIndex();
                highlight.startOffset = arrayList.get(i).getStartOffset();
                highlight.endOffset = arrayList.get(i).getEndOffset();
                highlight.color = arrayList.get(i).getColor();
                highlight.text = arrayList.get(i).getSelectedText();
                highlight.left = arrayList.get(i).getLeft();
                highlight.top = arrayList.get(i).getTop();
                highlight.note = arrayList.get(i).getNoteContent();
                highlight.isNote = arrayList.get(i).isNote();
                highlight.isOpen = arrayList.get(i).isOpen();
                highlight.datetime = TimestampConverter.fromDateToString(arrayList.get(i).getHighlightedDate());
                highlight.forSearch = arrayList.get(i).isForSearch();
                highlight.style = arrayList.get(i).getStyle();
                highlight.pageIndex = arrayList.get(i).getPageIndex();

                chapterHighlightsList.addHighlight(highlight);
            }

        }
        return chapterHighlightsList;
    }
    private int getDensity() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

    private float getFactor() {
        return (float) getDensity() / 240.f;
    }

    private int getPX(float dp) {
        return (int) (dp * getFactor());
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private class HighLightHandler implements HighlightListener {

        @Override
        public void onHighlightDeleted(Highlight highlight) {
            licenta.books.androidmobile.classes.Highlight highlightDb = creatorHighlight(highlight);
            highlightMethods.deleteHighlight(highlight.pagePositionInBook,highlight.text,book.get_id());
            loadHighLightsFromDb(user);
        }

        @Override
        public void onHighlightInserted(Highlight highlight) {
            licenta.books.androidmobile.classes.Highlight highlightDb = creatorHighlight(highlight);
            highlightMethods.insertHighlight(highlightDb);
            loadHighLightsFromDb(user);
        }



        @Override
        public void onHighlightUpdated(Highlight highlight) {
            Log.d("UpdateH",String.valueOf(highlight.color));
            highlightMethods.updateHighlight(highlightTrue.startIndex,highlightTrue.startOffset,highlightTrue.endIndex,highlightTrue.endOffset,highlightTrue.color,
                    highlightTrue.text,highlightTrue.note,highlightTrue.isNote,highlightTrue.style,
                    book.get_id(),highlightTrue.chapterIndex,highlightTrue.startIndex,highlightTrue.startOffset,highlightTrue.endIndex,highlightTrue.endOffset);
        }

        @Override
        public void onHighlightHit(Highlight highlight, int i, int i1, Rect startRect, Rect endRect) {
            refreshButtons();
            forDelete=false;
            bundleG = new Bundle();
            bundleG.putBoolean(Constants.KEY_HIGHLIGHT_EXISTS,true);
            if(highlight.color == Color.parseColor("#80f4cc70")){
                modifyButtonView(highlightColorYellow);
            }else if(highlight.color == Color.parseColor("#80f18d9e")){
                modifyButtonView(highlightColorPink);
            }else if(highlight.color == Color.parseColor("#80f98866")){
                modifyButtonView(highlightColorOrange);
            }else if(highlight.color == Color.parseColor("#8089da59")){
                modifyButtonView(highlightColorGreen);
            }else if(highlight.color == Color.parseColor("#80375e97")){
                modifyButtonView(highlightColorBlue);
            }
            isHighlighted=true;
            show=true;
            showOrHideToolbar();
            highlightTrue = highlight;
            selectionHighlightButtonsDisplay(startRect,endRect);

//            Toast.makeText(getApplicationContext(),highlight.text,Toast.LENGTH_LONG).show();
        }

        @Override
        public Highlights getHighlightsForChapter(int chapterIndex) {

            return creatorHighlightSkyEpub(highlightsList,chapterIndex);

        }

        @Override
        public Bitmap getNoteIconBitmapForColor(int color, int style) {
            Drawable icon = null;
            icon = getResources().getDrawable(R.drawable.ic_speaker_notes_yellow_24dp);


            return drawableToBitmap(icon);
        }

        @Override
        public void onNoteIconHit(Highlight highlight) {
//            Log.d("intra?","danu");
//            if(isBoxesShonw){
//                show=true;
//                showOrHideToolbar();
//                return;
//            }

            highlightTrue = highlight;
            currentHighlight = creatorHighlight(highlight) ;
            currentColor = highlight.color;
            if(!reflowableControl.isPaging()){
                Log.d("intra?","nu");
                dialogFragment = new NoteDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_NOTE_CONTENT,highlight.note);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"fragmentD");
            }
        }

        @Override
        public Rect getNoteIconRect(int i, int i1) {
            return new Rect(0, 0, getPX(32), getPX(32));
        }

        @Override
        public void onDrawHighlightRect(Canvas canvas, Highlight highlight, Rect rect) {

        }

        @Override
        public void onDrawCaret(Canvas canvas, Caret caret) {

        }
    }

    private void refreshButtons() {
        highlightColorYellow.setText(null);
        highlightColorBlue.setText(null);
        highlightColorGreen.setText(null);
        highlightColorPink.setText(null);
        highlightColorOrange.setText(null);
        forDelete = false;
    }

    private void modifyButtonView(Button highlightButton){
        if(highlightButton.getText()==null){
            forDelete=false;
        }else {
            forDelete=true;
        }
        highlightButton.setText("X");
        highlightButton.setTextColor(Color.parseColor("#80000000"));
        highlightButton.setTextSize(16f);

    }


    private Integer getBookmarkCode(Integer chapterIndex,Integer pageIndex){
        return chapterIndex * 1000 + pageIndex;
    }

    private void loadBookmarkList(List<Bookmark> arrayList){
        bookmarksList = arrayList;
    }

    private void loadHighlight(List<licenta.books.androidmobile.classes.Highlight> highlights){
        highlightsList = highlights;
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
            bookmarkMethods.deleteBookmark(bookmark.getPagePosition(),book.get_id());
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

    ////
    ////
    ////
    ////
    ////
    ////

//    @SuppressLint("NewApi")
//    public int getRawWidth() {
//        int width = 0, height = 0;
//        final DisplayMetrics metrics = new DisplayMetrics();
//        Display display = getWindowManager().getDefaultDisplay();
//        Method mGetRawH = null, mGetRawW = null;
//
//        try {
//            // For JellyBeans and onward
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                display.getRealMetrics(metrics);
//
//                width = metrics.widthPixels;
//                height = metrics.heightPixels;
//            } else {
//                mGetRawH = Display.class.getMethod("getRawHeight");
//                mGetRawW = Display.class.getMethod("getRawWidth");
//
//                try {
//                    width = (Integer) mGetRawW.invoke(display);
//                    height = (Integer) mGetRawH.invoke(display);
//                } catch (IllegalArgumentException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return 0;
//                } catch (IllegalAccessException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return 0;
//                } catch (InvocationTargetException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//            return width;
//        } catch (NoSuchMethodException e3) {
//            e3.printStackTrace();
//            return 0;
//        }
//    }
//
//    public  String getModelName() {
//        return Build.MODEL;
//    }
//
//    public  String getDeviceName() {
//        return Build.DEVICE;
//    }
//    public  boolean isNexus() {
//        String models[]={"mako","flo","grouper","maguro","crespo","hammerhead"};
//        String model = getModelName();
//        String device = getDeviceName();
//        for (int i=0; i<models.length; i++) {
//            String name = models[i];
//            if (name.equalsIgnoreCase(model) || name.equalsIgnoreCase(device)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public int getWidth() {
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int width = metrics.widthPixels;
//        if (isNexus() && isFullScreenForNexus) {
//            if (!this.isPortrait() && Build.VERSION.SDK_INT>=19) {
//                width = this.getRawWidth();
//            }
//        }
//        return width;
//    }
//
//    // modify for fullscreen
//    public int getHeight() {
//        if (Build.VERSION.SDK_INT>=19) {
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            int height = this.getRawHeight();
//            height+=ps(50);
//            if (Build.DEVICE.contains("maguro") && this.isPortrait()) {
//                height-=ps(65);
//            }
//
//            return height;
//        }else {
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            int height = metrics.heightPixels;
//            height+=ps(50);
//            return height;
//        }
//    }
//
//    @SuppressLint("NewApi")
//    public int getRawHeight() {
//        int width = 0, height = 0;
//        final DisplayMetrics metrics = new DisplayMetrics();
//        Display display = getWindowManager().getDefaultDisplay();
//        Method mGetRawH = null, mGetRawW = null;
//
//        try {
//            // For JellyBeans and onward
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                display.getRealMetrics(metrics);
//                width = metrics.widthPixels;
//                height = metrics.heightPixels;
//            } else {
//                mGetRawH = Display.class.getMethod("getRawHeight");
//                mGetRawW = Display.class.getMethod("getRawWidth");
//                try {
//                    width = (Integer) mGetRawW.invoke(display);
//                    height = (Integer) mGetRawH.invoke(display);
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                    return 0;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                    return 0;
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//            return height;
//        } catch (NoSuchMethodException e3) {
//            e3.printStackTrace();
//            return 0;
//        }
//    }
//
//
//
//    public void setFrame(View view,int dx, int dy, int width, int height) {
//        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
//        param.leftMargin = dx;
//        param.topMargin =  dy;
//        param.width = width;
//        param.height = height;
//        view.setLayoutParams(param);
//    }
//
//    public boolean isPortrait() {
//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation== Configuration.ORIENTATION_PORTRAIT) return true;
//        else return false;
//    }
//
//    // this is not 100% accurate function.
//    public boolean isTablet() {
//        return (getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK)
//                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//    }
//
//    public void makeFullscreen(Activity activity) {
//
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT>=19) {
//            activity.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_IMMERSIVE
//                            |	View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            |	View.SYSTEM_UI_FLAG_FULLSCREEN
//                            |	View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            |	View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            |	View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            |	View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            );
//        }else if (Build.VERSION.SDK_INT>=11) {
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        }
//    }
//
//    void dismissKeyboard(){
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(noteEditor.getWindowToken(),0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        makeFullscreen(this);
//    }
//
//    public void showKeyboard() {
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(noteEditor, 0);
//        noteEditor.requestFocus();
//    }
//
//    int oldNoteTop;
//    int oldNoteLeft;
//    boolean isNoteMoved = false;
//
//    void moveNoteBoxPositionForKeyboard() {
//        RelativeLayout.LayoutParams params =
//                (RelativeLayout.LayoutParams)noteBox.getLayoutParams();
//        int keyboardTop = (int)(this.getHeight()*0.6f);
//        int noteHeight = ps(300);
//        oldNoteTop = params.topMargin;
//        oldNoteLeft = params.leftMargin;
//        isNoteMoved = true;
//        int noteTop = keyboardTop - noteHeight - ps(80);
//        this.setFrame(noteBox, params.leftMargin, noteTop, noteBoxWidth,  noteHeight);
//    }
//
//    boolean keyboardHidesNote() {
//        if(!this.isPortrait() && !this.isTablet()) return false;
//        if(this.noteBox.getVisibility() != View.VISIBLE) return false;
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)noteBox.getLayoutParams();
//        int bottomY = params.topMargin + params.height;
//        int keyBoardTop = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.6f);
//
//        return bottomY >= keyBoardTop;
//    }
//
//    View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if(hasFocus){
//                processForKeyboard(true);
//            }else {
//                processForKeyboard(false);
//            }
//        }
//    };
//
//    private void processForKeyboard(boolean isShown) {
//        if(isShown){
//            if(this.keyboardHidesNote()){
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissKeyboard();
//                        moveNoteBoxPositionForKeyboard();
//                        showKeyboard();
//                    }
//                },100);
//            }
//        }else {
//            if(isNoteMoved){
//                this.restoreNoteBoxPosition();
//            }
//        }
//    }
//
//    public int getDensityDPI(){
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        return metrics.densityDpi;
//    }
//
//    public int getPs(float dip) {
//         float density = this.getDensityDPI();
//         float factor = density/240.f;
//         return (int) (dip*factor);
//    }
//
//    public int ps(float dip){
//        return this.getPs(dip);
//    }
//
//    void restoreNoteBoxPosition() {
//        int noteHeight = ps(300);
//        isNoteMoved = false;
//        this.setFrame(noteBox, oldNoteLeft, oldNoteTop, noteBoxWidth,  noteHeight);
//    }
//
//    public void hideNoteBox() {
//        if (currentHighlight!=null && noteEditor!=null && noteBox.getVisibility()==View.VISIBLE) saveNoteBox();
//        this.noteBox.setVisibility(View.INVISIBLE);
//        this.noteBox.setVisibility(View.GONE);
//        this.dismissKeyboard();
//        this.noteEditor.clearFocus();
//        isBoxesShonw = false;
//    }
//
//    public void moveSkyBox(CustomBox box,int boxWidth,int boxHeight, Rect startRect, Rect endRect) {
//        RelativeLayout.LayoutParams params =
//                (RelativeLayout.LayoutParams)box.getLayoutParams();
//        int topMargin = ps(80);
//        int bottomMargin = ps(80);
//        int boxTop=0;
//        int boxLeft=0;
//        int arrowX;
//        boolean isArrowDown;
//
//        if (startRect.top - topMargin > boxHeight) {
//            boxTop = startRect.top - boxHeight-ps(10);
//            boxLeft = (startRect.left+startRect.width()/2-boxWidth/2);
//            arrowX = (startRect.left+startRect.width()/2);
//            isArrowDown = true;
//        }else if ((this.getHeight()-endRect.bottom)-bottomMargin >boxHeight) { // ????????? ????????? ????????? ?????????.
//            boxTop = endRect.bottom+ps(10);
//            boxLeft = (endRect.left+endRect.width()/2-boxWidth/2);
//            arrowX = (endRect.left+endRect.width()/2);
//            isArrowDown = false;
//        }else {
//            boxTop = ps(100);
//            boxLeft = (startRect.left+startRect.width()/2-boxWidth/2);
//            arrowX = (startRect.left+startRect.width()/2);
//            isArrowDown = true;
//        }
//
//        if (boxLeft+boxWidth > this.getWidth()*.9) {
//            boxLeft = (int)(this.getWidth()*.9) - boxWidth;
//        }else if (boxLeft<this.getWidth()*.1) {
//            boxLeft = (int)(this.getWidth()*.1);
//        }
//
//        box.setArrowPosition(arrowX, boxLeft, boxWidth);
//        box.setArrowDirection(isArrowDown);
//        params.leftMargin = boxLeft;
//        params.topMargin = boxTop;
//        params.width = boxWidth;
//        params.height = boxHeight;
//        box.setLayoutParams(params);
//        box.invalidate();
//
//        boxFrame = new Rect();
//        boxFrame.left = boxLeft;
//        boxFrame.top = boxTop;
//        boxFrame.right = boxLeft+boxWidth;
//        boxFrame.bottom = boxTop+boxHeight;
//    }
//
//    public void showNoteBox() {
//        if (highlightTrue==null) return;
//        isBoxesShonw = true;
//        Rect startRect = highlightStartRect;//(222,1196-342,1230)
//        Rect endRect   = highlightEndRect;
//        int minWidth = Math.min(this.getWidth(),this.getHeight());
//        noteBoxWidth = 	   (int)(minWidth * 0.7);
//        noteBoxHeight = 	ps(300);
//        noteEditor.setText(highlightTrue.note);
//        noteBox.setBoxColor(Color.YELLOW);
//        this.moveSkyBox(noteBox,noteBoxWidth,noteBoxHeight,startRect,endRect);
//        noteBox.setVisibility(View.VISIBLE);
//
//    }
//
//    public void saveNoteBox() {
//        if (highlightTrue==null || noteEditor==null) return;
//        if (noteBox.getVisibility()!=View.VISIBLE) return;
//        boolean isNote;
//        String note = noteEditor.getText().toString();
//        if (note==null || note.length()==0) isNote = false;
//        else isNote = true;
//        highlightTrue.isNote = isNote;
//        highlightTrue.note = note;
//        highlightTrue.style=27;
//        if (highlightTrue.color==0) highlightTrue.color =currentColor;
//        reflowableControl.changeHighlightNote(highlightTrue, note);
//    }
//
//    @SuppressLint("RtlHardcoded")
//    public void makeNoteBox() {
//        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
//        noteBox = new CustomBox(this);
//        noteBox.setBoxColor(currentColor);
//        noteBox.setArrowHeight(ps(25));
//        noteBox.setArrowDirection(false);
//        param.leftMargin = ps(50);
//        param.topMargin =  ps(400);
//        int minWidth = Math.min(this.getWidth(),this.getHeight());
//        noteBoxWidth = 	   (int)(minWidth * 0.8);
//        param.width = noteBoxWidth;
//        param.height =     ps(300);
//        noteBox.setLayoutParams(param);
//        noteBox.setArrowDirection(false);
//
//        noteEditor = new EditText(this);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
//        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        params.width =  RelativeLayout.LayoutParams.FILL_PARENT;
//        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
//        noteEditor.setLayoutParams(params);
//        noteEditor.setBackgroundColor(Color.TRANSPARENT);
//        noteEditor.setMaxLines(1000);
//        noteEditor.setGravity(Gravity.TOP|Gravity.LEFT);
//        noteEditor.setOnFocusChangeListener(focusListener);
//        noteBox.contentView.addView(noteEditor);
//
//        renderRelative.addView(noteBox);
//        this.hideNoteBox();
//    }

    ////
    ////
    ////
    ////
    ////
    ////

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
        highlightDao = AppRoomDatabase.getInstance(getApplicationContext()).getHighlightDao();
        highlightMethods = HighlightMethods.getInstance(highlightDao);
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
        insertBookState(subscribeBookState());

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        insertBookState(subscribeBookState());

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        insertBookState(subscribeBookState());

        super.onBackPressed();
    }

}
