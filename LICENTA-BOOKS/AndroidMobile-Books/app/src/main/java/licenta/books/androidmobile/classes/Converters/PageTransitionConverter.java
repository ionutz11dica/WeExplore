package licenta.books.androidmobile.classes.Converters;

import android.arch.persistence.room.TypeConverter;
import android.graphics.pdf.PdfDocument;

import com.skytree.epub.PageTransition;

public class PageTransitionConverter {

    @TypeConverter
    public  static PageTransition toEnum(int status){
        if(status == 0){
            return PageTransition.None;
        }else if(status == 1){
            return PageTransition.Curl;
        }else if(status == 2){
            return PageTransition.Slide;
        }else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public  static Integer toInteger(PageTransition pageTransition){
        if(pageTransition == PageTransition.None){
            return 0;
        } else if(pageTransition == PageTransition.Curl){
            return 1;
        } else if(pageTransition == PageTransition.Slide){
            return 2;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }
}
