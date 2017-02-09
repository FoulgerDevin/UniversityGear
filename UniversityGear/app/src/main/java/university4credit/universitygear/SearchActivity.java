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
            conn.setRequestProperty("Authorization","Bearer v^1.1#i^1#r^0#p^3#I^3#f^0#t^H4sIAAAAAAAAAOVXa2wUVRTu9mmpgCgUgqjrQA0Is3N3Xrs7tBu3D9IqLZVtkUdJuTNzpx06O7PO3Gm7P4xNTUjU2hBFYkxFYgjRQBCJCIRgQoygIVER5YeIMQoJEKKB+IiPoHe2D7Y1lj5IbOL+2dx7z+s75ztn7gVd+YUPb6ne8ut0X0H2zi7Qle3zBYtAYX7e0hk52fPzskCGgG9n16Ku3O6cS6UOTBhJaTVykpbpIH9nwjAdKb1ZRrm2KVnQ0R3JhAnkSFiR4rHalRIbAFLStrClWAblr6kso3hWC4U4UQsDkdU4kSe75qDNBquMgoLIKkDlOSDKSAXeueO4qMZ0MDRxGcWCYIgGLA34BsBKAisBLsALwnrKvwbZjm6ZRCQAqGg6XCmta2fEOnqo0HGQjYkRKloTWxFfFauprKprKGUybEUH8hDHELvO8FWFpSL/Gmi4aHQ3TlpairuKghyHYqL9HoYblWKDwUwg/HSqIeA0ThAioiYICgTabUnlCstOQDx6HN6OrtJaWlRCJtZx6lYZJdmQNyMFD6zqiImaSr/397gLDV3TkV1GVZXH1jXGq1ZT/nh9vW216ypSPaRBjuNCohgSqChGDkkhspux7W7WDaN1wFe/wYFMj3BWYZmq7uXN8ddZuByRwNHI9PAZ6SFCq8xVdkzDXlCZcvxgGrnQeq+u/YV0cavplRYlSC786eWtizDIips8uF284HkV8SoPIS9yMKLKw3nh9frEuBH1yhOrr2eQDFN0AtptCCcNqCBaIal1E8jWVYnnZV6EikZzbFiheVVQ6EhEQDSLRCSykOOBxv/P6IGxrcsuRkMUGXmQxllGxRUrieotQ1dS1EiR9NQZIESnU0a1YpyUGKajoyPQwQUsu4VhAQgya2tXxpVWlIDUkKx+a2FaT1NDQUTL0SWcSpJoOgnziHOzhYpytloPbZwqd1NkHUeGQf4G2TsswujI3X+B6nhQpxZIT98hBmBSD3gEDyhWgrEgaWZvqzkdsX8sQozspoh/FdkBG0HVMo3U2PVaXELgfu2xKTmkGoH+XiQwRnj0en18BsbhVDfbCZctOzVOmMOVx6EDFcVyTTwRdwOq49DQXEMj88Nr14k4zFAfT5gmNFJYV5whl5PqslgyWaNOrS7jK2xEhi3daOrt3oeGjpevpZEqQ5ZHMqKBjFhNZoOTgq2idl1BzfoUg266hjEpXLUto0Eivd73X8CqY2KTQlWJ2qcaSVVVDEV4WabDcojcYQQ5REM5zNMoIkZUQlOgyGBSmCsMnQyGhtRU+wZWWw5G6uSgkZvo1ALlTZjBARMMhzUyZFhIbqaIpcOCwtOywAtjhTxiI+NG94+7PDP8PR3NSv+C3b5joNt3hDzJQQjQwaVgSX5OY27OnZSjYxRwoKnKVmdAh1rA0VtM8ly0UaANpZJQt7PzfbXf96x7OuMlv3MjmDf0li/MCRZlPOzBgpsnecGZc6cHQ4AFPGAFFnDrwcKbp7nB4tzZXb+lmJJ7C64evdr+7plDz9GrwVsHwfQhIZ8vLyu325dloctP3ld46LXzPy2/uGH3S7PfW1z92fKi/Ve22qeSkXXijb4Dd13/iKbVZOxgzpxi+50Fuy+UvPCDce3krsRXbfDRFw+//Ym+49w9l5c9e3fTK00tG/Rvc6xzZ1Mf7+OKe2Kbe37f09Z0ve0h9MiRkudbfnn/+KXikl19s56KLNq7uGneiS8j+MeZG32vSqUb/kLHGa1V3fvn5e/mMdO23XFy7ukLfZ+/sWba1otLvv5jznH+xs8P8Eu3Hznx5rEPl8SeaM4veP3YF9fA/mV7Zh9daxeFZ/RemNV8+FRv1fbzXKKUb7vSs4J5EG5pvRGfHzzT+8yBgtOb7m/s+6Cx+tqmHWc//Wbbvo0vLyx97Hxlfxn/BlwEOvJjEQAA");
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
}
