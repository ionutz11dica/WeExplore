package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.skytree.epub.PagingInformation;
import com.skytree.epub.PagingListener;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SearchListener;
import com.skytree.epub.SearchResult;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.State;
import com.skytree.epub.StateListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import licenta.books.androidmobile.activities.DialogFragments.ColorsDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.FontsDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.NoteDialogFragment;
import licenta.books.androidmobile.activities.others.BookAnnotations;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.activities.others.HelperApp;
import licenta.books.androidmobile.activities.others.HelperSettings;
import licenta.books.androidmobile.activities.others.ShakeDetector;
import licenta.books.androidmobile.adapters.SearchAdapter;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.Chapter;
import licenta.books.androidmobile.classes.Converters.ArrayIntegerConverter;
import licenta.books.androidmobile.classes.Converters.TimestampConverter;
import licenta.books.androidmobile.classes.CycleDay;
import licenta.books.androidmobile.classes.Estimator;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookStateDao;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.EstimatorDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DaoMethods.BookStateMethods;
import licenta.books.androidmobile.database.DaoMethods.BookmarkMethods;
import licenta.books.androidmobile.database.DaoMethods.EstimatorMethods;
import licenta.books.androidmobile.database.DaoMethods.HighlightMethods;
import licenta.books.androidmobile.fragments.AnnotationFragment;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.readingEstimator.AverageIndicators;
import licenta.books.androidmobile.patterns.readingEstimator.ChapterIndexes;
import licenta.books.androidmobile.patterns.readingEstimator.DifficultyRead;
import licenta.books.androidmobile.patterns.readingEstimator.GunningFogFormula;


