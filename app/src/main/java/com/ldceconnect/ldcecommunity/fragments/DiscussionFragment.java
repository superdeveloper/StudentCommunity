package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;


import com.ldceconnect.ldcecommunity.StarredThreadsActivity;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.customlayouts.SlidingDrawer;
import com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.DepartmentView;
import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.GroupViewActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.RecyclerItemClickListener;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.StudentProfileActivity;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.DummyDataModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.ParserUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class DiscussionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private ArrayList<Discussion> loadedThreads;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private EndlessRecyclerOnScrollListener scrollListener;

    public DiscussionFragment() {
    }

    @SuppressLint("ValidFragment")
    public DiscussionFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.color = color;
        this.activity = activity;
        this.loadContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discussion, container, false);

        LoadDataModel dm = LoadDataModel.getInstance();
        if(loadContext == LoadDataModel.LoadContext.LOAD_GROUP_THREADS ||
                loadContext == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS ) {
            this.loadedThreads = dm.loadedGroupThreads;
        }
        else if(loadContext == LoadDataModel.LoadContext.LOAD_USER_THREADS ||
                loadContext == LoadDataModel.LoadContext.LOAD_USER_DETAILS ) {
            this.loadedThreads = dm.loadedUserThreads;
        }
        else if( loadContext == LoadDataModel.LoadContext.LOAD_STARRED_THREADS || loadContext == LoadDataModel.LoadContext.LOAD_STARRED_THREADS_FOR_RECYCLER)
        {
            this.loadedThreads = dm.loadedStarredThreads;
        }
        else
            this.loadedThreads = dm.loadedThreads;


        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_discussion_bg);
        frameLayout.setBackgroundColor(color);

        handler = new Handler();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_discussion_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.setHasFixedSize(true);

        adapter = new SimpleRecyclerAdapter(activity,this.loadedThreads,ApplicationModel.CardLayout.CARD_DISCUSSION);
        recyclerView.setAdapter(adapter);


        scrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //add progress item
                loadedThreads.add(null);
                adapter.notifyItemInserted(loadedThreads.size());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Integer progressbarLoc = loadedThreads.size();

                        try {
                            Map<String, JSONObject> jsonObjectsArray;
                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                jsonObjectsArray = new LoadDataAsync(activity, loadContext, null).execute().get();
                            }
                        } catch (InterruptedException ex) {

                            ex.printStackTrace();
                        } catch (ExecutionException ex2) {

                            ex2.printStackTrace();
                        } catch (CancellationException ce) {
                            ce.printStackTrace();
                        }

                        //remove progress item
                        if (loadedThreads.size() > 0 && loadedThreads.size() >= progressbarLoc) {
                            loadedThreads.remove(progressbarLoc - 1);
                            adapter.notifyItemRemoved(progressbarLoc - 1);
                        }

                        // Notify Insert for newly added data
                        adapter.notifyItemInserted(loadedThreads.size());

                    }
                }, 2000);
                System.out.println("load");
            }

            @Override
            public void hideToolbar()
            {

            }

            @Override
            public void showToolbar()
            {

            }
        };

        recyclerView.setOnScrollListener(scrollListener);

        //Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_discussions);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if(activity.getClass() == StudentProfileActivity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedUserThreads.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_USER_THREADS, null).execute().get();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ExecutionException ex2) {
                    ex2.printStackTrace();
                } catch ( CancellationException ce)
                {
                    ce.printStackTrace();
                }
            }
        }
        else if( activity.getClass() == GroupViewActivity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedGroupThreads.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_GROUP_THREADS, null).execute().get();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ExecutionException ex2) {
                    ex2.printStackTrace();
                } catch ( CancellationException ce)
                {
                    ce.printStackTrace();
                }
            }

        }else if( activity.getClass() == ExploreCommunity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedThreads.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetLoading();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_THREADS, null).execute().get();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ExecutionException ex2) {
                    ex2.printStackTrace();
                } catch ( CancellationException ce)
                {
                    ce.printStackTrace();
                }
            }
        }
        else if( activity.getClass() == StarredThreadsActivity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedStarredThreads.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetLoading();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_STARRED_THREADS_FOR_RECYCLER, null).execute().get();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ExecutionException ex2) {
                    ex2.printStackTrace();
                } catch ( CancellationException ce)
                {
                    ce.printStackTrace();
                }
            }
        }
    }

}
