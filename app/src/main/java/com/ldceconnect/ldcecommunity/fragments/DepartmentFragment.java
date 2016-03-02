package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.ldceconnect.ldcecommunity.LoadDataActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.RecyclerItemClickListener;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
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
public class DepartmentFragment extends Fragment {
    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;

    private AppCompatActivity activity;

    public DepartmentFragment() {
    }

    @SuppressLint("ValidFragment")
    public DepartmentFragment(AppCompatActivity activity,int color) {
        this.activity = activity;
        this.color = color;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_department_bg);
        frameLayout.setBackgroundColor(color);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_department_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.setHasFixedSize(true);

        handler = new Handler();

        LoadDataModel dm = LoadDataModel.getInstance();

        List<Integer> listImage = new ArrayList<Integer>();
        for (int i = 0; i < dm.loadedDepartments.size(); i++) {
            listImage.add(R.drawable.department_logo);
        }

        adapter = new SimpleRecyclerAdapter(activity,dm.loadedDepartments,ApplicationModel.CardLayout.CARD_DEPARTMENT, listImage);
        recyclerView.setAdapter(adapter);

        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position >= 0) {

                            Department d = (Department) adapter.dataModels.get(position);

                            LoadDataModel.loadDepartmentId = d.id;
                            LoadDataModel.loadDepartmentTitle = d.name;
                            LoadDataModel.loadDepartmentNumMembers = d.nummembers;

                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                LoadDataModel ldm = LoadDataModel.getInstance();
                                ldm.loadedDepartmentMembers.clear();
                                ldm.loadedDepartmentForDetail.clear();
                                new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS, null).execute();
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
                LoadDataModel dm = LoadDataModel.getInstance();

                //add progress item
                dm.loadedDepartments.add(null);
                adapter.notifyItemInserted(dm.loadedDepartments.size());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoadDataModel dm = LoadDataModel.getInstance();

                        Integer progressbarLoc = dm.loadedDepartments.size();
                        //add items one by one
                        /*Integer current_load = 0;
                        if(dm.loadedDepartments.size() >= dm.MAX_LOADED_DEPARTMENTS)
                            current_load = 0;
                        else if (dm.loadedDepartments.size() <= dm.MAX_LOADED_DEPARTMENTS - dm.NUM_LOAD_ITEMS_ON_PAGE)
                            current_load = dm.NUM_LOAD_ITEMS_ON_PAGE;
                        else if (dm.loadedDepartments.size() > dm.MAX_LOADED_DEPARTMENTS - dm.NUM_LOAD_ITEMS_ON_PAGE &&
                                dm.loadedDepartments.size() < dm.MAX_LOADED_DEPARTMENTS)
                            current_load = dm.MAX_LOADED_DEPARTMENTS - dm.loadedDepartments.size();*/

                        try {
                            Map<String, JSONObject> jsonObjectsArray;
                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_DEPARTMENTS, null).execute().get();
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
                        if (dm.loadedDepartments.size()> 0 && dm.loadedDepartments.size() >= progressbarLoc) {
                            dm.loadedDepartments.remove(progressbarLoc - 1);
                            adapter.notifyItemRemoved(progressbarLoc - 1);
                        }

                        // Notify Insert for newly added data
                        adapter.notifyItemInserted(dm.loadedDepartments.size());

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

        return view;
    }
}
