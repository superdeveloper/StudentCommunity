package com.ldceconnect.ldcecommunity.fragments;

/**
 * Created by Nevil on 3/7/2016.
 */

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
/**
 * Created by NEVIL on 11/26/2015.
 */
public class ActionButtonFragment extends Fragment {
    int color;
    SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;

    public ActionButtonFragment() {
    }

    @SuppressLint("ValidFragment")
    public ActionButtonFragment(int color) {
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actionbuttons, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_actionbuttons_bg);
        frameLayout.setBackgroundColor(color);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());

        return view;
    }
}

