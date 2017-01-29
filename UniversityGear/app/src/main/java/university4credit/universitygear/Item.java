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
    Float  price;
    Float  origPrice;
    String condition;

    Item(String item) {
        try {
            JSONObject jsonItems = new JSONObject(item);
            Log.e("JSONOBJECT", "" + jsonItems);
        } catch(JSONException jsonE) {
            Log.e("JSONOBJECT", "Failed to create JSON item");
        }

        ArrayList itemFeed = new ArrayList<>();
    }

    Item(String itemID, Float price, Float origPrice, String condition){
        
    }
}
