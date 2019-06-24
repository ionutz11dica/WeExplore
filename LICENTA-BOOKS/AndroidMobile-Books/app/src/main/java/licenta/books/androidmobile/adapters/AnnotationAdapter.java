package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BookAnnotations;
import licenta.books.androidmobile.classes.Converters.TimestampConverter;

public class AnnotationAdapter extends BaseAdapter {

    Activity context;
    private ArrayList<BookAnnotations> bookAnnotations;
    private LayoutInflater inflater;

    public AnnotationAdapter(Activity context, ArrayList<BookAnnotations> bookAnnotations) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.bookAnnotations = bookAnnotations;
    }


    @Override
    public int getCount() {
        return bookAnnotations.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_annotation,null,true);

        ImageView iv_bookmark = rowView.findViewById(R.id.iv_bookmark);
        TextView tv_highlight = rowView.findViewById(R.id.tv_highlight);
        TextView tv_note = rowView.findViewById(R.id.tv_note_highlight);
        TextView tv_date = rowView.findViewById(R.id.tv_date_info);
        TextView tv_chapter = rowView.findViewById(R.id.tv_chapter_info);
//        String publishedDate = "<fonts color =#b7b8b6>Date</fonts>&nbsp &nbsp &nbsp <fonts color=#000>"+book.getPublishedDate()+"</fonts>";
        tv_note.setVisibility(View.GONE);

        if(bookAnnotations.get(position).getBookmark() != null){
            iv_bookmark.setVisibility(View.VISIBLE);
            String date = TimestampConverter.fromDateToString(bookAnnotations.get(position).getBookmark().getBookmarkDate());
            formatDate(tv_date, date);
            String chapterName = bookAnnotations.get(position).getBookmark().getChapterName();
            getChapterName(position, tv_highlight, chapterName);

            tv_chapter.setText(null);
        }
        if(bookAnnotations.get(position).getHighlight() !=null){
            setHighlightTextDesign(position, tv_highlight);

            String date = TimestampConverter.fromDateToString(bookAnnotations.get(position).getHighlight().getHighlightedDate());
            formatDate(tv_date, date);
            String chapterName = bookAnnotations.get(position).getHighlight().getChapterName();
            if(chapterName !=null){
                tv_chapter.setText(chapterName);
            }else{
                tv_chapter.setText("Chapter "+bookAnnotations.get(position).getHighlight().getChapterIndex());
            }

            iv_bookmark.setVisibility(View.GONE);
            if(bookAnnotations.get(position).getHighlight().isNote()){
                tv_note.setVisibility(View.VISIBLE);
                tv_note.setText(bookAnnotations.get(position).getHighlight().getNoteContent());
            }

        }

        return rowView;
    }

    private void getChapterName(int position, TextView tv_highlight, String chapterName) {
        if(chapterName !=null){
            tv_highlight.setText(chapterName);
        }else{
            tv_highlight.setText("Chapter "+bookAnnotations.get(position).getBookmark().getChapterIndex());
        }
    }

    private void formatDate(TextView tv_date, String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy");
        Date dateFormtted=null;

        try {
             dateFormtted = simpleDateFormat.parse(date);
             date = simpleDateFormat.format(dateFormtted);
             date = date.substring(0,1).toUpperCase()+date.substring(1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_date.setText(date);
        tv_date.setTextColor(Color.DKGRAY);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setHighlightTextDesign(int position, TextView tv_highlight) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/crimsontext.ttf");

        Spanned text1=Html.fromHtml("&ldquo;");
        Spanned text2=Html.fromHtml("&rdquo;");
        String text3 = bookAnnotations.get(position).getHighlight().getSelectedText();

        SpannableString span1 = new SpannableString(text1.toString());
        span1.setSpan(new ForegroundColorSpan(Color.parseColor("#b7b8b6")), 0, 1, 0);

        span1.setSpan(new AbsoluteSizeSpan(60),0,text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2.toString());
        span2.setSpan(new ForegroundColorSpan(Color.parseColor("#b7b8b6")), 0, 1, 0);
        span2.setSpan(new AbsoluteSizeSpan(60),0,text2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span3 = new SpannableString(text3);
        span3.setSpan(new ForegroundColorSpan(Color.parseColor("#"+Integer.toHexString(bookAnnotations.get(position).getHighlight().getColor()))), 0, text3.length(), 0);
        span3.setSpan(new AbsoluteSizeSpan(35),0,text3.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1,span3,span2);
        tv_highlight.setText(finalText);
        tv_highlight.setTypeface(typeface);
    }


}
