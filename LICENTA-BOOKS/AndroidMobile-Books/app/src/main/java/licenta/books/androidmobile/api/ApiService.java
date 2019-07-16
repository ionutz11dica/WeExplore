package licenta.books.androidmobile.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import licenta.books.androidmobile.activities.others.Title;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.Review;
import licenta.books.androidmobile.classes.User;
import licenta.books.androidmobile.patterns.Carousel.Photo;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
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
    Call<User> syncUserBooksAddEmail(@Path("bookId") String book_id, @Path("email") String email);

    @PATCH("users/{bookId}/{username}/{password}")
    Call<ResponseBody> syncUserBooksAddUsername(@Path("bookId") String book_id, @Path("username") String username, @Path("password") String password);


    //delete book
    @PUT("users/{email}/{bookId}")
    Call<ResponseBody> syncUserBooksDeleteEmail(@Path("email") String email,@Path("bookId") String book_id);

    @PUT("users/{username}/{password}/{bookId}")
    Call<ResponseBody> syncUserBooksDeleteUsername( @Path("username") String username, @Path("password") String password, @Path("bookId") String book_id);

    @GET("books/fetch/{isbn}")
    Call<BookE> getBookByISBN(@Path("isbn") String isbn);

    @GET("users/{email}")
    Call<ArrayList<BookE>> getScannedBooks(@Path("email") String email);

//    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @PUT("users/delete/{email}")
    Call<RequestBody> deleteScannedBooks(@Path("email") String email, @Body String books);

    @GET("books/someImageBooks")
    Call<ArrayList<BookE>> getMostDownloadedBooks();

    @GET("books/{elementSearched}")
    Call<ArrayList<BookE>> getBooksSearched(@Path("elementSearched") String elementSearched);

    @GET("users/wantToReadBooks/{email}")
    Call<ArrayList<BookE>> getWantedBooks(@Path("email") String email);

    @PATCH("users/wantToRead/{bookId}/{email}")
    Call<User> addWantToRead(@Path("bookId") String bookId,@Path("email") String email);

    @PATCH("users/wantToRead/delete/{email}/{bookId}")
    Call<User> deleteWantToRead(@Path("email") String email,@Path("bookId") String bookId);

    @GET("books/categorySearch/{category}")
    Call<ArrayList<BookE>> getCategoryBooks(@Path("category") String category);

    @PUT("books/nodownloads/{id}")
    Call<ResponseBody> increaseNoDownloads(@Path("id")String id);

    @GET("books/randomBooks")
    Call<ArrayList<BookE>> getRandomBooks();

}
