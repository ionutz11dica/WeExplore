package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.fragments.ScannerFragment;

public class ScannedBooksAdapter extends ArrayAdapter<BookE> {
    private Activity context;
    private ArrayList<BookE> books;

    public static ArrayList<String> checks = new ArrayList<>();
    public ArrayList<Integer> allPos = new ArrayList<>();

    private OnCheckedChangedListener listener;

    public ScannedBooksAdapter(Activity context, ArrayList<BookE> books) {
        super(context, R.layout.row_scannedbooks, books);
        this.context = context;
        this.books = books;
    }

    public void setOnCheckedChangedListener(OnCheckedChangedListener listener) {
        this.listener = listener;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.row_scannedbooks, null, false);
        allPos.add(position);
        TextView title = view.findViewById(R.id.tv_title_lv);
        TextView authors = view.findViewById(R.id.tv_authors_lv);
        TextView isbn = view.findViewById(R.id.tv_isbn_lv);
        ImageView cover = view.findViewById(R.id.iv_bookcover_lv);
        CheckBox select = view.findViewById(R.id.rb_select);

        select.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(!isChecked){
                if(ScannerFragment.flagForFlag){
                    checks.clear();
                    listener.onCheckChanged(checks);
                }else {
                    checks.remove(books.get(position).get_id());

                    listener.onCheckChanged(checks);
                    notifyDataSetChanged();
                }
            }else{
                Log.d("Pos:", String.valueOf(position));
                checks.add(books.get(position).get_id());

                listener.onCheckChanged(checks);
                notifyDataSetChanged();
            }
        });

        BookE bookE = books.get(position);
        title.setText(bookE.getTitle());
        authors.setText(BookAdapter.convertStringFromArray(bookE));
        isbn.setText(bookE.getIsbn());
        Glide.with(getContext())
                .load(bookE.getImageLink())
                .placeholder(R.drawable.ic_error_outline_24dp)
                .into(cover);

        return view;
    }

   public interface OnCheckedChangedListener{
        void onCheckChanged(ArrayList<String> checks);
   }
}
