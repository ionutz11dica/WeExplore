package licenta.books.androidmobile.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.w3c.dom.Text;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BlurBuilder;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.interfaces.Constants;


public class InfoFragment extends Fragment {
    private BookE book;
    private OnFragmentInteractionListener mListener;

    private TextView tvBookDescription;
    private TextView tvBookInformationDate;
    private TextView tvBookInformationPublisher;
    private TextView tvBookInformationFormat;
    private TextView tvBookInformationCategories;
    private TextView tvBookInformationPages;
    private TextView tvBookInformationPermissions;

    public InfoFragment() {
        // Required empty public constructor
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        blurCoverBook(view);
        initComp(view);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private BookE getReadingBook(){
        Disposable d = RxBus.subscribeBook(new Consumer<BookE>() {
            @Override
            public void accept(BookE bookE) throws Exception {
                book = bookE;
            }
        });
        d.dispose();
        return book;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blurCoverBook(View view) {
        BookE bookE = getReadingBook();
        final LinearLayout parent = view.findViewById(R.id.ll_details_parent);
        final LinearLayout blurBackground = parent.findViewById(R.id.ll_details);
        final ImageView bookCover = blurBackground.findViewById(R.id.iv_bookcover);
        final TextView description = parent.findViewById(R.id.tv_book_description_fragment);
//        parent.setOnClickListener();
//        blurBackground.setOnClickListener(this);


        Glide.with(this)
                .asBitmap()
                .load(bookE.getImageLink())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap resultImg = BlurBuilder.blurImage(getActivity().getApplicationContext(), resource);
                        Bitmap brightnessImg = BlurBuilder.changeBitmapContrastBrightness(resultImg, 0.55f, 1);
                        BitmapDrawable drawable = new BitmapDrawable(brightnessImg);

                        blurBackground.setBackgroundDrawable(drawable);
                        bookCover.setImageBitmap(resource);
                        description.setText(book.getDescription());
                    }
                });
    }

    private void initComp(View view){
        tvBookDescription = view.findViewById(R.id.tv_book_description_fragment);
        tvBookInformationDate = view.findViewById(R.id.tv_book_information_fragment_publishedDate);
        tvBookInformationFormat = view.findViewById(R.id.tv_book_information_fragment_format);
        tvBookInformationPages = view.findViewById(R.id.tv_book_information_fragment_pages);
        tvBookInformationPublisher = view.findViewById(R.id.tv_book_information_fragment_publisher);
        tvBookInformationCategories = view.findViewById(R.id.tv_book_information_fragment_categories);
        tvBookInformationPermissions = view.findViewById(R.id.tv_book_information_fragment_permissions);

        tvBookDescription.setText(book.getDescription());

        String publishedDate = "<font color =#b7b8b6>Date</font>&nbsp &nbsp &nbsp <font color=#000>"+book.getPublishedDate()+"</font>";
        tvBookInformationDate.setText(Html.fromHtml(publishedDate));

        String format = "<font color =#b7b8b6>Format</font>&nbsp &nbsp &nbsp <font color=#000>EPUB</font>";
        tvBookInformationFormat.setText(Html.fromHtml(format));

        String publisher = "<font color =#b7b8b6>Publisher</font>&nbsp &nbsp &nbsp <font color=#000>"+book.getPublisher()+"</font>";
        tvBookInformationPublisher.setText(Html.fromHtml(publisher));

        String pages = "<font color =#b7b8b6>Pages</font>&nbsp &nbsp &nbsp <font color=#000>"+book.getPageCount()+"</font>";
        tvBookInformationPages.setText(Html.fromHtml(pages));

        String categories = "<font color =#b7b8b6>Categories</font>&nbsp &nbsp &nbsp <font color=#000>"+BookE.convertFromArray(book.getAuthors())+"</font>";
        tvBookInformationCategories.setText(Html.fromHtml(categories));

        String permissions = "<font color =#b7b8b6>Permissions</font>&nbsp &nbsp &nbsp <font color=#000>&#8226; Allow viewing on any device<br/> \t \t \t \t \t \t &nbsp &nbsp &nbsp &nbsp &#8226; Allow copying/pasting</font>";
        tvBookInformationPermissions.setText(Html.fromHtml(permissions));

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
