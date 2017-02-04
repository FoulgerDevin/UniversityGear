package university4credit.universitygear;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "university4credit.universitygear.MESSAGE";
    Button mButton;
    EditText mEdit;
    TextView mText;
    String newtext;
    Context context;
    int mindistance;
    String tempstring;
    String tempstr;
    String join;
    String temp;
    String [] database = {"Oregon","State", "University", "Jersey", "Mug", "Nike", "Underarmour" };
    public List<Item> itemFeed = null;
    // Is the button now checked?


    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mButton = (Button)findViewById(R.id.button1);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                temp = checkedRadioButton.getText().toString();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEdit = (EditText)findViewById(R.id.editText1);
                String [] ParsedWords = mEdit.getText().toString().split("\\s+") ;
                for(int x = 0; x < ParsedWords.length;x++){
                    mindistance = 3;
                    for(int y = 0; y < database.length; y++){
                        if(distance(ParsedWords[x],database[y])<2){
                            tempstring = database[y];
                            break;
                        }
                        else if(distance(ParsedWords[x],database[y])<mindistance){
                            tempstring = database[y];
                        }
                        else tempstring = ParsedWords[x];
                    }
                    ParsedWords[x]=tempstring;
                }

                join = ParsedWords[0];
                for(int x = 1; x < ParsedWords.length;x++){
                    join += " "; join+=ParsedWords[x];
                }
                join.toLowerCase();
                new SearchItemTask().execute(join);

                FileOutputStream stream = null;
                try {
                    stream = openFileOutput("history.txt", Context.MODE_APPEND);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    stream.write(join.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("E","tempstr = " + tempstr);
            }
        });

    }

    private class SearchItemTask extends AsyncTask<String,Void,List<Item>> {
        private String temp;
        private Context tempc;
        private String itemLimit = "200";
        URL url;
        HttpURLConnection conn;
        InputStream inputstream;
        String result = "";

        private String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();

        }
        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected List<Item> doInBackground(String... params) {
            //this is the url that i passed in
            String urlString = "https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q="+params[0]+"&limit=" + itemLimit;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection)url.openConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //this is the header that you need to pass in. The authorization key is something you get from the eBAy website. I can tell you how to get them but I think we can just use my key
            //conn.setRequestProperty("Authorization", "Bearer " + context.getString(R.string.OAuth));
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXW2wUVRju9GqpBTEEVEQ3Axi0zu6Z++5IV3dbLqv0ItsixWg9M3O2HZidWefMtN1IbFNMDdFoSEgIIFoTogEVfYREjNEH8MHEJ0qIwUijCBIfTLBIInhme2G7xtILiU3cl8055799///9Z84P+sorHxvYODBSTVUUD/aBvmKKYqtAZXlZzcKS4gfKikCeADXYt6qvtL/kl7UYps2MshnhjG1hFOhJmxZWcpu1tOdYig2xgRULphFWXE1Jxho2KVwQKBnHdm3NNulAor6WlgVZimhhHshQZ+WIRHatcZstdi0tyFwEykCSBJnXVQ2Rc4w9lLCwCy23luYAKzOAYwDfAniFFxVWDoYFbhsd2IIcbNgWEQkCOpoLV8npOnmxTh0qxBg5LjFCRxOx9cmmWKJ+XWPL2lCerehYHpIudD08eVVn6yiwBZoemtoNzkkrSU/TEMZ0KDrqYbJRJTYezCzCz6VaiMiAC6eAHlaRporcHUnlettJQ3fqOPwdQ2dSOVEFWa7hZm+XUZINdTvS3LFVIzGRqA/4f8960DRSBnJq6XXxWFtrct1mOpBsbnbsLkNHuo+U5XlekkTCpqiLMEkhctpTtmd2IEcf8zVqcCzTBc7qbEs3/LzhQKPtxhEJHBWmh8tLDxFqspqcWMr1g8qXE8fTyEnb/LqOFtJzOy2/tChNchHILW9fhHFW3OLBneJFRJOAJPA8B1MiSAkFtPB7fVbUiPrViTU3h5AKs0waOjuQmzGhhhiNZNZLI8fQFUFQBQlqKYbnwhoj6KLGRCIiYjgkIYmDvEAC+p+xw3UdQ/VcNMGQwoMczlo6qdkZ1GybhpalC0Vyl84YH3pwLd3puhklFOru7g5280Hb6QhxALChrQ2bklonSkN6Qta4vTBj5Kjh38VEXnGzGRJNDyEecW510FHe0Zuh42bjXpask8g0yd84eSdFGC3c/Reo2Ic6v0D6+pgYgBkj6BM8qNnpkA1JL/tb7bmIA9MRCqlelvjXkRN0ENRty8xOX6/DIwQe1Z6eEibVCI72IoFR6NHv9RkZmIFTw+oiXLad7AxhTlaegQ7UNNuz3Nm4G1OdgUbKM1OGafrtOhuHeeozCdOCZtY1NDzhck5dFstkEvr86jKhzkHksmVaLaPL/9AwyfhWRhXElCQIXJhRUTglS6o4J9g66jI01G7MM+iWZ5pzwtXQMSUk0usH/wNYjaHYnFDVo675RlKRUwWdU1kmIgsqI5AnC6PqgDxzZKQhkQVCSuTmhLnONMjF0JKdb9/AjTZ2kT43aOQhOr9A+TfM+AWjh0WVkcOCQF6mPGRgBPGMEBG16UIu2Mh70f3jKR+aPE5Hi3I/tp86CfqpE2QiBzJg2BrwaHlJa2nJ3TQ2XBTE0NJVuydowFQQGx0WmRYdFNyBshloOMXlVMPwm229eYP84AvgvolRvrKErcqb68GDt07K2EXLqlkyLQIe8LzIytvAylunpezS0iXZmv2ha8uWDI/cOF20c+93bebnNgWqJ4QoqqyotJ8q2rfrwqn4IxdHeq8edbZ8PxRwi3rOLjhdNbRrgNpQWr3/+UtnO19cuH0AUz99srV1w4qDJy8s7d1dVClabw+t+bhaPPzD8VjzqVPX3nu/9+rjNxoiHyx6+tCZxHMH/jxmblfolb9ebDj/88PnFte1V/R2/xhvO3bjvERRf20+O1ASSATvGhoqP1M2+FXULv/UHrj39yPXj67mh1fXNzH9N68cfqnywP5LVS8/UfPu8vSeVy6807mifvkbZ5aEL3/DD+25eV379rflnz0ZeerI/a+vrDhhhuxzi69Wnx9+bXX8UMY+0nbzrX1Xvgb3dH70zO4Fey/Hs4HKL/GIeRzV/bH+1WU73UyxseaLD1c9VDFaxr8BhVb80WIRAAA=");
            //I added this header just so it can get the json object. I'm not sure if its necessary but can't hurt to put that in
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("Content-Type","application/json");
            try {
                //since im getting item feeds, I set my request method to get
                conn.setRequestMethod("GET");
                //conn.setDoOutput(false);
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            int stat;
            try {
                //this line is here to test the code. If it returns 200 then it succeeds otherwise theres something wrong with the payload
                stat = conn.getResponseCode();
                Log.e("Connection code", " " + stat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("e", "Params= "+params[0]);
            try {
                //get the item feed
                inputstream = conn.getInputStream();
                result = getStringFromInputStream(inputstream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Item items = null;
            if (result != null && result.length() > 0) {
                items = new Item(result);
                itemFeed = items.itemFeed;
            }
            Log.e("Inputsteam", " " + inputstream);
            Log.e("Result string", "" + result);

            //Starting activity with search results passed in
            return items.itemFeed;
        }
        @Override
        protected void onPostExecute(List strings) {
            Intent intent = new Intent(SearchActivity.this, DisplaySearchResultsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, result);
            startActivity(intent);
        }
    }
}
