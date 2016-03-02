package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class GroupDetailFragment extends Fragment {
    int color;
    SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    public TextView about;
    public TextView category;
    public TextView admin;

    public GroupDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public GroupDetailFragment(int color) {
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groupdetail, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_groupdetail_bg);
        frameLayout.setBackgroundColor(color);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());

        LoadDataModel ldm = LoadDataModel.getInstance();
        if( ldm.loadedGroupForDetail.size() > 0) {
            Group g = ldm.loadedGroupForDetail.get(0);
            about = (TextView) view.findViewById(R.id.groupdetail_about);
            about.setText(g.descritpion);

            category = (TextView) view.findViewById(R.id.groupdetail_type_static);
            category.setText(g.category);

            admin = (TextView) view.findViewById(R.id.groupdetail_admin_name);
            admin.setText(g.adminname);
        }

        return view;
    }
}
