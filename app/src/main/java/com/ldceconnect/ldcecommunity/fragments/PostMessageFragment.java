package com.ldceconnect.ldcecommunity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.DepartmentView;
import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.DrawerActivity;
import com.ldceconnect.ldcecommunity.EndlessRecyclerOnScrollListener;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.RecyclerItemClickListener;
import com.ldceconnect.ldcecommunity.SimpleRecyclerAdapter;
import com.ldceconnect.ldcecommunity.StudentProfileActivity;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.DummyDataModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class PostMessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    int color;
    public SimpleRecyclerAdapter adapter;
    Intent intent;
    LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private AppCompatActivity activity;
    private LoadDataModel.LoadContext loadContext;
    private ArrayList<Post> loadedPosts;
    private EditText editPost;
    private TextView textPost;
    private ImageView deleteButton;
    private ImageView editButton;
    private ImageView okButton;
    private ImageView cancelButton;
    private EditText parentPostMessage;
    private ImageButton parentSubmitButton;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public EndlessRecyclerOnScrollListener scrollListener;

    public RecyclerView recyclerView;

    public PostMessageFragment() {
    }

    @SuppressLint("ValidFragment")
    public PostMessageFragment(AppCompatActivity activity,int color,LoadDataModel.LoadContext context) {
        this.activity = activity;
        this.color = color;
        this.loadContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_message, container, false);

        LoadDataModel ldm = LoadDataModel.getInstance();
        if( loadContext == LoadDataModel.LoadContext.LOAD_THREAD_POSTS)
        {
            this.loadedPosts = ldm.loadedThreadPosts;
        }
        else
        {
            this.loadedPosts = ldm.loadedPosts;
        }

        RelativeLayout rl = (RelativeLayout)container.getParent();
        parentPostMessage = (EditText)((RelativeLayout)container.getParent()).getChildAt(1);
        parentSubmitButton = (ImageButton) ((RelativeLayout)container.getParent()).getChildAt(2);

        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_post_message_bg);
        frameLayout.setBackgroundColor(color);

        handler = new Handler();

        recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_post_message_scrollableview);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        adapter = new SimpleRecyclerAdapter(activity,this.loadedPosts,ApplicationModel.CardLayout.CARD_POST_MESSAGE);
        recyclerView.setAdapter(adapter);


        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position >= 0) {

                            LoadDataModel ldm = LoadDataModel.getInstance();
                            Post p = (Post) adapter.dataModels.get(position);

                            LoadDataModel.loadPostId = p.id;
                            LoadDataModel.loadPostText = p.text;

                            deleteButton = (ImageView) view.findViewById(R.id.post_message_listitem_deletebutton);
                            editButton = (ImageView) view.findViewById(R.id.post_message_listitem_editbutton);
                            editPost = (EditText) view.findViewById(R.id.post_message_listitem_edit_text);
                            textPost = (TextView) view.findViewById(R.id.post_message_listitem_text);
                            okButton = (ImageView) view.findViewById(R.id.post_message_listitem_okbutton);
                            cancelButton = (ImageView) view.findViewById(R.id.post_message_listitem_cancelbutton);
                            Boolean isOwner = ApplicationUtils.isUserOwnerOfPost(ldm.loadedThreadPosts, p.id);

                            if (isOwner &&
                                    (deleteButton.getVisibility() == View.GONE || deleteButton.getVisibility() == View.INVISIBLE) &&
                                    (editButton.getVisibility() == View.GONE || editButton.getVisibility() == View.INVISIBLE)) {
                                deleteButton.setVisibility(View.VISIBLE);
                                deleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(activity)
                                                .setMessage("Delete the post ?")
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {

                                                        ArrayList<Object> ids = new ArrayList<>();
                                                        ids.add(LoadDataModel.loadPostId);
                                                        new UploadDataAsync(activity, LoadDataModel.UploadContext.UPLOAD_DELETE_POST, ids).execute();

                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null).show();
                                    }
                                });
                                editButton.setVisibility(View.VISIBLE);
                                editButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editPost.setVisibility(View.VISIBLE);
                                        textPost.setVisibility(View.GONE);
                                        editButton.setVisibility(View.GONE);
                                        deleteButton.setVisibility(View.GONE);
                                        okButton.setVisibility(View.VISIBLE);
                                        cancelButton.setVisibility(View.VISIBLE);
                                        parentPostMessage.setVisibility(View.GONE);
                                        parentSubmitButton.setVisibility(View.GONE);

                                        editPost.setText(textPost.getText());
                                        editPost.requestFocus();
                                    }
                                });

                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editPost.setVisibility(View.GONE);
                                        textPost.setVisibility(View.VISIBLE);
                                        editButton.setVisibility(View.GONE);
                                        deleteButton.setVisibility(View.GONE);
                                        okButton.setVisibility(View.GONE);
                                        cancelButton.setVisibility(View.GONE);
                                        parentPostMessage.setVisibility(View.VISIBLE);
                                        parentSubmitButton.setVisibility(View.VISIBLE);

                                        ArrayList<Object> ids = new ArrayList<>();
                                        ids.add(LoadDataModel.loadPostId);
                                        ids.add(editPost.getText().toString());
                                        new UploadDataAsync(activity, LoadDataModel.UploadContext.UPLOAD_UPDATE_POST_TEXT, ids).execute();

                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editPost.setVisibility(View.GONE);
                                        textPost.setVisibility(View.VISIBLE);
                                        editButton.setVisibility(View.GONE);
                                        deleteButton.setVisibility(View.GONE);
                                        okButton.setVisibility(View.GONE);
                                        cancelButton.setVisibility(View.GONE);
                                        parentPostMessage.setVisibility(View.VISIBLE);
                                        parentSubmitButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else if (isOwner &&
                                    (okButton.getVisibility() == View.VISIBLE ||
                                            cancelButton.getVisibility() == View.VISIBLE)) {

                            } else {
                                deleteButton.setVisibility(View.GONE);
                                editButton.setVisibility(View.GONE);
                                okButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
        );*/

        scrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                LoadDataModel dm = LoadDataModel.getInstance();

                //add progress item
                loadedPosts.add(null);
                adapter.notifyItemInserted(loadedPosts.size());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Integer progressbarLoc = loadedPosts.size();

                        Integer current_load = 0;/*
                        if(dm.loadedPosts.size() >= dm.MAX_LOADED_POSTS)
                            current_load = 0;
                        else if (dm.loadedPosts.size() <= dm.MAX_LOADED_POSTS - dm.NUM_LOAD_ITEMS_ON_PAGE)
                            current_load = dm.NUM_LOAD_ITEMS_ON_PAGE;
                        else if (dm.loadedPosts.size() > dm.MAX_LOADED_POSTS - dm.NUM_LOAD_ITEMS_ON_PAGE &&
                                dm.loadedPosts.size() < dm.MAX_LOADED_POSTS)
                            current_load = dm.MAX_LOADED_POSTS - dm.loadedPosts.size();*/

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
                        if (loadedPosts.size() > 0 && loadedPosts.size() >= progressbarLoc) {
                            loadedPosts.remove(progressbarLoc - 1);
                            adapter.notifyItemRemoved(progressbarLoc - 1);
                        }

                        // Notify Insert for newly added data
                        adapter.notifyItemInserted(loadedPosts.size());
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
                System.out.println("load");
            }

            @Override
            public void hideToolbar()
            {
                //((DiscussionViewActivity)activity).doExitAnim();
                //isToolbarhidden = true;
            }

            @Override
            public void showToolbar()
            {
                //((DiscussionViewActivity)activity).doEnterAnim();
                //isToolbarhidden = false;
            }
        };

        recyclerView.setOnScrollListener(scrollListener);

        //Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_posts);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if(activity.getClass() == DiscussionViewActivity.class)
        {
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadedThreadPosts.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetLoading();

                try {
                    Map<String, JSONObject> jsonObjectsArray;
                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        jsonObjectsArray = new LoadDataAsync(activity, LoadDataModel.LoadContext.LOAD_THREAD_POSTS, null).execute().get();
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
