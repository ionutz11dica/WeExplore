package licenta.books.androidmobile.activities.DialogFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.TypefaceAdapter;
import licenta.books.androidmobile.patterns.DefinitionAsync.DefinitionCall;

public class DefinitionWordDialogFragment extends DialogFragment {
    OnCompleteListenerDefinition listener;
    private TextView word;
    private TextView partOfSpeech;
    private TextView definition;
    String url;
    Bundle bundle;
    String wordSearched;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.definition_word_dialog_fragment, container, false);
        word = view.findViewById(R.id.tv_word);
        partOfSpeech = view.findViewById(R.id.tv_type_Word);
        definition = view.findViewById(R.id.tv_definition);
        bundle = getArguments();
        if(bundle!=null){
            wordSearched = bundle.getString("definitionKey");
            if(wordSearched!=null){
                word.setText(wordSearched);
            }
        }



        sendRequest();
        Window window = getDialog().getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER | Gravity.BOTTOM);

        return view;
    }


    private String dictionaryEntries() {
        final String language = "en-gb";
        final String word = wordSearched;
        final String fields = "definitions";
        final String strictMatch = "false";
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
    }


    public void sendRequest(){
        DefinitionCall definitionCall = new DefinitionCall(getContext(),definition,partOfSpeech);
        url = dictionaryEntries();
        definitionCall.execute(url);
    }
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.listener = (DefinitionWordDialogFragment.OnCompleteListenerDefinition)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public interface OnCompleteListenerDefinition{
        void onCompleteDefinition();
    }
}
