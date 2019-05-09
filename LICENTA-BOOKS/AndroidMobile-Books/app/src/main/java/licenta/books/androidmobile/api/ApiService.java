package licenta.books.androidmobile.api;

import java.util.ArrayList;

import io.reactivex.Observable;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Review;
import licenta.books.androidmobile.classes.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @Streaming
    @Headers("Content-Type: application/json")
    @POST("users/login")
    Call<User> loginUser(@Body User user);

    @GET("books")
    Call<ArrayList<BookE>> getBooks();

    @GET("reviews")
    Call<ArrayList<Review>> getReviews();

    @GET
    @Headers({ "Content-Type: application/epub+zip","Accept: application/epub+zip"})
    Call<ResponseBody> downloadBookSync2(@Url String fileID);


    @Streaming
    @GET
    @Headers({ "Content-Type: application/epub+zip","Accept: application/epub+zip","Transfer-Encoding: chunked"})
    Observable<ResponseBody> downloadBookAsync(@Url String url);


    @GET
    Call<ResponseBody> downloadBitmap(@Url String url);

    //adauga book per user
    @PATCH("users/{bookId}/{email}")
    Call<ResponseBody> syncUserBooksAddEmail(@Path("bookId") String book_id, @Path("email") String email);

    @PATCH("users/{bookId}/{username}/{password}")
    Call<ResponseBody> syncUserBooksAddUsername(@Path("bookId") String book_id, @Path("username") String username, @Path("password") String password);


    //delete book
    @PUT("users/{email}/{bookId}")
    Call<ResponseBody> syncUserBooksDeleteEmail(@Path("email") String email,@Path("bookId") String book_id);

    @PUT("users/{username}/{password}/{bookId}")
    Call<ResponseBody> syncUserBooksDeleteUsername( @Path("username") String username, @Path("password") String password, @Path("bookId") String book_id);




}
