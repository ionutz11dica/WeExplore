package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DetailsActivity;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Converters.ArrayStringConverter;
import licenta.books.androidmobile.interfaces.Constants;

public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder> {
    private List<BookE> list = Collections.emptyList();
    private Context context;


    public ShelfAdapter(List<BookE> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ShelfViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shelf_card,viewGroup,false);
        return new ShelfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShelfViewHolder shelfViewHolder, final int i) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(list.get(i).getImage(),0,list.get(i).getImage().length);


        shelfViewHolder.imageView.setImageBitmap(bitmap);
        shelfViewHolder.title.setText(list.get(i).getTitle());
        shelfViewHolder.authors.setText(ArrayStringConverter.fromArrayList(list.get(i).getAuthors()));

        final Button button = shelfViewHolder.buttonContext;

        shelfViewHolder.buttonContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,button);
                popupMenu.inflate(R.menu.shelfbooks_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.shelfbooks_menu_details:
                                Intent intent = new Intent(context, DetailsActivity.class);
                                intent.putExtra(Constants.KEY_BOOK,list.get(i));

                                String bookCover = list.get(i).getImageLink().replace("zoom=0","zoom=4");
                                bookCover = bookCover.replace("curl","none");
                                intent.putExtra(Constants.KEY_IMAGE_URL,bookCover);

                                context.startActivity(intent);
                                return true;
                            case R.id.shelfbooks_menu_addCollection:
                                Toast.makeText(context,"Va urma!",Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void insert(int position, BookE bookE){
        list.add(position,bookE);
        notifyItemInserted(position);
    }

    public void remove(BookE bookE){
        int pos = list.indexOf(bookE);
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    public class ShelfViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView authors;
        ImageView imageView;
        Button buttonContext;

        ShelfViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.shelf_cardView);
            title = itemView.findViewById(R.id.tv_shelf_title);
            authors = itemView.findViewById(R.id.tv_shelf_authors);
            imageView = itemView.findViewById(R.id.iv_shelf_coverbook);
            buttonContext = itemView.findViewById(R.id.btn_shelf_buttonOptions);



            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();

                    Log.i("Poz: ",String.valueOf(pos));
                    if (pos != RecyclerView.NO_POSITION){

                    }
                }
            });
        }
    }
}
