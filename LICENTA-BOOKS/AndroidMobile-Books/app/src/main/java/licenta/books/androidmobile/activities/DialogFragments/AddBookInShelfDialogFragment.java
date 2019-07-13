package licenta.books.androidmobile.activities.DialogFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.ShelvesDialogAdapter;
import licenta.books.androidmobile.classes.CollectionPOJO;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookCollectionJoinDao;
import licenta.books.androidmobile.database.DaoMethods.BookCollectionJoinMethods;
import licenta.books.androidmobile.interfaces.Constants;

public class AddBookInShelfDialogFragment extends DialogFragment implements ShelvesDialogAdapter.OnClickItem{
    OnCompleteAddBooksListener listener;
    Bundle bundle;
    RecyclerView recyclerView;
    ShelvesDialogAdapter shelvesDialogAdapter;
    private BookCollectionJoinDao bookCollectionJoinDao;
    private BookCollectionJoinMethods bookCollectionJoinMethods;
    User user;
    private ArrayList<Integer> collectionIds = new ArrayList<>();
    private Button addBooks;

    public AddBookInShelfDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_book_shelf_dialog_fragment,null,false);
        bundle = getArguments();
        if(bundle!=null){
            user = bundle.getParcelable("userTest");
        }
        recyclerView = view.findViewById(R.id.recyclerView_shelves);
        addBooks = view.findViewById(R.id.btn_add_books_shelf);
        openDb();




        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        recyclerView.setLayoutManager(layoutManager);
        initArray();

        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(Constants.KEY_COLLECTION_IDS, collectionIds);
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });

        return view;
    }

    private void setAdapterListener() {
        shelvesDialogAdapter.setOnClickItem(this);

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
                        shelvesDialogAdapter = new ShelvesDialogAdapter(getContext(), collections);
                        recyclerView.setAdapter(shelvesDialogAdapter);
                        setAdapterListener();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG","Fail");
                    }
                });

    }

    private void openDb(){
        bookCollectionJoinDao = AppRoomDatabase.getInstance(getContext()).getBookCollectionJoinDao();
        bookCollectionJoinMethods = BookCollectionJoinMethods.getInstance(bookCollectionJoinDao);
    }

    @Override
    public void respond(ArrayList<Integer> integers) {
        collectionIds = integers;
    }

    public interface OnCompleteAddBooksListener{
        void onCompleteAddBooks(Collections collections);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (AddBookInShelfDialogFragment.OnCompleteAddBooksListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
