package university4credit.universitygear;

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

import org.json.JSONException;
import org.json.JSONObject;

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

public class SearchActivity extends AppCompatActivity {
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
                mEdit   = (EditText)findViewById(R.id.editText1);
                mText = (TextView)findViewById(R.id.textView1);
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

    private class SearchItemTask extends AsyncTask< String,Void,Item> {
        private String temp;
        private Context tempc;
        URL url;
        HttpURLConnection conn;
        InputStream inputstream;

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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Item doInBackground(String... params) {
            //this is the url that i passed in
            String urlString = "https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q="+params[0]+"&limit=10";
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXeWwUVRjv0kPK4R2oBOM6kCCS2Z2ZnZndGdnV7YFsoIfdFoHY1Dczb9qB2ZnNvJm2E1GbKqCCROQMoDYgEalRokFDIsYoCX+QQBP4gyjgQTgSiIkoh1Ea32wPtjWWHiQ2cf/ZvO991+/7ft+b96jWgsLHV81fdX2y765x7a1U6zifj55IFRbkz7k7d9y0/BwqS8HX3jqzNa8t9+JcBFJ6WqyGKG0aCPpbUrqBxIwwSjiWIZoAaUg0QAoi0ZbFZLx8ocgEKDFtmbYpmzrhT5RGCU4IybIUDgkSq0qqCrDU6PVZY0YJKNMKS4VUlVIYAPkQ3kfIgQkD2cCwowRD0WGSoklGqKF4keFEJhIIhUJLCf8iaCHNNLBKgCJimXTFjK2VlevgqQKEoGVjJ0QsEZ+XrIwnSssqauYGs3zFeuqQtIHtoP6rElOB/kVAd+DgYVBGW0w6sgwRIoKx7gj9nYrx3mRGkH6m1BE+DEIsJcmSIIVpjr8jpZxnWilgD56HJ9EUUs2oitCwNdu9XUVxNaRlULZ7VhXYRaLU7/094wBdUzVoRYmy4viS2mRZNeFPVlVZZpOmQMVDSuO0eZ4TeCJmQ4RLCK161XT0BmgpPbG6HfZUekCwEtNQNK9uyF9h2sUQJw4HlofOKg9WqjQqrbhqe0ll60V6y0izS72+djfSsRsNr7UwhWvhzyxv34ReVtziwZ3iBUdLAsPxkTCgw6wA5P688GZ9ZNyIee2JV1UFoQRcMgWs5dBO60CGpIxL66SgpSkiy0osD2SVDDERmWQVTiYFgYMkA3nIMx5fVfZ/Rg/btjTJsWEfRQZuZHBGiaRspmGVqWuySwxUyZw6PYRoQVGi0bbTYjDY3NwcaA4FTKshyFAUHVxcvjApN8IUPnZ7dbXbK5NahhoyxFZIE203jbNpwczDwY0GIhaylCpg2W6x4+J1Euo6/utlb78MYwOl/wIVeVDHFkjPHmEHIK0FPIIHZDMVNAEeZk9Un8nYPxSloOS4OL4CrYAFgWIaujt0uwYHE7jbemhGCHcj0D2LGMaAiN6sD8/BMIJqRhPmsmm5w4TZ33gYNkCWTcewRxKux3QYFqqjq5que+M6koBZ5sNJ0wC6a2sy6gs5qimLp9MJZWxNGVtiQXzYkrWG1uR9aMhk8WJSYjmVZ1kmQkowooZ5iRsVbAU2aTKs18YYdMPR9VHhKm8YDBKe9R3/BayKYHxUqEph01gjKcdIrMJINCmEWYlk8ZWFlBQKX3PCUIYcTbEqx4wKc4mu4YOhxh1r38D5JrKhMjpo+CY6tkB5J0zvAaNEOIkMR1gW30xDgAQCDJGswMlDhTxAkHWj+8ddPtj/PR3LyfzoNt9Bqs13AD/JqTBF0nOo2QW5tXm5kwik2TCAgKFIZktAA2oAaQ0Gfi5aMLAcummgWeMKfOVn1y55Oesl315HFfW95Qtz6YlZD3tq+q2dfPqeqZPpMEUzAsUzHBNZSs24tZtHT8l78MTTuxb99Pq7D095cv0R98eivXmnyq5Rk/uUfL78nLw2X457qquu8/nqd9D++2ccvbqgeuP6C1+/tm5++V8LklfOd7zS2PXNtt/cX9addLfush+59+ctR9evXb37Q2X/8TcWr555aOvBou05XTmfh85tP9vx7YYbtdEbs359dvYfRZsOdu6s239zmbyxnqodf/2rLZdvsodOXiLe/J3vmFJy9cUPrm2/b8a5xz5ZN33ThOdOlH625/RHgtuyt3DHA99/V7CsS7pw5OIPh6eJB4xNZ74UYtcnwLrjK9Y0vDWxqsB34KXz6kMvTyo89uqZjqKKS4k/Hz38/ttrOlayiQlfdG7bd+5YyemSsheuuO3VT+yxPn3q4qyjmzev2CBd/njzpffKKjt2rhQ6F0zlxuu77bMr9hV2t/Fv9RqMFGMRAAA=");
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
            int stat = 0;
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
                tempstr = getStringFromInputStream(inputstream);



            } catch (IOException e) {
                e.printStackTrace();
            }
            Item items = new Item(tempstr);
            Log.e("Inputsteam", " " + inputstream);

            return items;
        }
        @Override
        protected void onPostExecute(Item strings) {
            //I set the result on the screen for testing purpose.
            mText.setText(tempstr);


        }

    }


}

