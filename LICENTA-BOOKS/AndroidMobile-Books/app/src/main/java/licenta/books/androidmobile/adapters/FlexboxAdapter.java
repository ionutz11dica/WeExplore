package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.Collections;

public class FlexboxAdapter extends RecyclerView.Adapter<FlexboxAdapter.ViewHolder> {

    Context context;
    List<Collections> arrayList ;

    public FlexboxAdapter(Context context,List<Collections> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public FlexboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_flexbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlexboxAdapter.ViewHolder holder, int position) {

        holder.title.setText(arrayList.get(position).getNameCollection());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Merge?"+position,Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }
}

