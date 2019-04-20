package licenta.books.androidmobile.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.fragments.ChapterFragment;
import licenta.books.androidmobile.fragments.InfoFragment;

public class AnnotationBookActivity extends AppCompatActivity implements InfoFragment.OnFragmentInteractionListener,ChapterFragment.OnFragmentInteractionListener {
    Toolbar toolbar;
    SegmentedGroup segmentedBtn;
    RadioButton btnInfo;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    Fragment selectedFragment;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_book);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new InfoFragment()).commit();
        }
        initComp();



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initComp(){
        toolbar = findViewById(R.id.annotation_toolbar);
//        toolbar.setTitle(bookE.getTitle());
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        segmentedBtn = findViewById(R.id.segmented2);
        segmentedBtn.setTintColor(Color.DKGRAY);
        btnInfo = findViewById(R.id.btn_infobook);
        btnInfo.setChecked(true);

//        selectedFragment = new InfoFragment();
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment,selectedFragment).addToBackStack("hai");
//        fragmentTransaction.commit();

    }

    public void handleChangeFragment(View view){

        if(view == findViewById(R.id.btn_infobook)){
            selectedFragment = new InfoFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment,selectedFragment);
            fragmentTransaction.commit();
        }else if(view == findViewById(R.id.btn_contents)){

            selectedFragment = new ChapterFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment,selectedFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
