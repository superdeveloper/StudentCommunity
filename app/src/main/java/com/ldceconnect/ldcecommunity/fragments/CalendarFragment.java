package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.customlayouts.SlidingDrawer;
import com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by Nevil on 3/7/2016.
 */
public class CalendarFragment extends Fragment {

    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;

    private EndlessRecyclerOnScrollListener scrollListener;

    public CalendarFragment() {
    }

    @SuppressLint("ValidFragment")
    public CalendarFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.color = color;
        this.activity = activity;
        this.loadContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_calendar_bg);
        frameLayout.setBackgroundColor(color);

        handler = new Handler();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_calendar_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.setHasFixedSize(true);

        LoadDataModel ldm = LoadDataModel.getInstance();

        adapter = new SimpleRecyclerAdapter(activity,ldm.loadedThreads, ApplicationModel.CardLayout.CARD_CALENDARITEM);
        recyclerView.setAdapter(adapter);

        TextView calendar_date = (TextView) view.findViewById(R.id.calendar_day_title);
        calendar_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout rellayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.dialog_calendar, null);
                new MaterialDialog.Builder(activity)
                        .title("Select Date")
                        .customView(rellayout, true)
                        .positiveText("Ok")
                        .negativeText("Cancel").show();
            }
        });



        return view;
    }
}
