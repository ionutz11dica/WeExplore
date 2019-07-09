package licenta.books.androidmobile.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import xyz.sahildave.widget.SearchViewLayout;

import static android.support.constraint.Constraints.TAG;

public class SearchFragment extends Fragment {
    Toolbar toolbarSearch;
    FloatingActionButton fab;
    SearchViewLayout searchViewLayout;
    private OnFragmentSearchListener listener;


    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout,container,false);
        toolbarSearch = view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbarSearch);
        searchViewLayout = view. findViewById(R.id.search_view_container);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        searchViewLayout.setExpandedContentSupportFragment(getActivity(),this); //error
        searchViewLayout.handleToolbarAnimation(toolbarSearch);
        searchViewLayout.setCollapsedHint("Collapsed Hint");
        searchViewLayout.setExpandedHint("Expanded Hint");
//        searchViewLayout.setHint("Global Hint");

        ColorDrawable collapsed = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ColorDrawable expanded = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.default_color_expanded));
        searchViewLayout.setTransitionDrawables(collapsed, expanded);
        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                searchViewLayout.collapse();
                Snackbar.make(searchViewLayout, "Start Search for - " + searchKeyword, Snackbar.LENGTH_LONG).show();
            }
        });
        searchViewLayout.setOnToggleAnimationListener(new SearchViewLayout.OnToggleAnimationListener() {
            @Override
            public void onStart(boolean expanding) {
                if (expanding) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onFinish(boolean expanded) { }
        });
        searchViewLayout.setSearchBoxListener(new SearchViewLayout.SearchBoxListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s + "," + start + "," + count + "," + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s + "," + start + "," + before + "," + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s);
            }
        });


        return view;
    }

    public void onButtonPressed() {
        if (listener != null) {
            listener.onFragmentSearch();
        }
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
