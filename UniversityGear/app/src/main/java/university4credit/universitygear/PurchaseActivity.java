package university4credit.universitygear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Devin on 2/23/2017.
 */

public class PurchaseActivity extends AppCompatActivity{
    Boolean showBillingInformation = false;

    //Button billingButton = (Button)findViewById(R.id.sameAsShippingButton);

    /*Intent itemIntent = getIntent();
    String itemId    = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_ID);
    String itemTitle = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_TITLE);
    String itemPrice = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_PRICE);*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        /*Bundle itemBundle = getIntent().getExtras();
        String itemId    = itemBundle.getString("item_id");
        String itemTitle = itemBundle.getString("item_title");
        String itemPrice = itemBundle.getString("item_price");*/

        //This block is to hide the billing info section if it is the same as the
        //shipping info
        final CheckBox billingCheck = (CheckBox)findViewById(R.id.sameAsShipping);
        billingCheck.setChecked(false);
        billingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            RelativeLayout billingInfoSection = (RelativeLayout)findViewById(R.id.billingInfoSection);

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (billingCheck.isChecked()) {
                    billingInfoSection.setVisibility(billingInfoSection.GONE);
                } else {
                    billingInfoSection.setVisibility(billingInfoSection.VISIBLE);
                }
            }
        });

        Intent itemIntent = getIntent();
        String itemId    = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_ID);
        String itemTitle = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_TITLE);
        String itemPrice = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_PRICE);

        TextView title = (TextView)findViewById(R.id.checkoutTitle);
        title.setText(itemTitle);

        TextView price = (TextView)findViewById(R.id.checkoutPrice);
        price.setText(itemPrice);

        new PurchaseItemTask().execute();
    }

    private class PurchaseItemTask extends AsyncTask<Void, Void, Void> {
        URL purchaseUrl;
        HttpURLConnection apiConnection;
        InputStream sessionStream;
        String sessionId;
        String streamResult = null;
        JSONObject purchaseInitiateJSON;
        String checkoutSessionId = null;

        @Override
        protected Void doInBackground(Void... params) {
            //set the URL to be used in starting the purchase session
            String purchaseUrlString = getString(R.string.purchaseInitiateUrl);
            try {
                purchaseUrl = new URL(purchaseUrlString);
            } catch(MalformedURLException e) {
                e.printStackTrace();
                Log.e("PURCHASE URL", "URL Failed");
            }

            //Open the connection using the purchase url
            try {
                apiConnection = (HttpURLConnection)purchaseUrl.openConnection();
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("CONNECTION","Failed to connect");
            }

            //Set the correct header information
            SharedPreferences sharedPreferences = getSharedPreferences("oauth", Context.MODE_PRIVATE);
            apiConnection.setRequestProperty("Authorization", "Bearer " +  sharedPreferences.getString("oAuthToken",""));
            apiConnection.setRequestProperty("Accept", "application/json");
            apiConnection.setRequestProperty("Content-Type", "application/json");

            //Set the correct request method
            try {
                apiConnection.setRequestMethod("POST");
            } catch(ProtocolException e) {
                e.printStackTrace();
                Log.e("REQUEST METHOD", "Failed to set request method");
            }

            Log.e("API CONNECTION", "Connection: " + apiConnection);

            int responseCode = 0;
            try {
                responseCode = apiConnection.getResponseCode();
                Log.i("RESPONSE CODE", "Response code: " + responseCode);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("RESPONSE CODE", "Response code: " + responseCode);
            }

            //Open the input stream
            try {
                sessionStream = apiConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(sessionStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                streamResult = total.toString();
                //streamResult  = getStringFromInputStream(sessionStream);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("SESSION STREAM", "Could not get a session stream string");
            }

            //Create a JSON object from the returned streamResult and get
            //the checkoutSessionId
            try {
                 purchaseInitiateJSON = new JSONObject(streamResult);
                if (streamResult.contains("sessionCheckoutId")) {
                    checkoutSessionId = purchaseInitiateJSON.getString("sessionCheckoutId");
                }
            } catch(JSONException e) {
                Log.e("JSON CREATION", "Failed to create a JSON object");
            }

            if (checkoutSessionId != null) {
                Log.i("CHECKOUT ID", "ID: " + checkoutSessionId);
            } else {
                Log.i("CHECKOUT ID", "ID is null");
            }

            return null;
        }
    }
}
