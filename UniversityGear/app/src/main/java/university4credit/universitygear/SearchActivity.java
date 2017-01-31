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
                mEdit = (EditText)findViewById(R.id.editText1);
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
        protected Item doInBackground(String... params) {
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXeWwUVRjv9jIVqAYREDVZp3gAmdk59xi7i9uDdE1bFratHNb6ZuZNOzA7s5k303ajpKUEECWgBqkokQYI0ZBoYuQPNah4JB5A4v2HEARMUIMaDdHEgvhme7BdY+lBYhP3n81777t+3/f73ryP7i4uWbipZtMfMzzX5fd10935Hg8zjS4pLlpUWpA/ryiPzhLw9HXP7y7sKfi+HIGknhKXQ5QyDQS9nUndQGJmM0w4liGaAGlINEASItGWxUS0rlZkKVpMWaZtyqZOeGNVYYJjggJU/ArNSkFO4gS8awzZbDDDRDDEygLN+LmQILNqkMbnCDkwZiAbGHaYYGkmQNIMyTENTFDkaJELUDznX0V4m6CFNNPAIhRNRDLhihldKyvW0UMFCEHLxkaISCy6JLE0Gquqrm8o92XZigzmIWED20EjV5WmAr1NQHfg6G5QRlpMOLIMESJ8kQEPI42K0aFgJhB+JtUCzQZVjgUAKAGOZq5JJpeYVhLYo4fh7mgKqWZERWjYmp2+WkJxMqQ1ULYHV/XYRKzK6/4tc4CuqRq0wkR1RXRlY6J6OeFNxOOW2a4pUHGBMhzH+f1CyE9EbIhwBqHVopqO3gotZdDXgMHBROc4qzQNRXPThrz1pl0BceAwNz1sVnqw0FJjqRVVbTeoLDlXaECOCa1yyzpQR8duM9zKwiTOhTezvHoRhkhxhQbXiha8zEmAlWRJEjiWkfgcXri9PiFuRNzyRONxH5RAmkwCay20UzqQISnj1DpJaGmKyPMS7weySnJsUCZ5RZDJUEiAJAv90M8CjqdV/n9GD9u2NMmx4TBFcg8yOMNEQjZTMG7qmpwmckUyl84gITpRmGiz7ZTo83V0dFAdHGVarT6WphnfirrahNwGk4AYltWuLkxqGWrIEGshTbTTKRxNJ2Yedm60EhHOUuLAstMVThqvE1DX8d8Qe0dEGMnd/ReoyIU6tUC6+ggbACmNcglOyWbSZwLczO5WSyZi71iEfJKTxv4VaFEWBIpp6Omx67U6mMAD2mNTQrga1EAvYhi5Ht1eH5eBcTjVjHbMZdNKjxPmSOVx6ABZNh3Dnoi7QdVxaKiOrmq67rbrRBxmqY8nTAPoaVuT0bDLSXVZNJWKKVOry/hKC+LLlmw0tHb3Q0MmKlaQEi+ofp5ng6QEg2rALwmTgq3Adk2GLdoUg244uj4pXHWto0LCvf78fwCr3hedFKoq2D7VSCqwEq+wEkOGArxE8vjJQkoKjZ85AShDgaF5VWAnhblS1/DF0JCeat/AGhPZUJkcNPwSnVqg3Btm6IJRgoJEBoI8j1+mHCBBCHIkjwfRsULO2ch60f3jLe8bOU5H8jI/psdzmO7xvI4ncjpAk8wiekFxQWNhwXQCaTakEDAUyeykNKBSSGs18LRoQWotTKeAZuUXe+rObl3ZlTXI9zXTc4dH+ZICZlrWXE/fduWkiLlhzgwmQDMcwwQ5mgusosuunBYyswtnNTHawpMbLm8MH9ryxqnD+27+ZmfHjfSMYSGPpyivsMeT98hnx+edv/fU6brbL8zt2Hz+hZi6/p0vipSeo4/tf2kB2bO40e5aezD+9OoLP7Uwdf3nT1gvf91z9yFP17qm3jet7/b83lR6upl4sqz5eNmeMyV7+1/ZtezZkxurFt/67c7yjr+2nDi745xxj0V9EF/Yd+ajty6eO1Kwcv6Z1f5E6l0GBLrW3Hn/tGO7t2348/LeA77nyE8/f7C59ODMNrD9B2Xre3M2vn3gqf2Vs7suvfrQpXV3NV183PPMz5cX/LLCe6T0x152wwP9mzcfPPnEa0dVcLRXXV3blng/Xf6oJu7afUv/J70f/kZcqNnGrt9x37zose21VOL6F+94+Ktf2z/+cub0c9XaTbXirH27alD3QBn/Bosu2ZtiEQAA");
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
            }
            Log.e("Inputsteam", " " + inputstream);
            Log.e("Result string", "" + result);

            return null;//items;
        }
        @Override
        protected void onPostExecute(Item strings) {

        }

    }


}

