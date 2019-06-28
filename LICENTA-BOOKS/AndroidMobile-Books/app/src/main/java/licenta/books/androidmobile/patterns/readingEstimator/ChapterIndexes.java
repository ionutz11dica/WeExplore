package licenta.books.androidmobile.patterns.readingEstimator;

import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;

import licenta.books.androidmobile.classes.Converters.ArrayIntegerConverter;

public class ChapterIndexes {
    @TypeConverters({ArrayIntegerConverter.class})
    public ArrayList<Integer> chapterList;
}