public class ReaderBookActivity extends AppCompatActivity implements View.OnClickListener, NoteDialogFragment.OnCompleteListener,
        FontsDialogFragment.OnCompleteListenerFonts, ColorsDialogFragment.OnCompleteListenerColor, AnnotationFragment.OnFragmentInteractionListener  {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;


    GunningFogFormula difficultyRead;
    //Shaker
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    AverageIndicators averageIndicators;
    LinearLayout estimationLayout;
    TextView tvEstimation;
    Button btnDifficulty;

    int noOfWatchedPage = 0 ;
    int sumOfWordsPerWatchedPage = 0;
    int chapterIndex = 0;
    ArrayList<Integer> chapterIndexes;
    ArrayList<Integer> watchedPages = new ArrayList<>();


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
    EstimatorDao estimatorDao;
    EstimatorMethods estimatorMethods;

    licenta.books.androidmobile.classes.Highlight currentHighlight;
    Highlight highlightTrue;

    boolean show = true;
    BookE book;
    User user;
    Menu menuItems;

    Rect highlightStartRect, highlightEndRect;
    BookState bookStatePermanent;
    Bookmark bookmark;


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
    PagingListener pagingListener;


    //Highlight colors
    Button highlightColorYellow;
    Button highlightColorGreen;
    Button highlightColorPink;
    Button highlightColorBlue;
    Button highlightColorOrange;
    int currentColor;
    Button audioHighlightBtn;
    Button shareSelectedText;
    Button searchSelectedText;

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
    ArrayList<Estimator> estimatorArrayList = new ArrayList<>();
    ArrayList<Long> times = new ArrayList<>();
    ArrayList<Long> timesPerPage;


    HelperApp app;
    HelperSettings settings;
    int lineSpacing;
    TextView footer;
    View footerView;


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
    private boolean resume = false;

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
        initShaker();
    }

    private void initComp(){
        settings = new HelperSettings(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        bookStatePermanent = new BookState();


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
        seekBarPager = findViewById(R.id.reader_seekBar);
        tvPage = findViewById(R.id.reader_tv_page);

        //highlight buttons
        highlightColorYellow = findViewById(R.id.highlight_yellow);
        highlightColorGreen = findViewById(R.id.highlight_green);
        highlightColorPink = findViewById(R.id.highlight_pink);
        highlightColorBlue = findViewById(R.id.highlight_blue);
        highlightColorOrange = findViewById(R.id.highlight_orange);
        audioHighlightBtn = findViewById(R.id.btn_audio);
        shareSelectedText = findViewById(R.id.btn_share);
        searchSelectedText = findViewById(R.id.btn_search);

        highlightColorYellow.setOnClickListener(this);
        highlightColorGreen.setOnClickListener(this);
        highlightColorPink.setOnClickListener(this);
        highlightColorBlue.setOnClickListener(this);
        highlightColorOrange.setOnClickListener(this);
        audioHighlightBtn.setOnClickListener(this);
        shareSelectedText.setOnClickListener(this);
        searchSelectedText.setOnClickListener(this);

        //style layout
        styleLayout = findViewById(R.id.relative_test);
        styleLayout.setVisibility(View.GONE);
        styleLayout.setClickable(true);
        typeface = findViewById(R.id.typeface_btn);

        //shake layout
        estimationLayout = findViewById(R.id.estimation_layout);
        estimationLayout.setVisibility(View.GONE);
        estimationLayout.setClickable(true);

//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(estimationLayout);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//        bottomSheetBehavior.setPeekHeight(340);
//
//        bottomSheetBehavior.setHideable(false);

        tvEstimation = findViewById(R.id.tv_estimation);
        btnDifficulty = findViewById(R.id.grade_difficulty);


        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setVisibility(View.GONE);
        searchLayout.setClickable(true);
        searchEditText = findViewById(R.id.et_search);
        cancelTextView = findViewById(R.id.tv_search_cancel);
        searchListview = findViewById(R.id.lv_search_items);

        footerView =  ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        footerView.setClickable(true);
        footer = footerView.findViewById(R.id.footer_1);
        footer.setOnClickListener(this);
        footer.setClickable(true);

        brightnessControl = findViewById(R.id.brightness_seeker);
        fontMinus = findViewById(R.id.font_minus);
        fontPlus = findViewById(R.id.font_plus);

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
                    if(keySearch.length() > 0){
                        textSearchMethods(keySearch);
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
        setFooterListner();
        setSearchedItem();
        setSearchText();
//        onTouchEstimationView();
    }

    private void textSearchMethods(String keySearch) {
        showIndicator();
        hideProgressDialog();
        clearSearchResults(1);
        hideSelectionButtonsPopup();
        reflowableControl.searchKey(keySearch);
    }

    private void setShareText(){
        Intent shareIntent = new Intent();
        shareSelectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,highlightTrue.text);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
    }

    private void setSearchText(){

        searchSelectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSelected = highlightTrue.text;
                searchEditText.setText(textSelected);
                slideUp(searchLayout);
                textSearchMethods(textSelected);
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
                    Estimator.SECOND_INFERIOR_LIMIT +=1000;
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
                    Estimator.SECOND_INFERIOR_LIMIT -=1000;
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
            Estimator.SECOND_INFERIOR_LIMIT +=800;
            checkLineSpacing();
            reflowableControl.changeLineSpacing(getRealLineSpacing(lineSpacing));
        }
    }
    public void increaseLineSpace() {
        if(lineSpacing !=4){
            this.lineSpacing++;
            Estimator.SECOND_INFERIOR_LIMIT -=800;
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
        Disposable d = RxBus.subscribeBook(bookE -> book = bookE);
        d.dispose();

//        book = getIntent().getParcelableExtra(Constants.KEY_BOOK);
//        user = getIntent().getParcelableExtra(Constants.KEY_USER);

        Disposable disp = RxBus.subscribeUser(userr -> {
            user = userr;
            loadBookMarkFromDb(userr);
            loadHighLightsFromDb(userr);
        });
        disp.dispose();



        topToolbar.setTitle(book.getTitle());
        topToolbar.setTitleTextColor(Color.WHITE);
        topToolbar.setSubtitle(convertFromArray(book.getAuthors()));
        topToolbar.setSubtitleTextColor(Color.WHITE);
        topToolbar.setTitleTextAppearance(getApplicationContext(),R.style.CrimsonTextAppearance);
        topToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.CrimsonTextAppearance);
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
            space = 120;
        }else if(lineSpacing==1){
            space = 135;
        }else if(lineSpacing==2){
            space = 140;
        }else if(lineSpacing==3){
            space = 155;
        }else if(lineSpacing==4){
            space = 170;
        }else {
            this.lineSpacing = 1;
            space = 135;
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

        reflowableControl.setLineSpacing(135); // the value is supposed to be percent(%).

        reflowableControl.setHorizontalGapRatio(0.25);

        reflowableControl.setVerticalGapRatio(0.1);

        reflowableControl.setBookFontEnabled(true);

        reflowableControl.setPageTransition(PageTransition.Curl);

        reflowableControl.setFingerTractionForSlide(true);

        reflowableControl.setGlobalPagination(false);

//        reflowableControl.setLineSpacing(this.getRealLineSpacing(this.lineSpacing));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        reflowableControl.setLayoutParams(params);

        SkyProvider skyProvider = new SkyProvider();



        reflowableControl.setContentProvider(skyProvider);

        reflowableControl.useDOMForHighlight(false);

        reflowableControl.setNavigationAreaWidthRatio(0.0f); // both left and right side.

        //To get text of page in pageMovedListener in pageDescription
        reflowableControl.setGlobalOffset(true);
        reflowableControl.setExtractText(true);
        reflowableControl.setSigilStyleEnabled(false);
        reflowableControl.setBookStyleEnabled(true);
        reflowableControl.setBookFontEnabled(true);
        reflowableControl.setFontUnit("px");
        reflowableControl.setAutoAdjustContent(true);
//        reflowableControl.setFont("simplicity!!!/fonts/simplicity.ttf",15);

        if (this.getMaxSize()<=1280) {
            reflowableControl.setCurlQuality(1.0f);
        }else if (this.getMaxSize()<=1920) {
            reflowableControl.setCurlQuality(0.9f);
        }else {
            reflowableControl.setCurlQuality(0.8f);
        }

        reflowableControl.setBringDelayTime(500);
        // reloadDelayTime(default 100) is used for delay before reload (eg. changeFont, loadChapter or etc)
        reflowableControl.setReloadDelayTime(100);

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

        pagingListener = new PagingHandler();
        reflowableControl.setPagingListener(pagingListener);

        makeIndicator();

        return reflowableControl;
    }

    private void initShaker(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();

        shakeDetector.setOnShakeListener(count -> {
            if(estimationLayout.getVisibility()== View.GONE){
                slideUp(estimationLayout);
            }else{
                slideDown(estimationLayout);
            }

        });
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
    public int getMaxSize() {
        int width = this.getRawWidth();
        int height= this.getRawHeight();
        return Math.max(width,height);
    }

    @SuppressLint("NewApi")
    public int getRawWidth() {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
            return width;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

    @SuppressLint("NewApi")
    public int getRawHeight() {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");
                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return height;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
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
                moveSelectionButtonPopupToBellow(endRect.bottom + 30,
                        reflowableControl.getWidth() - selectionButtonsPopup.getWidth());
            }
        }else if(startRect.top > (reflowableControl.getHeight()/2)&& endRect.bottom > (selectionButtonsPopup.getHeight()/2)){
            if ((reflowableControl.getWidth() - startRect.left) > selectionButtonsPopup.getWidth()) {
                moveSelectionButtonPopupToUpper(startRect.top - selectionButtonsPopup.getHeight() - 30, startRect.left);
            } else {
                moveSelectionButtonPopupToUpper(startRect.top - selectionButtonsPopup.getHeight() - 30,
                        reflowableControl.getWidth() - selectionButtonsPopup.getWidth());
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
        int x=3;
    }

    @Override
    public void onTransferHighlightsList(ArrayList<licenta.books.androidmobile.classes.Highlight> highlights) {
        int y = 3;
    }

    @Override
    public void onCompleteFonts(Typeface typeface, String name,CustomFont customFont) {
        String nameFile = customFont.getFullName();
        if(!fontType.equalsIgnoreCase(nameFile)){
            settings.fontName = nameFile;

            reflowableControl.changeFont(settings.fontName,fontSize);
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
                hideIndicator();
                hideProgressDialog();
                if(!bookLoadTask) bookLoadTask=true;
            } else if(state.equals(State.LOADING)) {
                showLog("State =>", "LOADING");
                showProgressDialog("Loading","Please wait..",false);
//                showIndicator();
            }else if (state.equals(State.ROTATING)){
                showLog("State =>", "ROTATING");
                showProgressDialog("Loading","Please wait..",false);
//                showIndicator();
            }else if (state.equals(State.BUSY)){
                showLog("State =>", "BUSY");
                showProgressDialog("Loading","Please wait..",false);
//                showIndicator();

            }
        }
    }

String test= "ceva";

    private class PagerMoveHandler implements PageMovedListener {

        @Override
        public void onPageMoved(PageInformation pageInformation) {
            addTimesPerPage(System.currentTimeMillis());
            setSeekBarPager(pageInformation);

            show = true;
            textInPage = pageInformation.pageDescription;
            showOrHideToolbar();
            hideSelectionButtonsPopup();


            setChapterIndexes(pageInformation);
            setSumOfWordsPerWatchedPage(pageInformation);
            addToEstimatorList(createEstimatorPerPage(pageInformation.pageDescription));
            setEstimationAndDifficulty(pageInformation);

            Bookmark bookmark = getBookmark(pageInformation);

            if(textToSpeechInPage.isSpeaking())  textToSpeechInPage.stop();
            if(pageAutoFlip) speakingInPage(textInPage);

            pagePosition = pageInformation.pagePositionInBook;
            setStateBookOnPage(pageInformation);
            RxBus.publishBookState(bookStatePermanent);

            RxBus.publishBookMark(bookmark);
        }


        @Override
        public void onChapterLoaded(int i) {
            chapterIndex = i;

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

                RxBus.publishsChapterName("Chapter "+i);//probleme aici
                RxBus.publishsChapter(reflowableControl.getChapterIndex());
            }
        }
        @Override
        public void onFailedToMove(boolean b) {
            showLog("Fail?", String.valueOf(b));
        }
    }

    private Bookmark getBookmark(PageInformation pageInformation) {
        Integer bookmarkCode = getBookmarkCode(pageInformation.chapterIndex,pageInformation.pageIndex);
        Double pagePos = pageInformation.pagePositionInBook;
        Integer chapterIndex = pageInformation.chapterIndex;
        Integer pageIndex = pageInformation.pageIndex;
        String bookmarkPageInfo = "Chapter : " + pageInformation.chapterIndex + " Page In Chapter : " + pageInformation.pageIndex;
        String bookId = book.get_id();
        Integer userId = user.getUserId();
        String chapterName = pageInformation.chapterTitle;
        Bookmark bookmark = new Bookmark(bookmarkCode,pagePos,chapterIndex,pageIndex,bookmarkPageInfo, Calendar.getInstance().getTime(),chapterName,bookId,userId);
        boolean isBookmark = isBookmarkExists(bookmark);


        if(isBookmark){
            menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_white));
        }else{
            menuItems.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_bookmark_border_black_24dp));
        }
        return bookmark;
    }

    private void setStateBookOnPage(PageInformation pageInformation){
        bookStatePermanent.setPagePosition(pagePosition);
        bookStatePermanent.setNoChapter(pageInformation.chapterIndex);
        bookStatePermanent.setReadDate(Calendar.getInstance().getTime());
        bookStatePermanent.setFontType(fontType);
        bookStatePermanent.setFontSize(fontSize);
        bookStatePermanent.setBackgroundColor(backgroundColor);
        bookStatePermanent.setColorText(foregroundColor);
        bookStatePermanent.setPageTransition(pageTransition);
        bookStatePermanent.setThemeState(theme);
        bookStatePermanent.setBookId(book.get_id());
    }











    //ESTIMATOOOOOOOOOR


    public Estimator createEstimatorPerPage( String textPage ){

        if( textPage == null || DifficultyRead.getNoOfWords(textPage) < 100 ){
            return null;
        }
        Estimator estimator = new Estimator();
//        int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        if(timeOfDay >=0 && timeOfDay < 12){
//            estimator.setCycleDay(CycleDay.MORNING);
//        }else if(timeOfDay >= 12 && timeOfDay < 18){
//            estimator.setCycleDay(CycleDay.AFTERNOON);
//        }else if(timeOfDay >= 18 && timeOfDay <= 24){
//            estimator.setCycleDay(CycleDay.EVENING);
//        }
        difficultyRead = new DifficultyRead(textPage);
        System.out.println("Gunning fog score: "+difficultyRead.getGunningFog());

        estimator.setGunningFogScore(difficultyRead.getGunningFog());

        return estimator;
    }

    private synchronized void fetchAverageIndicators(){
        Integer cycleDay = null;
        int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >=0 && timeOfDay < 12){
            cycleDay = 0;
        }else if(timeOfDay >= 12 && timeOfDay < 18){
            cycleDay = 1;
        }else if(timeOfDay >= 18 && timeOfDay <= 24){
            cycleDay = 2;
        }
        Single<AverageIndicators> averageIndicatorsSingle = estimatorMethods.fetchAverageIndicators(book.get_id(),user.getUserId());
        averageIndicatorsSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AverageIndicators>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(AverageIndicators averageTime) {
                        estimationForChapter(averageTime);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private synchronized void fetchChapterIndexes(){
        Single<ChapterIndexes> arrayListSingle = estimatorMethods.fetchWatchedChapterIndexes(book.get_id(),user.getUserId());
        arrayListSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ChapterIndexes>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(ChapterIndexes integers) {
                        setChapterIndexes(integers.chapterList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        setChapterIndexes(new ArrayList<>());
                    }
                });
    }

    public void setChapterIndexes(ArrayList<Integer> arrayList){
        chapterIndexes = arrayList;
    }

    private void estimationForChapter(AverageIndicators averageIndicators){
        this.averageIndicators = averageIndicators;
        this.sumOfWordsPerWatchedPage = averageIndicators.sumWords;
        this.noOfWatchedPage = averageIndicators.sumPages;
        System.out.println(averageIndicators.avgGFS + " --- " + averageIndicators.avgTime);

    }

    @SuppressLint("SetTextI18n")
    private void setEstimationAndDifficulty(PageInformation information){
        averageIndicators.sumPages = noOfWatchedPage;
        averageIndicators.sumWords = sumOfWordsPerWatchedPage;
//        double GFS = 0;
//        for (Estimator est : estimatorArrayList) {
//            GFS += est.getGunningFogScore();
////                cycleDay = est.getCycleDay();
//        }
//        GFS = GFS/estimatorArrayList.size();
//        if(averageIndicators.avgGFS !=0){
//            averageIndicators.avgGFS = (GFS + averageIndicators.avgGFS)/2;
//        }else{
//            averageIndicators.avgGFS = GFS;
//        }

        long estimatedTime =(long) Estimator.chapterEstimator(information,averageIndicators);
        int hours   = (int) ((estimatedTime / (60*60)) % 24);
        int minutes = (int) ((estimatedTime / (60)) % 60);
        int seconds = (int) (estimatedTime ) % 60 ;

        tvEstimation.setText(String.valueOf(hours)+"hours "+String.valueOf(minutes)+"minutes "+String.valueOf(seconds)+ "seconds");
        btnDifficulty.setText(String.valueOf(DifficultyRead.round(averageIndicators.avgGFS,3)));
        btnDifficulty.setBackgroundDrawable(changeDrawableColor(getDifficultyColor(8)));
    }

    public Drawable changeDrawableColor( int newColor) {
        Drawable mDrawable=null;

        mDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded).mutate();

        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    private int getDifficultyColor(double grad){
        int lv = (int) Math.round(grad *5);
//        int R = (255 * lv)/100;
        int R = 2 * lv;
        int G = (255 * (100 - lv))/100;
        int B = 0;

       return Color.rgb(R,G,B);
    }


    public void addToEstimatorList(Estimator estimator){
        if(estimator !=null){
            estimatorArrayList.add(estimator);
        }
    }

    public void addTimesPerPage(long currentMillis){
        times.add(currentMillis);
    }


    public void createTimePerPage(){
        if(times.size() > 1) {
            timesPerPage = new ArrayList<>();
            for (int i = 0; i < times.size() - 1; i++) {
                if (times.get(i + 1) - times.get(i) >= Estimator.SECOND_INFERIOR_LIMIT && times.get(i + 1) - times.get(i) <= Estimator.SECOND_SUPERIOR_LIMIT) {
                    timesPerPage.add(times.get(i + 1) - times.get(i));
                }
            }
        }
    }


    public Estimator finalEstimator(){
        Estimator estimator=null;
        double GFS = 0;
        for (Estimator est : estimatorArrayList) {
            GFS += est.getGunningFogScore();
//                cycleDay = est.getCycleDay();
        }
        GFS = GFS/estimatorArrayList.size();
        if(averageIndicators.avgGFS !=0) {
            GFS = (GFS + averageIndicators.avgGFS) / 2;
        }
        CycleDay cycleDay = null;
        if(estimatorArrayList.size() > 0 && averageTimePerPage() > 0){
            estimator = new Estimator();

            estimator.setGunningFogScore(GFS);
            estimator.setTimePerPage(averageTimePerPage());
            estimator.setTotalOfWatchedWords(sumOfWordsPerWatchedPage);
            estimator.setTotalOfWatchedPages(noOfWatchedPage);
            estimator.setChapterIndexes(chapterIndexes);
            estimator.setBookId(book.get_id());
            estimator.setUserId(user.getUserId());
        }else{
            estimatorMethods.updateIndicators(sumOfWordsPerWatchedPage,noOfWatchedPage, GFS,ArrayIntegerConverter.fromArrayList(chapterIndexes),book.get_id(),user.getUserId());
        }
        return estimator;
    }

    public long averageTimePerPage(){
        long sum = 0;
        if(timesPerPage !=null && timesPerPage.size()>0) {
            for (Long l : timesPerPage) {
                sum += l;
            }
            sum = sum/timesPerPage.size();
        }
        return sum;
    }

    public void setSumOfWordsPerWatchedPage(PageInformation information){
        int code = uniqueCodePage(information.pageIndex);
//        && !chapterIndexes.contains(information.chapterIndex)
        if(!watchedPages.contains(code) ){
            watchedPages.add(code);
            sumOfWordsPerWatchedPage += DifficultyRead.getNoOfWords(information.pageDescription);
            setNoOfWatchedPage();
        }
    }

    public void setNoOfWatchedPage(){
        noOfWatchedPage += 1;
    }

    public int uniqueCodePage(int pageIndex){
        int code = pageIndex*chapterIndex - chapterIndex*99;

        return Math.abs(code);
    }

    public void setChapterIndexes(PageInformation information){
        if(!chapterIndexes.contains(information.chapterIndex) && information.pageIndex > Math.round(information.numberOfPagesInChapter/2)){
            chapterIndexes.add(information.chapterIndex);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onTouchEstimationView(){
        estimationLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float startY=0;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endY = event.getY();
                        if(endY > startY){
                            slideDown(estimationLayout);
                        }
                }
                return true;
            }
        });
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
                if(arrayList.get(i).getHighlightedDate()!=null) {
                    highlight.datetime = TimestampConverter.fromDateToString(arrayList.get(i).getHighlightedDate());
                }
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

    private class PagingHandler implements PagingListener{

        @Override
        public void onPagingStarted(int i) {
            tvPage.setText("Loading page..");
        }

        @Override
        public void onPaged(PagingInformation pagingInformation) {
            tvPage.setText("Loading page..");
        }

        @Override
        public void onPagingFinished(int i) {

        }

        @Override
        public int getNumberOfPagesForPagingInformation(PagingInformation pagingInformation) {
            return pagingInformation.numberOfPagesInChapter;
        }
    }

    private class SeekBarHandler implements SeekBar.OnSeekBarChangeListener {
        PageInformation information;

        public void setInformation(PageInformation information){
            this.information = information;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int pagePosBook;

            PageInformation pageInformation;
            if(reflowableControl.isGlobalPagination()){
                int pos = progress;
//                pagePosBook =reflowableControl.getPagePositionByPageIndexInBook(pos);
//                pageInformation = reflowableControl.getPageInformation(pagePosBook);
            }else{
                pagePosBook = reflowableControl.getPageIndexInChapter();
                pageInformation = reflowableControl.getPageInformationByPageIndex(pagePosBook);
            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int position = seekBar.getProgress();
            int chapterIndex = reflowableControl.getChapterIndex();
            if(reflowableControl.isGlobalPagination()){
                int pos = position;
                int  pagePosBook = reflowableControl.getPageIndexInChapter();
                reflowableControl.gotoPageInChapter(pagePosBook);
            }else{
//                double pageDelta = ((1.0f/information.numberOfChaptersInBook)/information.numberOfPagesInChapter);
////                double pagePosBook = information.pagePositionInBook+information.pagePositionInChapter/(position*5);
//                reflowableControl.gotoPageByPagePositionInBook(pageDelta);
                reflowableControl.gotoPageInChapter(position-1);
            }

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
                addFooterToSearchListView();
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
            footer.setText("Search finished");
            addSearchResult(searchResult,2);
            hideIndicator();
            hideProgressDialog();

        }
    }

    private void addFooterToSearchListView(){
        footer.setText("Search more");
        searchListview.addFooterView(footerView,null,false);
    }

    private void setFooterListner(){

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Footer:"," footer clicked");
//                removeLastResult();
                reflowableControl.searchMore();
            }
        });
    }

    public void setSearchedItem(){
        searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reflowableControl.gotoPageBySearchResult(searchResults.get(position),Color.parseColor("#efb1ff"));
                slideDown(searchLayout);
            }
        });
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

