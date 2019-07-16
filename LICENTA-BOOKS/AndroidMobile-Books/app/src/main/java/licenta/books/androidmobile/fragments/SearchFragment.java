package licenta.books.androidmobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import licenta.books.androidmobile.activities.others.BlurBuilder;
import licenta.books.androidmobile.adapters.BookAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private OnFragmentSearchListener listener;
    private OnFragmentGenreSwitchListener switchListenr;
    private SearchBox search;
    private Toolbar toolbar;
    private ArrayList<BookE> titlesAuthors;
    private ApiService apiService;

    private LinearLayoutManager firstManager;
    private MultiSnapRecyclerView firstRecyclerView;
    private MultiSnapRecyclerView secondRecyclerView;
    private BookAdapter firstAdapter;
    private BookAdapter secondAdapter;

    private ImageView leftFirst;
    private ImageView leftSecond;
    private ImageView centerImv;
    private ImageView rightSecond;
    private ImageView rightFirst;
    RelativeLayout relativeBackground;
    private ArrayList<ImageView> images;
    private AnimationDrawable animationDrawable;
    private AnimationDrawable animationDrawable2;

    private View view;

    private ScrollView scrollView;
    private LinearLayout ll_noBooks;
    private TextView genreClassic;
    private TextView genreRomance;
    private TextView genreFiction;
    private TextView genreNonFiction;
    private TextView genreScience;
    private TextView genreInspirational;
    private TextView noDownloads;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.search_layout,container,false);
        initApiService();

        initComp(view);
        getTitlesForSearchView();
        listenerMenuToolbar();

        getRandomBooks();
        animationDrawable = (AnimationDrawable) relativeBackground.getBackground();
        animationDrawable2 = (AnimationDrawable) toolbar.getBackground();



        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable2.setEnterFadeDuration(2000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable2.setExitFadeDuration(2000);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()&&
                animationDrawable2 !=null && !animationDrawable2.isRunning() ) {
            animationDrawable2.start();
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning() &&
                animationDrawable2 !=null && animationDrawable2.isRunning()) {
            animationDrawable.stop();
            animationDrawable2.stop();
        }
    }

    private void listenerMenuToolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            openSearch();
            return true;
        });
    }

    private View.OnClickListener listenerGenres(String category,String genreDescription){
        View.OnClickListener listener = v -> {
            switchListenr.onFragmentSwitch(category,genreDescription,null);
        };
        return listener;
    }

    private void initComp(View view) {
        search = view.findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);


        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Get books");
        toolbar.setTitleTextAppearance(getContext(),R.style.CrimsonTextAppearance);
        toolbar.setTitleTextColor(Color.WHITE);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        firstRecyclerView = view.findViewById(R.id.first_recycler_view);
        secondRecyclerView = view.findViewById(R.id.second_recycler_view);

        leftFirst = view.findViewById(R.id.left_first);
        leftSecond = view.findViewById(R.id.left_second);
        centerImv = view.findViewById(R.id.center_imv);
        rightSecond = view.findViewById(R.id.right_second);
        rightFirst = view.findViewById(R.id.right_first);
        images = new ArrayList<>();
        images.add(leftFirst);images.add(leftSecond);images.add(centerImv);images.add(rightSecond);images.add(rightFirst);

        ll_noBooks = view.findViewById(R.id.no_downloaded_books);


        ll_noBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchListenr.onFragmentSwitch("Most Downloaded Books",noDownloads.getText().toString(),titlesAuthors);
            }
        });

        scrollView = view.findViewById(R.id.scroll_search);

        genreClassic = view.findViewById(R.id.genre_classics);
        genreClassic.setOnClickListener(listenerGenres(genreClassic.getText().toString(),getString(R.string.classics_genre)));
        genreRomance = view.findViewById(R.id.genre_romance);
        genreRomance.setOnClickListener(listenerGenres(genreRomance.getText().toString(),getString(R.string.romance_genre)));
        genreFiction = view.findViewById(R.id.genre_ficton);
        genreFiction.setOnClickListener(listenerGenres(genreFiction.getText().toString(),getString(R.string.fiction_genre)));
        genreNonFiction = view.findViewById(R.id.genre_non_fiction);
        genreNonFiction.setOnClickListener(listenerGenres(genreNonFiction.getText().toString(),getString(R.string.nonfiction_genre)));
        genreScience = view.findViewById(R.id.genre_science);
        genreScience.setOnClickListener(listenerGenres(genreScience.getText().toString(),getString(R.string.science_genre)));
        genreInspirational = view.findViewById(R.id.genre_inspirational);
        genreInspirational.setOnClickListener(listenerGenres(genreInspirational.getText().toString(),getString(R.string.inspirational_genre)));

        loadImageForGenre("https://i.imgur.com/PS4vvGU.jpg",genreClassic," Classics");
        loadImageForGenre("https://i.imgur.com/BQDW6P3.jpg",genreRomance," Romance");
        loadImageForGenre("https://i.imgur.com/P0REIa6.jpg",genreFiction," Fiction");
        loadImageForGenre("https://i.imgur.com/ifd1a49.jpg",genreNonFiction," Non-Fiction");
        loadImageForGenre("https://i.imgur.com/ZjxlK9B.jpg",genreScience," Science");
        loadImageForGenre("https://i.imgur.com/0hcW1Lt.jpg",genreInspirational," Inspirational");

        noDownloads = view.findViewById(R.id.tv_no_downloads);

        relativeBackground = view.findViewById(R.id.relative_background);
    }

    private void loadImageForGenre(String url, TextView textView,String text) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap resultImg = BlurBuilder.blurImageGenre(getContext(), resource);
                        Bitmap brightnessImg = BlurBuilder.changeBitmapContrastBrightness(resultImg, 0.55f, 1);
                        Bitmap bitmapRadius = BlurBuilder.getRoundedCornerBitmap(brightnessImg, 20);
                        BitmapDrawable drawable = new BitmapDrawable(bitmapRadius);

                        textView.setBackgroundDrawable(drawable);
                        textView.setText(text);

                    }
                });
    }

    private void initApiService(){
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setTransparency(float alpha){
//        relativeBackground.setBackgroundColor(Color.parseColor("#1A795548"));
        leftFirst.setAlpha(alpha);
        leftSecond.setAlpha(alpha);
        centerImv.setAlpha(alpha);
        rightFirst.setAlpha(alpha);
        rightSecond.setAlpha(alpha);
    }

    public void openSearch() {

        setTransparency(0.15f);
        search.setHint("Search by title or authors");
//        centerImv.setImageAlpha();
        search.revealFromMenuItem(R.id.action_search, getActivity());
//        for (Photo title : titlesAuthors) {
//            SearchResult option = new SearchResult(title.title, getResources().getDrawable(
//                    R.drawable.ic_open_book));
//            search.addSearchable(option);
//        }
        search.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
                // Hamburger has been clicked
                Toast.makeText(getContext(), "Menu click",
                        Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                // Use this to tint the screen
//                hideKeyboardFrom(getContext(),search);
            }

            @Override
            public void onSearchClosed() {
                // Use this to un-tint the screen
                closeSearch();
                setTransparency(1f);
            }

            @Override
            public void onSearchTermChanged(String term) {
                // React to the search term changing
                // Called after it has updated results

            }

            @Override
            public void onSearch(String searchTerm) {
//                hideKeyboardFrom(getContext(),search);
                getSearchedBooks(searchTerm);
                scrollView.postDelayed(()-> scrollView.fullScroll(View.FOCUS_DOWN),500);


            }

            @Override
            public void onResultClick(SearchResult result) {
//                hideKeyboardFrom(getContext(),search);
                setTransparency(1f);
                getSearchedBooks(result.title);
//                search.populateEditText("");
            }

            @Override
            public void onSearchCleared() {
//                hideKeyboardFrom(getContext(),search);
            }

        });

    }


    protected void closeSearch() {
//        hideKeyboardFrom(getContext(),search);
        search.hideCircularly(getActivity());
        if(search.getSearchText().isEmpty())toolbar.setTitle("Get books");
        toolbar.setTitleTextColor(Color.WHITE);
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private synchronized void setImagesBooks(ArrayList<BookE> str){
        int sum = 0;
        for(int i = 0 ;i < images.size();i++) {
            Glide.with(getContext())
                    .load(str.get(i).getImageLink())
                    .placeholder(R.drawable.placeholder)
                    .into(images.get(i));
            sum+=str.get(i).getNoDownloads();
        }
        noDownloads.setText(String.valueOf(sum) +" Downloads");
    }


    public void getTitlesForSearchView(){
        Call<ArrayList<BookE>> call = apiService.getMostDownloadedBooks();
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(response.isSuccessful()){
                    Log.d("Response ","Suc");


                }
                setTitlesAuthors(response.body());
                setImagesBooks(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {
                Log.d("Response ",t.getMessage());
                Snackbar.make(view, "Please check your network", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    void getSearchedBooks(String elementSearched){
        Call<ArrayList<BookE>> call = apiService.getBooksSearched(elementSearched);

        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(!response.isSuccessful()){
//                    Toast.makeText(getContext(),response.message(),Toast.LENGTH_LONG).show();
                    return;
                }
                if(response.body()!=null){
                    firstAdapter = new BookAdapter(getContext(),response.body());
                    firstManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                    firstRecyclerView.setLayoutManager(firstManager);
                    firstRecyclerView.setAdapter(firstAdapter);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRandomBooks(){
        Call<ArrayList<BookE>> call = apiService.getRandomBooks();
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(response.body()!=null){
                    secondAdapter = new BookAdapter(getContext(),response.body());
                    firstManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                    secondRecyclerView.setLayoutManager(firstManager);
                    secondRecyclerView.setAdapter(secondAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {

            }
        });
    }


    private void setTitlesAuthors(ArrayList<BookE> arrayList){
        this.titlesAuthors = arrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            hideKeyboardFrom(getContext(),view);
             getSearchedBooks(matches.get(0));
            scrollView.postDelayed(()-> scrollView.fullScroll(View.FOCUS_DOWN),500);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentSearchListener) {
            listener = (SearchFragment.OnFragmentSearchListener) context;
            switchListenr = (SearchFragment.OnFragmentGenreSwitchListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onButtonPressed() {
        if (listener != null) {
            listener.onFragmentSearch();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        switchListenr = null;
    }

    public interface OnFragmentSearchListener {

        void onFragmentSearch();
    }

    public interface OnFragmentGenreSwitchListener {
        void onFragmentSwitch(String category,String genreDescription,ArrayList<BookE> mostDownloaded);
    }
}
