package licenta.books.androidmobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import licenta.books.androidmobile.adapters.BookAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.patterns.Carousel.Photo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private OnFragmentSearchListener listener;
    private SearchBox search;
    private Toolbar toolbar;
    private ArrayList<Photo> titlesAuthors;
    private ApiService apiService;

    private LinearLayoutManager firstManager;
    private MultiSnapRecyclerView firstRecyclerView;
    private BookAdapter firstAdapter;

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

    private LinearLayout ll_noBooks;


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

        animationDrawable = (AnimationDrawable) relativeBackground.getBackground();
        animationDrawable2 = (AnimationDrawable) toolbar.getBackground();


        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable2.setEnterFadeDuration(3000);
        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable2.setExitFadeDuration(2000);
//        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
//        anim.setDuration(10000);
//
//        float[] hsv;
//        final int[] runColor = new int[1];
//        int hue = 0;
//        hsv = new float[3]; // Transition color
//        hsv[1] = 1;
//        hsv[2] = 1;
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//
//                hsv[0] = 360 * animation.getAnimatedFraction();
//
//                runColor[0] = Color.HSVToColor(hsv);
//                relativeBackground.setBackgroundColor(runColor[0]);
//            }
//        });
//
//        anim.setRepeatCount(Animation.INFINITE);
//
//        anim.start();
//
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            int i=0;
//            public void run() {
//                relativeBackground.setBackgroundResource(imageArray[i]);
//                final TransitionDrawable background = (TransitionDrawable) relativeBackground.getBackground();
//                background.startTransition(300);
//
//                i++;
//                if(i>imageArray.length-1)
//                {
//                    i=0;
//                }
//                handler.postDelayed(this, 5000);  //for interval...
//            }
//        };
//        handler.postDelayed(runnable, 2000); //for initial delay..

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()&& animationDrawable2 !=null && !animationDrawable2.isRunning()) {
            // start the animation
            animationDrawable2.start();
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning() && animationDrawable2 !=null && animationDrawable2.isRunning()) {
            // stop the animation
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
                Toast.makeText(getContext(),"merge?",Toast.LENGTH_LONG).show();
            }
        });

        relativeBackground = view.findViewById(R.id.relative_background);
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

    private synchronized void setImagesBooks(ArrayList<Photo> str){
        for(int i = 0 ;i < images.size();i++) {
            Glide.with(getContext())
                    .load(str.get(i).imageLink)
                    .placeholder(R.drawable.ic_error_outline_24dp)
                    .into(images.get(i));
        }
    }


    public void getTitlesForSearchView(){
        Call<ArrayList<Photo>> call = apiService.getRandomTitles();
        call.enqueue(new Callback<ArrayList<Photo>>() {
            @Override
            public void onResponse(Call<ArrayList<Photo>> call, Response<ArrayList<Photo>> response) {
                if(response.isSuccessful()){
                    Log.d("Response ","Suc");


                }
                setTitlesAuthors(response.body());
                setImagesBooks(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Photo>> call, Throwable t) {
                Log.d("Response ",t.getMessage());
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

    private void setTitlesAuthors(ArrayList<Photo> arrayList){
        this.titlesAuthors = arrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        hideKeyboardFrom(getContext(),search);
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            search.populateEditText(matches.get(0));
            hideKeyboardFrom(getContext(),view);
            getSearchedBooks(matches.get(0));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentSearchListener) {
            listener = (SearchFragment.OnFragmentSearchListener) context;
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
    }

    public interface OnFragmentSearchListener {
        // TODO: Update argument type and name
        void onFragmentSearch();
    }
}
