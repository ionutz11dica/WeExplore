package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;

public class GenreBooksAdapter extends ArrayAdapter<BookE> {
    private Activity context;
    private ArrayList<BookE> books;
    private ArrayList<BookE> wantToRead;
    private OnButtonClickListener onButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClick(BookE bookE, boolean isWanted);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.onButtonClickListener = listener;
    }


    public GenreBooksAdapter(Activity context, ArrayList<BookE> books,ArrayList<BookE> wantToRead) {
        super(context, R.layout.row_genre_wants, books);
        this.context = context;
        this.books = books;
        this.wantToRead = wantToRead;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_genre_wants, null, false);
        TextView title = view.findViewById(R.id.tv_title_genre);
        TextView authors = view.findViewById(R.id.tv_authors_genre);
        TextView publicationYear = view.findViewById(R.id.tv_no_downloads_genre);
        Button button = view.findViewById(R.id.btn_wants_to_read);
        ImageView imageView = view.findViewById(R.id.imv_cover_genre);


        if(isWantToRead(books.get(position))){
            button.setBackgroundColor(Color.WHITE);
            button.setTextColor(Color.parseColor("#1abb9c"));
            button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_confirm, 0, 0, 0);
        }

        BookE bookE = books.get(position);
        title.setText(bookE.getTitle());
        authors.setText(BookAdapter.convertStringFromArray(bookE));
        publicationYear.setText("No. Downloads: "+String.valueOf(bookE.getNoDownloads()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(),"nervuuu",Toast.LENGTH_LONG).show();
                if(button.getCurrentTextColor()==Color.WHITE){
                    button.setBackgroundColor(Color.WHITE);
                    button.setTextColor(Color.parseColor("#1abb9c"));
                    button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_confirm, 0, 0, 0);
                    onButtonClickListener.onButtonClick(bookE,true);
                }else{
                    button.setBackgroundColor(Color.parseColor("#1abb9c"));
                    button.setTextColor(Color.WHITE);
                    button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    onButtonClickListener.onButtonClick(bookE,false);
                }

            }
        });

        Glide.with(getContext())
                .load(bookE.getImageLink())
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(imageView);

        return view;
    }

    private boolean isWantToRead(BookE bookE){
        for(BookE book: wantToRead){
            if(book.get_id().contains(bookE.get_id())){
                return true;
            }
        }
        return false;
    }

}
