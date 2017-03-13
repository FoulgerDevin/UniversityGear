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
    public final static String ITEM_ID = "university4credit.universitygear.ID";
    private static final String TAG = "RecyclerViewExample";
    private String searchResults;

    private List<Item> feedsList;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Item feed;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);

        Intent intent = getIntent();
        searchResults = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

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
                    feed = new Item(searchResults, false);
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
                    //@Override
                    public void onItemClick(Item item) {
                        //Toast.makeText(DisplaySearchResultsActivity.this, item.title, Toast.LENGTH_LONG).show();
                        Intent singleItemIntent = new Intent(DisplaySearchResultsActivity.this, DisplaySingleItemActivity.class);
                        singleItemIntent.putExtra(ITEM_ID, item.itemID);
                        startActivity(singleItemIntent);
                    }
                });

                scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        Toast.makeText(DisplaySearchResultsActivity.this, "Scroll Listener is Working", Toast.LENGTH_SHORT).show();
                        String results = SearchActivity.callSearchAPI();
                    }
                };
                mRecyclerView.addOnScrollListener(scrollListener);
            } else {
                Toast.makeText(DisplaySearchResultsActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void loadNextDataFromApi() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        //Item feed2 = new Item(searchResults, false);
        //feedsList = feed2.itemFeed;
        String results = SearchActivity.callSearchAPI();
        //feed.loadMoreItems(results);
        //feedsList = feed.itemFeed;
    }

}