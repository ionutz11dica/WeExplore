package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.RxJava.RxBus;

public class ChapterAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> content;
    private  BookState bookStateUse;

    public ChapterAdapter(Activity context, ArrayList<String> content) {
        super(context, R.layout.row_chapter_lv, content);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.content = content;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_chapter_lv, null, true);

        TextView tvContent = rowView.findViewById(R.id.tv_chapter);
        View viewStatus = rowView.findViewById(R.id.view_chapter);

        Disposable d = RxBus.subscribeBookState(new Consumer<BookState>() {
            @Override
            public void accept(BookState bookState) throws Exception {
                bookStateUse = bookState;
            }
        });
        d.dispose();
        if(bookStateUse.getNoChapter()!=content.indexOf(content.get(position))){
            viewStatus.setBackgroundColor(Color.TRANSPARENT);
        }

        tvContent.setText(content.get(position));

        return rowView;

    }
}
