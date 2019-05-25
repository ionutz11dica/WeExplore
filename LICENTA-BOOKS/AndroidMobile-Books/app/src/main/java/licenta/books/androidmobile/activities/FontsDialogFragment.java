package licenta.books.androidmobile.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.TypefaceAdapter;
import licenta.books.androidmobile.interfaces.Constants;

public class FontsDialogFragment extends DialogFragment {
    ListView fontsList;
    OnCompleteListenerFonts listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.activity_fonts_dialog_fragment,container,false);
         fontsList = view.findViewById(R.id.lv_typeface_fonts);

         TypefaceAdapter adapter = new TypefaceAdapter(getActivity(), Constants.TYPEFACE_NAMES);
         fontsList.setAdapter(adapter);

         fontsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"font/"+Constants.TYPEFACE_NAMES[position].toLowerCase()+".ttf");
                 listener.onCompleteFonts(typeface,Constants.TYPEFACE_NAMES[position],position);
                 dismiss();
             }
         });

         Window window = getDialog().getWindow();
         window.setGravity(Gravity.CENTER|Gravity.BOTTOM);

         return view;
    }

    public interface OnCompleteListenerFonts{
        void onCompleteFonts(Typeface typeface,String name,int index);
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (FontsDialogFragment.OnCompleteListenerFonts)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

}
