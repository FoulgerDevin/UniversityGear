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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
 *
 */

public class PurchaseActivity extends AppCompatActivity{
    List<EditText> allFields = new ArrayList<>();

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
        String itemTitle = itemIntent.getStringExtra("itemTitle");
        String itemPrice = itemIntent.getStringExtra("itemPrice");

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
                List<EditText> fieldList = new ArrayList<>();
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
                fieldValues.put("expirationMonth", ((EditText)findViewById(R.id.expirationMonth)).getText().toString());
                fieldValues.put("firstNameCard", ((EditText)findViewById(R.id.firstNameCard)).getText().toString());
                fieldValues.put("lastNameCard", ((EditText)findViewById(R.id.lastNameCard)).getText().toString());
                fieldValues.put("cardType", ((Spinner)findViewById(R.id.cardType)).getSelectedItem().toString());
                fieldValues.put("cardCVV", ((EditText)findViewById(R.id.cardCVV)).getText().toString());
                fieldValues.put("expirationYear", ((EditText)findViewById(R.id.expirationYear)).getText().toString());
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
        List<EditText> fieldList = new ArrayList<>();
        List<EditText> fieldsToExclude = new ArrayList<>();

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
        Map<Integer, String> fieldValues = new HashMap<>();

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
        Map<String, String> orderInfo;
        String checkoutSessionId = null;

        Intent itemIntent = getIntent();
        String itemId     = itemIntent.getStringExtra(DisplaySingleItemActivity.ITEM_ID);


