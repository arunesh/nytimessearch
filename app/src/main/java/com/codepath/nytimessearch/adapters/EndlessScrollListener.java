package com.codepath.nytimessearch.adapters;

import android.util.Log;
import android.widget.AbsListView;

import static android.R.attr.max;

/**
 * Created by arunesh on 10/24/16.
 */

public abstract  class EndlessScrollListener implements AbsListView.OnScrollListener {

    // Additional items to keep beyond the visible limit.
    private int visibleThreshold = 5;

    // Current page in the API call.
    protected int currentPage = 0;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    // True if we are still waiting for the last set of data to load.
    protected boolean loading = true;

    // Sets the starting page index
    private int startingPageIndex = 0;

    private int maxLimitPage = -1; // maximum value for "page"

    public EndlessScrollListener() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            if (maxLimitPage > 0 && (currentPage + 1)>= maxLimitPage) {
                Log.d("DEBUG", "Not loading, limit reached.");
                return;
            }
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int page, int totalItemsCount);

    // The implicit assumption when reset() is called is that we are loading more data.
    public void reset() {
        currentPage = startingPageIndex;
        loading = true;
        maxLimitPage = -1;
    }

    public void onLoaded(int page, int totalItemCount) {
        Log.d("DEBUG", "Loaded page: " + page + " total:" + totalItemCount);
        currentPage  = page;
        loading = false;
        previousTotalItemCount = totalItemCount;
    }

    public void onLoadFailed(int page, int totalItemCount) {
        currentPage = page - 1;
        loading = false;
        previousTotalItemCount = totalItemCount;
    }

    public void setLimitReached(int page, int totalItemCount) {
        loading = false;
        maxLimitPage = page;
        previousTotalItemCount = totalItemCount;
    }

    public int getVisibleThreshold() {
        return visibleThreshold;
    }
}
