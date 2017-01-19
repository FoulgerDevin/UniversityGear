package university4credit.universitygear;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchActivity extends AppCompatActivity {
    Button mButton;
    EditText mEdit;
    TextView mText;
    String newtext;
    Context context;
    int mindistance;
    String tempstring;
    String[] database = {"Oregon", "State", "University", "Jersey", "Mug", "Nike", "Underarmour"};

    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
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
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.button1);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEdit = (EditText) findViewById(R.id.editText1);
                mText = (TextView) findViewById(R.id.textView1);
                String[] ParsedWords = mEdit.getText().toString().split("\\s+");
                for (int x = 0; x < ParsedWords.length; x++) {
                    mindistance = 5;
                    for (int y = 0; y < database.length; y++) {
                        if (distance(ParsedWords[x], database[y]) < 2) {
                            tempstring = database[y];
                            break;
                        } else if (distance(ParsedWords[x], database[y]) < mindistance) {
                            tempstring = database[y];
                        }
                    }
                    ParsedWords[x] = tempstring;
                }
                String join;
                join = ParsedWords[0];
                for (int x = 1; x < ParsedWords.length; x++) {
                    join += " ";
                    join += ParsedWords[x];
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
                mText.setText("You are looking for  " + join);
            }
        });
    }
}