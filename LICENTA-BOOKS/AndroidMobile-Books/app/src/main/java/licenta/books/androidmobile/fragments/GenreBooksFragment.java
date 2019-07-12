package licenta.books.androidmobile.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.GenreBooksAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreBooksFragment extends Fragment implements GenreBooksAdapter.OnButtonClickListener {
    private OnFragmentGenreBooksListener listener;
    private TextView genreTitle;
    private ListView categoryListview;
    private ApiService apiService;
    private GenreBooksAdapter adapter;
    private ProgressDialog progressDialog;
    Bundle bundle;
    String category;
    User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.genre_books_layout,container,false);
        genreTitle = view.findViewById(R.id.genre_title);
        categoryListview = view.findViewById(R.id.lv_genre);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.setTitle("Fetching Data");
        apiService = ApiClient.getRetrofit().create(ApiService.class);

        Disposable d = RxBus.subscribeUser(us -> user = us);
        d.dispose();

        bundle = getArguments();
        if(bundle!=null){
            category = bundle.getString("testCategory");
            if(category!=null){
                genreTitle.setText(category);
            }
        }

        Call<ArrayList<BookE>> call = apiService.getCategoryBooks(category);
        progressDialog.show();
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public  void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(response.isSuccessful()){
                    Log.d("Intra?","Da");
                    ArrayList<BookE> allBooks = response.body();
                    Call<ArrayList<BookE>> callWanted = apiService.getWantedBooks(user.getEmail());
                    getWantedBooks(callWanted, allBooks);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {

            }
        });


        return view;
    }

    private void getWantedBooks(Call<ArrayList<BookE>> call,ArrayList<BookE> arrayList){
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {

                    ArrayList<BookE> bookES = response.body();
                    if(bookES ==null){
                        bookES = new ArrayList<>();
                    }

                    adapter = new GenreBooksAdapter(getActivity(),arrayList,bookES);
                    setInterfaceListener();
                    categoryListview.setAdapter(adapter);
                    progressDialog.dismiss();
            }


            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {

            }
        });
    }

    private void setInterfaceListener() {
        adapter.setOnButtonClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentSearchListener) {
            listener = (GenreBooksFragment.OnFragmentGenreBooksListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onButtonPressed() {
        if (listener != null) {
            listener.onFragmentGenreBooks();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onButtonClick(BookE bookE, boolean isWanted) {
        Toast.makeText(getContext(),bookE.getTitle()+ " " + isWanted,Toast.LENGTH_LONG).show();
        if(!isWanted){
            Call<User> callDelete = apiService.deleteWantToRead(user.getEmail(),bookE.get_id());
            callDelete.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        Log.d("WantToRead ","Succ DELETED");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }else{
            Call<User> callAdd = apiService.addWantToRead(bookE.get_id(),user.getEmail());
            callAdd.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        Log.d("WantToRead ","Succ Add");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }

    public interface OnFragmentGenreBooksListener {
        // TODO: Update argument type and name
        void onFragmentGenreBooks();
    }
}
