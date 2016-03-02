package com.ldceconnect.ldcecommunity.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.PostViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {

    //private Map<String, Bitmap> bitmapDictionary;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String cuid;
    private LocalPostsArrayAdapter localPostsArrayAdapter;

    int color;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private ArrayList<Discussion> loadedThreads;
    public com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout mSwipeRefreshLayout;

    //private LocalPostsArrayAdapter localPostsArrayAdapter;
    @SuppressLint("ValidFragment")
    public PostFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {

        this.color = color;
        this.activity = activity;
        this.loadContext = context;

        LoadDataModel dm = LoadDataModel.getInstance();
        if(context == LoadDataModel.LoadContext.LOAD_GROUP_THREADS ||
                context == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS ) {
            this.loadedThreads = dm.loadedGroupThreads;
        }
        else if(context == LoadDataModel.LoadContext.LOAD_USER_THREADS ||
                context == LoadDataModel.LoadContext.LOAD_USER_DETAILS ) {
            this.loadedThreads = dm.loadedUserThreads;
        }
        else
            this.loadedThreads = dm.loadedThreads;
    }

    public PostFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        localPostsArrayAdapter = new LocalPostsArrayAdapter(getActivity(), R.layout.card_discussions_list_item,this.loadedThreads);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_home_post_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.frag_home_posts_list);

        listView.setAdapter(localPostsArrayAdapter);
        listView.setOnItemClickListener(new LocalPostListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocalPostData();
            }
        });

        handler = new Handler();



        return view;
    }

    @Override
    public void onResume() {
        //Log.d("PostFragment.onResume()", "Starting localPosts intent service ...");
        refreshLocalPostData();
        super.onResume();
    }

    private void refreshLocalPostData() {

    }




    class LocalPostListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Discussion d = loadedThreads.get(position);

            ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DISCUSSION);
            ApplicationModel.AppEventModel.setDiscussionTabClickId(position);

            LoadDataModel ldm = LoadDataModel.getInstance();

            LoadDataModel.loadThreadId = d.id;
            LoadDataModel.loadThreadTitle = d.title;

            if (LoadDataModel.isCurrentDataLoadFinished == true) {

                ldm.loadedThreadPosts.clear();
                ldm.loadedThreadForDetail.clear();
                new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_THREAD_DETAILS, null).execute();
            }

        }

    }



    public class LocalPostsArrayAdapter extends ArrayAdapter<Discussion> {

        private ArrayList<Discussion> localPosts;

        public LocalPostsArrayAdapter(Context context, int listViewId, ArrayList<Discussion> localPosts) {
            super(context, listViewId, localPosts);
            this.localPosts = localPosts;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final PostViewHolder localPostViewHolder;

            if ( convertView == null ) {

                // get the convertView (reused between getView() calls)
                LayoutInflater vi = LayoutInflater.from(getContext()); //getLayoutInflater(); //(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.card_discussions_list_item, null);

                // create our view holder, and set it as the tag of the convertView.
                localPostViewHolder = new PostViewHolder(getContext(), position, convertView);
                convertView.setTag(localPostViewHolder);

            } else {

                // get our view holder from the convertView
                localPostViewHolder = (PostViewHolder) convertView.getTag();

            }

            final Discussion localPost = localPosts.get(position);

            localPostViewHolder.build(getContext(), localPost);

            return convertView;



        }
    }

}
