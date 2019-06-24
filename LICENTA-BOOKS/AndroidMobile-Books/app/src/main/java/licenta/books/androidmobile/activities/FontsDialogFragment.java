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

import java.util.ArrayList;
import java.util.Objects;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.activities.others.HelperApp;
import licenta.books.androidmobile.adapters.TypefaceAdapter;
import licenta.books.androidmobile.interfaces.Constants;

public class FontsDialogFragment extends DialogFragment {
    ListView fontsList;
    OnCompleteListenerFonts listener;
    HelperApp helperApp;
    private ArrayList<CustomFont> customFonts = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.activity_fonts_dialog_fragment,container,false);
         helperApp = (HelperApp) Objects.requireNonNull(getActivity()).getApplication();
         fontsList = view.findViewById(R.id.lv_typeface_fonts);
         createFontsList();

         TypefaceAdapter adapter = new TypefaceAdapter(getActivity(), customFonts);
         fontsList.setAdapter(adapter);

         fontsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Typeface typeface = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(),customFonts.get(position).fontFileName);
                 listener.onCompleteFonts(typeface,customFonts.get(position).fontFaceName,customFonts.get(position));
                 dismiss();
             }
         });

         Window window = getDialog().getWindow();
         window.setGravity(Gravity.CENTER|Gravity.BOTTOM);

         return view;
    }

    public interface OnCompleteListenerFonts{
        void onCompleteFonts(Typeface typeface,String name,CustomFont customFont);
    }

    public void createFontsList(){
        customFonts.addAll(helperApp.customFonts);
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
