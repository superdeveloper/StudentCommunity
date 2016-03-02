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
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.RecyclerItemClickListener;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.DummyDataModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class StudentDetailFragment extends Fragment {
    int color;
    Intent intent;
    private AppCompatActivity activity;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    public SimpleRecyclerAdapter adapter;

    public StudentDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public StudentDetailFragment(AppCompatActivity activity,int color) {
        this.activity = activity;
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studentdetail, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_studentdetail_bg);
        frameLayout.setBackgroundColor(color);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_group_scrollableview_big);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        LoadDataModel ldm = LoadDataModel.getInstance();
        List<Integer> listImage = new ArrayList<Integer>();
        for (int i = 0; i < ldm.loadedUserGroups.size(); i++) {
            listImage.add(R.drawable.group_icon_medium);
        }

        adapter = new SimpleRecyclerAdapter(activity,ldm.loadedUserGroups,ApplicationModel.CardLayout.CARD_GROUP_BIG,listImage);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
