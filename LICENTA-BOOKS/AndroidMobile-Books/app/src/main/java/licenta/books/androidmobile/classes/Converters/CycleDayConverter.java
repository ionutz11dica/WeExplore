package licenta.books.androidmobile.classes.Converters;

import android.arch.persistence.room.TypeConverter;

import licenta.books.androidmobile.classes.CycleDay;

public class CycleDayConverter {

    @TypeConverter
    public static CycleDay toEnumCycle(int status){
        switch (status){
            case 0:
                return CycleDay.MORNING;
            case 1:
                return CycleDay.AFTERNOON;
            case 2:
                return CycleDay.EVENING;
            default:
                return CycleDay.MORNING;
        }
    }

    @TypeConverter
    public static int toInt(CycleDay cycleDay){
        switch (cycleDay){
            case MORNING:
                return 0;
            case AFTERNOON:
                return 1;
            case EVENING:
                return 2;
            default:
                return 0;
        }
    }
}
