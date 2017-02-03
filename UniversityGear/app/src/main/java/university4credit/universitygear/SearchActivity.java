package university4credit.universitygear;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.io.BufferedReader;
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
    public List<Item> itemFeed;
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXa2wUVRTu9kWaWjQGEB/UZVBR6szeeezs7qRd3T6AFVoKW5BWmjqPO8vA7Mxm7ky7EwVqoyQKMRATIAEjGoLB1w9Eo/GBiUQjlCAYSFV+QMQgRKIRFQRNvLN9sK2x9EFiE3d/TO6d8/rO+c6dc0Fnccmc9fPXXyrzTcrf2Qk6830+uhSUFBdVTC7Iv6MoD+QI+HZ23tNZ2FXwQyUSU3paWAJR2jQQ9GdSuoGE7GYV4ViGYIpIQ4IhpiASbFlIxOoXCgwFhLRl2qZs6oQ/XltFMBFAq1wEcAwflsIKh3eNfptNZhXBA46PhFnA87wSAmH8GiEHxg1ki4aN1QEdIgFDAroJ0AJgBUBTDMe2EP5l0EKaaWARChDRbLRCVtfKCXX4SEWEoGVjI0Q0HpubWBSL19Y1NFUGcmxF+9KQsEXbQYNXNaYC/ctE3YHDu0FZaSHhyDJEiAhEez0MNirE+oMZQ/jZTNNMKCRDBoIIA2UVMjcklXNNKyXaw8fh7WgKqWZFBWjYmu1eL6M4G9IqKNt9qwZsIl7r9x6LHVHXVA1aVURddax5aaJuCeFPNDZaZrumQCWLlGVZng9GeCJqQ4RTCK021XT0JLSUPl+9BvsyPcRZjWkompc35G8w7WqIA4eD0wOEYE56sNAiY5EVU20vqFw5tj+NTLjFq2tvIR17peGVFqZwLvzZ5fWL0M+Kazy4UbyQaRCRaVnkuTAI0Zw6hBder4+JG1GvPLHGxgCURJdMidZqaKd1UYakjFPrpKClKQLHSRwvyirJMmGZ5JSgTEYiQUgykIc8I7IcULn/GT1s29Ikx4YDFBn6IouzikjIZho2mromu8RQkeyp00eIDKoiVtp2WggEOjo6qA6WMq1kgAGADiyvX5iQV8KUSAzIatcXJrUsNWSItZAm2G4aR5PBzMPOjSQRZS2lUbRst9px8ToBdR0/+tk7KMLo0N1/gYo8qBMLpKePsAExrVEewSnZTAVMETezt9WWjdg/EqGA5LjYvwItyoKiYhq6O3K9pIMJ3Ks9MiWEq0H19iKGMdSj1+ujMjAKp5rRjrlsWu4oYQ5WHoWOKMumY9hjcdenOgoN1dFVTde9dh2Lwxz10YRpiLprazIacDmuLoul03FlYnUZV2NBfNiSSw2t3fvQkInq5aTEBVWe45gwKcGwGuKl4LhgK7Bdk2GbNsGgG46ujwtXfXJYSLjXt/8HsBoCsXGhqoXtE42kQUbiFEaiyUiIk0gOjyykpAA85oSgDIM04NQgMy7MNbqGD4Ymd6J9A+ebyIbK+KDhSXRigfJOmP4DRgkHJTIU5jg8mbIiKUYgS3KRoDxSyEM2cia6f8zygcHX6Whe9kd3+T4CXb738Y0chABJV4AHiguWFhbcRCDNhhQSDUUyM5QmqhTSkga+LlqQWg3dtKhZ+cW++u82Nq/LucjvbAXTB67yJQV0ac69Htx17U0RffNtZXQI4GEV/1lAt4BZ194W0tMKp6xZJZUe+vlQh9M2D537cvOOR9YeLQVlA0I+X1FeYZcvL3h219bYse7TF+LJXdUHt1lMyWOvP7X8zCvRLfvfPfDLy2cXPHrkoSvzwcxt5e3TeuQLrcbWvNKnmal7fpJ3rY31lLfO3f5ZZsare3uorz/Yw58+3Hyq7PRbH3avir44nZ996b0pz7uLNwkvBJgVdRWbz525++Jzu387b1E1yckzmrds2Htn6/n9lw8e/qKCO/T7vg2zW2rfuNxiTiWl3YG3j4ZPzAtVb5r1zurWy5kTU7gN2pvrJh157ZaN5ufdZGXHiWPmg5FvxQyjVYae2I9+TJ3ct+Dqs+XFL7lNhcHIgXL+mY9XEDNvvXLq+z/v5R6OzAn2fPLk1U9P/nVx8f3da/648NU3tcdvf/z4rzvu6y3j3wp+9b9iEQAA");
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
            itemFeed = items.itemFeed;

            return items.itemFeed;
        }
        @Override
        protected void onPostExecute(List<Item> strings) {

        }

    }


}

