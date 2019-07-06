package licenta.books.androidmobile.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import licenta.books.androidmobile.database.DaoMethods.BookCollectionJoinMethods;
import licenta.books.androidmobile.database.DaoMethods.CollectionMethods;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.StrategySortBooks.Sorter;
import licenta.books.androidmobile.patterns.StrategySortBooks.StrategySort;

import static android.app.Activity.RESULT_OK;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ShelfBooks extends Fragment implements CreateShelfDialogFragment.OnCompleteListenerShelf,FlexboxAdapter.OnClickItem, StrategySortDialogFragment.OnCompleteListenerStrategySort
                                            {
    private OnFragmentInteractionListener listener;
    private CollectionDao collectionDao;
    private CollectionMethods collectionMethods;
    private BookCollectionJoinDao bookCollectionJoinDao;
    private BookCollectionJoinMethods bookCollectionJoinMethods;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RecyclerView recyclerView;
    LinearLayout ll_books;
    Toolbar shelfToolbar;

    User user;


    List<CollectionPOJO> arrayList = new ArrayList<>();
    List<BookE> bookES ;
    FlexboxAdapter adapter;
    ShelfBooksAdapter adapterBooks;
    ListView lvBooksShelf;
    private Button addShelf;
    private ImageView imvOptions;
    private TextView titleShelf;
    private Button reverse;
    private boolean isReversed = false;
    private TextView strategySort;
    private ImageView ivHome;

    public ShelfBooks() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        recyclerView.setLayoutManager(layoutManager);




        addShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateShelfDialogFragment createShelfDialogFragment = new CreateShelfDialogFragment();
                assert getFragmentManager() != null;
                createShelfDialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("shelf"),1);
                createShelfDialogFragment.show(getFragmentManager(),"tag2");
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initComp(View view) {
        addShelf = view.findViewById(R.id.btn_add_shelf);
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
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    CollectionPOJO collections = data.getParcelableExtra(Constants.KEY_COLLECTION);
                    if(collections!=null){
                        arrayList.add(collections);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    StrategySort strategySort = data.getParcelableExtra(Constants.KEY_STRATEGY);
                    String strategyName = data.getStringExtra(Constants.KEY_STRATEGY_NAME);
                    Sorter sorter = new Sorter(strategySort);
                    sorter.sorting(bookES);
                    adapterBooks.notifyDataSetChanged();
                    this.strategySort.setText(strategyName);
                }
                break;
        }
    }

    private View.OnClickListener reverseListener = v -> {
        if(!isReversed){
            reverse.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_upward_black_24dp,0,0,0);
            java.util.Collections.reverse(bookES);
            adapterBooks.notifyDataSetChanged();
            isReversed = true;
        }else{
            reverse.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_downward_black_24dp,0,0,0);
            java.util.Collections.reverse(bookES);
            adapterBooks.notifyDataSetChanged();
            isReversed = false;
        }
    };



    private View.OnClickListener homeListener = v -> {

        addShelf.setVisibility(View.VISIBLE);
        animateViewDisappear(imvOptions);
        animateViewDisappear(ll_books);
//        animateViewDisappear(ll_books);
//        animateViewDisappear(ivHome);
//        ll_books.setVisibility(View.GONE);
        titleShelf.setText("My books");
        ivHome.setVisibility(View.INVISIBLE);

    };

    private View.OnClickListener imvOptionsListener = v -> {
        ShelfOptionsDialogFragment shelfOptionsDialogFragment = new ShelfOptionsDialogFragment();
        shelfOptionsDialogFragment.show(getFragmentManager(),"tager");
    };

    private View.OnClickListener strategySortListener = v -> {
        StrategySortDialogFragment strategySortDialogFragment = new StrategySortDialogFragment();
        assert getFragmentManager() != null;
        strategySortDialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("shelf"),2);
        strategySortDialogFragment.show(getFragmentManager(),"sorter");
    };

    private void animateViewAppear(View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(300)
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
    }


    @Override
    public void onCompleteCreateShelf(Collections collections) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void respond(CollectionPOJO collection) {
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
                        adapterBooks = new ShelfBooksAdapter(getActivity(),bookES);
                        lvBooksShelf.setAdapter(adapterBooks);
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


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
