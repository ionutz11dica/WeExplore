package licenta.books.androidmobile.patterns.readingEstimator;

import java.text.BreakIterator;

public class SentenceExtractor {

    public  int markBoundaries(String target, BreakIterator iterator) {
        StringBuilder markers = new StringBuilder();
        markers.setLength(target.length() + 1);
        for (int k = 0; k < markers.length(); k++) {
            markers.setCharAt(k, ' ');
        }
        int count = 0;
        iterator.setText(target);
        int boundary = iterator.first();
        while (boundary != BreakIterator.DONE) {
            ++count;
            boundary = iterator.next();
        }
        int countSentences = count - 1;
        if(countSentences == 0) {
            return 0;
        }
        return count-1;
    }

}
