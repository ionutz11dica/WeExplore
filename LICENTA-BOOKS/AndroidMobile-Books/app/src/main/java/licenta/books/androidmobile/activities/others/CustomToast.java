package licenta.books.androidmobile.activities.others;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.LoginActivity;

public final class CustomToast {
    private View layout;
    private TextView mTextView;
    private ImageView mImageView;


    public CustomToast(Context context) {
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(LoginActivity.LAYOUT_INFLATER_SERVICE);
        View layout =inflater.inflate(R.layout.custom_toast_layout,null);
        init(layout);
    }

    private void init(View view) {
        layout = view.findViewById(R.id.mbContainer);
        layout.setVisibility(View.GONE);
        mTextView =  view.findViewById(R.id.tv_message);
        mImageView = view.findViewById(R.id.iv_toast);

    }

    public void show(String message, int id,Context context) {

        layout.setVisibility(View.VISIBLE);
        mTextView.setText(message);
        mImageView.setImageResource(id);
        if(id==0){
            mImageView.setVisibility(View.GONE);
        }

        Toast toast = new Toast(context);

        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void show(String message,Context context) {
        show(message,0,context);

    }


}
