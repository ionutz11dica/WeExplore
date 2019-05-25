package licenta.books.androidmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.DetailsActivity;
import licenta.books.androidmobile.activities.others.CheckForNetwork;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Converters.ArrayStringConverter;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.classes.UserBookJoin;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.database.DAO.UserBookJoinDao;
import licenta.books.androidmobile.database.DAO.UserDao;
import licenta.books.androidmobile.database.DaoMethods.UserBookMethods;
import licenta.books.androidmobile.database.DaoMethods.UserMethods;
import licenta.books.androidmobile.interfaces.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder> {
    private ApiService apiService;
    private List<BookE> list = Collections.emptyList();
    private Context context;
    private SharedPreferences sharedPreferences;

    private UserBookJoinDao userBookJoinDao;
    private UserBookMethods userBookMethods;
    private UserDao userDao;
    private UserMethods userMethods;



    public ShelfAdapter(List<BookE> list, Context context) {
        this.list = list;
        this.context = context;
        apiService = ApiClient.getRetrofit().create(ApiService.class);
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREF_USER,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ShelfViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shelf_card,viewGroup,false);
        openDb();
        return new ShelfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShelfViewHolder shelfViewHolder, final int i) {
       // if(CheckForNetwork.isConnectedToNetwork(context)){
            Glide.with(context)
                    .load(list.get(i).getImageLink())
                    .placeholder(R.drawable.ic_error_outline_24dp)
                    .into(shelfViewHolder.imageView);
       // }
        shelfViewHolder.title.setText(list.get(i).getTitle());
        shelfViewHolder.authors.setText(ArrayStringConverter.fromArrayList(list.get(i).getAuthors()));

        final Button button = shelfViewHolder.buttonContext;

        shelfViewHolder.buttonContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,button);
                popupMenu.inflate(R.menu.shelfbooks_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.shelfbooks_menu_details:
                                Intent intent = new Intent(context, DetailsActivity.class);
                                intent.putExtra(Constants.KEY_BOOK,list.get(i));

                                String bookCover = list.get(i).getImageLink().replace("zoom=0","zoom=4");
                                bookCover = bookCover.replace("curl","none");
                                intent.putExtra(Constants.KEY_IMAGE_URL,bookCover);

                                context.startActivity(intent);
                                return true;
                            case R.id.shelfbooks_menu_addCollection:

                                Toast.makeText(context,"Va urma!",Toast.LENGTH_LONG).show();
                            case R.id.shelfbooks_menu_delete:
                                String status = sharedPreferences.getString(Constants.KEY_STATUS,null);

                                if(status.equals("with")){
                                    String email = sharedPreferences.getString(Constants.KEY_USER_EMAIL,null);
                                    final Call<ResponseBody> call = apiService.syncUserBooksDeleteEmail(email,list.get(i).get_id());

                                    Single<User> user = userMethods.verifyExistenceGoogleAcount(email);
                                    user.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<User>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public synchronized void onSuccess(User user) {
                                                    UserBookJoin userBookJoin = new UserBookJoin(list.get(i).get_id(),user.getUserId());
                                                    userBookMethods.deleteUserBook(userBookJoin);
                                                    deleteBookCloud(call);
                                                    remove(list.get(i));
                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }
                                            });



                                }else{
                                    String username = sharedPreferences.getString(Constants.KEY_USER_USERNAME,null);
                                    String password = sharedPreferences.getString(Constants.KEY_USER_PASSWORD,null);
                                    final Call<ResponseBody> call = apiService.syncUserBooksDeleteUsername(username,password,list.get(i).get_id());
                                    Single<User> user = userMethods.verifyAvailableAccount(username,password);
                                    user.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<User>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public synchronized void onSuccess(User user) {
                                                    UserBookJoin userBookJoin = new UserBookJoin(list.get(i).get_id(),user.getUserId());
                                                    userBookMethods.deleteUserBook(userBookJoin);
                                                    deleteBookCloud(call);
                                                    remove(list.get(i));
                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }
                                            });
                                }


                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


    }

    private void openDb(){
        userBookJoinDao = AppRoomDatabase.getInstance(context).getUserBookDao();
        userBookMethods = UserBookMethods.getInstance(userBookJoinDao);
        userDao = AppRoomDatabase.getInstance(context).getUserDao();
        userMethods = UserMethods.getInstance(userDao);
    }

    private void deleteBookCloud(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Succes: ", response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void insert(int position, BookE bookE){
        list.add(position,bookE);
        notifyItemInserted(position);
    }

    public void remove(BookE bookE){
        int pos = list.indexOf(bookE);
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    public class ShelfViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView authors;
        ImageView imageView;
        Button buttonContext;

        ShelfViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.shelf_cardView);
            title = itemView.findViewById(R.id.tv_shelf_title);
            authors = itemView.findViewById(R.id.tv_shelf_authors);
            imageView = itemView.findViewById(R.id.iv_shelf_coverbook);
            buttonContext = itemView.findViewById(R.id.btn_shelf_buttonOptions);



            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();

                    Log.i("Poz: ",String.valueOf(pos));
                    if (pos != RecyclerView.NO_POSITION){

                    }
                }
            });
        }
    }
}
