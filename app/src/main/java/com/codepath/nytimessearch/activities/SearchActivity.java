package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.nytimessearch.adapters.EndlessScrollListener;
import com.codepath.nytimessearch.data.Article;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.data.Filters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.nytimessearch.data.Article.fromJSONArray;


public class SearchActivity extends AppCompatActivity {
    public static final String API_KEY = "392973d3573947fbad541d56ea18a9c4";
    public static final int REQUEST_CODE = 101;
    public static final int BASE_GRIDVIEW_CAPACITY = 30; // we load a min of 50 items.
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<Article> articles;
    ArrayAdapter<Article> articleArrayAdapter;
    Filters searchFilters;  // Set to null when there are no filters.
    Handler handler = new Handler();


    private final class SearchEndlessScrollListener extends  EndlessScrollListener {
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            Log.d("DEBUG", "Calling load for page " + page + " and total = " + totalItemsCount);
            makeSearchApiCall(false, page);
            return true;
        }
    }

    SearchEndlessScrollListener endlessScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    private void setupViews() {
        etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);

        gvResults.setAdapter(articleArrayAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("article", articleArrayAdapter.getItem(position));
                startActivity(intent);
            }
        });
        endlessScrollListener = new SearchEndlessScrollListener();
        gvResults.setOnScrollListener(endlessScrollListener);
    }

    public void onArticleSearch(View view) {
        // When the user clicks the reset button, we clear the data in the list.
        makeSearchApiCall(true, 0);
    }

    private void loadMoreIntoGridviewIfNeeded(final int currentPage) {
        Log.d("DEBUG", "array adapter count:" + articleArrayAdapter.getCount());
        if (articleArrayAdapter.getCount() < BASE_GRIDVIEW_CAPACITY) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    makeSearchApiCall(false, currentPage + 1);
                }
            }, 2000L);
        }
    }

    private void makeSearchApiCall(boolean reset, int page) {
        final int targetPage = reset ? 0 : page;

        String query = etQuery.getText().toString();
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.add("api-key", API_KEY);
        params.add("page", String.valueOf(targetPage));
        if (!android.text.TextUtils.isEmpty(query)) {
            params.add("q", query);
        }

        if (searchFilters != null) {
            addFilters(params);
        }
        Log.d("DEBUG", "URL:" + url);
        Log.d("DEBUG", params.toString());
        if (reset) {
            articleArrayAdapter.clear();
            endlessScrollListener.reset();
        }
        httpClient.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArticle = null;
                try {
                    jsonArticle = response.getJSONObject("response").getJSONArray("docs");
                    ArrayList<Article> resultList = Article.fromJSONArray(jsonArticle);
                    articleArrayAdapter.addAll(resultList);
                    endlessScrollListener.onLoaded(targetPage, articleArrayAdapter.getCount());
                    if (resultList.size() >= 10) {
                        loadMoreIntoGridviewIfNeeded(targetPage);
                    }
                    if (resultList.size() == 0) {
                        endlessScrollListener.setLimitReached(targetPage, articleArrayAdapter.getCount());
                    }
                    Log.d("DEBUG", "resultList size = "  + resultList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Toast.makeText(SearchActivity.this, "Too fast.", Toast.LENGTH_LONG).show();
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
                endlessScrollListener.onLoadFailed(targetPage, articleArrayAdapter.getCount());
            }
        });
    }

    private void addFilters(RequestParams params) {
        params.add("sort", searchFilters.isSortOrderNewest() ?  "newest" : "oldest");
        String beginDate = String.format("%4d%02d%02d", searchFilters.getYear(), searchFilters.getMonth(),
                searchFilters.getDayOfMonth());
        params.add("begin_date", beginDate);
        ArrayList<String> newsDeskValues = searchFilters.getNewsDeskValues();
        if (newsDeskValues.size() > 0) {
            String newsDeskString = "";
            for(int i = 0; i < newsDeskValues.size(); i ++) {
                newsDeskString += newsDeskValues.get(i);
                if (i < newsDeskValues.size() - 1) {
                    newsDeskString += " ";  // Add a space.
                }
            }
            params.add("fq", "news_desk:(" + newsDeskString + ")");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miFilter) {
            Intent filtersIntent = new Intent(getApplicationContext(), FiltersActivity.class);
            if (searchFilters != null) {
                filtersIntent.putExtra("filters", searchFilters);
            }
            startActivityForResult(filtersIntent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                searchFilters =  data.getParcelableExtra("filters");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
