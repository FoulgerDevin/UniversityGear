package university4credit.universitygear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SearchActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "university4credit.universitygear.MESSAGE";
    Button mButton;
    EditText mEdit;
    TextView mText;
    String newtext;
    Context context;
    int mindistance;
    String tempstring;
    String join;
    String temp;
    String [] database = {"Oregon","State", "University", "Jersey", "Mug", "Nike", "Underarmour" };
    // Is the button now checked?


    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mButton = (Button)findViewById(R.id.button1);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                temp = checkedRadioButton.getText().toString();
            }
        });

//        mButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                mEdit   = (EditText)findViewById(R.id.editText1);
//                mText = (TextView)findViewById(R.id.textView1);
//                String [] ParsedWords = mEdit.getText().toString().split("\\s+") ;
//                for(int x = 0; x < ParsedWords.length;x++){
//                    mindistance = 3;
//                    for(int y = 0; y < database.length; y++){
//                        if(distance(ParsedWords[x],database[y])<2){
//                            tempstring = database[y];
//                            break;
//                        }
//                        else if(distance(ParsedWords[x],database[y])<mindistance){
//                            tempstring = database[y];
//                        }
//                        else tempstring = ParsedWords[x];
//                    }
//                    ParsedWords[x]=tempstring;
//                }
//
//                join = ParsedWords[0];
//                for(int x = 1; x < ParsedWords.length;x++){
//                    join += " "; join+=ParsedWords[x];
//                }
//
//                FileOutputStream stream = null;
//                try {
//                    stream = openFileOutput("history.txt", Context.MODE_APPEND);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    stream.write(join.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if( mEdit.getText().toString().length()!=0)
//                    mText.setText("You are looking for  "+ temp +" "+join);
//            }
//        });
//
    }

    public void search(View view) {
        Intent intent = new Intent(this, DisplaySearchResultsActivity.class);
        EditText searchText = (EditText) findViewById(R.id.editText1);
        String message = searchText.getText().toString();

        mEdit   = (EditText)findViewById(R.id.editText1);
        mText = (TextView)findViewById(R.id.textView1);
        String [] ParsedWords = mEdit.getText().toString().split("\\s+") ;
        for(int x = 0; x < ParsedWords.length;x++){
            mindistance = 3;
            for(int y = 0; y < database.length; y++){
                if(distance(ParsedWords[x],database[y])<2){
                    tempstring = database[y];
                    break;
                }
                else if(distance(ParsedWords[x],database[y])<mindistance){
                    tempstring = database[y];
                }
                else tempstring = ParsedWords[x];
            }
            ParsedWords[x]=tempstring;
        }

         join = ParsedWords[0];
         for(int x = 1; x < ParsedWords.length;x++){
            join += " "; join+=ParsedWords[x];
         }

         FileOutputStream stream = null;
         try {
            stream = openFileOutput("history.txt", Context.MODE_APPEND);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }

         try {
            stream.write(join.getBytes());
         } catch (IOException e) {
            e.printStackTrace();
         }
         if( mEdit.getText().toString().length()!=0) {
             //I believe we can enforce this so that a user can't hit submit until they've typed in something.
             mText.setText("You are looking for  "+ temp +" "+join);
         }

        intent.putExtra(EXTRA_MESSAGE, "Displaying search Results for " + join);
        //intent.putExtra(EXTRA_MESSAGE, mText);
        startActivity(intent);
    }

}