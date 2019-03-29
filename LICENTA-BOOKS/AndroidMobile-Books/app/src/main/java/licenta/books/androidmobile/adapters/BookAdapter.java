package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DetailsActivity;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.interfaces.Constants;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {


    private Context mContext;
    private List<BookE> bookList;


    public BookAdapter(Context mContext, List<BookE> movieList){
        this.mContext = mContext;
        this.bookList = movieList;
    }

    @NonNull
    @Override
    public BookAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookAdapter.MyViewHolder viewHolder, int i){

        viewHolder.title.setText(bookList.get(i).getTitle());
        String authors = convertStringFromArray(bookList.get(i));
        viewHolder.authors.setText(authors);
        String bookCover = bookList.get(i).getImageLink().replace("zoom=0","zoom=4");

        Glide.with(mContext)
                .load(bookCover)
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(viewHolder.thumbnail);
    }

    @Override
    public int getItemCount(){
        return bookList.size();
    }

    public String convertStringFromArray(BookE book){
        StringBuilder sb = new StringBuilder();
        for(String author : book.getAuthors()){
            if(book.getAuthors().size()>1){
                sb.append(author).append(",");
            }else{
                sb.append(author);
            }
        }
        return sb.toString();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
         TextView title;
         ImageView thumbnail;
         TextView authors;

         MyViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.tv_title);
            thumbnail = view.findViewById(R.id.iv_thumbnail);
            authors = view.findViewById(R.id.tv_authors);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    Log.i("Poz: ",String.valueOf(pos));
                    if (pos != RecyclerView.NO_POSITION){
                        BookE clickedDataItem = bookList.get(pos);
                        String bookCover = bookList.get(pos).getImageLink().replace("zoom=0","zoom=4");
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra(Constants.KEY_IMAGE_URL,bookCover);
                    intent.putExtra("ceva",bookList.get(pos));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                   // Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
                }
            });
        }
    }
}
