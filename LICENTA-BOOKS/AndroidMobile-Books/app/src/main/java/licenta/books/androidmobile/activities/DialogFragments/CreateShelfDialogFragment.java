package licenta.books.androidmobile.activities.DialogFragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.classes.CollectionPOJO;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.CollectionDao;
import licenta.books.androidmobile.database.DaoMethods.CollectionMethods;
import licenta.books.androidmobile.interfaces.Constants;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class CreateShelfDialogFragment extends DialogFragment {
    OnCompleteListenerShelf listener;
    EditText edShelfName;
    Button btnCreate;
    CollectionDao collectionDao;
    CollectionMethods collectionMethods;
    User user;
    Bundle bundle;
    String shelfName;
    CollectionPOJO collectionPOJO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shelf_dialog_fragment,null,false);
        bundle = getArguments();

        if(bundle!=null) {
          collectionPOJO = bundle.getParcelable(Constants.KEY_COLLECTION);
        }
        openDb();
        edShelfName = view.findViewById(R.id.et_shelf_create);
        btnCreate = view.findViewById(R.id.btn_create_shelf);
        if(collectionPOJO!=null){
            edShelfName.setText(collectionPOJO.collectionName);
            btnCreate.setText("Update Shelf");
        }
        Disposable d = RxBus.subscribeUser(us -> user = us);
        d.dispose();
        btnCreate.setOnClickListener(btnCreateListener);



        return view;
    }

    private View.OnClickListener btnCreateListener = v -> {
        if(collectionPOJO == null) {
            if (edShelfName.getText().toString().length() > 0) {
                Collections collection = new Collections(Objects.hash(edShelfName.getText().toString()),
                        edShelfName.getText().toString(), user.getUserId());
                CollectionPOJO pojo = new CollectionPOJO();
                pojo.bookIds = 0;
                pojo.collectionName = edShelfName.getText().toString();

                collectionMethods.insertCollection(collection);
                Intent i = new Intent();
                i.putExtra(Constants.KEY_COLLECTION, pojo);
                i.putExtra(Constants.KEY_IS_UPDATE_SHELF,false);
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                hideKeyboardFrom(getContext(),v);
                dismiss();
            } else {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.edit_animation);
                edShelfName.startAnimation(shake);
            }
        }else{
            if(edShelfName.getText().length()>0){
                collectionMethods.updateShelf(Objects.hash(edShelfName.getText().toString()),edShelfName.getText().toString(),collectionPOJO.collectionName);
                collectionPOJO.collectionName = edShelfName.getText().toString();
                Intent i = new Intent();
                i.putExtra(Constants.KEY_COLLECTION, collectionPOJO);
                i.putExtra(Constants.KEY_IS_UPDATE_SHELF,true);
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                hideKeyboardFrom(getContext(),v);
                dismiss();
            }else {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.edit_animation);
                edShelfName.startAnimation(shake);
            }

        }
    };


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface OnCompleteListenerShelf{
        void onCompleteCreateShelf(Collections collections);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void openDb(){
        collectionDao = AppRoomDatabase.getInstance(getContext()).getCollectionDao();
        collectionMethods = CollectionMethods.getInstance(collectionDao,getContext());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (CreateShelfDialogFragment.OnCompleteListenerShelf)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
