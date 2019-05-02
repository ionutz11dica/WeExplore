package licenta.books.androidmobile.classes.RxJava;

import android.support.annotation.NonNull;

import java.util.ArrayList;

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
    private static BehaviorSubject<Integer> behaviorSubjectDownloadPercent = BehaviorSubject.create();
    private static BehaviorSubject<ArrayList<String>> behaviorSubjectChapterList = BehaviorSubject.create();
    private static BehaviorSubject<Integer> behaviorSubjectChapter = BehaviorSubject.create();
    private static BehaviorSubject<String> behaviorSubjectChapterName = BehaviorSubject.create();

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

    public static void publishBookState(@NonNull BookState message){
        behaviorSubjectBookState.onNext(message);
    }

    public static Disposable subscribeDownloadProgress(@NonNull Consumer<Integer> action){
        return behaviorSubjectDownloadPercent.subscribe(action);
    }

    public static void publishsDownloadProgress(@NonNull Integer message){
        behaviorSubjectDownloadPercent.onNext(message);
    }

    public static Disposable subscribeChapter(@NonNull Consumer<Integer> action){
        return behaviorSubjectChapter.subscribe(action);
    }

    public static void publishsChapter(@NonNull Integer message){
        behaviorSubjectChapter.onNext(message);
    }

    public static Disposable subscribeChapterList(@NonNull Consumer<ArrayList<String>> action){
        return behaviorSubjectChapterList.subscribe(action);
    }

    public static void publishsChapterList(@NonNull ArrayList<String> message){
        behaviorSubjectChapterList.onNext(message);
    }

    public static Disposable subscribeChapterName(@NonNull Consumer<String> action){
        return behaviorSubjectChapterName.subscribe(action);
    }

    public static void publishsChapterName(@NonNull String message){
        behaviorSubjectChapterName.onNext(message);
    }
}
