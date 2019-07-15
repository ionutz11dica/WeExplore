package licenta.books.androidmobile.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DetailsActivity;
import licenta.books.androidmobile.activities.others.CustomScrollview;
import licenta.books.androidmobile.activities.others.GridviewScrollable;
import licenta.books.androidmobile.adapters.GenreBooksAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.interfaces.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreBooksFragment extends Fragment implements GenreBooksAdapter.OnButtonClickListener {
    private OnFragmentGenreBooksListener listener;
    private TextView genreTitle;
    private TextView genreTitleToolbar;
    private ReadMoreTextView readMoreTextView;
    private GridviewScrollable categoryListview;
    private ApiService apiService;
    private GenreBooksAdapter adapter;
    private ProgressDialog progressDialog;
    private ImageView homeBtn;
    private ArrayList<BookE> allBooks;
    private ArrayList<BookE> mostDownloaded;
    private CustomScrollview scrollView;


    Bundle bundle;
    String category;
    String description;
    User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.genre_books_layout,container,false);
        genreTitle = view.findViewById(R.id.genre_title);
        genreTitleToolbar = view.findViewById(R.id.title_toolbar_genres);
        categoryListview = view.findViewById(R.id.gridview_top);
        readMoreTextView = view.findViewById(R.id.genre_description);
        homeBtn = view.findViewById(R.id.btn_home_genres);
        scrollView = view.findViewById(R.id.scroll_genre);
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
            description = bundle.getString("testDescription");
            mostDownloaded = bundle.getParcelableArrayList("testMostDownloaded");
            if(category!=null){
                genreTitle.setText(category);
                readMoreTextView.setText(description+"djasdjqwdhawhfahwfuahwufhauhwfuahuh uh uhqudhwauh udh hauwh duahwduhauh duhu huah duahw uhwau hdwuah uah uhdwuad hudahwu hdauh uah hwa uawu hdauh uah uhwau ua huwh uaw ");
            }
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFragment();
            }


        });

        if(mostDownloaded == null) {
            Call<ArrayList<BookE>> call = apiService.getCategoryBooks(category);
            progressDialog.show();
            call.enqueue(new Callback<ArrayList<BookE>>() {
                @Override
                public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                    if (response.isSuccessful()) {
                        Log.d("Intra?", "Da");
                        setAllBooks(response);
//
                        Call<ArrayList<BookE>> callWanted = apiService.getWantedBooks(user.getEmail());
                        getWantedBooks(callWanted, allBooks);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {

                }
            });
        }else{
            Call<ArrayList<BookE>> callWanted = apiService.getWantedBooks(user.getEmail());
            getWantedBooks(callWanted,mostDownloaded);
        }

        scrollView.setOnScrollListener(new CustomScrollview.OnScrollListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
              if (y >= 56) {
                  if(genreTitleToolbar.getVisibility() == View.INVISIBLE){
                      genreTitleToolbar.setText(category);
//                      genreTitleToolbar.setVisibility(View.VISIBLE);
                      animateViewAppear(genreTitleToolbar);
                  }
                  System.out.println("dwwn");
            }else {
                  genreTitleToolbar.setVisibility(View.INVISIBLE);
              }
            }
        });



        return view;
    }



    private void setAllBooks(Response<ArrayList<BookE>> response) {
        allBooks = response.body();
    }

    private void hideFragment() {
        listener.onFragmentGenreBooks(this);
    }


    private void getWantedBooks(Call<ArrayList<BookE>> call,ArrayList<BookE> arrayList){
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(response.isSuccessful()){

                }
                    ArrayList<BookE> bookES = response.body();
                    if(bookES ==null){
                        bookES = new ArrayList<>();
                    }

                    adapter = new GenreBooksAdapter(getActivity(),arrayList,bookES);
                    setInterfaceListener();
                    categoryListview.setExpanded(true);
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
            listener.onFragmentGenreBooks(this);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onButtonClick(BookE bookE, boolean isWanted) {
//        Toast.makeText(getContext(),bookE.getTitle()+ " " + isWanted,Toast.LENGTH_LONG).show();
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

    private void animateViewAppear(View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    public interface OnFragmentGenreBooksListener {
        // TODO: Update argument type and name
        void onFragmentGenreBooks(GenreBooksFragment genreBooksFragment);
    }
}
