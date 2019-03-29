package licenta.books.androidmobile.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BlurBuilder;
import licenta.books.androidmobile.activities.others.CheckForSDCard;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.interfaces.Constants;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    Intent intent;
    BookEDao bookEDao;
    private CompositeDisposable compositeDisposable;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        compositeDisposable = new CompositeDisposable();

        bookEDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookEDao();

        ArrayList<String> authors = new ArrayList<>();
        authors.add("UNUL");
        authors.add("Doi");
        ArrayList<String> categ = new ArrayList<>();
        categ.add("UNUL");
        categ.add("Doi");
        BookE bookE = new BookE("Muntdadaidai Carpati",authors,categ,222,"Ceva mare care plutea pe o mare in zare","Donel","11-11-2011"
                ,new byte[12],"undeva pe marte","1111112222");

       // bookEDao.insert(bookE);
        insertBook(bookE);
       // loadBook();
//
        intent = getIntent();
        final String url = intent.getStringExtra(Constants.KEY_IMAGE_URL);
        final BookE book = intent.getParcelableExtra("ceva");



       // bookEDao.insert(bookE);

//       List<BookE> listDinDb = bookEDao.getBooksFromDb();
//       if(listDinDb.size()>0){
//           Toast.makeText(getApplicationContext(),listDinDb.get(0).getIsbn(),Toast.LENGTH_LONG).show();
//       }

        final LinearLayout parent = findViewById(R.id.ll_details_parent);
        final LinearLayout blurBackground = parent.findViewById(R.id.ll_details);
        final ImageView bookCover = blurBackground.findViewById(R.id.iv_bookcover);
        final TextView description = parent.findViewById(R.id.tv_book_description);
        parent.setOnClickListener(this);
        blurBackground.setOnClickListener(this);
        final Button btnDownload = findViewById(R.id.btn_details_download);


        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap resultImg = BlurBuilder.blurImage(getApplicationContext(), resource);
                        Bitmap brightnessImg = BlurBuilder.changeBitmapContrastBrightness(resultImg,0.55f,1);
                        BitmapDrawable drawable = new BitmapDrawable(brightnessImg);

                        blurBackground.setBackgroundDrawable(drawable);
                        bookCover.setImageBitmap(resource);
                        description.setText(book.getDescription());
                    }
                });


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"SD Card not found",Toast.LENGTH_LONG).show();

                if (CheckForSDCard.isSDCardPresent()) {
                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                       DownloadFile();

                    } else {
                        //If permission is not present request for the same.
                        EasyPermissions.requestPermissions(DetailsActivity.this, getString(R.string.write_file), Constants.WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }


                } else {
                    Toast.makeText(getApplicationContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();

                }


//                view.getSettings().setJavaScriptEnabled(true);
//                view.loadUrl(book.getDownloadLink());
//
////                DownloadFile();
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private void insertBook(final BookE bookE) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                bookEDao.insert(bookE);

                return null;
            }
        }.execute();
    }

    private void loadBook() {
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                ArrayList<String> authors = new ArrayList<>();
                authors.add("UNUL");
                authors.add("Doi");
                ArrayList<String> categ = new ArrayList<>();
                categ.add("UNUL");
                categ.add("Doi");
                BookE bookE = new BookE("Muntidai Carpati",authors,categ,222,"Ceva mare care plutea pe o mare in zare","Donel","11-11-2011"
                        ,new byte[12],"undeva pe marte","1111112222");

                bookEDao.insert(bookE);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(getApplicationContext(), "Book has been inserted", Toast.LENGTH_LONG).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        disposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DetailsActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        DownloadFile();


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onClick(View v) {

    }



    private void DownloadFile(){
        final BookE book = intent.getParcelableExtra("ceva");
        ApiService downloadService = ApiClient.getRetrofit().create(ApiService.class);

        Call<ResponseBody> call = downloadService.downloadBookSync2(book.getFileID());

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG: ", "server contacted and has file");
                     String fileName = "marogladumenzau.epub";
//                    new AsyncTask<Void,Void,Void>(){
//                        @Override
//                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),fileName);

                            Log.d("TAG: ", "file download was a success? " + writtenToDisk);
//                            return null;
//                        }
//                    }.execute();

                } else {
                    Log.d("TAG: ", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body,String fileName) {
        try {
            // todo change the file location/name according to your needs

            File epubFile = new File(getExternalFilesDir(null) + File.separator + fileName );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[1024*100];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(epubFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("TAG", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
