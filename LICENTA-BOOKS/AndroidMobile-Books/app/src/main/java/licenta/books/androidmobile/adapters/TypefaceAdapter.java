package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.interfaces.Constants;

public class TypefaceAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] content;


    public TypefaceAdapter(Activity context, String[] content) {
        super(context, R.layout.row_typeface_lv, content);
        this.context = context;
        this.content = content;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_typeface_lv, null, false);

        TextView typeface = view.findViewById(R.id.tv_typeface);
        typeface.setTextColor(Color.GRAY);
          Typeface tf = Typeface.createFromAsset(context.getAssets(),"font/"+content[position].toLowerCase()+".ttf");
        typeface.setText(content[position]);
        typeface.setTypeface(tf);

        return view;
    }
}