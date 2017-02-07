package university4credit.universitygear;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_view);

        Intent singleItemIntent = getIntent();
        String itemId = singleItemIntent.getStringExtra(DisplaySearchResultsActivity.ITEM_ID);
        //Log.e("ITEM ID PASSED IN", "" + itemId);

        new DownloadItemTask().execute(itemId);
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

            connection.setRequestProperty("Authorization", "Bearer " + getString(R.string.appKey));
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

            item = new Item(result, true);

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

            TextView title = (TextView)findViewById(R.id.itemTitle);
            title.setText(item.sItem.title);

            TextView price = (TextView)findViewById(R.id.itemPrice);
            price.setText(item.sItem.price);

            TextView description = (TextView)findViewById(R.id.description);
            description.setText(Html.fromHtml(Html.fromHtml(item.sItem.shortDescription).toString()));
            description.setVisibility(description.GONE);

            TextView condition = (TextView)findViewById(R.id.conditionGiven);
            condition.setText(item.sItem.condition);

            //TextView quantity = (TextView)findViewById(R.id.);
            //quantity.setText(item.);

            TextView categoryLabel = (TextView)findViewById(R.id.category);
            categoryLabel.setVisibility(categoryLabel.GONE);
            TextView category = (TextView)findViewById(R.id.categoryGiven);
            category.setVisibility(category.GONE);
            //category.setText(item.);

            TextView returns = (TextView)findViewById(R.id.returnPolicyGiven);
            returns.setText(item.sItem.returnsAccepted + " " + item.sItem.refundMethod);
        }
    }
}
