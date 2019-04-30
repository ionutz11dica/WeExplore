package licenta.books.androidmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SkyProvider;

public class Test2 extends AppCompatActivity {
    ReflowableControl reflowableControl;
    RelativeLayout renderRelative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        renderRelative = findViewById(R.id.readerRelativeLayout);

        renderRelative.addView(setUpReflowableController("Alice in WonderWorld.epub","/sdcard/Android/data/licenta.books.androidmobile/files"));
//

    }

    private ReflowableControl setUpReflowableController(String fileName, String baseDirectoryPath) {
        showLog("baseDirectoryPath",baseDirectoryPath);
        reflowableControl = new ReflowableControl(getApplicationContext());


//        fileName = prepareFileNameWithWhiteSpaceReplacement(fileName);

//        showLog("fileName", String.valueOf(fileName));

        reflowableControl.setBookName(fileName);
        reflowableControl.setBaseDirectory(baseDirectoryPath);
        reflowableControl.setLicenseKey("0000-0000-0000-0000");

//        reflowableControl.setLicenseKey("04FE-082A-0B15-0CFB");
        reflowableControl.setDoublePagedForLandscape(true);

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
        reflowableControl.setStartPositionInBook(0);
        reflowableControl.useDOMForHighlight(false);
        reflowableControl.setNavigationAreaWidthRatio(0.0f); // both left and right side.
        //To get text of page in pageMovedListener in pageDescription
        reflowableControl.setExtractText(true);

//        Log.d("Page number",);

//        reflowableControl.setMediaOverlayListener(new MediaOverlayHandler()); //For Audio Book

//        showLog("Media Overlay Available : ", String.valueOf(reflowableControl.isMediaOverlayAvailable()));

        //Listeners
//        selectionHandler = new SelectionHandler();
//        reflowableControl.setSelectionListener(selectionHandler);
//
//        pageMoveHandler = new PageMoveHandler();
//        reflowableControl.setPageMovedListener(pageMoveHandler);
//
//        highlightHandler = new HighlightHandler();
//        reflowableControl.setHighlightListener(highlightHandler);
//
//        stateHandler = new StateHandler();
//        reflowableControl.setStateListener(stateHandler);
//
//        bookmarkHandler = new BookmarkHandler();
//        reflowableControl.setBookmarkListener(bookmarkHandler);

        return reflowableControl;
    }

    private void showLog(String tag, String value) {
        if(tag.length() > 22) {
            tag = tag.substring(0, 22);
        }
        Log.d(tag, value);
    }
}
