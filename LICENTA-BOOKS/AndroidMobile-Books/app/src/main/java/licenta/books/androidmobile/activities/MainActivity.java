package licenta.books.androidmobile.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.adapters.BookAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;

import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity   {
    MultiSnapRecyclerView firstRecyclerView;
    LinearLayoutManager firstManager;
    BookAdapter firstAdapter;
    ApiService apiService;
    UserDao userDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openDao();

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

    }

    void openDao(){
        userDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserDao();
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
