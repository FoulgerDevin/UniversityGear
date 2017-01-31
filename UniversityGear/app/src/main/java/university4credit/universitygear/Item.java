package university4credit.universitygear;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Devin on 1/27/2017.
 */

public class Item {
    String itemID;
    String price;
    String origPrice;
    String condition;
    JSONObject jsonItems = null;
    JSONObject itemSummaries = null;

    Item(String item) {
        try {
            if (item != null) {
                jsonItems = new JSONObject(item);
                Log.e("JSONOBJECT", "" + jsonItems);
            }
        } catch(JSONException jsonE) {
            Log.e("JSONOBJECT", "Failed to create JSON item");
        }

        try {
            itemSummaries = new JSONObject(jsonItems.getString("itemSummaries"));
            Log.e("Item Summaries object", "" + itemSummaries);
        } catch(JSONException jsonE) {
            Log.e("ITEM SUMMARIES", "Could not create a JSON object from item summaries");
        }

        ArrayList itemFeed = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                Item newItem = new Item(itemSummaries.getString("itemId"), "1", "2", "used");
                Log.e("ITEM LIST", "" + newItem);
            } catch(JSONException jsonE) {

            }
        }
    }

    Item(String id, String givenPrice, String oPrice, String cond){
        itemID = id;
        price = givenPrice;
        origPrice = oPrice;
        condition = cond;
    }
}
