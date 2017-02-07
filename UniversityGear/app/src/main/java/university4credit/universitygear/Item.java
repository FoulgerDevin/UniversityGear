package university4credit.universitygear;

import android.text.Html;
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
    String subtitle;
    String shortDescription;
    String price;
    String condition;
    String returnsAccepted;
    String refundMethod;
    //URL imageURL;
    String imageURL;
    JSONObject jsonItems = null;
    JSONArray itemSummaries = null;
    List<Item> itemFeed = new ArrayList();
    Item sItem;

    /*
     * This constructor is used to create an array of items. It should be passed
     * a JSON object that contains the items.
     */
    Item(String item, Boolean isSingleItem) {
        String con = "Unspecified";
        String shortDesc = "None provided by user";
        String imageUrl = null;
        String rReturns = "Unspecified";
        String rAccepts = "Unspecified";
        String sTitle = "Unspecified";
        if (!isSingleItem) {
            try {
                if (item != null) {
                    jsonItems = new JSONObject(item);
                    Log.e("JSON ITEM SUMMARIES", "" + jsonItems.getString("itemSummaries"));
                    itemSummaries = new JSONArray(jsonItems.getString("itemSummaries"));
                }
            } catch(JSONException jsonE) {
                Log.e("JSONOBJECT", "Failed to create JSON item");
            }

            for (int i = 0; i < itemSummaries.length(); i++) {
                if (itemSummaries != null) {
                    try {
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
                                con, imageUrl, null, null, null, null);
                        itemFeed.add(newItem);

                    } catch (JSONException jsonE) {
                        Log.e("ITEMFEED ARRAY", "Failed to create item feed on iteration " + i);
                    }
                }
            }
        } else {
            if (item != null) {
                try {
                    JSONObject singleItem = new JSONObject(item);
                    JSONObject singleItemPrice = new JSONObject(singleItem.getString("price"));
                    JSONObject singleItemImage;
                    JSONObject singleItemReturns;
                    if (item.contains("returnTerms")) {
                        singleItemReturns = new JSONObject(singleItem.getString("returnTerms"));
                        if (item.contains("returnsAccepted")) { rAccepts = singleItemReturns.getString("returnsAccepted"); }
                        if (item.contains("refundMethod")) { rReturns = singleItemReturns.getString("refundMethod");}
                    }
                    if (item.contains("image")) {
                        singleItemImage = new JSONObject(singleItem.getString("image"));
                        imageUrl = singleItemImage.getString("imageUrl");
                    }
                    if (item.contains("condition")) {
                        con = singleItem.getString("condition");
                    }
                    if (item.contains("description")) {
                        shortDesc = singleItem.getString("description");
                    }
                    if (item.contains("generatedShortDescription")) {
                        shortDesc = singleItem.getString("generatedShortDescription");
                    }
                    if (item.contains("subtitle")) {
                        sTitle = singleItem.getString("subtitle");
                    }
                    sItem = new Item(singleItem.getString("itemId"),
                            singleItem.getString("title"), singleItemPrice.getString("value"),
                            con, imageUrl, sTitle, shortDesc, rAccepts, rReturns);
                } catch(JSONException jsonE) {
                    Log.e("SINGLE ITEM", "Failed to create a single item");
                }
            }
        }
    }

    /*
     * This constructor is used to build individual items and is passed in all
     * the necessary information.
     */
    Item(String id, String itemTitle, String givenPrice, String cond, String image, String sTitle, String sDescription, String rAccepted, String rMethod){
        itemID = id;
        title = itemTitle;
        price = "$" + givenPrice;
        condition = cond;
        if (image != null) {
            imageURL = image;//new URL(image);
        }
        subtitle = sTitle;
        shortDescription = sDescription;
        returnsAccepted = rAccepted;
        if (rMethod != null && rMethod.equals("MONEY_BACK")) {
            refundMethod = "Money back";
        } else {
            refundMethod = rMethod;
        }
    }
}
