package licenta.books.androidmobile.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skytree.epub.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BookAnnotations;
import licenta.books.androidmobile.adapters.AnnotationAdapter;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Bookmark;
import licenta.books.androidmobile.classes.Highlight;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookmarkDao;
import licenta.books.androidmobile.database.DAO.HighlightDao;
import licenta.books.androidmobile.database.DaoMethods.BookmarkMethods;
import licenta.books.androidmobile.database.DaoMethods.HighlightMethods;
import licenta.books.androidmobile.interfaces.AnnotationFamily;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.prototypeAnnotation.ListBookAnnotations;
import licenta.books.androidmobile.patterns.prototypeAnnotation.Prototype;
import licenta.books.androidmobile.patterns.strategyAnnotationSort.AllSelected;
import licenta.books.androidmobile.patterns.strategyAnnotationSort.Switcher;
import licenta.books.androidmobile.patterns.strategyAnnotationSort.TypeSelection;


public class AnnotationFragment extends Fragment implements View.OnClickListener {

    private ArrayList<BookAnnotations> bookAnnotations ;
    private ArrayList<BookAnnotations> permanentList ;

    private ListView lv_annotations;

    Switcher switcher;
    ArrayList<BookAnnotations> listBookAnnotations;

    private TextView tvShowAll;
    private Button btnHighlightYellow;
    private Button btnHighlightGreen;
    private Button btnHighlightPink;
    private Button btnHighlightBlue;
    private Button btnHighlightOrange;
    private ImageView btnBookmarks;
    private Bundle bundle;
    private AnnotationAdapter annotationAdapter;
    ArrayList<Button> buttons;
    private OnFragmentInteractionListener mListener;

    public AnnotationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);
        initComp(view);

        bundle = this.getArguments();
        if(bundle !=null){
            bookAnnotations =  bundle.getParcelableArrayList(Constants.KEY_BOOKS_ANNOTATION_LIST);
        }
        ViewCompat.setNestedScrollingEnabled(lv_annotations, true);
        refreshAdapter(bookAnnotations);

        lv_annotations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onTransferBookAnnotation(bookAnnotations.get(position));
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        return view;
    }

    public void initComp(View view){
        lv_annotations = view.findViewById(R.id.lv_annotations);
        tvShowAll  = view.findViewById(R.id.tv_show_All);
        btnHighlightYellow = view.findViewById(R.id.btn_highlight_yellow);
        btnHighlightGreen = view.findViewById(R.id.btn_highlight_green);
        btnHighlightPink = view.findViewById(R.id.btn_highlight_pink);
        btnHighlightBlue = view.findViewById(R.id.btn_highlight_blue);
        btnHighlightOrange = view.findViewById(R.id.btn_highlight_orange);
        btnBookmarks = view.findViewById(R.id.btn_bookmark);

        tvShowAll.setOnClickListener(this);
        btnHighlightYellow.setOnClickListener(this);
        btnHighlightGreen.setOnClickListener(this);
        btnHighlightPink.setOnClickListener(this);
        btnHighlightBlue.setOnClickListener(this);
        btnHighlightOrange.setOnClickListener(this);
        btnBookmarks.setOnClickListener(this);

        switcher = new Switcher(0,Constants.ALL_SELECTED_SWITCH);
        buttons = new ArrayList<>();
        buttons.add(btnHighlightBlue);
        buttons.add(btnHighlightOrange);
        buttons.add(btnHighlightGreen);
        buttons.add(btnHighlightPink);
        buttons.add(btnHighlightYellow);
    }

    private void refreshAdapter(ArrayList<BookAnnotations> arrayList){
        listBookAnnotations = new ArrayList<>();
        for(BookAnnotations book : arrayList){
            listBookAnnotations.add(new BookAnnotations(book.getBookmark(),book.getHighlight()));
        }
        annotationAdapter = new AnnotationAdapter(getActivity(),listBookAnnotations);
        lv_annotations.post(() -> lv_annotations.setAdapter(annotationAdapter));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_show_All:
                Log.d("Test: ", "Intra?");
                setSwitcher(0,Constants.ALL_SELECTED_SWITCH);
                break;
            case R.id.btn_highlight_yellow:
                verifiedButton(btnHighlightYellow);
                setSwitcher(-2131440528,Constants.HIGHLIGHT_SELECTED_SWITCH);
                break;
            case R.id.btn_highlight_green:
                verifiedButton(btnHighlightGreen);
                setSwitcher(-2138449319,Constants.HIGHLIGHT_SELECTED_SWITCH);
                break;
            case R.id.btn_highlight_pink:
                verifiedButton(btnHighlightPink);
                setSwitcher(-2131653218,Constants.HIGHLIGHT_SELECTED_SWITCH);
                break;
            case R.id.btn_highlight_blue:
                verifiedButton(btnHighlightBlue);
                setSwitcher(-2143854953,Constants.HIGHLIGHT_SELECTED_SWITCH);
                break;
            case R.id.btn_highlight_orange:
                verifiedButton(btnHighlightOrange);
                setSwitcher(-2131130266,Constants.HIGHLIGHT_SELECTED_SWITCH);
                break;
            case R.id.btn_bookmark:
                setSwitcher(0,Constants.BOOKMARK_SELECTED_SWITCH);
                break;
        }
    }

    public void setSwitcher(int color, TypeSelection typeSelection){
        refreshAdapter(bookAnnotations);
        switcher.setTypeSelection(typeSelection);
        switcher.setColor(color);
        switcher.switchTypeSelection(color,listBookAnnotations);

        annotationAdapter.notifyDataSetChanged();
    }

    public void verifiedButton(Button button){
        for(Button btn: buttons){
            if(btn.getId() == button.getId()){
                button.setText("\u2713");
                button.setTextSize(18);
                button.setTextColor(Color.WHITE);
            }else{
                btn.setText(null);
            }
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public interface OnFragmentInteractionListener {
        void onTransferBookAnnotation(BookAnnotations bookAnnotation);
    }

}
