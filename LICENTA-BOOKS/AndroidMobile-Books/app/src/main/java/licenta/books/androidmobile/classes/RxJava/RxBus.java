package licenta.books.androidmobile.classes.RxJava;

import android.support.annotation.NonNull;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.classes.BookState;
import licenta.books.androidmobile.classes.User;

public final class RxBus {
    private static BehaviorSubject<User> behaviorSubjectUser = BehaviorSubject.create();
    private static BehaviorSubject<BookE> behaviorSubjectBook = BehaviorSubject.create();
    private static BehaviorSubject<BookState> behaviorSubjectBookState = BehaviorSubject.create();
    private static BehaviorSubject<Float> behaviorSubjectDownloadPercent = BehaviorSubject.create();

    public static Disposable subscribeUser(@NonNull Consumer<User> action){
        return behaviorSubjectUser.subscribe(action);
    }

    public static void publishUser(@NonNull User message){
        behaviorSubjectUser.onNext(message);
    }

    public static Disposable subscribeBook(@NonNull Consumer<BookE> action){
        return behaviorSubjectBook.subscribe(action);
    }

    public static void publishBook(@NonNull BookE message){
        behaviorSubjectBook.onNext(message);
    }

    public static Disposable subscribeBookState(@NonNull Consumer<BookState> action){
        return behaviorSubjectBookState.subscribe(action);
    }

    public static void publishBook(@NonNull BookState message){
        behaviorSubjectBookState.onNext(message);
    }

    public static Disposable subscribeDownloadProgress(@NonNull Consumer<Float> action){
        return behaviorSubjectDownloadPercent.subscribe(action);
    }

    public static void publishsDownloadProgress(@NonNull Float message){
        behaviorSubjectDownloadPercent.onNext(message);
    }
}
