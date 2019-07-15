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
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.interfaces.Constants;

public class TypefaceAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> fonts;


    public TypefaceAdapter(Activity context, ArrayList<String> fonts) {
        super(context, R.layout.row_typeface_lv, fonts);
        this.context = context;
        this.fonts = fonts;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_typeface_lv, null, false);
        TextView typeface = view.findViewById(R.id.tv_typeface);
        typeface.setTextColor(Color.GRAY);
        Typeface tf = null;
        if(fonts.get(position).toLowerCase().equals("monospace")){
            tf = Typeface.MONOSPACE;
            typeface.setText(fonts.get(position));
            typeface.setTypeface(tf);
        }else if(fonts.get(position).toLowerCase().equals("sans-serif")){
            tf = Typeface.SANS_SERIF;
            typeface.setText(fonts.get(position));
            typeface.setTypeface(tf);
        }else if(fonts.get(position).toLowerCase().equals("cursive")){
            tf =  Typeface.createFromAsset(context.getAssets(),"fantasy.ttf");
            typeface.setText(fonts.get(position));
            typeface.setTypeface(tf);
        }else if(fonts.get(position).toLowerCase().equals("fantasy")){
            tf =  Typeface.createFromAsset(context.getAssets(),"inconsolata.ttf");
            typeface.setText(fonts.get(position));
            typeface.setTypeface(tf);
        }else if(fonts.get(position).toLowerCase().equals("timesroman")){
            tf =  Typeface.DEFAULT;
            typeface.setText(fonts.get(position));
            typeface.setTypeface(tf);
        }




        return view;
    }
}