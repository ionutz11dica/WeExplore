package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.StrategySortBooks.StrategySort;

public class StrategySortAdapter extends ArrayAdapter<StrategySort> {
    private Activity context;
    private StrategySort[] strategies;


    public StrategySortAdapter(Activity context, StrategySort[] strategies) {
        super(context, R.layout.row_typeface_lv, strategies);
        this.context = context;
        this.strategies = strategies;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_typeface_lv, null, false);

        TextView sortType = view.findViewById(R.id.tv_typeface);
        sortType.setTextColor(Color.GRAY);
        sortType.setText(Constants.STRATEGY_SORT[position]);

        return view;
    }
}
