package licenta.books.androidmobile.activities.DialogFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.ShelfOptionsAdapter;
import licenta.books.androidmobile.interfaces.Constants;

public class ShelfOptionsDialogFragment extends DialogFragment {
    ListView lvOptions;
    Button btnCancel;
    OnCompleteListenerOptions listener;
    ShelfOptionsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.shelf_option_dialog_fragment,container,false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        lvOptions = view.findViewById(R.id.lv_options);
        btnCancel = view.findViewById(R.id.brn_cancel_options);
        btnCancel.setTextColor(Color.GRAY );

        adapter = new ShelfOptionsAdapter(getActivity(), Constants.OPTIONS_SHELF);
        lvOptions.setAdapter(adapter);

//        StrategySortAdapter adapter = new StrategySortAdapter(getActivity(), Constants.STRATEGY_SORTERS);
//        strategySortLv.setAdapter(adapter);
//
//        strategySortLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent();
//                StrategySort strategySort = Constants.STRATEGY_SORTERS[position];
//                i.putExtra(Constants.KEY_STRATEGY, (Parcelable) strategySort);
//                i.putExtra(Constants.KEY_STRATEGY_NAME,Constants.STRATEGY_SORT[position]);
//                assert getTargetFragment() != null;
//                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
//                dismiss();
//            }
//        });

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER|Gravity.BOTTOM);

        return view;
    }

    public interface OnCompleteListenerOptions{
        void onCompleteStrategy(String strategy);
    }



    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (OnCompleteListenerOptions)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
