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
                mEdit.setSelection(2);
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#p^3#I^3#r^0#f^0#t^H4sIAAAAAAAAAOVXa2wURRxn+yIVCoHwjoZjJRht9m72cXt3G3rm+kCqfZzclVAUmtnd2XZlb/fYx7ULREqJRBJIND4wYkyFIGIkvoiBD4RIMBgJUeADQoIgSMQQQxRNMRFx9tor1xpLHyQ28b5cZub/+v3m95+dAZ0lpY9tWbqlp4yYWNDdCToLCIKeBEpLisunFBbMK54A8gyI7s6FnUVdhdcWWzClpYVlyEobuoV8HSlNt4TsZAXpmLpgQEu1BB2mkCXYkpCI1dcJjB8IadOwDcnQSF9tdQXJMCCIYFgCoqhEQgyNZ/VczKRRQYZ4yNCywiApEoYKL+N1y3JQrW7ZULexP6BDFKApFiQBLXCcwAJ/MBJZSfqWI9NSDR2b+AEZzZYrZH3NvFqHLhVaFjJtHISM1saWJBpjtdU1DcnFgbxY0T4eEja0HWvgqMqQkW851Bw0dBoray0kHElClkUGor0ZBgYVYrliRlF+luqQSEt0iOdlGUgS4OF9oXKJYaagPXQd3owqU0rWVEC6rdruvRjFbIjPIcnuGzXgELXVPu/vaQdqqqIis4KsqYw1NyVqlpG+RDxuGhlVRrKHlGZZlueDEZ6M2sjCFCKzRTEcrRWZcl+u3oB9TA9KVmXosurxZvkaDLsS4cLRYHrYPHqwUaPeaMYU2ysq347N0RjCdoHcRjp2m+5tLUphLnzZ4b03IaeKuzq4X7qIyIwMI+EIZFkQivDsQF14vT46bUS97YnF4wEkQpdKQXMNstMalBAlYWqdFDJVGYcSOR5KCsUyYYni5KBERSJBRDGIRzwDWQ4o3P9MHrZtqqJjo36JDF7I4qwgE5KRRnFDUyWXHGySPXX6BNFhVZBttp0WAoH29nZ/O+s3zNYAAwAdWFFfl5DaUAqfBTlb9d7GlJqVhoSwl6UKtpvG1XRg5eHkeisZZU05Dk3brXRcPE4gTcN/OfUOqDA6ePZfoFoe1PEF0vO3cACYVv2ewP2SkQoYEDezN9WSrdg3HKOA6Lg4v4xMv4mgbOiaO3y/VgcLuNd7eE4W3g1/by9iGIMyer0+sgAjSKrqGaxlw3RHCHOg8wh8oCQZjm6PJl2f6wg8FEdTVE3z2nU0CfPcR1KmDjXXViWrP+WYuiyWTtfK46vLuCoT4cOWatLVjPehoRKVKyiRCyo8xzFhSkRhJcSLwTHBllFGlVCLOs6g646mjQlXfetQkHCvv/VfwGoIxMaEqhplxptIg4zIyYxIU5EQJ1IcvrJQogzwNSeEJBSkAacEmTFhrtJUfDAk3fH2DVxqWDaSxwYN30THFyjvhMkdMHI4KFKhMMfhmykLKRhBLMVFgtJwIQ+ayLvR/eMuHxj4no5OyP7oLuIw6CIO4Sc5CAGKLgePlhQ2FRVOJi3VRn4L6rJodPhVqPgttVXHz0UT+dcgNw1Vs6CEqL+yrXlj3ku+exWY0/+WLy2kJ+U97MGDd1eK6amzy+gQoFlMHcexYCV4+O5qET2raMbZXU1rL2bqZhzZnll9cN+JM1dOzboByvqNCKJ4QlEXMeHIdx1PEg+tNx/YuVU72nLz2ym3Nhz/+qd14f1XP2xrLnWVk80zUgUb5v31InvAlzx+641HZpZZF7fW9DTuYX/ZeXv70cePLFy0buKOz3ZPO733bWZx3fnZyy59tZZff+1Z4UyP7+c5hHs43F0+7b1V4Lc7m0u4Sc/T+y7/WDb1XYL9/vOrxZ9sC+wqSCajG+NvTj/2xAJ28ub43LN1p169WLqt/BpRcU4JR0+Gp7pfdH30+uoT53/49IU/qczN+c13XrN7Duof/HFgxa+XziVeunHgLNjz8qVnjs29ieouHHL8m4618cQrm97fuLfl4/L98xfdfur6/B1fLpg+c86Sy+E7v19Yf/1WxzvJkt2lp7/p3ca/AecbINVjEQAA");
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

