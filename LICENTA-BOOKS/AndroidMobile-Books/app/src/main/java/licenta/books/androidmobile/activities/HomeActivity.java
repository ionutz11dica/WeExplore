package licenta.books.androidmobile.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.fragments.AnnotationFragment;
import licenta.books.androidmobile.fragments.ScannerFragment;

public class HomeActivity extends AppCompatActivity implements ScannerFragment.OnScannerInteractionListener {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComp();
    }

    private void initComp(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = new ScannerFragment();

//                switch (item.getItemId()){
//                    case R.id.nav_scanner:
//                        selectedFragment = new ScannerFragment();
//                        break;
//                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frament_contianer, selectedFragment).commit();
                return true;
            };

    @Override
    public void onFragmentInteraction(String uri) {

    }

}
