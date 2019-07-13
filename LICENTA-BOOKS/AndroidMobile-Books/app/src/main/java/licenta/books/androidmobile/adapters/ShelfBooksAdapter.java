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
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;

public class ShelfBooksAdapter extends ArrayAdapter<BookE> {
    private Activity context;
    private List<BookE> books;
    private List<BookE> origData;

    public ShelfBooksAdapter(Activity context, List<BookE> books) {
        super(context, R.layout.row_shelf_books, books);
        this.context = context;
        this.books = books;
        this.origData = new ArrayList<>(books);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_shelf_books, null, false);

//        if (selectedIds.contains(position)) {
//            view.setSelected(true);
//            view.setPressed(true);
//            view.setBackgroundColor(Color.parseColor("#FF9912"));
//        }
//        else
//        {
//            view.setSelected(false);
//            view.setPressed(false);
//            view.setBackgroundColor(Color.TRANSPARENT);
//        }

        TextView title = view.findViewById(R.id.tv_title_shelf);
        TextView authors = view.findViewById(R.id.tv_authors_shelf);
        TextView publicationYear = view.findViewById(R.id.tv_publication_year_shelf);
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

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    List<BookE> founded = new ArrayList<>();
                    for (BookE book : books) {
                        if (book.getTitle().toLowerCase().contains(constraint)) {
                            founded.add(book);
                        }
                    }
                    results.values = founded;
                    results.count = founded.size();
                } else {
                    results.values = origData;
                    results.count = origData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                for (BookE book : (List<BookE>) results.values) {
                    add(book);
                }
                notifyDataSetChanged();
            }

        };
    }


}
