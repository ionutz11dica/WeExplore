package licenta.books.androidmobile.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DialogFragments.CreateShelfDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.ShelfOptionsDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.StrategySortDialogFragment;
import licenta.books.androidmobile.adapters.FlexboxAdapter;
import licenta.books.androidmobile.adapters.ShelfBooksAdapter;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.CollectionPOJO;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookCollectionJoinDao;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DaoMethods.BookCollectionJoinMethods;
import licenta.books.androidmobile.database.DaoMethods.CollectionMethods;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.StrategySortBooks.Sorter;
import licenta.books.androidmobile.patterns.StrategySortBooks.StrategySort;

import static android.app.Activity.RESULT_OK;


@TargetApi(Build.VERSION_CODES.KITKAT)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ShelfBooks extends Fragment implements CreateShelfDialogFragment.OnCompleteListenerShelf,FlexboxAdapter.OnClickItem, StrategySortDialogFragment.OnCompleteListenerStrategySort
                                            {
    private OnFragmentInteractionListener listener;

    private OnSwitchFragment onSwitchFragmentListener;

    private CollectionDao collectionDao;
    private CollectionMethods collectionMethods;
    private BookCollectionJoinDao bookCollectionJoinDao;
    private BookCollectionJoinMethods bookCollectionJoinMethods;
    private UserBookJoinDao userBookJoinDao;
    private UserBookMethods userBookMethods;

    RecyclerView recyclerView;
    LinearLayout ll_books;
    Toolbar shelfToolbar;

    User user;


    List<CollectionPOJO> arrayList = new ArrayList<>();
    List<BookE> bookES ;
    List<BookE> readingBooks ;
    FlexboxAdapter adapter;
    ShelfBooksAdapter adapterBooks;
    ShelfBooksAdapter adapterReadingBooks;
    ListView lvBooksShelf;
    private Button addShelf;
    private EditText searchReadingBooks;
    private TextView cancelSearch;
    private ImageView imvOptions;
    private TextView titleShelf;
    private Button reverse;
    private boolean isReversed = false;
    private boolean isUpdate = false;
    private TextView strategySort;
    private TextView sizeReading;
    private ImageView ivHome;
    Bundle bundleShelf = new Bundle();
    int selectedPos;

    private LinearLayout searchLayout;
    private LinearLayout wantToRead;
    private LinearLayout reading;
    private LinearLayout ll_search;
    private ListView lvSearchedBook;
    private boolean isReading = true;

    public ShelfBooks() {
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelfbooks,container,false);
        Disposable d = RxBus.subscribeUser(us -> user = us);
        d.dispose();

        initComp(view);


        openDb();


        recyclerView = view.findViewById(R.id.recyclerView);
        initArray();
        fetchReadingBooks();
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initComp(View view) {
        addShelf = view.findViewById(R.id.btn_add_shelf);
        addShelf.setOnClickListener(addShelfListener);

        ll_books = view.findViewById(R.id.books_layout);
        shelfToolbar = view.findViewById(R.id.shelfbooks_toolbar);
        imvOptions = view.findViewById(R.id.imv_options);
        imvOptions.setOnClickListener(imvOptionsListener);

        titleShelf = view.findViewById(R.id.toolbar_title);
        reverse = view.findViewById(R.id.btn_reverse);
        reverse.setOnClickListener(reverseListener);

        strategySort = view.findViewById(R.id.strategy_sorting);
        ivHome = view.findViewById(R.id.btn_home);
        ivHome.setOnClickListener(homeListener);

        lvBooksShelf = view.findViewById(R.id.lv_books_shelf);
        strategySort.setOnClickListener(strategySortListener);

        wantToRead = view.findViewById(R.id.ly_wants);


        reading = view.findViewById(R.id.ly_reading);
        reading.setOnClickListener(readingListener);

        searchReadingBooks = view.findViewById(R.id.et_search_book_db);
        searchReadingBooks.addTextChangedListener(searchListener);
        searchReadingBooks.setOnFocusChangeListener(searchFocusListener);

        ll_search = view.findViewById(R.id.search_layout);
        lvSearchedBook = view.findViewById(R.id.lv_searched_books);

        cancelSearch = view.findViewById(R.id.tv_search_cancel_books);
        cancelSearch.setOnClickListener(cancelSearchListener);
        searchLayout = view.findViewById(R.id.ll_search_db);

        sizeReading = view.findViewById(R.id.tv_size_reading);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    CollectionPOJO collections = data.getParcelableExtra(Constants.KEY_COLLECTION);
                    boolean update = data.getBooleanExtra(Constants.KEY_IS_UPDATE_SHELF,false);
                    refreshLayout();
                    if(collections!=null && !update ){
                        if(!verifyExistence(collections.collectionName)) {
                            arrayList.add(collections);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        }
                    }else if(collections != null){
                        arrayList.set(selectedPos,collections);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    StrategySort strategySort = data.getParcelableExtra(Constants.KEY_STRATEGY);
                    String strategyName = data.getStringExtra(Constants.KEY_STRATEGY_NAME);
                    Sorter sorter = new Sorter(strategySort);
                    if(isReading){
                        sorter.sorting(readingBooks);
                        adapterReadingBooks.notifyDataSetChanged();
                    }else {
                        sorter.sorting(bookES);
                        adapterBooks.notifyDataSetChanged();
                    }

                    this.strategySort.setText(strategyName);
                }
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    int pos = data.getIntExtra(Constants.POSITION_OPTIONS,-1);
                    if(pos == -1){
                        break;
                    }
                    switch (pos){
                        case 0 : //add
                           onSwitchFragmentListener.onSwitchFragment(R.id.item_searchBooks);
                           break;
                        case 1 : //update
                            isUpdate = true;
                            bundleShelf.putBoolean(Constants.KEY_IS_UPDATE_SHELF, true);
                            bundleShelf.putParcelable(Constants.KEY_COLLECTION,arrayList.get(selectedPos));
                            addShelf.performClick();
                            break;
                        case 2 :  //delete

                            ll_books.setVisibility(View.GONE);
                            imvOptions.setVisibility(View.GONE);
                            ivHome.setVisibility(View.INVISIBLE);
                            refreshLayout();
                            collectionMethods.deleteCollection(arrayList.get(selectedPos).collectionName);
                            arrayList.remove(selectedPos);
                            adapter.notifyDataSetChanged();
                            break;

                    }
                }
        }
    }

        private void refreshLayout() {
            addShelf.setVisibility(View.VISIBLE);
            titleShelf.setText("My books");
        }

        private boolean verifyExistence(String name){
            for(CollectionPOJO o : arrayList){
                if(o.collectionName.equals(name)){
                    return true;
                }
            }
            return false;
        }

         private View.OnClickListener reverseListener = v -> {
        if(!isReversed){
            reverse.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_upward_black_24dp,0,0,0);

            if(isReading){
                java.util.Collections.reverse(readingBooks);
                adapterReadingBooks.notifyDataSetChanged();
            }else {
                java.util.Collections.reverse(bookES);
                adapterBooks.notifyDataSetChanged();
            }

            isReversed = true;
        }else{
            reverse.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_downward_black_24dp,0,0,0);
            if(isReading){
                java.util.Collections.reverse(readingBooks);
                adapterReadingBooks.notifyDataSetChanged();
            }else {
                java.util.Collections.reverse(bookES);
                adapterBooks.notifyDataSetChanged();
            }
            isReversed = false;
        }
    };

    private View.OnClickListener addShelfListener = v -> {
        ll_books.setVisibility(View.GONE);
        imvOptions.setVisibility(View.GONE);
        ivHome.setVisibility(View.INVISIBLE);
        refreshLayout();
        CreateShelfDialogFragment createShelfDialogFragment = new CreateShelfDialogFragment();
        if(isUpdate){
            createShelfDialogFragment.setArguments(bundleShelf);
        }else{
            createShelfDialogFragment.setArguments(null);
        }

        assert getFragmentManager() != null;
        createShelfDialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("shelf"),1);
        createShelfDialogFragment.show(getFragmentManager(),"tag2");
        isUpdate=false;
    };



    private View.OnClickListener homeListener = v -> {

        addShelf.setVisibility(View.VISIBLE);
        animateViewDisappear(imvOptions);
        animateViewDisappear(ll_books);
        titleShelf.setText("My books");
        ivHome.setVisibility(View.INVISIBLE);

    };

    private View.OnClickListener imvOptionsListener = v -> {
        ShelfOptionsDialogFragment shelfOptionsDialogFragment = new ShelfOptionsDialogFragment();
        assert getFragmentManager() != null;
        shelfOptionsDialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("shelf"),3);
        shelfOptionsDialogFragment.show(getFragmentManager(),"tager");
    };

    private View.OnClickListener strategySortListener = v -> {
        StrategySortDialogFragment strategySortDialogFragment = new StrategySortDialogFragment();
        assert getFragmentManager() != null;
        strategySortDialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("shelf"),2);
        strategySortDialogFragment.show(getFragmentManager(),"sorter");
    };

    private View.OnClickListener readingListener = v -> {
        readingLayoutTest();
        isReading = true;
        lvBooksShelf.setAdapter(adapterReadingBooks);
    };


    private TextWatcher searchListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            adapterReadingBooks.getFilter().filter(s);
        }
    };

    private View.OnFocusChangeListener searchFocusListener = (v, hasFocus) -> {
        if(hasFocus){

            animateViewAppear(cancelSearch);
            ll_search.setVisibility(View.VISIBLE);
            lvSearchedBook.setAdapter(adapterReadingBooks);
        }else{
            ll_search.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener cancelSearchListener = v -> {
        ll_search.setVisibility(View.GONE);
        hideKeyboardFrom(getContext(),v);
        searchReadingBooks.clearFocus();
        searchReadingBooks.setText(null);
        cancelSearch.setVisibility(View.GONE);
    };

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void fetchReadingBooks() {
        Single<List<BookE>> listSingle = userBookMethods.getAllUserBooksFromDatabase(user.getUserId());
        listSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<BookE>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("Suc", "intra?");

                    }

                    @Override
                    public void onSuccess(List<BookE> bookES) {
                        Log.d("Suc", String.valueOf(bookES.size()));
                        //                        setShelfBooks(bookES);

                        setReadingBooks(bookES);
//                        setShelfBooks(bookES);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Suc", "Nope");
                    }
                });
    }

    private void readingLayoutTest() {
        animateViewAppear(ll_books);
        ivHome.setVisibility(View.VISIBLE);
        addShelf.setVisibility(View.INVISIBLE);
        titleShelf.setText("Reading..");
    }

     private void animateViewAppear(View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void animateViewDisappear(View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void initArray() {
        Single<List<CollectionPOJO>> collections = bookCollectionJoinMethods.fetchUserCollection(user.getUserId());
        collections.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CollectionPOJO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<CollectionPOJO> collections) {
                        Log.d("Size", String.valueOf(collections.size()));
                        adapter = new FlexboxAdapter(getContext(), collections);
                        recyclerView.setAdapter(adapter);
                        setCollectionList(collections);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG","Fail");
                    }
                });

    }

    private void setCollectionList(List<CollectionPOJO> arrayList){
        this.arrayList = arrayList;
        adapter.setOnClickItem(this);
    }

    private void setShelfBooks(List<BookE> arrayList){
        this.bookES = arrayList;
        adapterBooks = new ShelfBooksAdapter(getActivity(),bookES);
        lvBooksShelf.setAdapter(adapterBooks);

    }
    private void setReadingBooks(List<BookE> books){
        this.readingBooks = books;
        adapterReadingBooks = new ShelfBooksAdapter(getActivity(),books);
        lvBooksShelf.setAdapter(adapterReadingBooks);
        sizeReading.setText(books.size() +" books");}



    @Override
    public void onCompleteCreateShelf(Collections collections) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void respond(CollectionPOJO collection,int pos) {
        selectedPos = pos;
        isReading = false;
        titleShelf.setText(collection.collectionName);
        animateViewAppear(imvOptions);
        ll_books.setClickable(true);
//        ll_books.setVisibility(View.VISIBLE);
        animateViewAppear(ll_books);
        ivHome.setVisibility(View.VISIBLE);

        addShelf.setVisibility(View.INVISIBLE);

        Single<List<BookE>> listSingle = bookCollectionJoinMethods.fetchCollectionBooks(user.getUserId(),collection.collectionName);
        listSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<BookE>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("Suc", "intra?");
                    }

                    @Override
                    public void onSuccess(List<BookE> bookES) {
                        Log.d("Suc", String.valueOf(bookES.size()));
                        setShelfBooks(bookES);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Suc", "Nope");
                    }
                });

    }


    private void openDb(){
        collectionDao = AppRoomDatabase.getInstance(getContext()).getCollectionDao();
        collectionMethods = CollectionMethods.getInstance(collectionDao,getContext());

        bookCollectionJoinDao = AppRoomDatabase.getInstance(getContext()).getBookCollectionJoinDao();
        bookCollectionJoinMethods = BookCollectionJoinMethods.getInstance(bookCollectionJoinDao);

        userBookJoinDao = AppRoomDatabase.getInstance(getContext()).getUserBookDao();
        userBookMethods = UserBookMethods.getInstance(userBookJoinDao);



    }

    public void onButtonPressed(Uri uri) {
        if (listener != null) {
            listener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShelfBooks.OnFragmentInteractionListener) {
            listener = (ShelfBooks.OnFragmentInteractionListener) context;
            onSwitchFragmentListener = (OnSwitchFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCompleteStrategy(String strategy) {

    }

    public interface OnSwitchFragment{
        void onSwitchFragment(int id);
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
