package licenta.books.androidmobile.activities.DialogFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.ColorsAdapter;
import licenta.books.androidmobile.interfaces.Constants;

public class ColorsDialogFragment extends DialogFragment {
    GridView gridView;
    Bundle bundle;
    Integer currentColor;
    Boolean status;
    OnCompleteListenerColor listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.activity_colors_dialog_fragment,container,false);
         gridView = view.findViewById(R.id.grid_view_colors);
         final Integer[] colors = colorGenerator();

         //trebuie verifica de unde vine;  // true = background // false = foreground
         bundle = getArguments();

         if(bundle !=null){
             status = bundle.getBoolean(Constants.KEY_STATUS_COLOR);
             currentColor = bundle.getInt(Constants.KEY_CURRENT_COLOR);

         }else{
             currentColor=null;
         }



         ColorsAdapter colorsAdapter = new ColorsAdapter(getContext(),colors,currentColor);
         gridView.setAdapter(colorsAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onCompleteColors(colors[position],status);
                dismiss();
            }
        });



         return view;
    }


    @SuppressLint("Range")
    public Integer[] colorGenerator(){
        List<Integer> colorList = new ArrayList<>();

        int R = 245;
        int G;
        int B;
        int decreser = 0;
        for(int i = 0; i<5;i++){
            R = R-decreser;
            G = 245;
            for(int j=0;j < 5; j++){
                G = G-decreser;
                B = 245;
                for(int k =0;k<5;k++){
                    B = B-decreser;
                    colorList.add(Color.rgb(R,G,B));
                    decreser = 48;
                }
            }
        }
        return colorList.toArray(new Integer[0]);
    }

    public interface OnCompleteListenerColor{
        void onCompleteColors(Integer color, boolean status);
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (ColorsDialogFragment.OnCompleteListenerColor)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
