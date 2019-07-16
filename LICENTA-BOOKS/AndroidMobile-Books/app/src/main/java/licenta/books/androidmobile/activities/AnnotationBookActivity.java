package licenta.books.androidmobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BookAnnotations;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Converters.ArrayStringConverter;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.fragments.AnnotationFragment;
import licenta.books.androidmobile.fragments.ChapterFragment;
import licenta.books.androidmobile.fragments.InfoFragment;
import licenta.books.androidmobile.fragments.ScannerFragment;
import licenta.books.androidmobile.fragments.SearchFragment;
import licenta.books.androidmobile.fragments.ShelfBooks;
import licenta.books.androidmobile.interfaces.Constants;

public class AnnotationBookActivity extends AppCompatActivity implements InfoFragment.OnFragmentInteractionListener,ChapterFragment.OnFragmentInteractionListener, AnnotationFragment.OnFragmentInteractionListener {
    Toolbar toolbar;
    SegmentedGroup segmentedBtn;
    RadioButton btnInfo;
    Intent intent = new Intent();
    Fragment selectedFragment;

    BookE book;
    ArrayList<BookAnnotations> annotations;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_book);



        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont,new InfoFragment()).commit();
        }
        initComp();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initComp(){
        book = getReadingBook();
        getBookAnnotations();
//        RxBus.publishBook(book);
        toolbar = findViewById(R.id.annotation_toolbar);
        toolbar.setTitle(book.getTitle());
        toolbar.setSubtitle(convertFromArray(book.getAuthors()));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        segmentedBtn = findViewById(R.id.segmented2);
        segmentedBtn.setTintColor(Color.LTGRAY);
        btnInfo = findViewById(R.id.btn_infobook);
        btnInfo.setChecked(true);

    }

    public void handleChangeFragment(View view){
        if(view == findViewById(R.id.btn_infobook)){
            selectedFragment = new InfoFragment();
            toolbar.setBackgroundColor(Color.TRANSPARENT);
            segmentedBtn.setTintColor(Color.LTGRAY);
            segmentedBtn.setBackgroundColor(Color.TRANSPARENT);
        }else if(view == findViewById(R.id.btn_contents)){
            toolbar.setBackgroundColor(Color.WHITE);
            segmentedBtn.setBackgroundColor(Color.WHITE);

            toolbar.setTitleTextColor(Color.GRAY);
            toolbar.setSubtitleTextColor(Color.GRAY);

            segmentedBtn.setTintColor(Color.LTGRAY);
            selectedFragment = new ChapterFragment();
        } else if(view == findViewById(R.id.btn_annotations)) {
            toolbar.setBackgroundColor(Color.WHITE);
            segmentedBtn.setBackgroundColor(Color.WHITE);
            segmentedBtn.setTintColor(Color.LTGRAY);

            toolbar.setTitleTextColor(Color.GRAY);
            toolbar.setSubtitleTextColor(Color.GRAY);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.KEY_BOOKS_ANNOTATION_LIST,annotations);

            selectedFragment = new AnnotationFragment();
            selectedFragment.setArguments(bundle);

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cont,selectedFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }

    private BookE getReadingBook(){
        Disposable d = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                book = bookE;
            }
        });
        d.dispose();
        return book;
    }

    private void getBookAnnotations(){
        Disposable d = RxBus.subscribeBookAnnotation(new Consumer<ArrayList<BookAnnotations>>() {
            @Override
            public void accept(ArrayList<BookAnnotations> bookAnnotations) throws Exception {
                annotations = bookAnnotations;
            }
        });
        d.dispose();
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

    @Override
    public void onTransferBookAnnotation(BookAnnotations bookAnnotation) {
        intent.putExtra(Constants.KEY_BOOK_ANNOTATION,bookAnnotation);
        setResult(RESULT_OK,intent);
//        finish();
    }

    @Override
    public void onTransferHighlightsList(ArrayList<Highlight> highlights) {
        intent.putParcelableArrayListExtra(Constants.KEY_HIGHLIGHTS_DELETED,highlights);
        setResult(RESULT_OK,intent);
//        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
