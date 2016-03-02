package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.DepartmentView;
import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
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
import com.ldceconnect.ldcecommunity.model.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class StudentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private ArrayList<User> loadedMembers;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    public StudentFragment() {
    }

    @SuppressLint("ValidFragment")
    public StudentFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.activity = activity;
        this.color = color;
        this.loadContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        LoadDataModel dm = LoadDataModel.getInstance();
        if(loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_MEMBERS ||
                loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS ) {
            this.loadedMembers = dm.loadedDepartmentMembers;
        }
        else if(loadContext == LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS ||
                loadContext == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS ) {
            this.loadedMembers = dm.loadedGroupMembers;
        }
        else if ( loadContext == LoadDataModel.LoadContext.LOAD_SEARCH_MEMBERS)
        {
            this.loadedMembers = dm.loadSearchSelectedUsers;
        }
        else
            this.loadedMembers = dm.loadedMembers;

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_student_bg);
        frameLayout.setBackgroundColor(color);

        handler = new Handler();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_student_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        List<Integer> listImage = new ArrayList<Integer>();
        for (int i = 0; i < this.loadedMembers.size(); i++) {
            listImage.add(R.drawable.avatar_small);
        }

        adapter = new SimpleRecyclerAdapter(activity,this.loadedMembers,ApplicationModel.CardLayout.CARD_STUDENT,listImage);
        recyclerView.setAdapter(adapter);

        /* add code for student card touch listener*/
        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position >= 0) {

                            if (loadContext != LoadDataModel.LoadContext.LOAD_SEARCH_MEMBERS) {

                                User g = (User) adapter.dataModels.get(position);
                                String userName = g.fname + " " + g.lname;

                                LoadDataModel.loadUserId = g.userid;
                                LoadDataModel.loadUserName = userName;

                                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                    LoadDataModel ldm = LoadDataModel.getInstance();
                                    ldm.loadedUserGroups.clear();
                                    ldm.loadedUserThreads.clear();
                                    ldm.loadedUserForDetail.clear();
                                    new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_USER_DETAILS, null).execute();
                                }

                            } else {
                                //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
        );*/

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                if (loadContext != LoadDataModel.LoadContext.LOAD_SEARCH_MEMBERS) {

                    //add progress item
                    loadedMembers.add(null);
                    adapter.notifyItemInserted(loadedMembers.size());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Integer progressbarLoc = loadedMembers.size();

                            //add items one by one
                            Integer current_load = 0;/*
                        if(dm.loadedMembers.size() >= dm.MAX_LOADED_MEMBERS)
                            current_load = 0;
                        else if (dm.loadedMembers.size() <= dm.MAX_LOADED_MEMBERS - dm.NUM_LOAD_ITEMS_ON_PAGE)
                            current_load = dm.NUM_LOAD_ITEMS_ON_PAGE;
                        else if (dm.loadedMembers.size() > dm.MAX_LOADED_MEMBERS - dm.NUM_LOAD_ITEMS_ON_PAGE &&
                                dm.loadedMembers.size() < dm.MAX_LOADED_MEMBERS)
                            current_load = dm.MAX_LOADED_MEMBERS - dm.loadedMembers.size();*/

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
                            if (loadedMembers.size() > 0 && loadedMembers.size() >= progressbarLoc) {
                                loadedMembers.remove(progressbarLoc - 1);
                                adapter.notifyItemRemoved(progressbarLoc - 1);
                            }

                            // Notify Insert for newly added data
                            adapter.notifyItemInserted(loadedMembers.size());

                        }
                    }, 2000);
                    System.out.println("load");
                }
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

        //Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_users);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if(activity.getClass() == GroupViewActivity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedGroupMembers.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS, null).execute().get();
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
        }else if(activity.getClass() == DepartmentView.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedDepartmentMembers.clear();
                adapter.notifyDataSetChanged();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_DEPARTMENT_MEMBERS, null).execute().get();
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
