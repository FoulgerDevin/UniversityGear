package university4credit.universitygear;

import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Devin on 2/23/2017.
 */

public class PurchaseActivity extends AppCompatActivity{
    List<EditText> allFields = new ArrayList<EditText>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);


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

        //Get item information without having to use another API call
        Intent itemIntent = getIntent();
        String itemId    = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_ID);
        String itemTitle = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_TITLE);
        String itemPrice = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_PRICE);

        Log.i("ITEM ID", "" + itemId);

        //Set the title of the item
        TextView title = (TextView)findViewById(R.id.checkoutTitle);
        title.setText(itemTitle);

        //Set the price of the item
        TextView price = (TextView)findViewById(R.id.checkoutPrice);
        price.setText(itemPrice);

        //Purchase button. It should get the correct information and then
        //execute the purchase async task to call the
        Button purchaseButton = (Button)findViewById(R.id.checkoutButton);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EditText> fieldList = new ArrayList<EditText>();
                Map<String, String>  fieldValues = new HashMap<>();

                fieldList.addAll(checkEditText((RelativeLayout)findViewById(R.id.itemDetailsSection), false));
                fieldList.addAll(checkEditText((RelativeLayout)findViewById(R.id.userShippingSection), false));
                fieldList.addAll(checkEditText((RelativeLayout)findViewById(R.id.billingInfoSection), billingCheck.isChecked()));

                //fieldValues.putAll(getEditTextValues((RelativeLayout)findViewById(R.id.itemDetailsSection), false));

                fieldValues.put("firstName", ((EditText)findViewById(R.id.firstName)).getText().toString());
                fieldValues.put("lastName", ((EditText)findViewById(R.id.lastName)).getText().toString());
                fieldValues.put("streetAddress", ((EditText)findViewById(R.id.streetAddress)).getText().toString());
                fieldValues.put("streetAddress2", ((EditText)findViewById(R.id.streetAddress2)).getText().toString());
                fieldValues.put("city", ((EditText)findViewById(R.id.city)).getText().toString());
                fieldValues.put("state", ((Spinner)findViewById(R.id.state)).getSelectedItem().toString());
                fieldValues.put("zipCode", ((EditText)findViewById(R.id.zipCode)).getText().toString());
                fieldValues.put("email", ((EditText)findViewById(R.id.email)).getText().toString());
                fieldValues.put("phoneNumber", ((EditText)findViewById(R.id.phoneNumber)).getText().toString());
                fieldValues.put("cardNumber", ((EditText)findViewById(R.id.cardNumber)).getText().toString());
                fieldValues.put("expirationDate", ((EditText)findViewById(R.id.expirationDate)).getText().toString());
                fieldValues.put("firstNameCard", ((EditText)findViewById(R.id.firstNameCard)).getText().toString());
                fieldValues.put("lastName", ((EditText)findViewById(R.id.lastNameCard)).getText().toString());
                if (!billingCheck.isChecked()) {
                    fieldValues.put("streetAddressBilling", ((EditText) findViewById(R.id.billingAddress)).getText().toString());
                    fieldValues.put("streetAddress2Billing", ((EditText) findViewById(R.id.billingAddress2)).getText().toString());
                    fieldValues.put("cityBilling", ((EditText) findViewById(R.id.cityBilling)).getText().toString());
                    fieldValues.put("stateBilling", ((Spinner) findViewById(R.id.stateBilling)).getSelectedItem().toString());
                    fieldValues.put("zipCodeBilling", ((EditText) findViewById(R.id.zipCodeBilling)).getText().toString());
                    fieldValues.put("phoneNumberBilling", ((EditText) findViewById(R.id.phoneNumberBilling)).getText().toString());
                }



                Log.i("FIELD LIST", "" + fieldList.get(0));
                Log.i("FIELD VALUE KEY SET", "" + fieldValues.keySet());
                Log.i("FIELD VALUE 0", "" + fieldValues.get("firstName"));

                Boolean emptyField = false;
                for (EditText field : fieldList) {
                    if (field.getText().toString().equals("")) {
                        emptyField = true;
                        break;
                    }
                }

                if (!emptyField) {
                    Log.i("START ACTIVITY", "No fields were empty so start the purchase");
                    allFields.addAll(fieldList);
                    allFields.add((EditText)findViewById(R.id.billingAddress2));
                    allFields.add((EditText)findViewById(R.id.streetAddress2));

                    new PurchaseItemTask().execute(fieldValues);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
                    builder.setTitle("Missing information");
                    builder.setMessage("Please fill out all of the required fields");
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private List<EditText> checkEditText(RelativeLayout layout, Boolean isChecked) {
        List<EditText> fieldList = new ArrayList<EditText>();
        List<EditText> fieldsToExclude = new ArrayList<EditText>();

        fieldsToExclude.add((EditText)findViewById(R.id.billingAddress2));
        fieldsToExclude.add((EditText)findViewById(R.id.streetAddress2));

        if (!isChecked) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof EditText) {
                    if (!fieldsToExclude.contains(layout.getChildAt(i))) {
                        fieldList.add((EditText) layout.getChildAt(i));
                    }
                }
            }
        }

        return fieldList;
    }

    private Map<Integer, String> getEditTextValues(RelativeLayout layout, Boolean isChecked) {
        Map<Integer, String> fieldValues = new HashMap<Integer, String>();

        if (!isChecked) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof EditText) {
                    fieldValues.put((layout.getChildAt(i)).getId(), ((EditText) layout.getChildAt(i)).getText().toString());
                }
            }
        }

        return fieldValues;
    }

    private class PurchaseItemTask extends AsyncTask<Map<String, String>, Void, Void> {
        URL purchaseUrl;
        HttpURLConnection apiConnection;
        InputStream sessionStream;
        String sessionId;
        String streamResult = null;
        JSONObject purchaseInitiateJSON;
        String checkoutSessionId = null;

        Intent itemIntent = getIntent();
        String itemId    = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_ID);

        @Override
        protected Void doInBackground(Map<String, String>... values) {
            //set the URL to be used in starting the purchase session
            String purchaseUrlString = getString(R.string.purchaseInitiateUrl);

            JSONObject userInfo = new JSONObject();
            JSONObject shippingAddress = new JSONObject();
            JSONArray  lineItemInputs = new JSONArray();

            try {
                JSONObject shippingInfo = new JSONObject();
                JSONObject lineItem = new JSONObject();

                userInfo.put("contactEmail", values[0].get("email"));
                userInfo.put("contactFirstName", values[0].get("firstName"));
                userInfo.put("contactLastName", values[0].get("lastName"));

                shippingInfo.put("recipient", values[0].get("firstName") + " " + values[0].get("lastName"));
                String phoneNumber = values[0].get("phoneNumber");
                phoneNumber.replace("(", "").replace(")", "").replace("-", " ");
                shippingInfo.put("phoneNumber", phoneNumber);
                shippingInfo.put("addressLine1", values[0].get("streetAddress"));
                if (!values[0].get("streetAddress2").equals("")) {
                    shippingInfo.put("addressLine2", values[0].get("streetAddress2"));
                }
                shippingInfo.put("stateOrProvince", values[0].get("state"));
                shippingInfo.put("postalCode", values[0].get("zipCode"));
                shippingInfo.put("country", "US");

                //shippingAddress.put("shippingAddress", shippingInfo);

                userInfo.put("shippingAddress", shippingInfo);

                lineItem.put("quantity", 1);
                lineItem.put("itemId", itemId);

                lineItemInputs.put(lineItem);

                userInfo.put("lineItemInputs", lineItemInputs);

                Log.i("JSON USER INFO", "" + userInfo);

            } catch(JSONException e) {
                Log.e("JSON INFORMATION", "Could not create JSON object");
            }

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
                OutputStreamWriter wr = new OutputStreamWriter(apiConnection.getOutputStream());
                wr.write(userInfo.toString());
                wr.flush();
            } catch(ProtocolException e) {
                e.printStackTrace();
                Log.e("REQUEST METHOD", "Failed to set request method");
            } catch(IOException e) {
                Log.e("OUTPUT STREAM WRITER", "Could not write to stream");
            }

            Log.e("API CONNECTION", "Connection: " + apiConnection);

            //get the response code
            int responseCode = 0;
            try {
                responseCode = apiConnection.getResponseCode();
                Log.i("RESPONSE CODE", "Response code: " + responseCode);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("RESPONSE CODE", "Response code: " + responseCode);
            }

            //Open the input stream
            if (responseCode == 200) {
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
                } catch (IOException e) {
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
                } catch (JSONException e) {
                    Log.e("JSON CREATION", "Failed to create a JSON object");
                }

                if (checkoutSessionId != null) {
                    Log.i("CHECKOUT ID", "ID: " + checkoutSessionId);
                } else {
                    Log.i("CHECKOUT ID", "ID is null");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
        }
    }
}
