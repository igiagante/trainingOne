package views;

/**
 * Created by igiagante on 3/7/15.
 */
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import connections.Connection;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotalItemCount = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        //Log.d("totalItemCount", String.valueOf(totalItemCount));
        //Log.d("previousTotal", String.valueOf(previousTotalItemCount));

        if (loading) {
            //Log.d("loading", "Must be true");
            if (totalItemCount > previousTotalItemCount) {
               // Log.d("total > previous", String.valueOf(totalItemCount > previousTotalItemCount));
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

        boolean a = (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold);

//        Log.d("loading", String.valueOf(loading));
//        Log.d("total - visible", String.valueOf((totalItemCount - visibleItemCount)));
//        Log.d("first + threshold", String.valueOf(firstVisibleItem + visibleThreshold));
//        Log.d("a", String.valueOf(a));

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            Log.d("onScrolled", "onLoadMore(current_page)");
            onLoadMore(current_page);
            loading = true;
            current_page++;
        }
    }

    public abstract void onLoadMore(int current_page);

    public void reset(int previousTotal, boolean loading) {
        this.previousTotalItemCount = previousTotal;
        this.loading = loading;
    }
}
