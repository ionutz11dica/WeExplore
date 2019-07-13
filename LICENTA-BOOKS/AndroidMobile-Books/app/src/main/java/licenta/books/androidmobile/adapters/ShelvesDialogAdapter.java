package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DialogFragments.AddBookInShelfDialogFragment;
import licenta.books.androidmobile.classes.CollectionPOJO;

public class ShelvesDialogAdapter extends RecyclerView.Adapter<ShelvesDialogAdapter.ViewHolder> {

    Context context;
    List<CollectionPOJO> arrayList ;
    OnClickItem onClickItem;
    ArrayList<Integer> collectionsIds = new ArrayList<>();

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    public interface OnClickItem {
        void respond(ArrayList<Integer> ids);
    }

    public ShelvesDialogAdapter(Context context, List<CollectionPOJO> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @Override
    public ShelvesDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_flexbox, parent, false);

        return new ShelvesDialogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(arrayList.get(position).collectionName +" (" + arrayList.get(position).bookIds+")");
        titleSetting(holder);
        holder.title.setOnClickListener(v -> {
            if(collectionsIds.contains(arrayList.get(position).idCollection)){
                collectionsIds.remove(arrayList.get(position).idCollection);
                holder.title.setBackgroundResource(R.drawable.rounded_corner_green);
                holder.title.setText(arrayList.get(position).collectionName +" (" + arrayList.get(position).bookIds+")");
                titleSetting(holder);
            }else{
                collectionsIds.add(arrayList.get(position).idCollection);
                holder.title.setBackgroundResource(R.drawable.rounded_corner_bluemarin);
                holder.title.setText("\u2713 "+arrayList.get(position).collectionName +" (" + arrayList.get(position).bookIds+")");
                titleSetting(holder);
            }
            onClickItem.respond(collectionsIds);
        });


    }

    private void titleSetting(ViewHolder holder) {
        holder.title.setPadding(7, 7, 7, 7);
        holder.title.setGravity(Gravity.CENTER);
        params.setMargins(10, 10, 10, 10);
        holder.title.setLayoutParams(params);
        holder.title.setTextSize(19f);
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

