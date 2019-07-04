package licenta.books.androidmobile.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.CreateShelfDialogFragment;
import licenta.books.androidmobile.adapters.FlexboxAdapter;
import licenta.books.androidmobile.adapters.ShelfAdapter;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.CollectionMethods;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.interfaces.Constants;

import static android.app.Activity.RESULT_OK;


public class ShelfBooks extends Fragment implements CreateShelfDialogFragment.OnCompleteListenerShelf {
    private OnFragmentInteractionListener listener;
    private CollectionDao collectionDao;
    private CollectionMethods collectionMethods;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RecyclerView recyclerView;

    User user;


    List<Collections> arrayList = new ArrayList<>();
    FlexboxAdapter adapter;

    private Button addShelf;
    private FlexboxLayout linearTags;
    int i =0;
    public ShelfBooks() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelfbooks,container,false);
        Disposable d = RxBus.subscribeUser(us -> user = us);
        d.dispose();

        addShelf = view.findViewById(R.id.btn_add_shelf);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Collections collections = data.getParcelableExtra(Constants.KEY_COLLECTION);
                    if(collections!=null){
                        arrayList.add(collections);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                }
        }
    }

    private void initArray() {
        Single<List<Collections>> collections = collectionDao.getAllUserCollections(user.getUserId());
        collections.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Collections>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Collections> collections) {
                        setCollectionList(collections);
                        adapter = new FlexboxAdapter(getContext(), arrayList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG","Fail");
                    }
                });




    }

    private void setCollectionList(List<Collections> arrayList){
        this.arrayList = arrayList;
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @SuppressLint("CommitPrefEdits")
    private void initSharedPref(){
        sharedPreferences = getActivity().getSharedPreferences(Constants.KEY_PREF_USER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }



    private void openDb(){
        collectionDao = AppRoomDatabase.getInstance(getContext()).getCollectionDao();
        collectionMethods = CollectionMethods.getInstance(collectionDao,getContext());


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
    public void onCompleteCreateShelf(Collections collections) {
        arrayList.add(collections);
        adapter.notifyDataSetChanged();
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getTag().toString()) {
//            case "nume1":
//                Toast.makeText(getContext(),"Merge?",Toast.LENGTH_LONG).show();
//                break;
//            }
//        }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
