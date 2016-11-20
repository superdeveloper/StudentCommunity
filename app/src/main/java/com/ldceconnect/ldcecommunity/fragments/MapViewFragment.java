package com.ldceconnect.ldcecommunity.fragments;

/**
 * Created by Nevil on 3/8/2016.
 */

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.customlayouts.SlidingDrawer;
import com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.ImageUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Nevil on 3/7/2016.
 */
public class MapViewFragment extends Fragment {

    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private GoogleMap mMap;
    private MapFragment fragment;
    private OnMapReadyCallback mapReadyCallback;

    private static final LatLng SAN_FRAN = new LatLng(23.033446, 72.546879);
    private EndlessRecyclerOnScrollListener scrollListener;
    private MarkerOptions ldMarkerOptions;

    public MapViewFragment() {
    }

    @SuppressLint("ValidFragment")
    public MapViewFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.color = color;
        this.activity = activity;
        this.loadContext = context;
        ldMarkerOptions = new MarkerOptions().position(SAN_FRAN).title("L D College of Engineering");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_mapview_bg);
        frameLayout.setBackgroundColor(color);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        if( mapReadyCallback == null) {
            mapReadyCallback = new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    // Add a marker in Sydney and move the camera
                    initializeMap();
                }
            };
        }

        if (mMap == null) {
            fragment.getMapAsync(mapReadyCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*FragmentManager fm = getChildFragmentManager();
        //fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }*/

        if( mapReadyCallback == null) {
            mapReadyCallback = new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    // Add a marker in Sydney and move the camera
                    initializeMap();
                }
            };
        }

        if (mMap == null) {
            fragment.getMapAsync(mapReadyCallback);
        }
    }

    private void initializeMap()
    {
        Marker ldMarker = mMap.addMarker(ldMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SAN_FRAN));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(ldMarker.getPosition()), 250, null);
        ldMarker.showInfoWindow();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        MapFragment f = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (f != null){
            getChildFragmentManager().beginTransaction().remove(f).commit();
        }

        //super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

            MapFragment f = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (f != null){
                getChildFragmentManager().beginTransaction().remove(f).commit();
            }

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (f != null){
            getChildFragmentManager().beginTransaction().remove(f).commit();
        }
    }*/
}
