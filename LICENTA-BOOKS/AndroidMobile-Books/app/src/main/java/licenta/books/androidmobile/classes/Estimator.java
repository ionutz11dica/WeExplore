package licenta.books.androidmobile.classes;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import licenta.books.androidmobile.classes.Converters.CycleDayConverter;

@Entity(tableName = "estimator",
            foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId"
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId"
                )
            },indices = {
        @Index(value = "userId"),
        @Index(value = "bookId")
})

public class Estimator implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @TypeConverters({CycleDayConverter.class})
    private CycleDay cycleDay;
    private long timePerPage;
    private double gunningFogScore;
    private String bookId;
    private Integer userId;

    @Ignore
    public static long SECOND_INFERIOR_LIMIT = 1000 * 45; //45 sec
    @Ignore
    public static long SECOND_SUPERIOR_LIMIT = 1600 * 100; //2 min 40 sec

    public Estimator( CycleDay cycleDay, long timePerPage, double gunningFogScore, String bookId, Integer userId ) {
        this.cycleDay = cycleDay;
        this.timePerPage = timePerPage;
        this.gunningFogScore = gunningFogScore;
        this.bookId = bookId;
        this.userId = userId;
    }

    @Ignore
    public Estimator(){

    }

    protected Estimator(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        timePerPage = in.readLong();
        gunningFogScore = in.readDouble();
        bookId = in.readString();
        userId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeLong(timePerPage);
        dest.writeDouble(gunningFogScore);
        dest.writeString(bookId);
        dest.writeInt(userId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Estimator> CREATOR = new Creator<Estimator>() {
        @Override
        public Estimator createFromParcel(Parcel in) {
            return new Estimator(in);
        }

        @Override
        public Estimator[] newArray(int size) {
            return new Estimator[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CycleDay getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(CycleDay cycleDay) {
        this.cycleDay = cycleDay;
    }

    public long getTimePerPage() {
        return timePerPage;
    }

    public void setTimePerPage(long timePerPage) {
        this.timePerPage = timePerPage;
    }

    public double getGunningFogScore() {
        return gunningFogScore;
    }

    public void setGunningFogScore(double gunningFogScore) {
        this.gunningFogScore = gunningFogScore;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }



}
