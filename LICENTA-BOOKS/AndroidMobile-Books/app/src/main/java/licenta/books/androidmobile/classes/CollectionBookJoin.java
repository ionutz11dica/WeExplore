package licenta.books.androidmobile.classes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;


@Entity(
        tableName = "collectionBook",
        primaryKeys = {"collectionId","bookId"},
        foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId"
                ),
                @ForeignKey(
                        entity = Collections.class,
                        parentColumns = "collectionId",
                        childColumns = "collectionId")
        },
        indices = {
                @Index(value = "collectionId"),
                @Index(value = "bookId")
        })
public class CollectionBookJoin {

    public @NonNull final String bookId;
    public @NonNull   final Integer collectionId;

    public CollectionBookJoin(@NonNull  final String bookId,@NonNull final  Integer collectionId) {
        this.bookId = bookId;
        this.collectionId = collectionId;
    }
}
