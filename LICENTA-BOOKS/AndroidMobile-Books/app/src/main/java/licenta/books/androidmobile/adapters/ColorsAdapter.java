package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import licenta.books.androidmobile.R;

public class ColorsAdapter extends BaseAdapter {
    private Context context;
    private final Integer[] colors;
    public Integer currentColor;

    public ColorsAdapter(Context context,Integer[] colors,Integer currentColor){
        this.context=context;
        this.colors=colors;
        this.currentColor= currentColor;
    }
    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        gridView = inflater.inflate(R.layout.row_gridview_color,null,false);

        ImageView btnColor = gridView.findViewById(R.id.cv_color);
        btnColor.setBackgroundDrawable(changeDrawableColor(context,colors[position]));


//            btnColor.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context,"merge aici?",Toast.LENGTH_LONG).show();
//                }
//            });

        return gridView;
    }

    public Drawable changeDrawableColor(Context context,  int newColor) {
        Drawable mDrawable=null;
        if(this.currentColor.equals(newColor)){
             mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check_circle_black_24dp).mutate();

        }else{
             mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_remove_circle_black_24dp).mutate();
        }
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }
}
