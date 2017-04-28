package university4credit.universitygear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Devin on 2/5/2017.
 */

public class DisplaySingleItemActivity extends AppCompatActivity {
    public final static String ITEM_ID = "university4credit.universitygear.ID";
    public final static String ITEM_TITLE = "university4credit.universitygear.TITLE";
    public final static String ITEM_CURRENCY = "university4credit.universitygear.CURRENCY";
    public final static String ITEM_PRICE = "university4credit.universitygear.PRICE";
    Item item;

    //This string is used for getting the description and passing to webview
    String webData;
    //Intent purchaseIntent = new Intent(DisplaySingleItemActivity.this, PurchaseActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_view);

        Intent singleItemIntent = getIntent();
        final String itemId = singleItemIntent.getStringExtra(DisplaySearchResultsActivity.ITEM_ID);
        //Log.e("ITEM ID PASSED IN", "" + itemId);

        Button descriptionButton = (Button) findViewById(R.id.description);
        descriptionButton.setVisibility(descriptionButton.GONE);

        new DownloadItemTask().execute(itemId);


        Button buyButton = (Button)findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent purchaseIntent = new Intent(DisplaySingleItemActivity.this, PurchaseActivity.class);
                purchaseIntent.putExtra(ITEM_ID, itemId);
                //purchaseIntent.putExtra(ITEM_TITLE, item.sItem.title);
                //purchaseIntent.putExtra(ITEM_PRICE, item.sItem.price);
                startActivity(purchaseIntent);
            }
        });

        descriptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent webViewIntent = new Intent(DisplaySingleItemActivity.this, DescriptionActivity.class);
                webViewIntent.putExtra("itemURL",webData);
                startActivity(webViewIntent);
            }
        });
    }

    private class DownloadItemTask extends AsyncTask<String, Void, Item> {
        private URL url;
        private HttpURLConnection connection;
        private InputStream inputStream;
        String result = "";

        @Override
        protected Item doInBackground(String... params) {
            //this is the url that i passed in
            String urlString = "https://api.sandbox.ebay.com/buy/browse/v1/item/"+params[0];
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                connection = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE);
            Log.i("OAuth Token", sharedPreferences.getString("oAuthToken", ""));
            connection.setRequestProperty("Authorization", "Bearer " + sharedPreferences.getString("oAuthToken",""));
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty("Content-Type","application/json");

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                Log.e("Connection code", "" + connection.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("e", "Params= "+params[0]);
            try {
                //get the item feed
                inputStream = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                result = total.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Log.e("Input Stream", " " + inputStream);
            //Log.e("Result", "" + result);

            Log.e("SINGLE ITEM", result);
            item = new Item(result, true);
            //Log.e("ITEM CATEGORY PATH", item.sItem.categoryPath);

            //Starting activity with search results passed in
            return item;
        }

        @Override
        protected void onPostExecute(Item item) {
            ImageView imageView = (ImageView)findViewById(R.id.itemImage);
            //Log.e("ITEM IMAGE",""+item.sItem.imageURL);
            if (item.sItem.imageURL != null) {
                Picasso.with(DisplaySingleItemActivity.this).load(item.sItem.imageURL)
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER);

            //Title
            TextView title = (TextView)findViewById(R.id.itemTitle);
            title.setText(item.sItem.title);

            //Price
            TextView price = (TextView)findViewById(R.id.itemPrice);
            price.setText(item.sItem.price);

            //Description
            Button description = (Button) findViewById(R.id.description);
            //description.setText(Html.fromHtml(item.sItem.shortDescription));
            webData = item.sItem.shortDescription;
            description.setVisibility(description.VISIBLE);

            //Condition
            TextView condition = (TextView)findViewById(R.id.conditionGiven);
            condition.setText(item.sItem.condition);

            //Quantity
            TextView quantity = (TextView)findViewById(R.id.quantityGiven);
            TextView quantity2 = (TextView)findViewById(R.id.quantityGiven2);
            if (item.sItem.availability.equals("Unspecified")) {
                quantity.setText(item.sItem.availability);
                quantity2.setVisibility(quantity2.GONE);
            } else {
                quantity.setText(item.sItem.availability);
                quantity2.setText(item.sItem.quantitySold);
            }

            //Category
            TextView category = (TextView)findViewById(R.id.categoryGiven);
            if (item.sItem.categoryPath.equals("Unspecified")) {
                TextView categoryLabel = (TextView)findViewById(R.id.category);
                categoryLabel.setVisibility(categoryLabel.GONE);
                category.setVisibility(category.GONE);
            } else {
                category.setText(item.sItem.categoryPath);
            }

            //Returns
            TextView returns = (TextView)findViewById(R.id.returnPolicyGiven);
            returns.setText(item.sItem.returnUnit);

            //Return payer
            TextView returnPayer = (TextView)findViewById(R.id.returnPayerGiven);
            returnPayer.setText(item.sItem.returnPayer);

            TextView guarantee = (TextView)findViewById(R.id.guaranteePolicyGiven);
            guarantee.setText(item.sItem.refundMethod);

            //Brand
            TextView brand = (TextView)findViewById(R.id.brandGiven);
            if (item.sItem.brand.equals("Unspecified")) {
                TextView brandLabel = (TextView)findViewById(R.id.brand);
                brand.setVisibility(brand.GONE);
                brandLabel.setVisibility(brandLabel.GONE);
            } else {
                brand.setText(item.sItem.brand);
            }

            //Pattern
            TextView pattern = (TextView)findViewById(R.id.patternGiven);
            if (item.sItem.pattern.equals("Unspecified")) {
                TextView patternLabel = (TextView)findViewById(R.id.pattern);
                patternLabel.setVisibility(patternLabel.GONE);
                pattern.setVisibility(pattern.GONE);
            } else {
                pattern.setText(item.sItem.pattern);
            }

            //Material
            TextView material = (TextView)findViewById(R.id.materialGiven);
            if (item.sItem.material.equals("Unspecified") || item.sItem.material.equals("Unbranded")) {
                TextView materialLabel = (TextView)findViewById(R.id.material);
                materialLabel.setVisibility(materialLabel.GONE);
                material.setVisibility(material.GONE);
            } else {
                material.setText(item.sItem.material);
            }

            //LAType
            TextView type = (TextView)findViewById(R.id.typeGiven);
            if (item.sItem.LAType.equals("Unspecified")) {
                TextView typeLabel = (TextView)findViewById(R.id.type);
                typeLabel.setVisibility(typeLabel.GONE);
                type.setVisibility(type.GONE);
            } else {
                type.setText(item.sItem.LAType);
            }

            //gender
            TextView gender = (TextView)findViewById(R.id.genderGiven);
            if (item.sItem.gender.equals("Unspecified")) {
                TextView genderLabel = (TextView)findViewById(R.id.gender);
                genderLabel.setVisibility(genderLabel.GONE);
                gender.setVisibility(gender.GONE);
            } else {
                gender.setText(item.sItem.gender);
            }

            Intent purchaseIntent = new Intent(DisplaySingleItemActivity.this, PurchaseActivity.class);
            //purchaseIntent.putExtra(ITEM_ID, item.sItem.itemID);
            purchaseIntent.putExtra(ITEM_PRICE, item.sItem.price);
            purchaseIntent.putExtra(ITEM_TITLE, item.sItem.title);
            /*Bundle bundle = new Bundle();
            bundle.putString("item_id", item.sItem.itemID);
            bundle.putString("item_title", item.sItem.title);
            bundle.putString("item_price", item.sItem.price);
            purchaseIntent.putExtras(bundle);*/
        }
    }
}
