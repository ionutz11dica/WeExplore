package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skytree.epub.Caret;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.NavPoints;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SearchListener;
import com.skytree.epub.SearchResult;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.State;
import com.skytree.epub.StateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BookAnnotations;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.activities.others.HelperApp;
import licenta.books.androidmobile.activities.others.HelperSettings;
import licenta.books.androidmobile.adapters.SearchAdapter;
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
import licenta.books.androidmobile.fragments.AnnotationFragment;
import licenta.books.androidmobile.interfaces.Constants;


public class ReaderBookActivity extends AppCompatActivity implements View.OnClickListener, NoteDialogFragment.OnCompleteListener,
        FontsDialogFragment.OnCompleteListenerFonts, ColorsDialogFragment.OnCompleteListenerColor , AnnotationFragment.OnFragmentInteractionListener {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;

    boolean isHighlighted=false;
    boolean forDelete = false;
    boolean isShow = true;

    NoteDialogFragment dialogFragment;
    Bundle bundleG;


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

    licenta.books.androidmobile.classes.Highlight currentHighlight;
    Highlight highlightTrue;

    boolean show = true;
    BookE book;
    User user;
    Menu menuItems;

    Rect highlightStartRect, highlightEndRect;


    //book information
    static double pagePosition;
    static Integer fontSize;
    static String fontType;
    static Integer backgroundColor;
    static Integer foregroundColor;
    static Boolean theme;

    PageTransition pageTransition;
    BookState disposableBookstate;
    Bookmark disposableBookmark;


    ProgressDialog progressDialog;
    ProgressBar progressBar;

    //Handlers
    SelectionHandler selectionHandler;
    StateHandler stateHandler;
    PagerMoveHandler pageMoveHandler;
    HighLightHandler highLightHandler;
    SearchHandler searchHandler;

    //Highlight colors
    Button highlightColorYellow;
    Button highlightColorGreen;
    Button highlightColorPink;
    Button highlightColorBlue;
    Button highlightColorOrange;
    int currentColor;
    Button audioHighlightBtn;
    Button shareText;

    //Buttons
    Button noteBtn;

    LinearLayout styleLayout;
    Button typeface;

    LinearLayout searchLayout;
    EditText searchEditText;
    TextView cancelTextView;
    ListView searchListview;

    //toolbar controllers
    TextView tvPage;
    SeekBar seekBarPager;
    ImageButton imgBtnContent;
    ImageButton imgBtnDayNightMode;
    ImageButton imgBtnStyleContent;
    ImageButton imgBtnSearch;

    //lists
    List<Bookmark> bookmarksList;
    List<licenta.books.androidmobile.classes.Highlight> highlightsList;
    Highlights chapterHighlightsList;

    //style layout
    SeekBar brightnessControl;
    ImageButton fontMinus,fontPlus;
    ImageButton marginMinus,marginPlus;
    ImageButton lineSpacingIncrease, lineSpacingDecrease;
    Button colorsBackground;
    Button colorsForeground;


    ArrayList<CustomFont> fonts = new ArrayList<>();
    ArrayList<SearchResult> searchResults = new ArrayList<>();
    ArrayList<BookAnnotations> bookAnnotations = new ArrayList<>();

    HelperApp app;
    HelperSettings settings;
    int lineSpacing;



    //audio player TTS ------------
    private int RINGER_MODE;
    private TextToSpeech textToSpeechInPage;
    private TextToSpeech textToSpeechInSelection;

    private String utteranceIdForPage = "my_page_pronounce";
    private String utteranceIdForTextInHighlight = "my_pronounce";

    private String textInPage;
    private String textInHighlight;

    private boolean pageAutoFlip = false;
    private boolean bookLoadTask = false;

    private MenuItem playPauseItem;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readerbook);
        app = (HelperApp)getApplication();
        openDb();
        makeFonts();
        initComp();
        initPathBook();

    }

    private void initComp(){
        settings = new HelperSettings(getApplicationContext());
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
        imgBtnStyleContent = findViewById(R.id.image_button_style);
        imgBtnDayNightMode = findViewById(R.id.image_button_dayNight);
        imgBtnSearch = findViewById(R.id.image_button_search);

        //highlight buttons
        highlightColorYellow = findViewById(R.id.highlight_yellow);
        highlightColorGreen = findViewById(R.id.highlight_green);
        highlightColorPink = findViewById(R.id.highlight_pink);
        highlightColorBlue = findViewById(R.id.highlight_blue);
        highlightColorOrange = findViewById(R.id.highlight_orange);
        audioHighlightBtn = findViewById(R.id.btn_audio);
        shareText = findViewById(R.id.btn_share);

        highlightColorYellow.setOnClickListener(this);
        highlightColorGreen.setOnClickListener(this);
        highlightColorPink.setOnClickListener(this);
        highlightColorBlue.setOnClickListener(this);
        highlightColorOrange.setOnClickListener(this);
        audioHighlightBtn.setOnClickListener(this);
        shareText.setOnClickListener(this);


        //style layout
        styleLayout = findViewById(R.id.relative_test);
        styleLayout.setVisibility(View.GONE);
        styleLayout.setClickable(true);
        typeface = findViewById(R.id.typeface_btn);


        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setVisibility(View.GONE);
        searchLayout.setClickable(true);
        searchEditText = findViewById(R.id.et_search);
        cancelTextView = findViewById(R.id.tv_search_cancel);
        searchListview = findViewById(R.id.lv_search_items);



        brightnessControl = findViewById(R.id.brightness_seeker);
        fontMinus = findViewById(R.id.font_minus);
        fontPlus = findViewById(R.id.font_plus);
        marginMinus = findViewById(R.id.margin_minus);
        marginPlus = findViewById(R.id.margin_plus);
        colorsBackground = findViewById(R.id.background_btn);
        colorsForeground = findViewById(R.id.textcolor_btn);
        lineSpacingIncrease = findViewById(R.id.line_spacing_plus);
        lineSpacingDecrease = findViewById(R.id.line_spacing_minus);


        //TTS AUDIO PLAYER ;
        textInPage ="";
        prepareAndroidTTSinPage(textInPage);

        textInHighlight ="";
        prepareAndroidTTSinHighlight(textInHighlight);


        noteBtn = findViewById(R.id.btn_note);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Intra?","ceva?");
                hideSelectionButtonsPopup();
                dialogFragment = new NoteDialogFragment();
                dialogFragment.setArguments(bundleG);

                dialogFragment.show(getSupportFragmentManager(),"myFragment");
                dialogFragment.setCancelable(false);

            }
        });

        imgBtnStyleContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(styleLayout.getVisibility() == View.GONE){
                    slideUp(styleLayout);
                }
            }
        });

        imgBtnDayNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),String.valueOf(theme),Toast.LENGTH_LONG).show();
                if(theme){
                    reflowableControl.changeBackgroundColor(backgroundColor);
                    reflowableControl.changeForegroundColor(foregroundColor);
                    theme = false;
                }else{
                    reflowableControl.changeBackgroundColor(Color.BLACK);
                    reflowableControl.changeForegroundColor(Color.WHITE);
                    theme = true;
                }
            }
        });

        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchLayout.getVisibility() == View.GONE) {
//                    reflowableControl.setClickable(false);

                    slideUp(searchLayout);
                }
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchLayout.getVisibility() == View.VISIBLE) {
//                    reflowableControl.setClickable(true);
                    hideSoftKeyboard(ReaderBookActivity.this);
                    slideDown(searchLayout);
                    searchLayout.setVisibility(View.GONE);
                }
            }
        });


        typeface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontsDialogFragment dialogFragment = new FontsDialogFragment();
                dialogFragment.show(getSupportFragmentManager(),"test");
            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NEXT){
                    String keySearch = searchEditText.getText().toString();
                    if(keySearch !=null && keySearch.length() > 0){
                        showIndicator();
                        clearSearchResults(1);
                        reflowableControl.searchKey(keySearch);

                    }
                }
                return false;
            }
        });

        colorsBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_CURRENT_COLOR,backgroundColor);
                bundle.putBoolean(Constants.KEY_STATUS_COLOR,true);
                ColorsDialogFragment dialogFragment = new ColorsDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"colors");
            }
        });

        colorsForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_CURRENT_COLOR,foregroundColor);
                bundle.putBoolean(Constants.KEY_STATUS_COLOR,false);
                ColorsDialogFragment dialogFragment = new ColorsDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"colors");
            }
        });



        setFontSizeBookMinus();
        setFontSizeBookPlus();
        setBrightnessControl();
        eventClickContent();
        setLineSpacingIncrease();
        setLineSpacingDecrease();
        setShareText();

    }

    private void setShareText(){
        Intent shareIntent = new Intent();
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,highlightTrue.text);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
    }

    private void setFontSizeBookMinus() {
        fontMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fontSize < 10) {
                    fontMinus.setEnabled(false);
                    fontMinus.setColorFilter(Color.LTGRAY);
                }else{
                    fontPlus.setEnabled(true);
                    fontPlus.setColorFilter(Color.BLACK);
                    fontSize--;
                    reflowableControl.changeFontSize(fontSize);
                }
            }
        });
    }

    private void setFontSizeBookPlus() {
        fontPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fontSize > 20) {
                    fontPlus.setEnabled(false);
                    fontPlus.setColorFilter(Color.LTGRAY);
                }else{
                    fontMinus.setEnabled(true);
                    fontMinus.setColorFilter(Color.BLACK);
                    fontSize++;
                    reflowableControl.changeFontSize(fontSize);
                }
            }
        });
    }

    private void setLineSpacingIncrease(){
        lineSpacingIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseLineSpace();
            }
        });
    }

    private void setLineSpacingDecrease() {
        lineSpacingDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseLineSpace();
            }
        });
    }

    public void decreaseLineSpace() {
        if(lineSpacing !=0){
            this.lineSpacing--;
            checkLineSpacing();
            reflowableControl.changeLineSpacing(getRealLineSpacing(lineSpacing));
        }
    }
    public void increaseLineSpace() {
        if(lineSpacing !=4){
            this.lineSpacing++;
            checkLineSpacing();
            reflowableControl.changeLineSpacing(getRealLineSpacing(lineSpacing));
        }
    }
    public void checkLineSpacing(){
        if(lineSpacing ==0){
            lineSpacingDecrease.setEnabled(false);
            lineSpacingDecrease.setColorFilter(Color.LTGRAY);
        }else{
            lineSpacingDecrease.setEnabled(true);
            lineSpacingDecrease.setColorFilter(Color.BLACK);
        }

        if(lineSpacing==4){
            lineSpacingIncrease.setEnabled(false);
            lineSpacingIncrease.setColorFilter(Color.LTGRAY);
        }else{
            lineSpacingIncrease.setEnabled(true);
            lineSpacingIncrease.setColorFilter(Color.BLACK);
        }
    }


    private void setBrightnessControl() {
        int cBrightness = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
        brightnessControl.setMax(255);
        brightnessControl.setProgress(cBrightness);


        brightnessControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Context context = getApplicationContext();
                boolean canWrite = Settings.System.canWrite(context);
                if(canWrite){
                    int sBrightness = progress*255/255;
                    Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,sBrightness);
                }else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void eventClickContent() {
        imgBtnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createList(highlightsList,bookmarksList);
                RxBus.publishBookAnnotation(bookAnnotations);
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

    public void slideUp(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                renderRelative.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);

    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        Animation slideD = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_animation);
        styleLayout.getGlobalVisibleRect(viewRect);
        if (isWithinLayoutBounds((int)ev.getRawX(),(int)ev.getRawY())) {
        }else{
            if(styleLayout.getVisibility()==View.VISIBLE){
                isShow = false;
                slideDown(styleLayout);
            }else{
                isShow=true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    boolean isWithinLayoutBounds(int xPoint, int yPoint) {
        int[] l = new int[2];
        styleLayout.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = styleLayout.getWidth();
        int h = styleLayout.getHeight();

        if (xPoint< x || xPoint> x + w || yPoint< y || yPoint> y + h) {
            return false;
        }
        return true;
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
        if(getSupportActionBar() !=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initBookVariableFromDataBase();

        String fileName = book.getTitle()+".epub";
        renderRelative.addView(setUpReflowableController(fileName,"/sdcard/Android/data/licenta.books.androidmobile/files"));

        checkLineSpacing();
    }

    public int getRealLineSpacing(int lineSpacing){
        int space=0;
        if(lineSpacing==0){
            space = 100;
        }else if(lineSpacing==1){
            space = 120;
        }else if(lineSpacing==2){
            space = 140;
        }else if(lineSpacing==3){
            space = 155;
        }else if(lineSpacing==4){
            space = 170;
        }else {
            this.lineSpacing = 1;
            space = 120;
        }
        return space;
    }

    private ReflowableControl setUpReflowableController(String fileName, String baseDirectoryPath) {
        showLog("baseDirectoryPath",baseDirectoryPath);

        reflowableControl = new ReflowableControl(this);

        showLog("fileName", String.valueOf(fileName));

        reflowableControl.setBookName(fileName);

        reflowableControl.setBaseDirectory(baseDirectoryPath);

        reflowableControl.setLicenseKey("04FE-082A-0B15-0CFB");

        reflowableControl.setDoublePagedForLandscape(true);

//        reflowableControl.setLineSpacing(135); // the value is supposed to be percent(%).

        reflowableControl.setHorizontalGapRatio(0.25);

        reflowableControl.setVerticalGapRatio(0.1);

        reflowableControl.setPageTransition(PageTransition.Curl);


        reflowableControl.setFingerTractionForSlide(true);

        reflowableControl.setLineSpacing(this.getRealLineSpacing(this.lineSpacing));

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
        reflowableControl.setSigilStyleEnabled(false);
        reflowableControl.setBookStyleEnabled(true);
        reflowableControl.setBookFontEnabled(false);
        reflowableControl.setFontUnit("px");

//      reflowableControl.setMediaOverlayListener(new MediaOverlayHandler()); //For Audio Book

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

        searchHandler = new SearchHandler();
        reflowableControl.setSearchListener(searchHandler);

        makeIndicator();
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
                        initReflowableControlDisplay(bookState.getFontType(),bookState.getFontSize(),bookState.getPagePosition(),bookState.getPageTransition(),
                                bookState.getThemeState(),bookState.getBackgroundColor(),bookState.getColorText());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Date date = Calendar.getInstance().getTime();
                        pagePosition = 0;
                        fontSize = 15;
                        fontType = "TimesRoman";
                        pageTransition = PageTransition.Curl;
                        theme = false; // false => nu e night mode // true => night mode
                        backgroundColor = Color.WHITE;
                        foregroundColor = Color.BLACK;

                        BookState bookState = new BookState(pagePosition,reflowableControl.getChapterIndex(),date,fontType,fontSize,backgroundColor,foregroundColor,PageTransition.Curl,theme,book.get_id());
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
//                        Toast.makeText(getApplicationContext(),String.valueOf(forDelete),Toast.LENGTH_LONG).show();
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
            case R.id.btn_audio:
                RINGER_MODE = statusRingerMode();
                if(RINGER_MODE == AudioManager.RINGER_MODE_NORMAL){
                    if(textToSpeechInSelection.isSpeaking()){
                        textToSpeechInSelection.stop();
                        setPlayButtonHighlight();
                    }else{
                        speakingInHighlight(textInHighlight);

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please switch from Silent/Vibrate Mode to Normal",Toast.LENGTH_LONG).show();
                }
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

            int verticalCenter = startRect.top +  ((endRect.bottom - startRect.top) / 2);

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

    @Override
    public void onCompleteFonts(Typeface type, String name,int index) {
//        reflowableControl.changeFont(name,fontSize);
        CustomFont customFont = this.getCustomFont(index);
        String namee = customFont.getFullName();
        reflowableControl.changeFont("reeniebeanie.ttf",15);
        typeface.setTypeface(type);
        typeface.setText(name);
        //update in database
    }

    @Override
    public void onCompleteColors(Integer color, boolean status) {
        if(status){
            backgroundColor = color;
            reflowableControl.changeBackgroundColor(color);
            colorsBackground.setBackgroundColor(color);

        }else{
            foregroundColor = color;
            reflowableControl.changeForegroundColor(color);
            colorsForeground.setBackgroundColor(color);

        }
    }

    @Override
    public void onTransferBookAnnotation(BookAnnotations bookAnnotation) {
        if(bookAnnotation.getHighlight()!=null){
            reflowableControl.gotoPageByHighlight(creatorHighlightReverse(bookAnnotation.getHighlight()));
        }
        if(bookAnnotation.getBookmark()!=null){
//            reflowableControl.gotoPageByNavPointIndex(bookAnnotation.getBookmark().getPageIndex());
            reflowableControl.gotoPageByPagePositionInBook(bookAnnotation.getBookmark().getPagePosition());
        }
    }


    private class SelectionHandler implements SelectionListener {

        @Override
        public void selectionStarted(Highlight highlight, Rect rect, Rect rect1) {
            show=true;
            hideSelectionButtonsPopup();
            pageAutoFlip = false;
            textInHighlight = "";

            if(textToSpeechInSelection.isSpeaking()) textToSpeechInSelection.stop();
            if(textToSpeechInPage.isSpeaking()) textToSpeechInPage.stop();
        }

        @Override
        public void selectionChanged(Highlight highlight, Rect rect, Rect rect1) {
            show=true;
            hideSelectionButtonsPopup();

            textInHighlight = "";
        }

        @Override
        public void selectionEnded(Highlight highlight, Rect startRect, Rect endRect) {
            currentHighlight = creatorHighlight(highlight);
            highlightTrue = highlight;
            highlightStartRect = startRect;
            highlightEndRect = endRect;
            isHighlighted=false;

            textInHighlight = highlight.text;

            refreshButtons();
            selectionHighlightButtonsDisplay(startRect, endRect);
        }

        @Override
        public void selectionCancelled() {
            if(isShow){
                showOrHideToolbar();
            }
            hideSelectionButtonsPopup();
            pageAutoFlip=false;

            shutTextToSpeechInPage();
            shutTextToSpeechInSelection();
        }
    }

    private void shutTextToSpeechInPage() {
        if(textToSpeechInPage.isSpeaking()) {
            textToSpeechInPage.stop();
            setPauseButtonTextToSpeech();
        }
    }


    private class StateHandler implements StateListener {

        @Override
        public void onStateChanged(State state) {
            if(state.equals(State.NORMAL)){
                showLog("State =>", "NORMAL");
                hideProgressDialog();
                hideIndicator();
                if(!bookLoadTask) bookLoadTask=true;
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
            show = true;
            textInPage = pageInformation.pageDescription;
            showOrHideToolbar();


            Integer bookmarkCode = getBookmarkCode(pageInformation.chapterIndex,pageInformation.pageIndex);
            Double pagePos = pageInformation.pagePositionInBook;
            Integer chapterIndex = pageInformation.chapterIndex;
            Integer pageIndex = pageInformation.pageIndex;
            String bookmarkPageInfo = "Chapter : " + pageInformation.chapterIndex + " Page In Chapter : " + pageInformation.pageIndex;
            String bookId = book.get_id();
            Integer userId = user.getUserId();
            String chapterName = pageInformation.chapterTitle;
            Bookmark bookmark = new Bookmark(bookmarkCode,pagePos,chapterIndex,pageIndex,bookmarkPageInfo,Calendar.getInstance().getTime(),chapterName,bookId,userId);
            boolean isBookmark = isBookmarkExists(bookmark);


            if(isBookmark){
                menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_black_24dp));
                //aici

            }else{
                menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_border_black_24dp));
            }

            if(textToSpeechInPage.isSpeaking())  textToSpeechInPage.stop();
            if(pageAutoFlip){
                speakingInPage(textInPage);
            }

//            show=true;
//            showOrHideToolbar();
            pagePosition = pageInformation.pagePositionInBook;
            RxBus.publishBookState(new BookState(pagePosition,pageInformation.chapterIndex,Calendar.getInstance().getTime(),fontType,fontSize,backgroundColor,foregroundColor,pageTransition,theme,book.get_id()));

            RxBus.publishBookMark(bookmark);
            Log.d("Text",pageInformation.pageDescription);



        }


        @Override
        public void onChapterLoaded(int i) {
            showLog("Chapter Index", String.valueOf(i));
            NavPoints tocNavPoints = reflowableControl.getNavPoints();
            ArrayList<Chapter> chapterList = new ArrayList<>();

            for(int index = 0 ;index < tocNavPoints.getSize();index++){
                chapterList.add(new Chapter(tocNavPoints.getNavPoint(index).text,tocNavPoints.getNavPoint(index).chapterIndex));
                showLog("Index",String.valueOf(index)+" " + reflowableControl.getNumberOfPagesInChapter());
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
        licenta.books.androidmobile.classes.Highlight highlightDb = new licenta.books.androidmobile.classes.Highlight(highlight.code,highlight.chapterIndex,reflowableControl.getChapterTitle(highlight.chapterIndex),highlight.pagePositionInBook,highlight.pagePositionInChapter,
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
        if(highlightMine.getHighlightedDate()!=null){
            highlight.datetime = TimestampConverter.fromDateToString(highlightMine.getHighlightedDate());
        }
        highlight.forSearch = highlightMine.isForSearch();
        highlight.style = highlightMine.getStyle();
        highlight.pageIndex = highlightMine.getPageIndex();

        return highlight;
    }

    private Highlights creatorHighlightSkyEpub(List<licenta.books.androidmobile.classes.Highlight> arrayList,int chapterIndex){
        chapterHighlightsList = new Highlights();
        if(arrayList==null){
            return chapterHighlightsList;
        }
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
            shutTextToSpeechInSelection();
            licenta.books.androidmobile.classes.Highlight highlightDb = creatorHighlight(highlight);
            highlightMethods.deleteHighlight(highlight.pagePositionInBook,highlight.text,book.get_id());
            loadHighLightsFromDb(user);
        }

        @Override
        public void onHighlightInserted(Highlight highlight) {
            shutTextToSpeechInSelection();
            licenta.books.androidmobile.classes.Highlight highlightDb = creatorHighlight(highlight);
            highlightMethods.insertHighlight(highlightDb);
            loadHighLightsFromDb(user);
        }



        @Override
        public void onHighlightUpdated(Highlight highlight) {
            shutTextToSpeechInSelection();
            Log.d("UpdateH",String.valueOf(highlight.color));
            highlightMethods.updateHighlight(highlightTrue.startIndex,highlightTrue.startOffset,highlightTrue.endIndex,highlightTrue.endOffset,highlightTrue.color,
                    highlightTrue.text,highlightTrue.note,highlightTrue.isNote,highlightTrue.style,
                    book.get_id(),highlightTrue.chapterIndex,highlightTrue.startIndex,highlightTrue.startOffset,highlightTrue.endIndex,highlightTrue.endOffset);
        }

        @Override
        public void onHighlightHit(Highlight highlight, int i, int i1, Rect startRect, Rect endRect) {
            refreshButtons();
            textInHighlight = highlight.text;
            forDelete=false;
            bundleG = new Bundle();
            bundleG.putBoolean(Constants.KEY_HIGHLIGHT_EXISTS,true);
            bundleG.putString(Constants.KEY_NOTE_CONTENT,highlight.note);
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

            highlightTrue = highlight;
            currentHighlight = creatorHighlight(highlight) ;
            currentColor = highlight.color;


            if(!reflowableControl.isPaging()){

                Log.d("intra?","nu");
                dialogFragment = new NoteDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.KEY_HIGHLIGHT_EXISTS,true);
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

    int numberOfSearched = 0;
    private class SearchHandler implements SearchListener {

        @Override
        public void onKeySearched(SearchResult searchResult) {
            addSearchResult(searchResult,0);
        }

        @Override
        public void onSearchFinishedForChapter(SearchResult searchResult) {
            if(searchResult.numberOfSearchedInChapter != 0){
                addSearchResult(searchResult,1);
                reflowableControl.pauseSearch();
                numberOfSearched = searchResult.numberOfSearched;
            }else{
                reflowableControl.searchMore();
                numberOfSearched = searchResult.numberOfSearched;
            }

        }

        @Override
        public void onSearchFinished(SearchResult searchResult) {
            addSearchResult(searchResult,2);
            hideIndicator();
        }
    }




    // SEARCH --------------

    private void clearSearchResults(int mode){
        if(mode == 0){
            hideSoftKeyboard(ReaderBookActivity.this);
            searchResults.clear();
        }else{
            searchResults.clear();
        }
    }

    private void setAdapterSearch(){

        SearchAdapter searchAdapter = new SearchAdapter(this,searchResults);
        searchListview.setAdapter(searchAdapter);
    }

    int i = 0;
    public void addSearchResult(SearchResult searchResult, int mode){
        i++;
        Log.i("CONTOR ",String.valueOf(i));
        Log.i("Mode ",String.valueOf(mode));
        if(mode == 0 ){
            searchResults.add(searchResult);
        }else{
            if(mode==1){
                reflowableControl.searchMore();
                searchResults.add(searchResult);
            }
            searchResults.add(searchResult);
        }
        setAdapterSearch();
    }

    public void makeIndicator() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        searchLayout.addView(progressBar);
        this.hideIndicator();
    }

    public void showIndicator() {
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)progressBar.getLayoutParams();
        params.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideIndicator() {
        if (progressBar!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void shutTextToSpeechInSelection() {
        if (textToSpeechInSelection.isSpeaking()) {
            textToSpeechInSelection.stop();
            setPlayButtonHighlight();
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
        bookmarksList = new ArrayList<>();
        bookmarksList = arrayList;
    }

    private void loadHighlight(List<licenta.books.androidmobile.classes.Highlight> highlights){
        highlightsList = new ArrayList<>();
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
            show = false;
        }else {
            topToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            topToolbar.setVisibility(View.VISIBLE);

            bottomToolbar.animate().translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            bottomToolbar.setVisibility(View.VISIBLE);

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

    private void initReflowableControlDisplay(String fontT,Integer fontS,Double pageP,PageTransition pageTran,Boolean tema,Integer backgroundC,Integer foregroundC){
        fontType = fontT;
        fontSize = fontS;
        if(fontType != null){
            typeface.setText(fontType);
            if(!fontType.equals("TimesRoman")){
                typeface.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),"font/"+fontType.toLowerCase()+".ttf"));
            }
            reflowableControl.setFont(fontType,fontSize);
        }
        pagePosition = pageP;
        pageTransition = pageTran;
        theme = tema;
        backgroundColor = backgroundC;
        foregroundColor = foregroundC;
        //change btn color
        colorsBackground.setBackgroundColor(backgroundC);
        colorsForeground.setBackgroundColor(foregroundC);


//        reflowableControl.changeFontSize(fontS);
        reflowableControl.setFont(fontT,fontS);
        if(theme){
            reflowableControl.changeBackgroundColor(Color.BLACK);
            reflowableControl.changeForegroundColor(Color.WHITE);
        }else{
            reflowableControl.changeBackgroundColor(backgroundC);
            reflowableControl.changeForegroundColor(foregroundC);
        }

        reflowableControl.setStartPositionInBook(pageP);
        reflowableControl.setPageTransition(pageTransition);
    }

    public void createList(List<licenta.books.androidmobile.classes.Highlight> highlights, List<Bookmark> bookmarks){
        bookAnnotations.removeAll(bookAnnotations);
        if(highlights !=null) {
            for (licenta.books.androidmobile.classes.Highlight highlight : highlights) {
                bookAnnotations.add(new BookAnnotations(highlight));
            }
        }
        if(bookmarks !=null){
            for (Bookmark bookmark : bookmarks){
                bookAnnotations.add(new BookAnnotations(bookmark));
            }
        }
        Log.d("Lista:",String.valueOf(bookAnnotations.size()));
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
            if(data.hasExtra("bookAnot")){
                BookAnnotations bookAnnotation = data.getParcelableExtra("bookAnot");
                if(bookAnnotation.getHighlight()!=null){
                    new Handler().postDelayed(() -> reflowableControl.gotoPageByHighlight(creatorHighlightReverse(bookAnnotation.getHighlight())),200);
                }else{
                    new Handler().postDelayed(() -> reflowableControl.gotoPageByPagePositionInBook(bookAnnotation.getBookmark().getPagePosition()),200);
                }

            }
            if(data.hasExtra(Constants.KEY_CHAPTER)) {
                if (data.getIntExtra(Constants.KEY_CHAPTER, 0) - 1 < 0) {
                    reflowableControl.gotoPageByNavPointIndex(0);
                } else {
                    reflowableControl.gotoPageByNavPointIndex(data.getIntExtra(Constants.KEY_CHAPTER, 0));
                }


            }
            insertBookState(subscribeBookState());

        }
    }

    public void makeFonts() {
        fonts.clear();
        for (int i = 0 ;i< Constants.TYPEFACE_NAMES.length;i++){
            fonts.add(0,new CustomFont(Constants.TYPEFACE_NAMES[i],""));
        }
        this.fonts.addAll(app.customFonts);
    }

    // CustomFont
    public CustomFont getCustomFont(int fontIndex) {
        if (fontIndex<0) fontIndex = 0;
        if (fontIndex>(fonts.size()-1)) fontIndex = fonts.size()-1;
        return fonts.get(fontIndex);
    }


    public void fontSelected(int index) {
        CustomFont customFont = this.getCustomFont(index);
        String name = customFont.getFullName();
        if (!settings.fontName.equalsIgnoreCase(name)) {
            settings.fontName = name;
//            checkSettings();
            Log.d("font name ",settings.fontName);
            reflowableControl.changeFont(settings.fontName,fontSize);
        }
    }






        //AUDIO MEDIA PLAYER ->>>>>>>>>>>>>>>>>>>>>
        private  void shutDownTTS() {
            if( textToSpeechInSelection !=null) {

                textToSpeechInSelection.stop();
                textToSpeechInSelection.shutdown();
            }
            if(textToSpeechInPage !=null){
                textToSpeechInPage.stop();
                textToSpeechInPage.shutdown();
            }
        }



    private void prepareAndroidTTSinPage(final String textInPage){
        textToSpeechInPage = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    speakingInPage(textInPage);
                }else{
                    showLog("Error TTS", "Error ");
                }
            }
        });

        textToSpeechInPage.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                pageAutoFlip=true;
                setPlayButtonTextToSpeech();
            }

            @Override
            public void onDone(String utteranceId) {
                if(pageAutoFlip){
                    goToNextPageInChapter();
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    private void prepareAndroidTTSinHighlight(final String textInHighlight){
        textToSpeechInSelection = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    speakingInHighlight(textInHighlight);
                }else{
                    showLog("Error TTS", "Error ");
                }
            }
        });

        textToSpeechInSelection.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                setPauseButtonHighlight();
            }

            @Override
            public void onDone(String utteranceId) {
                setPlayButtonHighlight();
            }

            @Override
            public void onError(String utteranceId) {
                setPlayButtonHighlight();
            }
        });
    }


    private void setPlayButtonHighlight(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audioHighlightBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_10dp));
            }
        });
    }

    private void setPauseButtonHighlight(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audioHighlightBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            }
        });
    }


    private void setPlayButtonTextToSpeech(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playPauseItem != null) {
                    playPauseItem.setIcon(R.drawable.ic_pause_circle_outline_black_32dp);
                }
            }
        });
    }

    private void setPauseButtonTextToSpeech(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playPauseItem != null) {
                    playPauseItem.setIcon(R.drawable.ic_play_circle_outline_black_32dp);
                }
            }
        });
    }

    private void goToNextPageInChapter(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reflowableControl.gotoNextPageInChapter();
            }
        });
    }

    private int statusRingerMode(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
            return AudioManager.RINGER_MODE_SILENT;
        }else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            return AudioManager.RINGER_MODE_VIBRATE;
        }
        return AudioManager.RINGER_MODE_NORMAL;
    }

    private void speakingInPage(String textInPage){
        if(bookLoadTask){
            if(textToSpeechInPage.isSpeaking()){
                textToSpeechInPage.stop();
                setPauseButtonTextToSpeech();
                pageAutoFlip=false;
            }else{
                textToSpeechInPage.setLanguage(Locale.US);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    textToSpeechInPage.speak(textInPage,TextToSpeech.QUEUE_ADD,null,utteranceIdForPage);
                }else{
                    textToSpeechInPage.speak(textInPage,TextToSpeech.QUEUE_ADD,null);
                }
            }
        }
    }

    private void speakingInHighlight(String textInHighlight){
        if(textToSpeechInSelection.isSpeaking()){
            textToSpeechInSelection.stop();
            setPlayButtonHighlight();
        } else {
            textToSpeechInSelection.setLanguage(Locale.ENGLISH);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                textToSpeechInSelection.speak(textInHighlight,TextToSpeech.QUEUE_ADD,null,utteranceIdForTextInHighlight);
            }else {
                textToSpeechInSelection.speak(textInHighlight,TextToSpeech.QUEUE_ADD,null);
            }
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
            case R.id.text_to_speech:
                playPauseItem = item;
                RINGER_MODE = statusRingerMode();
                if(RINGER_MODE == AudioManager.RINGER_MODE_NORMAL){
                    if(textToSpeechInPage.isSpeaking()){
                        textToSpeechInPage.stop();
                        setPauseButtonTextToSpeech();
                    }else{
                        speakingInPage(textInPage);
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Please switch from Silent/Vibrate Mode to Normal",Toast.LENGTH_LONG).show();
                }
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
        shutDownTTS();
        super.onBackPressed();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Disposable d = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                book = bookE;
                Log.d("Revine: ",book.getTitle());
            }
        });
        d.dispose();
    }
}
