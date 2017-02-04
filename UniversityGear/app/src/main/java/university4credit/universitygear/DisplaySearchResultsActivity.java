package university4credit.universitygear;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

public class DisplaySearchResultsActivity extends AppCompatActivity {
    private static final String TAG = "RecyclerViewExample";
    private String searchResults;

    private List<Item> feedsList;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);

        Intent intent = getIntent();
        searchResults = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        new DisplayResultsTask().execute();
    }

    public class DisplayResultsTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                    //parseResult(searchResults);
                    Item feed = new Item(searchResults);
                    feedsList = feed.itemFeed;
                    result = 1; // Successful
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerViewAdapter(DisplaySearchResultsActivity.this, feedsList);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Item item) {
                        Toast.makeText(DisplaySearchResultsActivity.this, item.title, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(DisplaySearchResultsActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String results) {
        try {
            JSONObject response = new JSONObject(results);
            JSONArray posts = response.optJSONArray("itemSummaries");
            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                JSONObject price = post.getJSONObject("price");
                JSONObject image = post.getJSONObject("image");

                FeedItem item = new FeedItem();
                item.setTitle(post.optString("title")); //Setting Image Title
                item.setThumbnail(image.optString("imageURL")); // Setting Item Image
                //Will Generalize the conditions to be either new or used.
                item.setPrice(price.optString("value") + " " + price.optString("currency") + "\n" + post.optString("condition"));

                //feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}