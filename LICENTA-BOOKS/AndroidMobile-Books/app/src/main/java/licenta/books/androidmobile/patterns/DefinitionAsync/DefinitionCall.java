package licenta.books.androidmobile.patterns.DefinitionAsync;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DefinitionCall extends AsyncTask<String, Integer, String> {
    final String app_id = "53039827";
    final String app_key = "eb798c7855bb293a070de248b6bdf503";
    String url;
    Context context;
    TextView tvDef;
    TextView partOfSpeech;

    public DefinitionCall(Context context, TextView def,TextView partOfSpeech){
        this.context = context;
        this.tvDef = def;
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    protected String doInBackground(String... params) {


        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String def;
        String pos;
        String finalPos="Part of Speech not found";
        try{
            Log.v("Result ",  result);
//            JSONObject js = new JSONObject(result);
//            JSONArray results = js.getJSONArray("results");
//
//
//
//
//            JSONObject lexEntries = results.getJSONObject(0);
//            JSONArray leArray = lexEntries.getJSONArray("lexicalEntries");
//
//
//
//            JSONObject entries = leArray.getJSONObject(0);
//            JSONArray e = entries.getJSONArray("lexicalEntries");
//
//
//
//            JSONObject sense = e.getJSONObject(0);
//            JSONArray senseArray = sense.getJSONArray("senses");
//
//            JSONObject deff = senseArray.getJSONObject(0);
//            JSONArray d = deff.getJSONArray("definitions");

            def = new JSONObject(result).getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").
                    getJSONObject(0).getJSONArray("entries").getJSONObject(0).getJSONArray("senses").
                    getJSONObject(0).getJSONArray("definitions").getString(0);
            pos =  new JSONObject(result).getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").
                    getJSONObject(0).getJSONObject("lexicalCategory").getString("text");
            if(pos!=null){
                finalPos = "<fonts color =#b7b8b6>Part of Speech: </fonts>&nbsp &nbsp &nbsp <fonts color=#000000>"+ pos +"</fonts>";
            }

            Log.v("Result ",  def);
            if(def!=null){
                tvDef.setText("Definition:  "+def);
                partOfSpeech.setText(Html.fromHtml(finalPos));
            }else{
                tvDef.setText("No definition found");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
//        Log.v("Result on dictionary","OnPostExecute" + result);
    }
}
