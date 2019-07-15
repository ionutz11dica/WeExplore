package licenta.books.androidmobile.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DialogFragments.AddBookInShelfDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.BookScanDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.CreateShelfDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.ShelfOptionsDialogFragment;
import licenta.books.androidmobile.activities.DialogFragments.StrategySortDialogFragment;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Collections;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.fragments.BackupFragment;
import licenta.books.androidmobile.fragments.GenreBooksFragment;
import licenta.books.androidmobile.fragments.ScannerFragment;
import licenta.books.androidmobile.fragments.SearchFragment;
import licenta.books.androidmobile.fragments.ShelfBooks;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics.BarcodeGraphicTracker;
import licenta.books.androidmobile.patterns.Carousel.Photo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static licenta.books.androidmobile.interfaces.Constants.KEY_PREF_USER;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class HomeActivity extends AppCompatActivity implements ScannerFragment.OnScannerInteractionListener, BarcodeGraphicTracker.BarcodeUpdateListener,
                                    BookScanDialogFragment.OnCompleteListenerBookScan,ShelfBooks.OnFragmentInteractionListener, CreateShelfDialogFragment.OnCompleteListenerShelf
                                    , StrategySortDialogFragment.OnCompleteListenerStrategySort, ShelfOptionsDialogFragment.OnCompleteListenerOptions, ShelfBooks.OnSwitchFragment
                                    , SearchFragment.OnFragmentSearchListener, SearchFragment.OnFragmentGenreSwitchListener, GenreBooksFragment.OnFragmentGenreBooksListener ,
                            AddBookInShelfDialogFragment.OnCompleteAddBooksListener,ShelfBooks.OnRefreshFragmentListener,BackupFragment.OnFragmentInteractionListener{

    public static final int REQUEST_CODE_OPENING = 1;
    public static final int REQUEST_CODE_CREATION = 2;
    public static final int REQUEST_CODE_PERMISSIONS = 2;

    BottomNavigationView bottomNavigationView;
    ApiService apiService;
    Bundle bundle = new Bundle();
    LinearLayout layout;
    RadioButton rbBooksScanned;
    User user;
    ArrayList<Photo> photosTitles;
    UserDao userDao;
    UserMethods userMethods;
    SharedPreferences sharedPreferences;

     Fragment scannerFragment ;
     Fragment shelfBooks  ;
     Fragment searchFragment ;
     Fragment backupFragment ;

     FragmentManager fm ;
    Fragment active;

    Fragment prev = null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Disposable d = RxBus.subscribeUser(userr -> user = userr);
        d.dispose();
        if (user == null) {
            openDb();
            sharedPreferences = getSharedPreferences(KEY_PREF_USER,MODE_PRIVATE);
            String with = sharedPreferences.getString(Constants.KEY_STATUS,"with");
            if(with.equals("with")){
                String email = sharedPreferences.getString(Constants.KEY_USER_EMAIL,null);
                Single<User> userSingle = userDao.verifyExistenceGoogleAcount(email);
                userSingle.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(User user) {
                                Log.d("Publish ","successfully");
                                setUserData(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("Publish ","Error");
                            }
                        });
            }else{
                String username = sharedPreferences.getString(Constants.KEY_USER_USERNAME,null);
                String password = sharedPreferences.getString(Constants.KEY_USER_PASSWORD,null);
                Single<User> userSingle = userDao.verifyAvailableAccount(username,password);
                userSingle.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(User user) {
                                Log.d("Publish ","successfully");
                                setUserData(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("Publish ","Error");
                            }
                        });
            }

        }

          scannerFragment = new ScannerFragment();
          shelfBooks = new ShelfBooks();
          searchFragment = new SearchFragment();
          backupFragment = new BackupFragment();

          fm = getSupportFragmentManager();
          active = searchFragment;

        fm.beginTransaction().add(R.id.frament_contianer,scannerFragment,"scanner").hide(scannerFragment).commit();
        fm.beginTransaction().add(R.id.frament_contianer,shelfBooks,"shelf").hide(shelfBooks).commit();
        fm.beginTransaction().add(R.id.frament_contianer,backupFragment,"backup").hide(backupFragment).commit();
        fm.beginTransaction().add(R.id.frament_contianer,searchFragment,"search").commit();
        initComp();
    }

    private void setUserData(User user) {
        RxBus.publishUser(user);
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
                        fm.beginTransaction().hide(active).show(shelfBooks).commit();
                        active = shelfBooks;
                        return true;
                    case R.id.item_Scanner:
                        fm.beginTransaction().hide(active).show(scannerFragment).commit();
                        active = scannerFragment;
                        return true;
                    case R.id.item_searchBooks:
                        fm.beginTransaction().hide(active).show(searchFragment).commit();
                        active = searchFragment;
                        return true;

                    case R.id.item_backUp:
                        fm.beginTransaction().hide(active).show(backupFragment).commit();
                        active = backupFragment;
                        return true;
                }
                return false;
            };

    public void getBookByISBN(String isbn){

        Call<BookE> call = apiService.getBookByISBN(isbn);

        call.enqueue(new Callback<BookE>() {
            @Override
            public void onResponse(Call<BookE> call, Response<BookE> response) {
                Log.d("ISBN Error", String.valueOf(response.code()));
                if(response.isSuccessful() ){
                    prev = getSupportFragmentManager().findFragmentByTag("Tag");
                    if(prev==null) {
                        assert response.body() != null;
                        RxBus.publishBook(response.body());

                        Call<User> updateScanned = apiService.syncUserBooksAddEmail(response.body().get_id(), user.getEmail());
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
                Log.d("ISBN Error",t.getMessage());
            }
        });
    }

    public void setPhotosTitles(ArrayList<Photo> photosTitles){
        this.photosTitles = photosTitles;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("test",photosTitles);
        active.setArguments(bundle);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void openDb(){
        userDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserDao();
        userMethods = UserMethods.getInstance(userDao);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCompleteCreateShelf(Collections name) {

    }

    @Override
    public void onCompleteStrategy(String strategy) {

    }

    @Override
    public void onSwitchFragment(int id) {
        bottomNavigationView.setSelectedItemId(id);
    }

    @Override
    public void onCompleteOptions() {

    }

    @Override
    public void onFragmentSearch() {

    }

    @Override
    public void onFragmentGenreBooks(GenreBooksFragment genresFragment) {
//        fm.beginTransaction().add(R.id.frament_contianer,genresFragment,"genres").hide(genresFragment).commit();
        fm.beginTransaction().hide(active).show(searchFragment).commit();
        active = searchFragment;
    }



    @Override
    public void onCompleteAddBooks(Collections collections) {

    }

    @Override
    public void onRefreshFragment() {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("shelf");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onFragmentSwitch(String category, String genreDescription,ArrayList<BookE> mostDownloaded) {
        final Fragment genresFragment = new GenreBooksFragment();
        fm.beginTransaction().add(R.id.frament_contianer,genresFragment,"genres").hide(genresFragment).commit();
        bundle.putString("testCategory",category);
        bundle.putString("testDescription",genreDescription);
        if(mostDownloaded!=null){
            bundle.putParcelableArrayList("testMostDownloaded",mostDownloaded);
        }
        genresFragment.setArguments(bundle);
        fm.beginTransaction().hide(active).show(genresFragment).commit();
        active = genresFragment;
    }
}
