package licenta.books.androidmobile.activities.DialogFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.CheckedOutputStream;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.activities.others.HelperApp;
import licenta.books.androidmobile.adapters.StrategySortAdapter;
import licenta.books.androidmobile.adapters.TypefaceAdapter;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.StrategySortBooks.StrategySort;

public class StrategySortDialogFragment extends DialogFragment {
    ListView strategySortLv;
    OnCompleteListenerStrategySort listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_fonts_dialog_fragment,container,false);
        strategySortLv = view.findViewById(R.id.lv_typeface_fonts);


        StrategySortAdapter adapter = new StrategySortAdapter(getActivity(), Constants.STRATEGY_SORTERS);
        strategySortLv.setAdapter(adapter);

        strategySortLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                StrategySort strategySort = Constants.STRATEGY_SORTERS[position];
                i.putExtra(Constants.KEY_STRATEGY, (Parcelable) strategySort);
                i.putExtra(Constants.KEY_STRATEGY_NAME,Constants.STRATEGY_SORT[position]);
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER|Gravity.BOTTOM);

        return view;
    }

    public interface OnCompleteListenerStrategySort{
        void onCompleteStrategy(String strategy);
    }



    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (OnCompleteListenerStrategySort) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
