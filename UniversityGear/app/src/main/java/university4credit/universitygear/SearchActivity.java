package university4credit.universitygear;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android


        .widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "university4credit.universitygear.MESSAGE";
    Button mButton;
    EditText mEdit;
    TextView mText;
    TextView num1,num2;
    String newtext;
    Context context;
    int mindistance;
    String tempstring;
    String tempstr;
    String temp = "";
    String schoolname;
    String join;
    CheckBox box1, box2, box3, box4;
    JSONObject json;
    String tempid = "";
    String price="";
    String [] database = {"Oregon","State", "University", "Jersey", "Mug", "Nike", "Underarmour" };
    public List<Item> itemFeed = null;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreference;

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
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        Intent intent = getIntent();
        schoolname = intent.getStringExtra("asdf");
        box1 = (CheckBox)findViewById(R.id.checkbox1);
        box2 = (CheckBox)findViewById(R.id.checkbox2);
        box3 = (CheckBox)findViewById(R.id.checkbox3);
        box4 = (CheckBox)findViewById(R.id.checkbox4);
        mButton = (Button)findViewById(R.id.button1);
        temp = "";
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                temp = "&filter=conditions:%7B"+checkedRadioButton.getText().toString()+"%7D";
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideSoftKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                if(box1.isChecked()){
                    tempid += "&category_ids=2228";
                }
                if(box2.isChecked()){
                    tempid += "&category_ids=11450";
                }
                if(box3.isChecked()){
                    tempid += "&category_ids=888";
                }
                if(box4.isChecked()){
                    tempid += "&category_ids=64882";
                }
                num1 = (EditText)findViewById(R.id.editText);
                num2 = (EditText)findViewById(R.id.editText2);

                if(num1.length()!=0){
                    if(num2.length()!=0){
                        price = "&filter=priceCurrency:USD&filter=price:["+num1.getText().toString()+".."+num2.getText().toString()+"]";
                    }
                    else{
                        price = "&filter=priceCurrency:USD&filter=price:["+num1.getText().toString()+"]";
                    }
                }
                else{
                    if(num2.length()!=0){
                        price = "&filter=priceCurrency:USD&filter=price:[.."+num2.getText().toString()+"]";
                    }
                    else{
                        price = "";
                    }
                }
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
                new SearchItemTask().execute(join,price,temp,tempid,schoolname);

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
        URL url, url2;
        HttpURLConnection conn,conn3;
        InputStream inputstream;
        String result = "";
        String oAuthtoken;

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
            String id = "BryanLia-Universi-SBX-18adaa5fd-2dd2c2e3:SBX-8adaa5fd4c3c-8c04-4d13-9adb-8196";
            byte[] data = new byte[0];
            try {
                data = id.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
            try {
                url = new URL("https://api.sandbox.ebay.com/identity/v1/oauth2/token");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn3 = (HttpURLConnection)url.openConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
            conn3.setRequestProperty("Authorization","Basic "+base64);
            conn3.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn3.setDoOutput(true);
            try {
                conn3.setRequestMethod("POST");
                //conn.setDoOutput(false);
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            String urlParameters  = "grant_type=client_credentials&redirect_uri=Bryan_Liauw-BryanLia-Univer-kheulrrfh&scope=https://api.ebay.com/oauth/api_scope";
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            conn3.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
            try {
                conn3.getOutputStream().write(postData);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();

                json = new JSONObject(sb.toString());
                oAuthtoken = json.getString("access_token");
                sharedPreference = getSharedPreferences("oauth", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();

                editor.putString("oAuthToken", oAuthtoken);
                editor.commit();
                Log.d("hey", oAuthtoken);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("e","asdf");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String urlString ="https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q="+params[4]+params[0]+params[1]+params[2]+params[3]+"&filter=deliveryCountry:US&filter=itemLocationCountry:US&\tfilter=buyingOptions:%7BFIXED_PRICE%7D&limit=" + itemLimit;
            byte[] ptext = urlString.getBytes();
            String val = "";
            try {
                val = new String(ptext,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            Log.d("e", urlString);
            Log.d("4", val);
            Log.d("3", params[3]);
            Log.d("2", params[2]);
            Log.d("1", params[1]);

            try {
                url = new URL(val);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection)url.openConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //this is the header that you need to pass in. The authorization key is something you get from the eBAy website. I can tell you how to get them but I think we can just use my key
            conn.setRequestProperty("Authorization", "Bearer " + oAuthtoken);

            //I added this header just so it can get the json object. I'm not sure if its necessary but can't hurt to put that in
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Connection", "close");
            try {
                //since im getting item feeds, I set my request method to get
                conn.setRequestMethod("GET");
                conn.setRequestProperty( "Accept-Encoding", "" );
                //conn.setDoOutput(false);
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                Log.e("ERROR", conn.getResponseMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("ERROR", conn.getRequestMethod());
            try {
                Log.e("ERROR", String.valueOf(conn.getResponseCode()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                //get the item feed
                inputstream = conn.getInputStream();
                result = getStringFromInputStream(inputstream);
            } catch (IOException e) {
                e.printStackTrace();

            }
            Item items = null;
            Log.e("RESULT STRING", "" + result);
            if (result != null && result.length() > 0) {
                items = new Item(result, false);
                itemFeed = items.itemFeed;
            }
            else {
                items = new Item(true);
            }
            //Log.e("Inputsteam", " " + inputstream);
            //Log.e("Result string", "" + result);
            //Starting activity with search results passed in
            return items.itemFeed;
        }
        @Override
        protected void onPostExecute(List strings) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(SearchActivity.this, DisplaySearchResultsActivity.class);
            if(result.isEmpty()) {
                //Display Message if no results are found
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("No Items Found");
                builder.setMessage("Your keyword/s did not return any items.");
                builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                
            } else {
                intent.putExtra(EXTRA_MESSAGE, result);
                startActivity(intent);
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(), 0);
    }
}
