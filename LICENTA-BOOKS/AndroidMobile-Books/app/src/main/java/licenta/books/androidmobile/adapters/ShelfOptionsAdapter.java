package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.interfaces.Constants;

public class ShelfOptionsAdapter extends ArrayAdapter<String> {
    private Activity context;
    private String[] strings;

    public ShelfOptionsAdapter(Activity context,String[] strings) {
        super(context, R.layout.row_typeface_lv,strings);
        this.context = context;
        this.strings = strings;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_typeface_lv, null, false);

        TextView sortType = view.findViewById(R.id.tv_typeface);
        sortType.setTextColor(Color.GRAY);

        if(position==2){
            sortType.setText(strings[position]);
            sortType.setTextColor(Color.parseColor("#f83e4b"));
        }else{
            sortType.setText(strings[position]);
        }

        return view;
    }
}
