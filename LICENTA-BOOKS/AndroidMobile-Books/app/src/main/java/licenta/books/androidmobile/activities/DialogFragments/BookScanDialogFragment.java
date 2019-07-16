package licenta.books.androidmobile.activities.DialogFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.reactivex.disposables.Disposable;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DetailsActivity;
import licenta.books.androidmobile.adapters.BookAdapter;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.interfaces.Constants;

public class BookScanDialogFragment extends DialogFragment {
    private OnCompleteListenerBookScan listener;
    Button moreDetails;
    TextView tvTitle;
    TextView tvAuthors;
    TextView tvIsbn;
    ImageView ivBookCover;
    RadioButton rbScannedBooks;

    Bundle bundle;
    BookE bookE;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bookscan_dialog_fragment,null,false);
        bundle = getArguments();

        initComp(view);

        return view;
    }

    private void initComp(View view){
        moreDetails = view.findViewById(R.id.btn_moreDetails);
        tvTitle = view.findViewById(R.id.tv_title_scan);
        tvAuthors = view.findViewById(R.id.tv_authors_scan);
        tvIsbn = view.findViewById(R.id.tv_isbn_scan);
        ivBookCover = view.findViewById(R.id.iv_bookcover_scan);
        rbScannedBooks = view.findViewById(R.id.btn_bookScanned);
        moreDetails.setTextColor(Color.DKGRAY);

        Drawable drawable = null;
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.btn_rounded).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(Constants.COLOR_PALLET), PorterDuff.Mode.SRC_IN));
        moreDetails.setBackgroundDrawable(drawable);

        Disposable d = RxBus.subscribeBook(book -> bookE = book);
        d.dispose();


                tvTitle.setText(bookE.getTitle());
                tvIsbn.setText(" ISBN: "+ bookE.getIsbn());
                tvAuthors.setText(" by "+BookAdapter.convertStringFromArray(bookE));

        Glide.with(getContext())
                .load(bookE.getImageLink())
                .placeholder(R.drawable.placeholder)
                .into(ivBookCover);

        moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DetailsActivity.class);
                i.putExtra(Constants.KEY_BOOK,bookE);
                startActivity(i);
            }
        });

    }

    public interface OnCompleteListenerBookScan{
        void onCompleteBookScanned(Integer color, boolean status);
    }

    public boolean isColorDark(int color){
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(600, 450);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (BookScanDialogFragment.OnCompleteListenerBookScan)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

}
