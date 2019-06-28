package licenta.books.androidmobile.classes.Converters;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;

public class ArrayIntegerConverter {

    @TypeConverter
    public static ArrayList<Integer> fromString(String value){
        String[] list = value.split(",");
        ArrayList<Integer> chapterIndexes = new ArrayList<>();
        for(int i = 0 ;i < list.length;i++){
            chapterIndexes.add(Integer.parseInt(list[i]));
        }

        return chapterIndexes;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list){
        StringBuilder chapterIndexes = new StringBuilder();
        for(int i = 0 ; i< list.size();i++){
            if(i != list.size()-1) {
                chapterIndexes.append(list.get(i)).append(",");
            }else{
                chapterIndexes.append(list.get(i));
            }
        }
        return chapterIndexes.toString();
    }
}
