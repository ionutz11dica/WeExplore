package licenta.books.androidmobile.activities;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import licenta.books.androidmobile.R;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shelf_dialog_fragment,null,false);
        openDb();
        edShelfName = view.findViewById(R.id.et_shelf_create);
        btnCreate = view.findViewById(R.id.btn_create_shelf);
        Disposable d = RxBus.subscribeUser(us -> user = us);
        d.dispose();
        btnCreate.setOnClickListener(btnCreateListener);



        return view;
    }

    private View.OnClickListener btnCreateListener = v -> {
        Collections collection = new Collections(Objects.hash(edShelfName.getText().toString()),
                edShelfName.getText().toString(), user.getUserId());
        collectionMethods.insertCollection(collection);
        Intent i = new Intent();
        i.putExtra(Constants.KEY_COLLECTION,collection);
        assert getTargetFragment() != null;
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    };


    public interface OnCompleteListenerShelf{
        void onCompleteCreateShelf(Collections collections);
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