        @Override
        protected Void doInBackground(Map<String, String>... values) {

            JSONObject userInfo = new JSONObject();
            JSONArray  lineItemInputs = new JSONArray();
            JSONObject credit = new JSONObject();
            JSONObject billingAddress = new JSONObject();

            //This block of code creates the JSON objects that contain the user information and
            //the billing information
            try {
                JSONObject shippingInfo = new JSONObject();
                JSONObject lineItem = new JSONObject();

                userInfo.put("contactEmail", values[0].get("email"));
                userInfo.put("contactFirstName", values[0].get("firstName"));
                userInfo.put("contactLastName", values[0].get("lastName"));

                shippingInfo.put("recipient", values[0].get("firstName") + " " + values[0].get("lastName"));
                String phoneNumber = values[0].get("phoneNumber");
                phoneNumber.replace("(", "").replace(")", "").replace("-", " ");
                phoneNumber = phoneNumber.substring(0,3) + " " + phoneNumber.substring(3, phoneNumber.length());
                phoneNumber = phoneNumber.substring(0,7) + " " + phoneNumber.substring(7, phoneNumber.length());
                shippingInfo.put("phoneNumber", phoneNumber);
                shippingInfo.put("addressLine1", values[0].get("streetAddress"));
                if (!values[0].get("streetAddress2").equals("")) {
                    shippingInfo.put("addressLine2", values[0].get("streetAddress2"));
                }
                shippingInfo.put("city", values[0].get("city"));
                shippingInfo.put("stateOrProvince", values[0].get("state"));
                shippingInfo.put("postalCode", values[0].get("zipCode"));
                shippingInfo.put("country", "US");

                userInfo.put("shippingAddress", shippingInfo);

                lineItem.put("quantity", 1);
                lineItem.put("itemId", itemId);

                lineItemInputs.put(lineItem);

                userInfo.put("lineItemInputs", lineItemInputs);

                Log.i("JSON USER INFO", "" + userInfo);

                //BILLING INFORMATION
                JSONObject creditInfo = new JSONObject();
                creditInfo.put("accountHolderName", values[0].get("firstNameCard") + " " + values[0].get("lastNameCard"));
                creditInfo.put("cardNumber", values[0].get("cardNumber"));
                creditInfo.put("cvvNumber", values[0].get("cardCVV"));
                creditInfo.put("expireMonth", values[0].get("expirationMonth"));
                creditInfo.put("expireYear", values[0].get("expirationYear"));
                if (values[0].get("cardType").contains("American Express")) {
                    creditInfo.put("brand", "AMERICAN_EXPRESS");
                } else if (values[0].get("cardType").contains("Discover")) {
                    creditInfo.put("brand", "DISCOVER");
                } else if (values[0].get("cardType").contains("Mastercard")) {
                    creditInfo.put("brand", "MASTERCARD");
                } else if (values[0].get("cardType").contains("Visa")) {
                    creditInfo.put("brand", "VISA");
                } else {
                    creditInfo.put("brand", "invalid");
                }

                if (values[0].containsKey("streetAddressBilling")) {
                    billingAddress.put("firstName", values[0].get("firstNameCard"));
                    billingAddress.put("lastName", values[0].get("lastNameCard"));
                    billingAddress.put("addressLine1", values[0].get("streetAddressBilling"));
                    if (!values[0].get("streetAddress2Billing").contains("")) {
                        billingAddress.put("addressLine2", values[0].get("streetAddress2Billing"));
                    }
                    billingAddress.put("city", values[0].get("cityBilling"));
                    billingAddress.put("stateOrProvince", values[0].get("stateBilling"));
                    billingAddress.put("postalCode", values[0].get("zipCodeBilling"));
                    billingAddress.put("country", "US");
                } else {
                    billingAddress.put("firstName", values[0].get("firstNameCard"));
                    billingAddress.put("lastName", values[0].get("lastNameCard"));
                    billingAddress.put("addressLine1", values[0].get("streetAddress"));
                    if (!values[0].get("streetAddress2").contains("")) {
                        billingAddress.put("addressLine2", values[0].get("streetAddress2"));
                    }
                    billingAddress.put("city", values[0].get("city"));
                    billingAddress.put("stateOrProvince", values[0].get("state"));
                    billingAddress.put("postalCode", values[0].get("zipCode"));
                    billingAddress.put("country", "US");
                }

                creditInfo.put("billingAddress", billingAddress);
                credit.put("creditCard", creditInfo);

                Log.i("CREDIT INFO", "" + credit);

            } catch(JSONException e) {
                Log.e("JSON INFORMATION", "Could not create JSON object userInfo");
            }

            //Start the purchase process by initiating the checkout session and updating
            //the users credit info
            String checkoutSessionId = initiateOrder(userInfo);
            updatePayment(checkoutSessionId, credit);
            orderInfo = makePayment(checkoutSessionId);
            Log.i("ORDER INFO", "" + orderInfo);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if (orderInfo.containsKey("purchaseStatus")) {
                Log.i("ORDER API", "Screen will change to a new one");
                Intent thanks = new Intent(PurchaseActivity.this, DisplayPurchaseInformation.class);
                thanks.putExtra("purchaseId", orderInfo.get("purchaseId"));
                thanks.putExtra("purchaseStatue", orderInfo.get("purchaseStatus"));
                startActivity(thanks);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
                builder.setTitle("Purchase was not completed");
                builder.setMessage("Please make sure that all fields are entered correctly");
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

        /*
         * This method is used to initiate the guest checkout session
         * Inputs: User info JSON
         * Returns: checkoutSessionId
         */
        private String initiateOrder(JSONObject userInfo) {
            URL purchaseUrl = null;
            HttpURLConnection apiConnection = null;
            InputStream sessionStream;
            JSONObject purchaseInitiateJSON;
            String streamResult = null;

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
                apiConnection.setDoInput(true);
                apiConnection.setDoOutput(true);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("CONNECTION","Failed to connect");
            }

            //Set the correct header information

            SharedPreferences sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE);
            Log.i("OAuth Token INITIATE", sharedPreferences.getString("oAuthToken2", ""));
            apiConnection.setRequestProperty("Authorization", "Bearer " + sharedPreferences.getString("oAuthToken2",""));
            apiConnection.setRequestProperty("Accept", "application/json");
            apiConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //Set the correct request method
            try {
                apiConnection.setRequestMethod("POST");
                OutputStream os = apiConnection.getOutputStream();
                os.write(userInfo.toString().getBytes("UTF-8"));
                os.close();
            } catch(ProtocolException e) {
                e.printStackTrace();
                Log.e("REQUEST METHOD", "Failed to set request method to POST");
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
                    if (streamResult.contains("checkoutSessionId")) {
                        checkoutSessionId = purchaseInitiateJSON.getString("checkoutSessionId");
                    }
                } catch (JSONException e) {
                    Log.e("JSON CREATION", "Failed to create a JSON object");
                }

                if (checkoutSessionId != null) {
                    Log.i("CHECKOUT ID", "" + checkoutSessionId);
                } else {
                    Log.i("CHECKOUT ID", "is null");
                }

                Log.i("INITIATE","SUCCESS");
;            } else {
                Log.e("INITIATE","FAILED");
            }

