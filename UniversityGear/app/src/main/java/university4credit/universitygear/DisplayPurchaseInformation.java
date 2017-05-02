package university4credit.universitygear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Devin on 4/8/2017
 */

public class DisplayPurchaseInformation extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_information);

        Intent purchaseIntent = getIntent();
        String purchaseId = purchaseIntent.getStringExtra("purchaseId");
        String purchaseStatus = purchaseIntent.getStringExtra("purchaseStatus");

        Log.i("PURCHASE ID", purchaseId);
        //Log.i("PURCHASE STATUS", purchaseStatus);

        TextView orderNumTextView = (TextView)findViewById(R.id.orderNum);
        orderNumTextView.setText(purchaseId);
    }
}
