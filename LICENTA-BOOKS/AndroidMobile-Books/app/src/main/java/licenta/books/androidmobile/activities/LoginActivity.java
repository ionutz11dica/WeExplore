package licenta.books.androidmobile.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.activities.others.CustomToast;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.interfaces.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean isSigninScreen = true;
    TextView tvSignupInvoker;
    TextView tvSigninInvoker;
    LinearLayout llSignin;
    LinearLayout llsignup;

    Button btnSignup;
    Button btnSignin;


    Button signInGoogleButton;

    TextInputEditText tie_signinUsername, tie_signinPassword;
    TextInputEditText tie_signupEmail, tie_signupUsername, tie_signupPassword;

    GoogleSignInClient mGoogleSignInClient;
    ApiService apiService;

    UserDao userDao;
    UserMethods userMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        apiService = ApiClient.getRetrofit().create(ApiService.class);

        initGoogleSignIn();
        initComp();
        showSigninForm();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
                if(isSigninScreen)
                    btnSignup.startAnimation(clockwise);
            }
        });
    }


    @SuppressLint("CommitPrefEdits")
    void initComp(){
        if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"Exista net",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"nu Exista net",Toast.LENGTH_LONG).show();

        }
        userDao = AppRoomDatabase.getInstance(getApplicationContext()).getUserDao();
        userMethods = UserMethods.getInstance(userDao);

        sharedPreferences = getSharedPreferences(Constants.KEY_PREF_USER,MODE_PRIVATE);

        editor = sharedPreferences.edit();
        editor.clear();

        tie_signinUsername = findViewById(R.id.signin_tie_username);
        tie_signinPassword = findViewById(R.id.signin_tie_password);

        tie_signupEmail = findViewById(R.id.signup_tie_email);
        tie_signupUsername = findViewById(R.id.signup_tie_user);
        tie_signupPassword = findViewById(R.id.signup_tie_password);


        llSignin = findViewById(R.id.llSignin);
        llSignin.setOnClickListener(this);

        llsignup = findViewById(R.id.llSignup);
        llsignup.setOnClickListener(this);

        tvSignupInvoker = findViewById(R.id.tvSignupInvoker);
        tvSigninInvoker = findViewById(R.id.tvSigninInvoker);

        btnSignup = findViewById(R.id.btnSignup);
        btnSignin= findViewById(R.id.btnSignin);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString(Constants.KEY_STATUS, "without");
                editor.apply();
                if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){
                    verifyLogin(null);
                }else{
                    verifyLoginFromDb();
                }
            }
        });

        tvSignupInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigninScreen = false;
                showSignupForm();
                btnSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())) {
                            editor.putString(Constants.KEY_STATUS, "without");
                            editor.apply();
                            createUser();
                        }else{
                            Toast.makeText(getApplicationContext(),"Network failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        tvSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigninScreen = true;
                showSigninForm();
            }
        });
    }

     @SuppressLint("StaticFieldLeak")
     void verifyLoginFromDb() {
        final CustomToast customToast = new CustomToast(this);
        final User userr = new User();
        final Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        if(verifySignin()){
            userr.setUsername(tie_signinUsername.getText().toString());
            userr.setPassword(tie_signinPassword.getText().toString());
            editor.putString(Constants.KEY_USER_USERNAME, userr.getUsername());
            editor.putString(Constants.KEY_USER_PASSWORD, userr.getPassword());
            editor.apply();

            Single<User> userDb  = userMethods.verifyAvailableAccount(userr.getUsername(),userr.getPassword());

            userDb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        Log.d("User: ",user.getEmail());
                            startActivity(intent);

                    }

                    @Override
                    public void onError(Throwable e) {
                        customToast.show("Passowrd or username incorrect",R.drawable.ic_error_outline_24dp,getApplicationContext());
                    }
                });

