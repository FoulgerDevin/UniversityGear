package university4credit.universitygear;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    String [] database = {"Oregon","State", "University", "Jersey", "Mug", "Nike", "Underarmour" };
    public List<Item> itemFeed = null;
    private ProgressBar progressBar;
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
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideSoftKeyboard();
                progressBar.setVisibility(View.VISIBLE);
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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#f^0#p^3#I^3#r^0#t^H4sIAAAAAAAAAOVXa2wUVRTu9kWwFtTwMIaSzYih0Mzundmd3dlJu7h9YEv6WLstjyrWOzN324HZmXHuTNtFDM0moAlCIlFAArYBjfwgvogmRk1F0YgSlKgp6A+IhITUiCZgjJaod7YPtjWWPkhs4v7Z3HvP6zvnO2fuBd35c1fuqN7xW6FrTnZvN+jOdrmYAjA3P69kXk72fXlZIEPA1du9rDs3lXOlFMOEagiNCBu6hpG7K6FqWEhvllG2qQk6xAoWNJhAWLAkIRapqxVYDxAMU7d0SVcpd01lGcXGAZRCKIB8ccQAxJFdbcRmk15GQUbi4z7klyUQ4HneOcfYRjUatqBmEX3ABGnA0oBvApzgYwSW8wSZYAvlXotMrOgaEfEAKpwOV0jrmhmxThwqxBiZFjFChWsiq2MNkZrKqvqmUm+GrfBwHmIWtGw8dlWhy8i9Fqo2mtgNTksLMVuSEMaUNzzkYaxRITISzDTCT6fax8Ag72PYEA9CAQncnlSu1s0EtCaOw9lRZDqeFhWQZilW8lYZJdkQNyHJGl7VExM1lW7n72EbqkpcQWYZVVUe2dAcq2qk3LFo1NQ7FBnJDlLG5/MFA4EgR4UthEkKkdlqmfYmRVXbh30NGRzO9DhnFbomK07esLtet8oRCRyNTw+bkR4i1KA1mJG45QSVKRccTmMgFGhx6jpUSNtq15zSogTJhTu9vHURRlhxkwe3ixcMh4AYDAGWBRwninAsL5xenx43wk55ItGoF4kwSSeguRlZhgolREsktXYCmYos+P2iPwClOO1jeYn2y5xEh0IcolkyEgIs9PlB3P8/o4dlmYpoW2iUIuMP0jjLqJikGyiqq4qUpMaLpKfOMCG6cBnVblmG4PV2dnZ6On0e3WzzsgAw3vV1tTGpHSVIzUdklVsL00qaGhIiWlgRrKRBoukizCPOtTYq7DPlKDStZLmdJOsYUlXyN8LeMRGGx+/+C1TsQJ1dIB19TAxAQ/E4BPdIesKrQ9LMzlZrOmL3ZIS8op0k/mVkekwEZV1Tk5PXa7MJgYe0J6eESTU8Q71IYIzz6PT61AxMwamidRAu62ZyijDHKk9BB0qSbmvWdNwNq05BI26rcTI/nHadjsMM9amEqUE1aSkSHnU5oy6LGEaNPLu6zF9hIjJs6WZN6XA+NHSsfD2NZBGyfiQiGoiIjYssMyPYMupQJNSqzDLomq2qM8JV1zYRJNLrB/8LWPXeyIxQVaKO2UZSWQ4EQ35RpHkxSO4wnBikocj7aRQKhGRCUyCJYEaYK1SFDIam5Gz7Blbr2ELyzKCRm+jsAuVMmJEBw/B8nAwZFpKbKWJpnpP8tMj5uclCHreRcaP7x13eO/Y9Hc5K/5iU6wOQcr1LnuQgCGimBKzIz2nOzbmTwoqFPBhqsqh3eRQY92ClTSPPRRN5NqOkARUzO99Vd+nZDdsyXvK9G8G9o2/5uTlMQcbDHiy5eZLHzF9cyAQBC3jAkScj1wLuv3mayyzKXdB4puHBS6uVIyffPnPt0OCCgb1fLO0DhaNCLldeVm7KldV/46V198z/BV44/tqHx96/XDy4c//PP1eUPtHz1e99p57bsf2pP5ffdeRqsXFM7fvo+IGF1nuJC/trr7/6/I16nMN+9utS4zEx8v2XSwbvPn9eLHjjraIfmkqLaz7GZ+knj52u9BQve+VidX7w86vfrCh5uqHywDMFA4cKB1ZdM3rOPrRnUVV7XvPl3SdN3/Ldy1HXur6/1u+RVWMp9SK1cNec2tOLrwzu6y85P/hTUb78beML/av4Oz7duu1g5eG9PX8crfcODmx5Z/thcWfR628+crSzz3j54tavQ5/8WLQvdaRT27PvXMuuG+CcNh8dmnfqu939VzYasd6e6usnHm8trjrxQOrRldGja1Jb1gyV8W/TJ5Q6YxEAAA==");
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
