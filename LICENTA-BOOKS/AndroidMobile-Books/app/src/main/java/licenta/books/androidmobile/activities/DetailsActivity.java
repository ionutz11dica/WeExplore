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
import android.widget.ProgressBar;
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

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
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
import licenta.books.androidmobile.downloadProgress.Download;
import licenta.books.androidmobile.downloadProgress.DownloadProgressListener;
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
    Button btnDownload;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        compositeDisposable = new CompositeDisposable();
        bookEDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookEDao();


        intent = getIntent();
        final String url = intent.getStringExtra(Constants.KEY_IMAGE_URL);
        final BookE book = intent.getParcelableExtra("ceva");


        final LinearLayout parent = findViewById(R.id.ll_details_parent);
        final LinearLayout blurBackground = parent.findViewById(R.id.ll_details);
        final ImageView bookCover = blurBackground.findViewById(R.id.iv_bookcover);
        final TextView description = parent.findViewById(R.id.tv_book_description);
        parent.setOnClickListener(this);
        blurBackground.setOnClickListener(this);
        btnDownload = findViewById(R.id.btn_details_download);


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
//                Toast.makeText(getApplicationContext(),"SD Card not found",Toast.LENGTH_LONG).show();

                if (CheckForSDCard.isSDCardPresent()) {
                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                       TestDownloadRxJava();

                    } else {
                        //If permission is not present request for the same.
                        EasyPermissions.requestPermissions(DetailsActivity.this, getString(R.string.write_file), Constants.WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();
                }

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

    private void TestDownloadRxJava(){
        final BookE book = intent.getParcelableExtra("ceva");
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Download download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);


            }
        };

        File outputFile = new File(getExternalFilesDir(null) + File.separator +"test.epub");

        new ApiClient(listener).downloadAPK("books/"+book.get_id(), outputFile, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(),"S-a descarcat cu mare failed",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(),"S-a descarcat",Toast.LENGTH_LONG).show();
            }
        });
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
                    final String fileName = "marogladumenzau.epub";
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),fileName);

                            Log.d("TAG: ", "file download was a success? " + writtenToDisk);
                            return null;
                        }
                    }.execute();

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

    private void progressBarDownloading(){
        int[] location = new int[2]; //coordonatele absolute;
        btnDownload.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        int width = btnDownload.getWidth()/4;
        int height = btnDownload.getHeight();

        btnDownload.setVisibility(View.GONE);
        RingProgressBar ringProgressBar = new RingProgressBar(getApplicationContext());
        ringProgressBar.setTextSize(16);
        ringProgressBar.setMax(100);
        ringProgressBar.setRingWidth(width);


    }
}
