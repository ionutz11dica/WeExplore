package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skytree.epub.SearchResult;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.Chapter;

public class SearchAdapter extends ArrayAdapter<SearchResult> {
    private final Activity context;
    private ArrayList<SearchResult> searchResults;

    public SearchAdapter(Activity context, ArrayList<SearchResult> searchResults) {
        super(context, R.layout.row_search_lv, searchResults);

        this.context = context;
        this.searchResults = searchResults;

    }

    @NonNull
    @Override
    public View getView(int position,  View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_search_lv, null, true);

        TextView searchContent = rowView.findViewById(R.id.tv_search_content);

        TextView chapter = rowView.findViewById(R.id.tv_search_chapter);

        searchContent.setText(searchResults.get(position).text);
        chapter.setText(searchResults.get(position).chapterTitle);

        return rowView;
    }
}
