package licenta.books.androidmobile.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.BlurBuilder;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.activities.others.CheckForSDCard;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.RxJava.RxBus;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.BookEDao;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.BookMethods;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.downloadProgress.Download;
import licenta.books.androidmobile.downloadProgress.DownloadProgressListener;
import licenta.books.androidmobile.interfaces.Constants;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    ProgressBar progressBar;
    Button btnDownload;

    Intent intent;
    ApiService apiService;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    UserBookJoinDao userBookJoinDao;
    BookEDao bookEDao;
    UserDao userDao;

    UserMethods userMethods;
    BookMethods bookMethods;
    UserBookMethods userBookMethods;
    BookE book;


    @SuppressLint("CommitPrefEdits")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        openDao();
        progressBar = findViewById(R.id.id_progress);
        sharedPreferences = getSharedPreferences(Constants.KEY_PREF_USER, 0);
        editor = sharedPreferences.edit();

        btnDownload = findViewById(R.id.btn_details_download);
        apiService = ApiClient.getRetrofit().create(ApiService.class);
        intent = getIntent();
        book = intent.getParcelableExtra(Constants.KEY_BOOK);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(book.getTitle());
        }

        blurCoverBook();
        createUserBookJoin(book,null);
        fetchBookBehaviour(book);






        btnDownload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                if (CheckForSDCard.isSDCardPresent()) {
                    if (EasyPermissions.hasPermissions(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        TestDownloadRxJava();
                    } else {
                        EasyPermissions.requestPermissions(DetailsActivity.this, getString(R.string.write_file),
                                Constants.WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DetailsActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        TestDownloadRxJava();


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onClick(View v) {

    }

    private void TestDownloadRxJava() {

        final UserBookJoin[] userBookJoin = new UserBookJoin[1];
        createUserBookJoin(book, userBookJoin);


        final File outputFile = new File(getExternalFilesDir(null) + File.separator + book.getTitle() + ".epub");
        @SuppressLint("SdCardPath") final String pathFile = "/sdcard/Android/data/licenta.books.androidmobile/files/"+book.getTitle() +".epub";
//        String filepath = outputFile.getAbsolutePath();
        Call<ResponseBody> call = apiService.downloadBitmap(book.getImageLink());
        if(btnDownload.getText().toString().equals("Download")) {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                            byte[] bytes = stream.toByteArray();

//                            book.setImage(bytes);
                            book.setPathBook(pathFile);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

            final DownloadProgressListener listener = new DownloadProgressListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    Download download = new Download();
                    download.setTotalFileSize(contentLength);
                    download.setCurrentFileSize(bytesRead);
                    int progress = (int) ((bytesRead * 100) / contentLength);
                    download.setProgress(progress);
                }
            };


            new ApiClient(listener).downloadAPK(Constants.BASE_URL + "books/download/" + book.getFileID(), outputFile, new Observer<InputStream>() {
                @Override
                public void onSubscribe(Disposable d) {
                    btnDownload.setText("Wait...");
                    bookMethods.insertBook(book);

                }

                @Override
                public void onNext(InputStream object) {
                    userBookMethods.insertUserBook(userBookJoin[0]);
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("Error:",e.getMessage());
                }

                @Override
                public synchronized void onComplete() {

                    btnDownload.setText("READ");
                    increseDownloads(book.get_id());



                }
            });
        }else{
            intent.putExtra(Constants.KEY_BOOK,book);
            RxBus.publishBook(book);
            intent = new Intent(getApplicationContext(),ReaderBookActivity.class);
            startActivity(intent);
//            Toast.makeText(getApplicationContext(),"Va urma!",Toast.LENGTH_LONG).show();
        }
    }

    private void createUserBookJoin(final BookE book, final UserBookJoin[] userBookJoin) {
        String status = sharedPreferences.getString(Constants.KEY_STATUS, null);


        if (status.equals("with")) {
            final String email = sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);



            Single<User> userSingle = userMethods.verifyExistenceGoogleAcount(email);
            userSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public synchronized void onSuccess(final User user) {
                            Log.d("User id: ", user.getUserId().toString());
                            if(userBookJoin==null){
                                verifyExistenceBook(book.get_id(),user.getUserId());
                            }else {
                                userBookJoin[0] = new UserBookJoin(book.get_id(), user.getUserId());
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("Err: ", e.getMessage());
                        }
                    });
        } else {
            final String username = sharedPreferences.getString(Constants.KEY_USER_USERNAME, null);
            final String password = sharedPreferences.getString(Constants.KEY_USER_PASSWORD, null);


            Single<User> userSingle = userMethods.verifyAvailableAccount(username, password);
            userSingle.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public synchronized void onSuccess(User user) {
                            Log.d("User id: ", user.getUserId().toString());
                            if(userBookJoin==null){
                                verifyExistenceBook(book.get_id(),user.getUserId());
                            }else {
                                userBookJoin[0] = new UserBookJoin(book.get_id(), user.getUserId());

                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }


    private void increseDownloads(String id){
        Call<ResponseBody> responseBodyCall = apiService.increaseNoDownloads(id);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blurCoverBook() {
        String url = intent.getStringExtra(Constants.KEY_IMAGE_URL);
        final BookE book = intent.getParcelableExtra(Constants.KEY_BOOK);

        if(url==null){
            url = book.getImageLink();
        }

        final LinearLayout parent = findViewById(R.id.ll_details_parent);
        final LinearLayout blurBackground = parent.findViewById(R.id.ll_details);
        final ImageView bookCover = blurBackground.findViewById(R.id.iv_bookcover);
        final TextView description = parent.findViewById(R.id.tv_book_description);
        parent.setOnClickListener(this);
        blurBackground.setOnClickListener(this);



        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap resultImg = BlurBuilder.blurImage(getApplicationContext(), resource);
                        Bitmap brightnessImg = BlurBuilder.changeBitmapContrastBrightness(resultImg, 0.55f, 1);
                        BitmapDrawable drawable = new BitmapDrawable(brightnessImg);

                        blurBackground.setBackgroundDrawable(drawable);
                        bookCover.setImageBitmap(resource);
                        description.setText(book.getDescription());
                    }
                });
    }

    private void fetchBookBehaviour(BookE bookE){
        RxBus.publishBook(bookE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void openDao() {
        bookEDao = AppRoomDatabase.getInstance(getApplicationContext()).getBookEDao();
        userBookJoinDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserBookDao();
        userDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserDao();
        userMethods = UserMethods.getInstance(userDao);
        bookMethods = BookMethods.getInstance(bookEDao);
        userBookMethods = UserBookMethods.getInstance(userBookJoinDao);
    }

    private void verifyExistenceBook(String bookId,Integer userId){
        Single<BookE> bookESingle = userBookMethods.getBookFromDatabase(userId,bookId);
        bookESingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<BookE>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BookE bookE) {
                        btnDownload.setText("Read");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Eroare?:",e.getMessage());
                        btnDownload.setText("Download");
                    }
                });
    }
}
