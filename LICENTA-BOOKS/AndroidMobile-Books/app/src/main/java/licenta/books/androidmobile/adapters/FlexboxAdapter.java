package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.CollectionPOJO;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.fragments.ShelfBooks;

public class FlexboxAdapter extends RecyclerView.Adapter<FlexboxAdapter.ViewHolder> {

    Context context;
    List<CollectionPOJO> arrayList ;
    OnClickItem onClickItem;

    public interface OnClickItem {
        void respond(CollectionPOJO collection,int pos);
    }



    public FlexboxAdapter(Context context, List<CollectionPOJO> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @Override
    public FlexboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_flexbox, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlexboxAdapter.ViewHolder holder, int position) {

        holder.title.setText(arrayList.get(position).collectionName +" (" + arrayList.get(position).bookIds+")");

        holder.title.setOnClickListener(v -> onClickItem.respond(arrayList.get(position),position));


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

