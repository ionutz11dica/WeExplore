package licenta.books.androidmobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.interfaces.Constants;

public class NoteDialogFragment extends DialogFragment {
    RelativeLayout notePopup;

    Button cancel;
    Button save;
    EditText noteContent;
    OnCompleteListener listener;
    boolean isUpdate=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.note_highlight_popup,container,false);



        notePopup = view.findViewById(R.id.relatve_layout_note);
        cancel= view.findViewById(R.id.btn_note_cancel);
        save = view.findViewById(R.id.btn_note_save);
        noteContent = view.findViewById(R.id.et_note_text);
        setupUI(noteContent);

        Bundle bundle = getArguments();
        if(bundle!=null && bundle.containsKey(Constants.KEY_NOTE_CONTENT) || bundle!=null && bundle.containsKey(Constants.KEY_HIGHLIGHT_EXISTS)) {
            String noteC = bundle.getString(Constants.KEY_NOTE_CONTENT);
            boolean isHighlighted = bundle.getBoolean(Constants.KEY_HIGHLIGHT_EXISTS);
            if(isHighlighted){
                noteContent.setText(null);
            }
            if(isHighlighted && noteC!=null){
                noteContent.setText(noteC);
            }
            isUpdate=true;
        }else {
            isUpdate=false;
        }

        if(noteContent.getText().toString().equals("")){
            save.setTextColor(getResources().getColor(R.color.granite));
            save.setClickable(false);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onComplete(noteContent.getText().toString(),isUpdate);
                getDialog().dismiss();
            }
        });


        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("")){
                    save.setTextColor(getResources().getColor(R.color.colorAccentTrans));
                    save.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputs = noteContent.getText().toString();
                if(!inputs.equals("")){
                    save.setTextColor(getResources().getColor(R.color.colorAccentTrans));
                }else{
                    save.setClickable(false);
                    save.setTextColor(getResources().getColor(R.color.granite));
                }

            }
        });


        return view;

    }

    public interface OnCompleteListener{
        void onComplete(String note,boolean isUpdated);
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if(focusedView!=null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());

                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
