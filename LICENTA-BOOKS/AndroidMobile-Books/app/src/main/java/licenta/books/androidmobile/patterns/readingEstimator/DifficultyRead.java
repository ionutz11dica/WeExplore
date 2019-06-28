package licenta.books.androidmobile.patterns.readingEstimator;

import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.Locale;

public class DifficultyRead implements GunningFogFormula {
    private static SentenceExtractor se = new SentenceExtractor();
    private static Locale currentLocale = new Locale("en", "US");
    static BreakIterator iterator = BreakIterator.getSentenceInstance(currentLocale);

    Integer sentences;
    Integer complex;
    Integer words;
    boolean isForTest =false;

    public DifficultyRead (String text) {
        if (text.length() > 100) {
            this.isForTest = true;
            this.sentences = getNoOfSentences(text + ".");
            this.words = getNoOfWords(text);
            this.complex = getNoOfComplexWords(text);
        }
    }

    @Override
    public double getGunningFog() {
        double score=0;
        if(isForTest && sentences>0 && words >0) {
             score = 0.4 * (words / sentences + 100 * complex / words);
        }else{
            return 0;
        }
        return round(score, 3);
    }

    private static int getNoOfSentences(String text){
        int numberOfSentences = se.markBoundaries(text,iterator);
        if(numberOfSentences > 0){
            return numberOfSentences;
        }else if (text.length() > 0){
            return 1;
        }else{
            return 0;
        }
    }

    private static String cleanLine(String text){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < text.length(); i++){
            char character = text.charAt(i);
            if( character < 128 && Character.isLetter(character)){
                stringBuilder.append(character);
            }else{
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString().toLowerCase();
    }

    public static int getNoOfWords(String text){
        String cleanText = cleanLine(text);
        String[] words = cleanText.split(" ");
        int noWords = 0;
        for(String w : words){
            if(w.length() > 0){
                noWords++;
            }
        }
        return noWords;
    }

    private static int getNoOfComplexWords(String text){
        String cleanText = cleanLine(text);
        String[] words = cleanText.split(" ");
        int complex = 0;
        for( String w: words){
            if(isComplex(w)){
                complex++;
            }
        }
        return complex;
    }

    private static boolean isComplex(String w){
        int syllables = Syllabify.syllable(w);
        return syllables > 2;
    }

    public static Double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
