package licenta.books.androidmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import licenta.books.androidmobile.R;

public class StylePopupActivity extends FragmentActivity {
    Button button;
    Animation slideUpAnimation, slideDownAnimation;
    RelativeLayout test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_popup);

        button = findViewById(R.id.test_id);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width,(int)(height*0.6));

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.y = 0;



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontsDialogFragment dialogFragment = new FontsDialogFragment();
                dialogFragment.show(getSupportFragmentManager(),"what");

            }
        });


    }
}