//        moveSearchScrollViewToEnd(searchAdapter);
    }

    int i = 0;
    public void addSearchResult(SearchResult searchResult, int mode){
        i++;
        Log.i("CONTOR ",String.valueOf(i));
        Log.i("Mode ",String.valueOf(mode));
        if(mode == 0 ){
            searchResults.add(searchResult);
        }
        setAdapterSearch();
    }

    public void makeIndicator() {
//        renderRelative = new RelativeLayout(this);
//        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
//        renderRelative.setLayoutParams(rlp);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        progressBar.setBackgroundColor(Color.LTGRAY);
        searchLayout.addView(progressBar);
        this.hideIndicator();
    }

    @SuppressLint("SetTextI18n")
    private void setSeekBarPager(PageInformation information){
        SeekBarHandler seekBarHandler = new SeekBarHandler();
        seekBarHandler.setInformation(information);
        seekBarPager.setOnSeekBarChangeListener(seekBarHandler);
        if(!reflowableControl.isGlobalPagination()){
            seekBarPager.setMax(information.numberOfPagesInChapter);
            seekBarPager.setProgress(information.pageIndex+1);
            tvPage.setText(information.pageIndex+1+"/" +String.valueOf(information.numberOfPagesInChapter)+ "Pages" );
        }
    }


    public void showIndicator() {
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)progressBar.getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_IN_PARENT,-1);
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
            menuItems.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_white));
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
                typeface.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),"font/"+"simplicity"+".ttf"));
            }
            if(fontSize>15){
                Estimator.SECOND_INFERIOR_LIMIT -= (fontSize-15)*800;
            }else{
                Estimator.SECOND_INFERIOR_LIMIT += (15-fontSize)*800;
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

    private void insertEstimation(Estimator estimator){
        if(estimator!=null){
            estimatorMethods.insertEstimation(estimator);
        }
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
        estimatorDao = AppRoomDatabase.getInstance(getApplicationContext()).getEstimatorDao();
        estimatorMethods = EstimatorMethods.getInstance(estimatorDao);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_CODE_CHAPTER && RESULT_OK == resultCode && data!=null){
            if(data.hasExtra(Constants.KEY_HIGHLIGHTS_DELETED)){
                ArrayList<licenta.books.androidmobile.classes.Highlight> highlightsDeleted  = data.getParcelableArrayListExtra(Constants.KEY_HIGHLIGHTS_DELETED);
//                Highlights highlightsForDeleted = creatorHighlightSkyEpub(highlightsDeleted,reflowableControl.getChapterIndex());
                creatorHighlightSkyEpub(highlightsDeleted,reflowableControl.getChapterIndex());
                for(int i = 0 ;i < chapterHighlightsList.getSize();i++){
                    reflowableControl.deleteHighlight(chapterHighlightsList.getHighlight(i));

                }
            }
            if(data.hasExtra(Constants.KEY_BOOK_ANNOTATION)){
                BookAnnotations bookAnnotation = data.getParcelableExtra(Constants.KEY_BOOK_ANNOTATION);
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
            Log.d("fonts name ",settings.fontName);
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
//                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);
                shutDownTTS();
                finish();
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
        resume = true;
        sensorManager.unregisterListener(shakeDetector);
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
        createTimePerPage();
        insertEstimation(finalEstimator());
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, accelerometer,	SensorManager.SENSOR_DELAY_UI);
        Intent intent = new Intent();
        Disposable d = RxBus.subscribeBook(bookE -> {
            book = bookE;
            Log.d("Revine: ",book.getTitle());
        });
        d.dispose();

        Disposable d2 = RxBus.subscribeUser(userr-> {
            user = userr;
//            Log.d("Revine: ",book.getTitle());
        });
        d2.dispose();

        fetchAverageIndicators();
        fetchChapterIndexes();
        if(resume) {
            intent = getIntent();
            loadHighLightsFromDb(user);
            loadBookMarkFromDb(user);
            if(intent.hasExtra(Constants.KEY_HIGHLIGHTS_DELETED)){
                ArrayList<licenta.books.androidmobile.classes.Highlight> highlightsForDelete = intent.getParcelableArrayListExtra(Constants.KEY_HIGHLIGHTS_DELETED);
                creatorHighlightSkyEpub(highlightsForDelete,reflowableControl.getChapterIndex());
                for(int i = 0 ;i < highlightsForDelete.size();i++){
                    reflowableControl.deleteHighlight(chapterHighlightsList.getHighlight(i));
                }
            }
        }
    }
}
