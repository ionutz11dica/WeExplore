package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;

public class ShelfBooksAdapter extends ArrayAdapter<BookE> {
    private Activity context;
    private List<BookE> books;



    public ShelfBooksAdapter(Activity context, List<BookE> books) {
        super(context, R.layout.row_shelf_books, books);
        this.context = context;
        this.books = books;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_shelf_books, null, false);
        TextView title = view.findViewById(R.id.tv_title_shelf);
        TextView authors = view.findViewById(R.id.tv_authors_shelf);
        TextView publicationYear= view.findViewById(R.id.tv_publication_year_shelf);
        TextView noPage = view.findViewById(R.id.tv_no_page_shelf);
        ImageView cover = view.findViewById(R.id.iv_bookcover_shelf);


        BookE bookE = books.get(position);
        title.setText(bookE.getTitle());
        authors.setText(BookAdapter.convertStringFromArray(bookE));
        publicationYear.setText(bookE.getPublishedDate());
        noPage.setText(String.valueOf(bookE.getPageCount()));
        Glide.with(getContext())
                .load(bookE.getImageLink())
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(cover);

        return view;
    }


}
