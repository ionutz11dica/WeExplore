package licenta.books.androidmobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.fragments.AnnotationFragment;
import licenta.books.androidmobile.fragments.ScannerFragment;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics.BarcodeGraphicTracker;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class HomeActivity extends AppCompatActivity implements ScannerFragment.OnScannerInteractionListener, BarcodeGraphicTracker.BarcodeUpdateListener,
                                    BookScanDialogFragment.OnCompleteListenerBookScan{
    BottomNavigationView bottomNavigationView;
    ApiService apiService;
    Bundle bundle = new Bundle();
    LinearLayout layout;
    RadioButton rbBooksScanned;

    final Fragment scannerFragment = new ScannerFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = scannerFragment;

    Fragment prev = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fm.beginTransaction().add(R.id.frament_contianer,scannerFragment,"scanner").hide(scannerFragment).commit();
        initComp();
    }

    private void initComp(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        apiService = ApiClient.getRetrofit().create(ApiService.class);
        bottomNavigationView.setSelectedItemId(R.id.item_searchBooks);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                switch (item.getItemId()){
                    case R.id.item_shelfBooks:
                        startActivity(new Intent(getApplicationContext(),ShelfActivity.class));
                        return true;
                    case R.id.item_Scanner:
                        fm.beginTransaction().hide(active).show(scannerFragment).commit();
                        active = scannerFragment;
                        return true;
                    case R.id.item_searchBooks:
                        //

                        return true;
                    case R.id.item_backUp:

                        return true;
                }
                return false;
            };

    public void getBookByISBN(String isbn){

        Call<BookE> call = apiService.getBookByISBN(isbn);

        call.enqueue(new Callback<BookE>() {
            @Override
            public void onResponse(Call<BookE> call, Response<BookE> response) {
                if(response.isSuccessful() ){
                    prev = getSupportFragmentManager().findFragmentByTag("Tag");
                    if(prev==null) {
                        assert response.body() != null;
                        RxBus.publishBook(response.body());

                        Call<User> updateScanned = apiService.syncUserBooksAddEmail(response.body().get_id(), "nicolae.ionut9711@gmail.com");
                        addBookToScannedBooks(response.body());
                        userScannedBookAddCloud(updateScanned);
                        BookScanDialogFragment bookScanDialogFragment = new BookScanDialogFragment();
                        bookScanDialogFragment.show(getSupportFragmentManager(), "Tag");
                        prev = getSupportFragmentManager().findFragmentByTag("Tag");
                    }else{
                        layout = findViewById(R.id.nav_scanner);
                        Snackbar.make(layout, "Book already scanned!",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                }else {
                    layout = findViewById(R.id.nav_scanner);
                    Snackbar.make(layout, "No books found!",
                            Snackbar.LENGTH_LONG)
                            .show();
                }
                Log.d("ISBN Error",String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<BookE> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addBookToScannedBooks(BookE book){
//        if(book.getIsbn()) {
//            ScannerFragment.scannedBooks.add(book);
//        }
    }

    @Override
    public void onFragmentInteraction(String uri) {

    }


    private void userScannedBookAddCloud(Call<User> call) {
        if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){
            rbBooksScanned=findViewById(R.id.btn_bookScanned);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }else{
            layout = findViewById(R.id.nav_scanner);
            Snackbar.make(layout, "No network found!",
                    Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        //verificam daca e isbn
        getBookByISBN(barcode.rawValue);


    }


    @Override
    public void onCompleteBookScanned(Integer color, boolean status) {

    }
}
