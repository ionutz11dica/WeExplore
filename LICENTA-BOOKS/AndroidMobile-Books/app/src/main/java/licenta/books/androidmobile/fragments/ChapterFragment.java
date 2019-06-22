package licenta.books.androidmobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.ReaderBookActivity;
import licenta.books.androidmobile.adapters.ChapterAdapter;
import licenta.books.androidmobile.classes.Chapter;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.interfaces.Constants;


public class ChapterFragment extends Fragment {
    ListView chapterListView;
    ArrayList<Chapter> arrayList = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public ChapterFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chpater, container, false);
        initComp(view);
        chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RxBus.publishsChapter(position);
                Intent intent = new Intent(getActivity(), ReaderBookActivity.class);
                intent.putExtra(Constants.KEY_CHAPTER,position);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();

            }
        });
        return view;
    }

    private void initComp(View view){
        getChapterName();



        ChapterAdapter chapterAdapter = new ChapterAdapter(getActivity(),arrayList);
        chapterListView = view.findViewById(R.id.chapter_fragment_listview);
        chapterListView.setAdapter(chapterAdapter);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void getChapterName(){
        Disposable d = RxBus.subscribeChapterList(new Consumer<ArrayList<Chapter>>() {
            @Override
            public void accept(ArrayList<Chapter> strings) throws Exception {
                arrayList = strings;
            }
        });
        d.dispose();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