            return checkoutSessionId;
        }

        private void updatePayment(String checkoutSessionId, JSONObject billingInfo) {
            //set the URL to be used when changing payment method
            String paymentUrlString = "https://api.sandbox.ebay.com/buy/order/v1/guest_checkout_session/" + checkoutSessionId + "/update_payment_info";
            URL paymentUrl = null;
            HttpURLConnection apiConnection = null;
            InputStream sessionStream;
            String streamResult = null;

            try {
                paymentUrl = new URL(paymentUrlString);
            } catch(MalformedURLException e) {
                e.printStackTrace();
                Log.e("PURCHASE URL", "URL Failed");
            }

            //Open the connection using the purchase url
            try {
                apiConnection = (HttpURLConnection)paymentUrl.openConnection();
                apiConnection.setDoInput(true);
                apiConnection.setDoOutput(true);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("CONNECTION","Failed to connect");
            }

            //Set the correct header information
            SharedPreferences sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE);
            Log.i("OAuth Token UPDATE CARD", sharedPreferences.getString("oAuthToken2", ""));
            apiConnection.setRequestProperty("Authorization", "Bearer " +  sharedPreferences.getString("oAuthToken2",""));
            //apiConnection.setRequestProperty("Authorization", "Bearer v^1.1#i^1#r^0#p^1#I^3#f^0#t^H4sIAAAAAAAAAOVXa2wUVRTuttuSphb8gVJ8xHUQEyQze2d29jVlV7YtTZvQbmUX0mJImcedMnR2Zpx7t+2aKLWpNYJExRiJAVOVxIBCBIxQCIJR5AeJQRIiECU08EONCWpMCFGCd6ZL2VZCK6yGxPkzueece+53vvOYuaC/ovKJoaahy9WeGaXD/aC/1ONhq0BlRfnCmWWlD5SXgAIDz3D/Y/3egbIfFiExo1vCMogs00DQ15fRDSS4whiVtQ3BFJGGBEPMQCRgWUglWpYKHAMEyzaxKZs65WtuiFFhRRFZRQ0CTlYCksoTqXHdZ9qMUVAMwlBQCbEqkDnoqBHKwmYDYdHAMYoDbJgGPA2iaRAWABACPMMH+ZWUbwW0kWYaxIQBVNxFK7h77QKot0YqIgRtTJxQ8eZEYyqZaG5Y0ppe5C/wFc/TkMIizqKJq3pTgb4Vop6Ftz4GudZCKivLECHKHx87YaJTIXEdzG3Ad5mWoRKFIKJwXDBMmJSLQmWjaWdEfGscjkRTaNU1FaCBNZybilHChrQWyji/aiUumht8zuuprKhrqgbtGLWkLtGRaGuj4ny9DRUN08sNrcfBSqfq2mmJD6ohnucitAQjajgkBfPnjDnLszzpoHrTII5IsMjXauI6SEDDidSEhGABNcQoaSTthIodQIV20esUstxKJ6djScziNYaTVpghPPjc5dQJGN+Nsa1JWQzHPUxWuAzFKNGyNIWarHRLMV89fShGrcHYEvz+3t5epjfAmHaXnwOA9be3LE3Ja2BGpFxbp9cde23qDbTmhiJDshNpAs5ZBEsfKVUCwOii4lyIByyf530irPhk6d8EBTH7JzZE0RoEBCU5xAIuEIgAKVyUWRPP16jfwQElMUdnRLsbYksXZUjLpM6yGWhrihAIqlwgokJaCUVVmo+qKi2RsUezKoQAQkmSo5H/UZ9Mt9LrdY0o06TSilTuRSr1JhNhqEy31G8aWko2Ldhm6pqc+69ic3p9evEFbKVNtHGuLpsj6xTUdfK6o3BlN5OdRZtbRUrkP2uZ2wtdE/HdFTTLR9kwx3KBaQ/rm8dF/mbuqrhkM8M4Q5ixRQubNkOgWTpEjA2RmbXJ3xeTdD7JabMbGmTCYdvUdWivYO+IBeT08d3Fg7Pf+8J+RHyIljZGCeHGb4okekfU6YL2OXZTGfmlbI7pykKECRAF2v/C590/8aoRL3EfdsBzCAx4RshtBYQBzS4ECyrKlnvL7qGQhiHJraFIZh+jiSqDtC6D/ErbkOmGOUvU7NIKT8uFVzrWFVxyhleBmvFrTmUZW1Vw5wEP3dCUs7PmVLNhwIMoORYE+JVg3g2tl73fO/t8E3+0sepZ/Rt+/8gJb+6+dNv2maB63MjjKS/xDnhKNqV2vMScV7fM8rzhPX366I9P7l2n1a7+/outaqRh96ujezYvXrXhTF167uCB6s7t87+t/uXrs4OdDz735sfMo1d6vnyk9dripgu75n2mVb22+6M/KzfPEdEp2nprZ1uq4d4rgWN/HL7IZrct+nzu6ktXz9W+xx3cNrKlCm8afef1Hnx1xu/x6iO182n1RM0edf/TP1/7TlST60HLiwfRhUPtJ0/1LDu+Vele33W5Zu/LAWHtIPPp6Ibnh3rA7N/UjuC+7V+dXz5YRtfuHMnwLQvO4rff/en4kQ8zJz94fOMnZuO2dnZBza5fO55hHw4mZ+zbeNp6f8eBVaOXQsm9vo4znZmNQzuOXzzWfu7wWBr/AhKYMg9+DgAA");
            apiConnection.setRequestProperty("Accept", "application/json");
            apiConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //Set the correct request method
            try {
                apiConnection.setRequestMethod("POST");
                OutputStream os = apiConnection.getOutputStream();
                os.write(billingInfo.toString().getBytes("UTF-8"));
                os.close();
            } catch(ProtocolException e) {
                e.printStackTrace();
                Log.e("REQUEST METHOD", "Failed to set request method");
            } catch(IOException e) {
                Log.e("OUTPUT STREAM WRITER", "Could not write to stream for billing info");
            }

            Log.e("API CONNECTION BILLING", "Connection: " + apiConnection);

            //get the response code
            int responseCode = 0;
            try {
                responseCode = apiConnection.getResponseCode();
                Log.i("RESPONSE CODE BILLING", "Response code: " + responseCode);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("RESPONSE CODE BILLING", "Response code: " + responseCode);
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

                Log.i("UPDATE CARD INFO", "SUCCESS");
            } else {
                Log.e("UPDATE CARD INFO", "FAILED");
            }
        }

        private Map<String, String> makePayment(String checkoutSessionId) {
            Map<String, String> orderInformation = new HashMap<>();
            URL purchaseUrl = null;
            HttpURLConnection apiConnection = null;
            InputStream purchaseStream = null;
            String streamResult = null;

            //Set the place purchase order URL
            String purchseOrderUrlString = "https://api.sandbox.ebay.com/buy/order/v1/guest_checkout_session/" + checkoutSessionId + "/place_order";

            try {
                purchaseUrl = new URL(purchseOrderUrlString);
            } catch (MalformedURLException e) {
                Log.e("PURCHASE URL", "Failed to create purchase URL");
            }

            try {
                apiConnection = (HttpURLConnection)purchaseUrl.openConnection();
                apiConnection.setDoInput(true);
                apiConnection.setDoOutput(true);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("CONNECTION","Failed to connect");
            }

            //Set the correct header information
            SharedPreferences sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE);
            Log.i("OAuth Token PURCHASE", sharedPreferences.getString("oAuthToken2", ""));
            apiConnection.setRequestProperty("Authorization", "Bearer " +  sharedPreferences.getString("oAuthToken2",""));
            //apiConnection.setRequestProperty("Authorization", "Bearer v^1.1#i^1#r^0#p^1#I^3#f^0#t^H4sIAAAAAAAAAOVXa2wUVRTuttuSphb8gVJ8xHUQEyQze2d29jVlV7YtTZvQbmUX0mJImcedMnR2Zpx7t+2aKLWpNYJExRiJAVOVxIBCBIxQCIJR5AeJQRIiECU08EONCWpMCFGCd6ZL2VZCK6yGxPkzueece+53vvOYuaC/ovKJoaahy9WeGaXD/aC/1ONhq0BlRfnCmWWlD5SXgAIDz3D/Y/3egbIfFiExo1vCMogs00DQ15fRDSS4whiVtQ3BFJGGBEPMQCRgWUglWpYKHAMEyzaxKZs65WtuiFFhRRFZRQ0CTlYCksoTqXHdZ9qMUVAMwlBQCbEqkDnoqBHKwmYDYdHAMYoDbJgGPA2iaRAWABACPMMH+ZWUbwW0kWYaxIQBVNxFK7h77QKot0YqIgRtTJxQ8eZEYyqZaG5Y0ppe5C/wFc/TkMIizqKJq3pTgb4Vop6Ftz4GudZCKivLECHKHx87YaJTIXEdzG3Ad5mWoRKFIKJwXDBMmJSLQmWjaWdEfGscjkRTaNU1FaCBNZybilHChrQWyji/aiUumht8zuuprKhrqgbtGLWkLtGRaGuj4ny9DRUN08sNrcfBSqfq2mmJD6ohnucitAQjajgkBfPnjDnLszzpoHrTII5IsMjXauI6SEDDidSEhGABNcQoaSTthIodQIV20esUstxKJ6djScziNYaTVpghPPjc5dQJGN+Nsa1JWQzHPUxWuAzFKNGyNIWarHRLMV89fShGrcHYEvz+3t5epjfAmHaXnwOA9be3LE3Ja2BGpFxbp9cde23qDbTmhiJDshNpAs5ZBEsfKVUCwOii4lyIByyf530irPhk6d8EBTH7JzZE0RoEBCU5xAIuEIgAKVyUWRPP16jfwQElMUdnRLsbYksXZUjLpM6yGWhrihAIqlwgokJaCUVVmo+qKi2RsUezKoQAQkmSo5H/UZ9Mt9LrdY0o06TSilTuRSr1JhNhqEy31G8aWko2Ldhm6pqc+69ic3p9evEFbKVNtHGuLpsj6xTUdfK6o3BlN5OdRZtbRUrkP2uZ2wtdE/HdFTTLR9kwx3KBaQ/rm8dF/mbuqrhkM8M4Q5ixRQubNkOgWTpEjA2RmbXJ3xeTdD7JabMbGmTCYdvUdWivYO+IBeT08d3Fg7Pf+8J+RHyIljZGCeHGb4okekfU6YL2OXZTGfmlbI7pykKECRAF2v/C590/8aoRL3EfdsBzCAx4RshtBYQBzS4ECyrKlnvL7qGQhiHJraFIZh+jiSqDtC6D/ErbkOmGOUvU7NIKT8uFVzrWFVxyhleBmvFrTmUZW1Vw5wEP3dCUs7PmVLNhwIMoORYE+JVg3g2tl73fO/t8E3+0sepZ/Rt+/8gJb+6+dNv2maB63MjjKS/xDnhKNqV2vMScV7fM8rzhPX366I9P7l2n1a7+/outaqRh96ujezYvXrXhTF167uCB6s7t87+t/uXrs4OdDz735sfMo1d6vnyk9dripgu75n2mVb22+6M/KzfPEdEp2nprZ1uq4d4rgWN/HL7IZrct+nzu6ktXz9W+xx3cNrKlCm8afef1Hnx1xu/x6iO182n1RM0edf/TP1/7TlST60HLiwfRhUPtJ0/1LDu+Vele33W5Zu/LAWHtIPPp6Ibnh3rA7N/UjuC+7V+dXz5YRtfuHMnwLQvO4rff/en4kQ8zJz94fOMnZuO2dnZBza5fO55hHw4mZ+zbeNp6f8eBVaOXQsm9vo4znZmNQzuOXzzWfu7wWBr/AhKYMg9+DgAA");
            apiConnection.setRequestProperty("Accept", "application/json");
            apiConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            //Set the correct request method
            try {
                apiConnection.setRequestMethod("POST");
            } catch(ProtocolException e) {
                e.printStackTrace();
                Log.e("REQUEST METHOD", "Failed to set request method");
            }

            //get the response code
            int responseCode = 0;
            try {
                responseCode = apiConnection.getResponseCode();
                Log.i("RESPONSE CODE PURCHASE", "Response code: " + responseCode);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("RESPONSE CODE PURCHASE", "Response code: " + responseCode);
            }

            //Open the input stream
            if (responseCode == 200) {
                try {
                    purchaseStream = apiConnection.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(purchaseStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    streamResult = total.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SESSION STREAM", "Could not get a session stream string");
                }

                //turn the string result into a JSON object
                JSONObject orderJSON;
                try {
                    orderJSON = new JSONObject(streamResult);
                    if (streamResult.contains("purchaseOrderId")) {
                        orderInformation.put("purchaseId", orderJSON.getString("purchaseOrderId"));
                    }
                    if (streamResult.contains("purchaseOrderPaymentStatus")) {
                        orderInformation.put("purchaseStatus", orderJSON.getString("purchaseOrderPaymentStatus"));
                    }
                } catch (JSONException e) {
                    Log.e("ORDER JSON", "Failed to create the order json from the stream result");
                }

                Log.i("MAKE PURCHASE", "SUCCESS");
            } else {
                Log.e("MAKE PURCHASE", "FAILED");
            }

            Log.e("API CONNECTION PURCHASE", "Connection: " + apiConnection);

            return orderInformation;
        }
    }
}
