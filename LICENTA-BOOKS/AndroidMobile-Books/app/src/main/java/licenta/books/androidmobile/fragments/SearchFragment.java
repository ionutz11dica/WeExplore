package licenta.books.androidmobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import licenta.books.androidmobile.R;

public class SearchFragment extends Fragment {

    private OnFragmentSearchListener listener;


    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout,container,false);
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
