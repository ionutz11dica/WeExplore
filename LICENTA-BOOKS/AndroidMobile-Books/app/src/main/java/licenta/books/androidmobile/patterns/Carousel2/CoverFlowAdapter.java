package licenta.books.androidmobile.patterns.Carousel2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.patterns.Carousel.Photo;

public class CoverFlowAdapter extends BaseAdapter {

    private ArrayList<Photo> data;
    private Context activity;

    public CoverFlowAdapter(Context context, ArrayList<Photo> objects) {
        this.activity = context;
        this.data = objects;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Photo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow_view, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.gameImage.setImageResource(data.get(position).getImageSource());
        Glide.with(activity)
                .load(data.get(position).imageLink)
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(viewHolder.gameImage);
        viewHolder.gameName.setText(data.get(position).title);



        return convertView;
    }



    private static class ViewHolder {
        private TextView gameName;
        private ImageView gameImage;

        public ViewHolder(View v) {
            gameImage = v.findViewById(R.id.image);
            gameName = v.findViewById(R.id.name);
        }
    }
}