//            new AsyncTask<User, Void,Boolean>(){
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//
//                }
//
//                @Override
//                protected Boolean doInBackground(User... users) {
////                    Single<User> userDb  = userDao.verifyAvailableAccount(userr.getUsername(),userr.getPassword());
//
//
//                    if(userDb !=null && userDb.getUsername().equals(userr.getUsername()) && userDb.getPassword().equals(userr.getPassword())){
//                        return true;
//                    }else{
//                        return false;
//                    }
//                }
//
//                @Override
//                protected void onPostExecute(Boolean user) {
//                    super.onPostExecute(user);
//                    if(user){
//                        startActivity(intent);
//                    }else{
//                        Toast.makeText(getApplicationContext(),"Incorect password or username",Toast.LENGTH_LONG).show();
//                    }
//                }
//            }.execute();
        }

    }

    @SuppressLint("StaticFieldLeak")
    void verifyLoginFromDbGoogleAccount(final GoogleSignInAccount account) {
        final User userr = new User();
        userr.setEmail(account.getEmail());

        final Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        editor.putString(Constants.KEY_USER_EMAIL, userr.getEmail());
        editor.apply();

        Single<User> userDb  = userDao.verifyExistenceGoogleAcount(userr.getEmail());
        userDb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        userMethods.insertUser(userr);
                    }
                });

    }

    void initGoogleSignIn(){
        signInGoogleButton = findViewById(R.id.sign_in_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInGoogleButton.setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("Fail" ,"signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account){
        if(account!=null){
           // createUserGoogle(account);
            verifyGoogleLogin(account);
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void showSignupForm() {

        ((LinearLayout.LayoutParams) llSignin.getLayoutParams()).weight = 0.15f;
        llSignin.requestLayout();

        ((LinearLayout.LayoutParams) llsignup.getLayoutParams()).weight = 0.85f;
        llsignup.requestLayout();

        tvSignupInvoker.setVisibility(View.GONE);
        tvSigninInvoker.setVisibility(View.VISIBLE);
        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_right_to_left);
        llsignup.startAnimation(translate);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        btnSignup.startAnimation(clockwise);

    }

    private void showSigninForm() {

        ((LinearLayout.LayoutParams) llSignin.getLayoutParams()).weight = 0.85f;
        llSignin.requestLayout();
        ((LinearLayout.LayoutParams) llsignup.getLayoutParams()).weight = 0.15f;
        llsignup.requestLayout();

        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_left_to_right);
        llSignin.startAnimation(translate);

        tvSignupInvoker.setVisibility(View.VISIBLE);
        tvSigninInvoker.setVisibility(View.GONE);
        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_left_to_right);
        btnSignin.startAnimation(clockwise);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.llSignin || view.getId() ==R.id.llSignup){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(new View(this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        switch (view.getId()){
            case R.id.sign_in_button:
                if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){
                    editor.putString(Constants.KEY_STATUS, "with");
                    signIn();
                    break;
                }else{
                    Toast.makeText(getApplicationContext(),"Nu exista conexiune la internet",Toast.LENGTH_LONG).show();
                    break;
                }
            case R.id.btnSignin:
                if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){

                }
            case R.id.btnSignup:
                if(CheckForNetwork.isConnectedToNetwork(getApplicationContext())){

                }else{
                    Toast.makeText(getApplicationContext(),"Connection problem",Toast.LENGTH_LONG).show();
                    break;
                }

        }
    }



    void createUser(){
        Call<User> call;
        final CustomToast customToast;
        final User user;
            if(verifySignup()) {
                user = new User(tie_signupEmail.getText().toString(),tie_signupUsername.getText().toString(),tie_signupPassword.getText().toString());

                call = apiService.loginUser(user);
            }else{
                customToast = new CustomToast(this);
                customToast.show("Invalid inputs",R.drawable.ic_input_24dp,this);
                return;
        }
        customToast = new CustomToast(this);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    customToast.show("That email is already used", getApplicationContext());
                    return;
                }
                 if(response.code() == 201){
                    userMethods.insertUser(user);
                    Toast.makeText(getApplicationContext(),"User "+ user.getEmail()+ " created successfully",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                 }else if(response.code() == 200){
                    customToast.show("That email is already used", getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }


    void verifyLogin(GoogleSignInAccount account){
        Call<User> call;
        final User user = new User();
        final CustomToast customToast = new CustomToast(this);
        if(account!=null) {
            user.setEmail(account.getEmail());
            call = apiService.loginUser(user);
        }else{
            if (verifySignin()) {
                user.setUsername(tie_signinUsername.getText().toString());
                user.setPassword(tie_signinPassword.getText().toString());
                editor.putString(Constants.KEY_USER_USERNAME, user.getUsername());
                editor.putString(Constants.KEY_USER_PASSWORD, user.getPassword());
                editor.apply();
                call = apiService.loginUser(user);
            } else {
                customToast.show("Complete field properly", R.drawable.ic_error_outline_24dp, this);
                return ;
            }
        }
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                if(!response.isSuccessful()){
                    customToast.show("Passowrd or username incorrect",R.drawable.ic_error_outline_24dp,getApplicationContext());
                    return;
                }
                Single<User> userSingle = userMethods.verifyAvailableAccount(user.getUsername(),user.getPassword());
                userSingle.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(User user) {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {
                                User user = new User();
                                user.setEmail(response.body().getEmail());
                                user.setUsername(response.body().getUsername());
                                user.setPassword(response.body().getPassword());
                                userMethods.insertUser(user);
                            }
                        });

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    void verifyGoogleLogin(final GoogleSignInAccount account){
        Call<User> call;
        final User user = new User();
        final CustomToast customToast = new CustomToast(this);
        final Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        user.setEmail(account.getEmail());
        call = apiService.loginUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    return;
                }

                    customToast.show("User has been logged in",R.drawable.ic_error_outline_24dp,getApplicationContext());
                    verifyLoginFromDbGoogleAccount(account);

                    startActivity(intent);
                }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }


    boolean verifySignup(){
        if(tie_signupEmail.getText() == null || tie_signupEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(tie_signupEmail.getText().toString()).matches()){
            return false;
        } else if( tie_signupUsername.getText() == null || tie_signupUsername.getText().toString().trim().isEmpty() || tie_signupUsername.getText().toString().length() < 3){
            return false;
        }else if(tie_signupPassword.getText() == null || tie_signupPassword.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    boolean verifySignin(){
       if( tie_signinUsername.getText() == null || tie_signinUsername.getText().toString().trim().isEmpty()){
            return false;
        }else if(tie_signinPassword.getText() == null || tie_signinPassword.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }


}
