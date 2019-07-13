package licenta.books.androidmobile.classes;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.skytree.epub.PageInformation;

import java.util.ArrayList;

import licenta.books.androidmobile.classes.Converters.ArrayIntegerConverter;
import licenta.books.androidmobile.patterns.readingEstimator.AverageIndicators;
import licenta.books.androidmobile.patterns.readingEstimator.DifficultyRead;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "estimator",
            foreignKeys = {
                @ForeignKey(
                        entity = BookE.class,
                        parentColumns = "bookId",
                        childColumns = "bookId",
                        onDelete = CASCADE
                )
            }, indices = {@Index(value = {"bookId"},unique = true)})

public class Estimator implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private int totalOfWatchedWords;
    private int totalOfWatchedPages;
    private long timePerPage;
    private double gunningFogScore;
    @TypeConverters({ArrayIntegerConverter.class})
    private ArrayList<Integer> chapterIndexes;
    private String bookId;
    private Integer userId;

    @Ignore
    public static long SECOND_INFERIOR_LIMIT = 1000 * 40; //40 sec
    @Ignore
    public static long SECOND_SUPERIOR_LIMIT = 1600 * 100; //2 min 40 sec
    @Ignore
    public static int POLISHING_CONSTANT_MULTIPLICATION = 10;
    @Ignore
    public static int POLISHING_CONSTANT_DIVISION = 2;

    public Estimator(int totalOfWatchedWords, int totalOfWatchedPages, long timePerPage, double gunningFogScore, ArrayList<Integer> chapterIndexes, String bookId, Integer userId) {

        this.totalOfWatchedWords = totalOfWatchedWords;
        this.totalOfWatchedPages = totalOfWatchedPages;
        this.timePerPage = timePerPage;
        this.gunningFogScore = gunningFogScore;
        this.chapterIndexes = chapterIndexes;
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
        totalOfWatchedWords = in.readInt();
        totalOfWatchedPages = in.readInt();
        timePerPage = in.readLong();
        gunningFogScore = in.readDouble();
        bookId = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeInt(totalOfWatchedWords);
        dest.writeInt(totalOfWatchedPages);
        dest.writeLong(timePerPage);
        dest.writeDouble(gunningFogScore);
        dest.writeString(bookId);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
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

//    public CycleDay getCycleDay() {
//        return cycleDay;
//    }
//
//    public void setCycleDay(CycleDay cycleDay) {
//        this.cycleDay = cycleDay;
//    }

    public int getTotalOfWatchedWords() {
        return totalOfWatchedWords;
    }

    public void setTotalOfWatchedWords(int totalOfWatchedWords) {
        this.totalOfWatchedWords = totalOfWatchedWords;
    }

    public int getTotalOfWatchedPages() {
        return totalOfWatchedPages;
    }

    public void setTotalOfWatchedPages(int totalOfWatchedPages) {
        this.totalOfWatchedPages = totalOfWatchedPages;
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

    public ArrayList<Integer> getChapterIndexes() {
        return chapterIndexes;
    }

    public void setChapterIndexes(ArrayList<Integer> chapterIndexes) {
        this.chapterIndexes = chapterIndexes;
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

    public static double chapterEstimator(PageInformation information, AverageIndicators averageIndicators){
        double TimePP;
        int TW;
        int avgWordPerPage = 0;
        double TimePW = 0;
        double GFS = 0;
        int noWordPerPage = DifficultyRead.getNoOfWords(information.pageDescription);

        if(averageIndicators.sumPages == 0 ){
            //valori default

        }else {
             TimePP = (double) averageIndicators.avgTime / 1000; // TimePP = TimePerPage -> transformam in secunde
             TW = averageIndicators.sumWords; // TW = TotalWords
             avgWordPerPage = TW / averageIndicators.sumPages;
             TimePW = TimePP / avgWordPerPage; // TimePW = TimePerWords
             GFS = averageIndicators.avgGFS; // GFS = Gunning Fog Score (coeficient de difiultate)
        }

        double estimatedTime = 0L;
        int noPage=0;
        if(information.pageIndex + 1 == information.numberOfPagesInChapter && noWordPerPage > 60){
            noPage = 1;
        }
        if(information.pageIndex + 1 == information.numberOfPagesInChapter && noWordPerPage == 0){
            noPage = 0;
        }
        if(information.pageIndex + 1 == information.numberOfPagesInChapter && noWordPerPage < 60){
            avgWordPerPage = (avgWordPerPage - noWordPerPage)/2;
            noPage=1;
        }
        if(information.pageIndex + 1 != information.numberOfPagesInChapter - 1 && DifficultyRead.getNoOfWords(information.pageDescription) > 0 ){
            noPage = information.numberOfPagesInChapter - (information.pageIndex+1);
        }
        if(information.pageIndex + 1 != information.numberOfPagesInChapter && (DifficultyRead.getNoOfWords(information.pageDescription) == 0|| DifficultyRead.getNoOfWords(information.pageDescription)< 50)){
            noPage = information.numberOfPagesInChapter -(information.pageIndex+1);
        }

        int RWtR = avgWordPerPage*noPage; //Remaining Words to Read

        estimatedTime = RWtR / ( (Math.pow(TimePW,GFS/10) * POLISHING_CONSTANT_MULTIPLICATION) / POLISHING_CONSTANT_DIVISION);

        return estimatedTime;
    }
}
