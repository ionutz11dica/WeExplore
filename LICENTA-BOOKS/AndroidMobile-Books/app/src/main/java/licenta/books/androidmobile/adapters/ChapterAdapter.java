package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    private  String chapterName;
    private Integer noPageChapter;

    public ChapterAdapter(Activity context, ArrayList<String> content) {
        super(context, R.layout.row_chapter_lv, content);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.content = content;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_chapter_lv, null, true);

        TextView tvContent = rowView.findViewById(R.id.tv_chapter);
        View viewStatus = rowView.findViewById(R.id.view_chapter);
        final TextView noPage = rowView.findViewById(R.id.tv_noPage);

        Disposable d = RxBus.subscribeChapterName(new Consumer<String>() {
            @Override
            public void accept(String nameChapter) throws Exception {
                chapterName = nameChapter;
            }
        });
        d.dispose();

        Disposable disposable = RxBus.subscribeChapter(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                noPageChapter = integer;
            }
        });
        disposable.dispose();

        if(!chapterName.equals(content.get(position))){
            viewStatus.setBackgroundColor(Color.TRANSPARENT);
            noPage.setVisibility(View.GONE);
            tvContent.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.97f));
        }else{
            noPage.setVisibility(View.VISIBLE);
            noPage.setText("p."+noPageChapter);
        }
        tvContent.setText(content.get(position));

        return rowView;

    }
}
