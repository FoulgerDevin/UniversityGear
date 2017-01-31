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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXf4gUVRy/vV9m52mRVJTkMv6iu2b3zezM7O7oLuz9CLfzfujemR7U8Wbmzd3o7Mw2783drZCdR5xEZhFFRKFX4R8ahphaRj8wyEJO0IIgRPC0QFKoUFMosjd7P9y76LwfQgftP8t77/vr8/1+vt95D/SUzq3oW913vdw3p7C/B/QU+nxcGZhbWlI5v6jwoZICkCfg6+9Z2lPcW3RxFYZpMyOvQzhjWxj5u9OmheXcZoxxHUu2ITawbME0wjJR5VSifo3MB4CccWxiq7bJ+JM1MUYVIYSID4swrPNIUuiuNWKz2Y4x0ZAkhqICz4VCkNN0nZ5j7KKkhQm0SIzhARdmAceGuGYgyoIkc2KAB3wr41+PHGzYFhUJACaeC1fO6Tp5sU4cKsQYOYQaYeLJxOOpxkSypraheVUwz1Z8OA8pAomLx66qbQ3510PTRRO7wTlpOeWqKsKYCcaHPIw1KidGgplG+LlURxCHwmpEQqrGaVCP3JFUPm47aUgmjsPbMTRWz4nKyCIGyd4uozQbyiakkuFVAzWRrPF7f2tdaBq6gZwYU1uV2NiSql3H+FNNTY7daWhI85BSsoTCkhQWmThBmKYQOW3EcTcZptkx7GvI4HCmxzmrti3N8PKG/Q02qUI0cDQ+PaG89FChRqvRSejECypfLjycRi4CWr26DhXSJR2WV1qUprnw55a3L8IIK27x4I7xIiJBneeiihrRFY2TxvLC6/XpcSPulSfR1BRECsyyaehsRiRjQhWxKk2tm0aOocmCoAgSVHU2xEdUVtBElY1GRcTSWYAkHoYEoAv/M3oQ4hiKS9AoRcYf5HDGmJRqZ1CTbRpqlhkvkps6w4ToxjGmg5CMHAx2dXUFukIB22kP8gBwwQ31a1JqB0pDZlTWuL0wa+SooSKqhQ2ZZDM0mm7KPOrcamfiIUdrgg7JVrlZuk4h06R/I+wdE2F8/O6/QMUe1NkF0tPH1ADMGAGP4AHVTgdtSJvZ22rLReyfjFBQcbPUv4acgIOgZltmdvJ67S4l8JD25JQwrUZgqBcpjHEevV6fmoEpODWsTspl28lOEeZY5SnoQFW1XYtMx92w6hQ0dNfU6fzw2nU6DvPUpxKmBc0sMVQ86nJGXZbIZJLa7OoyodpBdNiyLZbR6X1o2FTVBhZpCuQFpCAWKIjXFZ6bEWwNdRoqajNmGXTLNc0Z4apvnwgS7fW3/wtYDcHEjFDVoM7ZRlJNk8JRQVHYiBKmdxhRCbNQiQgsikpRjdIUqAqYEeZq06CDoTk7276Bq21MkDYzaPQmOrtAeRNmZMBwkYhOhwwP6c0U8WxEVAVWEQVxspDHbeTd6P5xlw+OfU/HC3I/rtf3Gej1HaVPchAGLFcJHi0taikumsdgg6AAhpam2N0BA+oBbLRb9LnooMBmlM1Awyks9dVf2LHxubyXfP9T4MHRt/zcIq4s72EPFt06KeEWPFDOhQEX4oAoSJzYCpbcOi3m7i9eWLSi0FW/XXSIW/Tl8vMFCweIcCMGykeFfL6SguJeX4G4u8I9fv3uj6XSvfvfS+yehw5vWXZC/O1m0ddVha09fefeuHRk91ZRMlIlLx0ubtlVvQ/DOSfe3L/z5LOLz/vmH3/tq/VfHL+68vIPXS+UnD9z6r7r29+t216xL218dLD+5GC47sAx/rHEyWee/KD8yNXGC31Xvq9zl97z8v5r3TcqF3Tv6JCeLlt3oOf03s3GtiVnW19hjlx8cetPhdV//pUGW5YPdK0UzlaueOKYCQrKy8j2YzcH614FAxWXHr53LzY+uetg3R8HO3+p+PCbd35s2XnuNDvw/oXXB3/vX3vlzPzEnmunNlR3lR04eqjt+Xm1yd7WwLZBp+3y4UcaPv318+/23XxL2rVsT8GqnxcPlfFvN8rbfmMRAAA=");
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
            //mText.setText(tempstr);
            //Starting activity with search results passed in
            Intent intent = new Intent(SearchActivity.this, DisplaySearchResultsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, tempstr);
            startActivity(intent);
        }

    }


}