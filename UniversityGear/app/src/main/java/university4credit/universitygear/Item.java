package university4credit.universitygear;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin on 1/27/2017.
 */

public class Item {
    String itemID;
    String title;
    String price;
    String condition;
    //URL imageURL;
    String imageURL;
    JSONObject jsonItems = null;
    JSONArray itemSummaries = null;
    List<Item> itemFeed = new ArrayList();

    /*
     * This constructor is used to create an array of items. It should be passed
     * a JSON object that contains the items.
     */
    Item(String item) {
        try {
            if (item != null) {
                jsonItems = new JSONObject(item);
                //Log.e("JSON ITEM SUMMARIES", "" + jsonItems.getString("itemSummaries"));
                itemSummaries = new JSONArray(jsonItems.getString("itemSummaries"));
            }
        } catch(JSONException jsonE) {
            Log.e("JSONOBJECT", "Failed to create JSON item");
        }

        for (int i = 0; i < itemSummaries.length(); i++) {
            if (itemSummaries != null) {
                try {
                    String con = "Unspecified";
                    String imageUrl = null;

                    JSONObject singleItem = new JSONObject(itemSummaries.getString(i));
                    JSONObject singleItemPrice = new JSONObject(singleItem.getString("price"));
                    JSONObject singleItemImage;
                    if (itemSummaries.getString(i).contains("image")) {
                        singleItemImage = new JSONObject(singleItem.getString("image"));
                        imageUrl = singleItemImage.getString("imageUrl");
                    }
                    if (itemSummaries.getString(i).contains("condition")) {
                        con = singleItem.getString("condition");
                    }
                    Item newItem = new Item(singleItem.getString("itemId"),
                            singleItem.getString("title"), singleItemPrice.getString("value"),
                            con, imageUrl);
                    itemFeed.add(newItem);

                 } catch (JSONException jsonE) {
                    Log.e("ITEMFEED ARRAY", "Failed to create item feed on iteration " + i);
                }
            }
        }
    }

    /*
     * This constructor is used to build individual items and is passed in all
     * the necessary information.
     */
    Item(String id, String itemTitle, String givenPrice, String cond, String image){
        itemID = id;
        title = itemTitle;
        price = "$" + givenPrice;
        condition = cond;
        if (image != null) {
            imageURL = image;//new URL(image);
        }
    }
}
