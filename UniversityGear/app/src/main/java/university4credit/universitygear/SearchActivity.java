package university4credit.universitygear;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
                new asynctask().execute(join);
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
    private class asynctask extends AsyncTask< String,Void,String> {
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
        protected String doInBackground(String... params) {
            //this is the url that i passed in
            String urlString = "https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q="+params[0]+"&limit=1";
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#f^0#p^1#I^3#r^0#t^H4sIAAAAAAAAAOVXb2wURRTv9dorDVSNEEuIyrEStcDuze5e9+423CV3BcJByxWuNNDQwNzuXLvt3e5mZ9b2CMRatQSCiQLygUQgYkzUxA9GMRjAogKxRDGQGCV8Akn8E/BPolSCqbPXo1yrgQqnIXG/bOa9N29+7/femz+gz1M9b2DpwNUaV1X5/j7QV+5y8VNBtady/n3u8lmVZaDIwLW/b25fRb/724UYZjOmvAph09Ax8vZmMzqW88IwY1u6bECsYVmHWYRlosjJaFOjLHBANi2DGIqRYbzxRWFGRH5JqBdQQOGDKuIRleo3fLYYVA8Qr6RBIAX5kOJPC1SPsY3iOiZQJ2FGAHyABTwrSC0AyH5J9vs5EKxvY7ytyMKaoVMTDjCRPFw5P9cqwnprqBBjZBHqhInEo0uSiWh80eIVLQt9Rb4iBR6SBBIbjx81GCrytsKMjW69DM5by0lbURDGjC8yusJ4p3L0Bpg7gJ+nOqUilQ+gYEhEQBTV0lC5xLCykNwahyPRVDadN5WRTjSSux2jlI1UF1JIYbSCuogv8jq/lTbMaGkNWWFmcSy6NtrczERiVg7qjRpkV+vaUw5YNhlbw/JBqEJYn1ZZQVUFRUBiYaFRbwWaJ6zUYOiq5pCGvSsMEkMUNZrIjVjEDTVK6AkrmiYOomI74QaHImhzkjqaRZt06k5eUZYS4c0Pb5+BsdmEWFrKJmjMw0RFnqIwA01TU5mJynwtFsqnF4eZTkJM2efr6enhekTOsDp8AgC8b01TY1LpRFnIUFun10fttdtPYLV8KAptY2ovk5xJsfTSWqUA9A4mIkiSJAQKvI+HFZko/YugKGbf+I4oVYeoCPFQTKFAyq/wqhAoRYdECkXqc3CgFMyxWWh1I2JmoIJYhdaZnUWWpspifVoQg2nEqlIozfpD6TSbqlcllk8jBBBKpZRQ8P/UKJMt9YaMRpUttNRKUu8lq/WlBiZInWyt/21oScUwUbOR0ZTcfxKb0+uTjk+01GZokVzMztFxEmUy9HdX4Sr5TK4v0cZVskT+w565s9g1SO6tqHl/sF4M8RLw311c9EJzT8WlGFnO2YY5C5rEsDgKzcwgzFkIG7ZFL2BcwjmUW4xupNMtjlhGJoOsVv6uWMBOI99bPFQ8c9hxgakPaGqjlFBufAak0Tui9XnQ3skY+VJ2juuwESYUiIqsf+GA941/bkTK8h/f7zoC+l2H6IsFBADLzwd1HvfqCvc0BmsE0dzqasro5TSY5rDWodPbtIW4bpQzoWaVe1xNF7evfbroobO/Hcwce+pUu/mpRe8e8PBNTSV/f20NHwC8IAHgl/z+NvDYTW0F/1DFjMbknNPDX9RwddfnbBgm24a2XT5dDWrGjFyuyrKKfleZlT17dktd67XYxxu/unRi1pmt3yeuRwd3/dh14NWa4cG9v7wnsEd+qDUqH13+0xS845NDQ1UHL/z6xN7j298dCv38QvvziZdnzFx2+jfP1S8DUuVcor/eMzJy4ZHv9vg2hbvh+/sOL4tx5z49cPCPD3/fPXto90l34qUFtrD5jaEt5xa8dnFP+5REZ9dWZujyzJa3ZenS401RH04vOV8+snJbtafMPHlq3QffaDD+dfbUtCubau23zh99oKpBq513fMfq4MY3Z5+ZM9B+7PI7nitzr3Ufq3JdOyQ1oVWtcVF+ZfCjOvnBz1nm+mc7D3dtfnIf8EyvmL7BfaLt6K7lL67sN9cNP1u9ZWBw5LnRNP4JaptfnYIOAAA=");
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
            return tempstr;
        }
        @Override
        protected void onPostExecute(String strings) {
            //I set the result on the screen for testing purpose.
            mText.setText(tempstr);


        }

    }


}
