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
        public String itemLimit = "10";
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXa2wUVRTutt02pbQEwqMaDesUSABnd96zO6EL2xbCAi2VLVigBu/M3C0DszPr3Jm24yPUSopCg6LGEAXZGEEhJvhIFEMUYiSCmBA0/DARlGgI8RUVI/2B0TvbB9saSx8kNnH/bO655/Wd850791IdRSULupZ3XS/zFednOqiOfJ+PLqVKivwLywvy7/TnUTkKvkzHnI7CzoKrixBI6WlpDURp00Aw0J7SDSRlhVWEYxmSCZCGJAOkIJJsRUrE6lZJTJCS0pZpm4qpE4F4bRXBK4yoymGRlhWeVQGLpUa/z0azihDCEUGEYZWlZSoCIjzeR8iBcQPZwLCrCIaiRZKiSZZupDiJ5iWeCYqcsIEIrIMW0kwDqwQpIppNV8raWjm5Dp8qQAhaNnZCROOxZYnVsXjt0vrGRaEcX9G+OiRsYDto8KrGVGFgHdAdOHwYlNWWEo6iQISIULQ3wmCnUqw/mTGkny21CGFEZiiFlSOCAIB8W0q5zLRSwB4+D0+iqWQyqypBw9Zs91YVxdWQt0DF7lvVYxfx2oD3d58DdC2pQauKWFodW782sXQNEUg0NFhmq6ZC1UNKsywrCHxEIKI2RLiE0NqUNB29BVpqX6xeh32VHhKsxjRUzasbCtSbdjXEicOh5aFyyoOVVhurrVjS9pLK1RP6y8hSG7y+9jbSsTcbXmthCtcikF3eugn9rLjJg9vFi7BCM4oIWZGGEQaIwmBeeLM+Nm5EvfbEGhpCUAYumQLWVmindaBAUsGldVLQ0lSJ42ROAEqSZJmwQnIqr5CRCA9JBgpQYADLUUnuf0YP27Y02bHhAEWGbmRxVhEJxUzDBlPXFJcYqpI9dfoI0Y6qiM22nZZCoba2tmAbGzStlhBDUXSoqW5VQtkMU4AY0NVurUxqWWooEFshTbLdNM6mHTMPBzdaiChrqQ3Ast1qx8XrBNR1/NfP3kEZRodK/wUq8qBOLJCePcIOQFoLegQPKmYqZAI8zJ5oUzbjwEiUQrLj4vgqtIIWBKpp6O7I7VocTOBe65EZIdyNYO8sYhhDInqzPjoHowiqGa2Yy6bljhLmYONR2ABFMR3DHku4PtNRWCQdPanpujeuYwmYYz6aNA2gu7amoIGQ45qyWDodVyfWlHE1FsSHLbnW0Fq9Dw2ZqG4iZY5PChzHhEkZhpOiIPPjgq3CVk2Bm7QJBt1wdH1cuOpahoOEZ33ffwGrPhQbF6pa2DrRSMozMqcyMk1GRE4mOXxlIWWVwtccESqQpykuyTPjwlyja/hgaHQn2jdwuYlsqI4PGr6JTixQ3gnTf8CoYV4mxTDH4ZspC0gQgSzJRXhlpJCHCHJudP+4y4cGv6ejedkf3en7gOr0vY+f5JRIkfRCan5RwdrCgskE0mwYRMBQZbM9qIFkEGktBn4uWjC4FbppoFn5Rb66b7vXb8t5yWceoCoG3vIlBXRpzsOeuuvmjp+eMquMFimaxeyleZ7ZQFXe3C2kZxZO7/yy+eSVi3PuPv39zorDB44ny8uuHaXKBpR8Pn9eYacvb7u/5OzGslOPn2zaVz/twzcz8yf3LPj1yNNTFn93f55wrrK5fvvKa4kjv3+12y1fcvkduvhG5o+Me735s1a+3f9Midb6WEemdNHMM0z6x/27utNHG4M1l6ZOeu6CE//lxLF7Pq24vHfSkb3SledXPuW/YLzytpMB215973O9qdT9Zvr2XS/O2lFRvqVmfWHxu5X7m+cu+WSj9cPpno49qXl65Z87Zy+7d+6Z2SenwrM7SnrWdL30BHdH+Z7ICzceunzg3KX47q+fPPXsowff+HnlimLi6qWHmQf9hw7PKA1vVJq+2Pbxzu55V2q6jp14+eLrK4QFS873PHKo+/zxFYtLXis7eOPwsY92znhr+rSZf/30W28b/wanr4AOYxEAAA==");
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
                result = getStringFromInputStream(inputstream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Item items = new Item(result);
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

