package licenta.books.androidmobile.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.Book;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreenActivity extends AppCompatActivity {
    private ApiService apiService;
    Retrofit retrofit;
    ArrayList<Book> books=new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        fetchingData();
        ArrayList<Book> ceva = fetchingData();
        intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.putParcelableArrayListExtra("arr",ceva);
                    startActivity(intent);

     }

    private void initRetrofit(){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.7:4000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

    }

    private ArrayList<Book> fetchingData(){
        initRetrofit();


        Call<ArrayList<Book>> call = apiService.getBooks();

        call.enqueue(new Callback<ArrayList<Book>>() {
            @Override
            public void onResponse(Call<ArrayList<Book>> call, Response<ArrayList<Book>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                }

              books =  response.body();

                for(Book r : books){
                    Log.i("id carti:",r.get_id());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Book>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
            }
        });


        return books;
    //        if(call.isExecuted()){
    //            intent = new Intent(getApplicationContext(),LoginActivity.class);
    //            intent.putParcelableArrayListExtra("arr",books);
    //            startActivity(intent);
    //        }
    }







}
