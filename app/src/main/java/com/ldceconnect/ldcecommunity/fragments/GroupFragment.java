package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.DepartmentView;
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
import com.ldceconnect.ldcecommunity.model.DummyDataModel;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class GroupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private ArrayList<Group> loadedGroups;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView recyclerView;

    public GroupFragment() {
    }

    @SuppressLint("ValidFragment")
    public GroupFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.activity = activity;
        this.color = color;

        this.loadContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        LoadDataModel dm = LoadDataModel.getInstance();
        if(loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS ||
                loadContext == LoadDataModel.LoadContext.LOAD_USER_DETAILS ) {
            this.loadedGroups = dm.loadedAppUserGroups;
        }
        else
            this.loadedGroups = dm.loadedGroups;


        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_group_bg);
        frameLayout.setBackgroundColor(color);

        handler = new Handler();

        recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_group_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.setHasFixedSize(true);

        List<Integer> listImage = new ArrayList<Integer>();
        for (int i = 0; i < this.loadedGroups.size(); i++) {
            listImage.add(R.drawable.group_icon);
        }

        adapter = new SimpleRecyclerAdapter(activity,this.loadedGroups,ApplicationModel.CardLayout.CARD_GROUP,listImage);
        recyclerView.setAdapter(adapter);

        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position >= 0) {
                            ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_GROUP);
                            ApplicationModel.AppEventModel.setGroupTabClickId(position);
                            Group g = (Group) adapter.dataModels.get(position);
                            LoadDataModel.loadGroupId = g.id;
                            LoadDataModel.loadGroupName = g.name;
                            ApplicationModel.loadedGroupIndex = position;

                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                LoadDataModel ldm = LoadDataModel.getInstance();
                                ldm.loadedGroupMembers.clear();
                                ldm.loadedGroupThreads.clear();
                                ldm.loadedGroupForDetail.clear();
                                new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_GROUP_DETAILS, null).execute();
                            }

                        } else {
                            //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );*/

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                //add progress item
                loadedGroups.add(null);
                adapter.notifyItemInserted(loadedGroups.size());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Integer progressbarLoc = loadedGroups.size();

                        //add items one by one

                        /*Integer current_load = 0;
                        Integer previousLoaded = 0;
                        if (dm.loadedGroups.size() >= dm.MAX_LOADED_GROUPS)
                            current_load = 0;
                        else if (dm.loadedGroups.size() <= dm.MAX_LOADED_GROUPS - dm.NUM_LOAD_ITEMS_ON_PAGE)
                            current_load = dm.NUM_LOAD_ITEMS_ON_PAGE;
                        else if (dm.loadedGroups.size() > dm.MAX_LOADED_GROUPS - dm.NUM_LOAD_ITEMS_ON_PAGE &&
                                dm.loadedGroups.size() < dm.MAX_LOADED_GROUPS)
                            current_load = dm.MAX_LOADED_GROUPS - dm.loadedGroups.size();*/

                        try {
                            Map<String, JSONObject> jsonObjectsArray;
                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                jsonObjectsArray = new LoadDataAsync(activity, loadContext, null).execute().get();
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (ExecutionException ex2) {
                            ex2.printStackTrace();
                        } catch ( CancellationException ce)
                        {
                            ce.printStackTrace();
                        }

                        //remove progress item
                        if(loadedGroups.size() > 0 && loadedGroups.size() >= progressbarLoc ) {
                            loadedGroups.remove(progressbarLoc - 1);
                            adapter.notifyItemRemoved(progressbarLoc - 1);
                        }

                        // Notify Insert for newly added data
                        adapter.notifyItemInserted(loadedGroups.size());

                        /*for (int i = 0; i < current_load; i++) {
                            Group d = new Group();

                            adapter.notifyItemInserted(dm.loadedGroups.size());
                            //d.name = "Group " + (dm.loadedGroups.size() + 1);
                            //dm.loadedGroups.add(d);
                        }*/
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
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
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_groups);
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
                ldm.loadedAppUserGroups.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, loadContext, null).execute().get();
                        if(jsonObjectsArray == null)
                        {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
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
        else if( activity.getClass() == ExploreCommunity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedGroups.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, loadContext, null).execute().get();
                        if(jsonObjectsArray == null)
                        {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
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
