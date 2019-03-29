package licenta.books.androidmobile.classes.Converters;

import android.annotation.SuppressLint;
import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import licenta.books.androidmobile.interfaces.Constants;

public class TimestampConverter {

    @SuppressLint("SimpleDateFormat")
    static DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String fromDateToString(Date date){

        return df.format(date);
    }
}
