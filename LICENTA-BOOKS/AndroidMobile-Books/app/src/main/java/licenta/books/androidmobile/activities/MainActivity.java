package licenta.books.androidmobile.activities;


import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.activities.others.CustomFont;
import licenta.books.androidmobile.activities.others.HelperApp;
import licenta.books.androidmobile.activities.others.HelperSettings;
import licenta.books.androidmobile.adapters.BookAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;

import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.interfaces.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity   {
    MultiSnapRecyclerView firstRecyclerView;
    LinearLayoutManager firstManager;
    BookAdapter firstAdapter;
    ApiService apiService;
    UserDao userDao;
    UserMethods userMethods;
    SharedPreferences sharedPreferences;
    HelperApp app;
    HelperSettings setting;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (HelperApp)getApplication();
        setting = new HelperSettings(getApplicationContext());
        openDao();
        registerFonts();

        if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){
            apiService = ApiClient.getRetrofit().create(ApiService.class);
            initComp();
            getBooks();
        }else{
            Toast.makeText(getApplicationContext(),"Internet Problem",Toast.LENGTH_LONG).show();
        }

    }

    void initComp(){
        firstRecyclerView = findViewById(R.id.first_recycler_view);
        sharedPreferences = getSharedPreferences(Constants.KEY_PREF_USER,MODE_PRIVATE);
        String status = sharedPreferences.getString(Constants.KEY_STATUS,null);
        if(status.equals("with")){
            String email = sharedPreferences.getString(Constants.KEY_USER_EMAIL,null);
            Single<User> user = userMethods.verifyExistenceGoogleAcount(email);
            user.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(User user) {
                            RxBus.publishUser(user);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }else{
            String username = sharedPreferences.getString(Constants.KEY_USER_USERNAME,null);
            String password = sharedPreferences.getString(Constants.KEY_USER_PASSWORD,null);

            Single<User> user = userMethods.verifyAvailableAccount(username,password);
            user.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(User user) {
                            RxBus.publishUser(user);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void openDao(){
        userDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserDao();
        userMethods = UserMethods.getInstance(userDao);

    }


    public void registerFonts() {
        this.registerCustomFont("Mayflower","Mayflower Antique.ttf");
        this.registerCustomFont("Bradleyhand","bradleyhand.ttf");
        this.registerCustomFont("Cantarell","cantarell.ttf");
        this.registerCustomFont("CrimsonText","crimsontext.ttf");
        this.registerCustomFont("Inconsolata","inconsolata.ttf");
        this.registerCustomFont("JosefinSans","josefinsans.ttf");
        this.registerCustomFont("Molengo","molengo.ttf");
        this.registerCustomFont("Simplicity","simplicity.ttf");
        this.registerCustomFont("ReenieBeanie","reeniebeanie.ttf");

    }

    public void registerCustomFont(String fontFaceName,String fontFileName) {
        setting.copyFontToDevice(fontFileName);
        app.customFonts.add(new CustomFont(fontFaceName,fontFileName));
    }

    void getBooks(){
        Call<ArrayList<BookE>> call = apiService.getBooks();

        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_LONG).show();
                    return;
                }
                if(response.body()!=null){
                    ArrayList<BookE> books = response.body();
                     firstAdapter = new BookAdapter(getApplicationContext(),books);
                     firstManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                     firstRecyclerView.setLayoutManager(firstManager);
                     firstRecyclerView.setAdapter(firstAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {

            }
        });


    }
}
