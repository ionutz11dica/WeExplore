package licenta.books.androidmobile.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
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
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.interfaces.Constants;

public class ShelfBooks extends Fragment {
    private OnFragmentInteractionListener listener;
    private UserBookJoinDao userBookJoinDao;
    private UserDao userDao;
    private UserBookMethods userBookMethods;
    private UserMethods userMethods;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public ShelfBooks() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        openDb();
        initSharedPref();
        getUserBooks();
        View view = inflater.inflate(R.layout.fragment_shelfbooks,container,false);
        return view;
    }


    @SuppressLint("CheckResult")
    private void getUserBooks(){
        User[] user = new User[1];
        List<BookE> Bookers = new ArrayList<>();
        createUser(user);
        Flowable<List<BookE>> books = userBookMethods.getAllUserBooksFromDatabase(2);
        books.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BookE>>() {
                    @Override
                    public void accept(List<BookE> bookES) throws Exception {
                        Log.d("SIZE LISTA: ",String.valueOf(bookES.size()));
                    }
                });
    }


    private void createUser(final User[] userr) {
        String status = sharedPreferences.getString(Constants.KEY_STATUS, null);

        if (status.equals("with")) {
            final String email = sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
            Single<User> userSingle = userMethods.verifyExistenceGoogleAcount(email);
            userSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(User user) {
                            Log.d("User id: ", user.getUserId().toString());
                            userr[0] = user;
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            final String username = sharedPreferences.getString(Constants.KEY_USER_USERNAME, null);
            final String password = sharedPreferences.getString(Constants.KEY_USER_PASSWORD, null);

            Single<User> userSingle = userMethods.verifyAvailableAccount(username, password);
            userSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(User user) {
                            Log.d("User id: ", user.getUserId().toString());
                            userr[0] = user;
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }



    @SuppressLint("CommitPrefEdits")
    private void initSharedPref(){
        sharedPreferences = getActivity().getSharedPreferences(Constants.KEY_PREF_USER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    private void openDb(){
        userBookJoinDao = AppRoomDatabase.getInstance(getContext()).getUserBookDao();
        userBookMethods = UserBookMethods.getInstance(userBookJoinDao);

        userDao = AppRoomDatabase.getInstance(getContext()).getUserDao();
        userMethods = UserMethods.getInstance(userDao);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
