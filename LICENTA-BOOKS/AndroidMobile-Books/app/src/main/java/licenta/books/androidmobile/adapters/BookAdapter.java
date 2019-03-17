package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.Book;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {


    private Context mContext;
    private List<Book> bookList;


    public BookAdapter(Context mContext, List<Book> movieList){
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
        String bookCover = bookList.get(i).getImageLink().replace("zoom=0","zoom=5");

        Glide.with(mContext)
                .load(bookCover)
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(viewHolder.thumbnail);
    }

    @Override
    public int getItemCount(){
        return bookList.size();
    }

    public String convertStringFromArray(Book book){
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
                   /* if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("movies", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();
                    }*/
                }
            });
        }
    }
}
