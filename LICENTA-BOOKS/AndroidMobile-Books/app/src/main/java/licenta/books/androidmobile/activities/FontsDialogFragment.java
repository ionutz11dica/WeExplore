package licenta.books.androidmobile.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import licenta.books.androidmobile.R;

public class FontsDialogFragment extends DialogFragment {
    ListView fontsList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.activity_fonts_dialog_fragment,container,false);
         fontsList = view.findViewById(R.id.lv_typeface_fonts);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER|Gravity.BOTTOM);

         return view;
    }
}
