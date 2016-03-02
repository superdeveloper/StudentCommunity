package com.ldceconnect.ldcecommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.UserCursorAdapter;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.SearchAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
//import com.ldceconnect.ldcecommunity.provider.ScheduleContract;


public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {

    //private static final String TAG = makeLogTag("SearchActivity");
    private static final String SCREEN_LABEL = "Search";
    private static final String ARG_QUERY = "query";
    private static final int MIN_QUERY_LENGTH = 3;

    private SearchView mSearchView;
    private String mQuery = "";
    private ListView mSearchResults;
    private ListView mSearchResultsAddButtons;
    private UserCursorAdapter mUserResultsAdapter;
    private SimpleCursorAdapter mThreadResultsAdapter;
    private SimpleCursorAdapter mPostResultsAdapter;
    private SimpleCursorAdapter mGroupResultsAdapter;
    private SimpleCursorAdapter mDepartmentResultsAdapter;
    //private SimpleCursorAdapter mResultsAdapter;
    private SimpleCursorAdapter mResultsAddButtonAdapter;

    private CheckBox resultsCheckbox;

    public MatrixCursor mUsersCursor = new MatrixCursor(ApplicationModel.SearchModel.user_columns);


    public MatrixCursor mThreadsCursor = new MatrixCursor(ApplicationModel.SearchModel.thread_columns);


    public MatrixCursor mGroupsCursor = new MatrixCursor(ApplicationModel.SearchModel.group_columns);

    public MatrixCursor mDepartmentsCursor = new MatrixCursor(ApplicationModel.SearchModel.department_columns);


    public MatrixCursor mPostsCursor = new MatrixCursor(ApplicationModel.SearchModel.post_columns);
    String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        caller = getIntent().getStringExtra("caller");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable up = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_up));
        DrawableCompat.setTint(up, getResources().getColor(R.color.app_body_text_2));

        mSearchView = (SearchView) findViewById(R.id.search_view);
        setupSearchView();
        mSearchResults = (ListView) findViewById(R.id.search_results);

        ApplicationModel.SearchContext searchContext = ApplicationModel.SearchModel.getSearchContext();

        if(searchContext == ApplicationModel.SearchContext.SEARCH_USER) {
            mUserResultsAdapter = new UserCursorAdapter(this,
                    R.layout.list_item_search_result_add_button, mUsersCursor,
                    new String[]{"name"},
                    new int[]{R.id.search_result_with_add_button});
            mSearchResults.setAdapter(mUserResultsAdapter);
            mSearchResults.setOnItemClickListener(this);
        }else if(searchContext == ApplicationModel.SearchContext.SEARCH_THREAD)
        {
            mThreadResultsAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_item_search_result, mThreadsCursor,
                    new String[] {"threadtitle"},
                    new int[]{R.id.search_result_snippet});
            mSearchResults.setAdapter(mThreadResultsAdapter);
            mSearchResults.setOnItemClickListener(this);

        }else if(searchContext == ApplicationModel.SearchContext.SEARCH_GROUP)
        {
            mGroupResultsAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_item_search_result, mGroupsCursor,
                    new String[] {"grouptitle"},
                    new int[]{R.id.search_result_snippet});
            mSearchResults.setAdapter(mGroupResultsAdapter);
            mSearchResults.setOnItemClickListener(this);

        }else if(searchContext == ApplicationModel.SearchContext.SEARCH_POST)
        {
            mPostResultsAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_item_search_result, mPostsCursor,
                    new String[] {"text"},
                    new int[]{R.id.search_result_snippet});
            mSearchResults.setAdapter(mPostResultsAdapter);
            mSearchResults.setOnItemClickListener(this);

        }else if(searchContext == ApplicationModel.SearchContext.SEARCH_DEPARTMENT)
        {
            mDepartmentResultsAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_item_search_result, mDepartmentsCursor,
                    new String[] {"deptname"},
                    new int[]{R.id.search_result_snippet});
            mSearchResults.setAdapter(mDepartmentResultsAdapter);
            mSearchResults.setOnItemClickListener(this);

        }

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQuery = query;

        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doEnterAnim();
        }

        overridePendingTransition(0, 0);

        TextView done = (TextView)findViewById(R.id.search_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(caller != null && caller.equals(GroupViewActivity.class.getName()))
                {
                    UserModel um = UserModel.getInstance();
                    ArrayList<Object> data = new ArrayList<>();
                    ArrayList<User> members = new ArrayList<User>();
                    LoadDataModel ldm = LoadDataModel.getInstance();

                    for ( int i = 0 ; i < ldm.loadSearchSelectedUsers.size(); i++)
                    {
                        User u1 = ldm.loadSearchSelectedUsers.get(i);
                        int id = ApplicationUtils.findUserInArray(ldm.loadedGroupMembers,u1.userid);
                        if( id < 0 )
                        {
                            members.add(u1);
                        }
                    }

                    data.add(LoadDataModel.loadGroupId);
                    data.add(members);

                    new UploadDataAsync(SearchActivity.this, LoadDataModel.UploadContext.UPLOAD_CREATE_GROUPMEMBERSHIP, data).execute();
                }
                else
                    dismiss(null);

            }
        });

        LoadDataModel ldm = LoadDataModel.getInstance();

        if(searchContext == ApplicationModel.SearchContext.SEARCH_USER && ldm.loadSearchSelectedUsers.size() > 0) {
            mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
            mUserResultsAdapter.swapCursor(mUsersCursor);
            mUserResultsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * As we only ever want one instance of this screen, we set a launchMode of singleTop. This
     * means that instead of re-creating this Activity, a new intent is delivered via this callback.
     * This prevents multiple instances of the search dialog 'stacking up' e.g. if you perform a
     * voice search.
     *
     * See: http://developer.android.com/guide/topics/manifest/activity-element.html#lmode
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                searchFor(query);
                mSearchView.setQuery(query, false);
            }
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        // Set the query hint.
        ApplicationModel.SearchContext searchContext = ApplicationModel.SearchModel.getSearchContext();
        if(searchContext == ApplicationModel.SearchContext.SEARCH_USER)
        {
            mSearchView.setQueryHint(getString(R.string.search_hint_users));
        }
        else if (searchContext == ApplicationModel.SearchContext.SEARCH_POST)
        {
            mSearchView.setQueryHint(getString(R.string.search_hint_posts));
        }
        else if ( searchContext == ApplicationModel.SearchContext.SEARCH_GROUP)
        {
            mSearchView.setQueryHint(getString(R.string.search_hint_groups));
        }
        else if ( searchContext == ApplicationModel.SearchContext.SEARCH_DEPARTMENT)
        {
            mSearchView.setQueryHint(getString(R.string.search_hint_department));
        }
        else if(searchContext == ApplicationModel.SearchContext.SEARCH_USER_ADD)
        {
            mSearchView.setQueryHint(getString(R.string.search_hint_add_users));
        }
        else
        {
            mSearchView.setQueryHint(getString(R.string.search_label_ldce));
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Nevil - do this
                searchFor(s);
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onBackPressed();
                return false;
            }
        });
        if (!TextUtils.isEmpty(mQuery)) {
            mSearchView.setQuery(mQuery, false);
        }
    }

    @Override
    public void onBackPressed() {
        LoadDataModel ldm = LoadDataModel.getInstance();
        ldm.loadSearchSelectedUsers.clear();
        ldm.loadSearchUsers.clear();
        dismiss(null);
    }

    public void dismiss(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    public void addSelectedUsersToResult()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        // Add Previously Selected users
        for ( int i = 0 ; i < dm.loadSearchSelectedUsers.size(); i++)
        {
            User u = dm.loadSearchSelectedUsers.get(i);
            if( u != null)
            {
                mUsersCursor.addRow(new Object[]{u.userid, u.userid, u.fname + " " + u.lname, u.email});
            }
        }
        mUserResultsAdapter.swapCursor(mUsersCursor);
        mUserResultsAdapter.notifyDataSetChanged();
    }

    public void addSearchedUsersToResult()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        // Add Previously Selected users
        for ( int i = 0 ; i < dm.loadSearchUsers.size(); i++)
        {
            User u = dm.loadSearchUsers.get(i);
            if( u != null)
            {
                if(!ApplicationUtils.isUserAlreadySelected(u.userid))
                    mUsersCursor.addRow(new Object[]{u.userid, u.userid, u.fname + " " + u.lname, u.email});
            }
        }
        mUserResultsAdapter.swapCursor(mUsersCursor);
        mUserResultsAdapter.notifyDataSetChanged();
    }

    /**
     * On Lollipop+ perform a circular reveal animation (an expanding circular mask) when showing
     * the search panel.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.
        View scrim = findViewById(R.id.scrim);
        scrim.animate()
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        // Next perform the circular reveal on the search panel
        final View searchPanel = findViewById(R.id.search_panel);
        if (searchPanel != null) {
            // We use a view tree observer to set this up once the view is measured & laid out
            searchPanel.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                            // As the height will change once the initial suggestions are delivered by the
                            // loader, we can't use the search panels height to calculate the final radius
                            // so we fall back to it's parent to be safe
                            int revealRadius = ((ViewGroup) searchPanel.getParent()).getHeight();
                            // Center the animation on the top right of the panel i.e. near to the
                            // search button which launched this screen.
                            Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
                                    searchPanel.getRight(), searchPanel.getTop(), 0f, revealRadius);
                            show.setDuration(250L);
                            show.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            show.start();
                            return false;
                        }
                    });
        }
    }

    /**
     * On Lollipop+ perform a circular animation (a contracting circular mask) when hiding the
     * search panel.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitAnim() {
        final View searchPanel = findViewById(R.id.search_panel);
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                + Math.pow(searchPanel.getHeight(), 2));
        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(200L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                android.R.interpolator.fast_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchPanel.setVisibility(View.INVISIBLE);
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        findViewById(R.id.scrim).animate()
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                .start();
    }

    private void searchFor(String query) {
        // ANALYTICS EVENT: Start a search on the Search activity
        // Contains: Nothing (Event params are constant:  Search query not included)
        Bundle args = new Bundle(1);
        if (query == null) {
            query = "";
        }
        args.putString(ARG_QUERY, query);

        if(!query.isEmpty())
        {
            LoadDataModel.searchQuery = query;

            try {
                Map<String, JSONObject> jsonObjectsArray;
                LoadDataModel ldm = LoadDataModel.getInstance();

                if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_USER
                        && query.length() >= MIN_QUERY_LENGTH) {
                    if (LoadDataModel.isCurrentSearchFinished == true) {
                        Boolean bool = new SearchAsync(this, ApplicationModel.SearchContext.SEARCH_USER, mUsersCursor).execute().get();
                        if (bool == true) {
                            ApplicationUtils.removeAlreadySelectedUsersFromSearchResult();
                            mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
                            mUserResultsAdapter.swapCursor(mUsersCursor);
                            mUserResultsAdapter.notifyDataSetChanged();

                            /*int items = mSearchResults.getCount();
                            for ( int i = 0; i < items; i++)
                            {
                                View v = mSearchResults.getChildAt(i);
                                if( v != null) {
                                    CheckBox cb = (CheckBox)v.findViewById(R.id.search_result_add_check);
                                    if (cb != null) {
                                        cb.setChecked(false);
                                    }
                                }
                            }*/

                            if( ldm.loadSearchUsers.size() > 0)
                            {
                                addSearchedUsersToResult();

                                /*int items1 = mSearchResults.getCount();
                                for ( int i = items; i < items1; i++)
                                {
                                    View v = mSearchResults.getChildAt(i);
                                    if( v != null) {
                                        CheckBox cb = (CheckBox)v.findViewById(R.id.search_result_add_check);
                                        if (cb != null) {
                                            cb.setChecked(true);
                                        }
                                    }
                                }*/
                            }

                            mQuery = query;

                        }
                    }
                }else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_THREAD)
                {
                    if (LoadDataModel.isCurrentSearchFinished == true && query.length() >= MIN_QUERY_LENGTH) {
                        Boolean bool = new SearchAsync(this, ApplicationModel.SearchContext.SEARCH_THREAD, mThreadsCursor).execute().get();
                        if (bool == true) {

                            mThreadsCursor = ApplicationUtils.getCursorFromThreadArray(ldm.loadSearchThreads);
                            mThreadResultsAdapter.swapCursor(mThreadsCursor);
                            mThreadResultsAdapter.notifyDataSetChanged();
                            mQuery = query;
                        }
                    }

                }else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_DEPARTMENT)
                {
                    if (LoadDataModel.isCurrentSearchFinished == true && query.length() >= MIN_QUERY_LENGTH) {
                        Boolean bool = new SearchAsync(this, ApplicationModel.SearchContext.SEARCH_DEPARTMENT, mDepartmentsCursor).execute().get();
                        if (bool == true) {
                            mDepartmentsCursor = ApplicationUtils.getCursorFromDepartmentArray(ldm.loadSearchDepartments);
                            mDepartmentResultsAdapter.swapCursor(mDepartmentsCursor);
                            mDepartmentResultsAdapter.notifyDataSetChanged();
                            mQuery = query;
                        }
                    }
                }else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_GROUP)
                {
                    if (LoadDataModel.isCurrentSearchFinished == true && query.length() >= MIN_QUERY_LENGTH) {
                        Boolean bool = new SearchAsync(this, ApplicationModel.SearchContext.SEARCH_GROUP, mGroupsCursor).execute().get();
                        if (bool == true) {
                            mGroupsCursor = ApplicationUtils.getCursorFromGroupArray(ldm.loadSearchGroups);
                            mGroupResultsAdapter.swapCursor(mGroupsCursor);
                            mGroupResultsAdapter.notifyDataSetChanged();
                            mQuery = query;
                        }
                    }
                }else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_POST)
                {
                    if (LoadDataModel.isCurrentSearchFinished == true && query.length() >= MIN_QUERY_LENGTH) {
                        Boolean bool = new SearchAsync(this, ApplicationModel.SearchContext.SEARCH_POST, mPostsCursor).execute().get();
                        if (bool == true) {
                            mPostsCursor = ApplicationUtils.getCursorFromPostArray(ldm.loadSearchPosts);
                            mPostResultsAdapter.swapCursor(mPostsCursor);
                            mPostResultsAdapter.notifyDataSetChanged();
                            mQuery = query;
                        }
                    }
                }
                else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_EVERYTHING)
                {

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
        else
        {
            LoadDataModel ldm = LoadDataModel.getInstance();
            if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_USER) {
                ldm.loadSearchUsers.clear();
                mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
                mUserResultsAdapter.swapCursor(mUsersCursor);
                mUserResultsAdapter.notifyDataSetChanged();

                if( ldm.loadSearchUsers.size() > 0)
                {
                    addSearchedUsersToResult();

                    /*int items1 = ldm.loadSearchSelectedUsers.size();
                    for ( int i = 0; i <  items1; i++)
                    {
                        View v = mSearchResults.getChildAt(i);
                        if( v != null) {
                            CheckBox cb = (CheckBox)v.findViewById(R.id.search_result_add_check);
                            if (cb != null) {
                                cb.setChecked(true);
                            }
                        }
                    }*/
                }

                //mSearchView.setQuery(LoadDataModel.searchQuery,true);
                //mSearchView.clearFocus();
                //mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
                //mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchUsers);

            }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_THREAD) {
                //mSearchView.setQuery(LoadDataModel.searchQuery,true);
                //mSearchView.clearFocus();
                mThreadsCursor = ApplicationUtils.getCursorFromThreadArray(ldm.loadSearchThreads);
                mThreadResultsAdapter.swapCursor(mThreadsCursor);
                mThreadResultsAdapter.notifyDataSetChanged();
            }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_GROUP) {
                //mSearchView.setQuery(LoadDataModel.searchQuery,true);
                mGroupsCursor = ApplicationUtils.getCursorFromGroupArray(ldm.loadSearchGroups);
                mGroupResultsAdapter.swapCursor(mGroupsCursor);
                mGroupResultsAdapter.notifyDataSetChanged();
            }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_DEPARTMENT) {
               // mSearchView.setQuery(LoadDataModel.searchQuery,true);
                //mSearchView.clearFocus();
                mDepartmentsCursor = ApplicationUtils.getCursorFromDepartmentArray(ldm.loadSearchDepartments);
                mDepartmentResultsAdapter.swapCursor(mDepartmentsCursor);
                mDepartmentResultsAdapter.notifyDataSetChanged();
            }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_POST) {
                //mSearchView.setQuery(LoadDataModel.searchQuery,true);
                //mSearchView.clearFocus();
                mPostsCursor = ApplicationUtils.getCursorFromPostArray(ldm.loadSearchPosts);
                mPostResultsAdapter.swapCursor(mPostsCursor);
                mPostResultsAdapter.notifyDataSetChanged();
            }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_EVERYTHING) {

            }

            mQuery = query;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = null;

        LoadDataModel ldm = LoadDataModel.getInstance();

        if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_USER) {
            c = mUserResultsAdapter.getCursor();
        }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_THREAD) {
            c = mThreadResultsAdapter.getCursor();
        }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_GROUP) {
            c = mGroupResultsAdapter.getCursor();
        }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_DEPARTMENT) {
            c = mDepartmentResultsAdapter.getCursor();
        }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_POST) {
            c = mPostResultsAdapter.getCursor();
        }else if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_EVERYTHING) {

        }


        c.moveToPosition(position);
        String rowItemId = null;

        if( c.getCount() > 0 )
            rowItemId = c.getString(0);

        ApplicationModel.SearchModel.SearchResult.clickedRowPosition = position;
        ApplicationModel.SearchModel.SearchResult.clickedRowItemId = rowItemId;

        resultsCheckbox = (CheckBox) view.findViewById(R.id.search_result_add_check);

        if( ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_USER &&
                ApplicationModel.SearchModel.isAddUserMode() == true) {


            int selectedUsers = ldm.loadSearchSelectedUsers.size();
            int loadedUsers = ldm.loadSearchUsers.size();
            int shownUsers = ldm.loadSearchUsers.size() + ldm.loadSearchSelectedUsers.size();

            // admin can not be removed from selected members
            if( position == 0 )
                return;

            if (resultsCheckbox.isChecked()) {
                resultsCheckbox.setChecked(false);
                resultsCheckbox.setVisibility(View.INVISIBLE);

                if( position < shownUsers && position < selectedUsers  ) {
                    User u = ldm.loadSearchSelectedUsers.get(position);
                    if( u != null) {
                        ldm.loadSearchSelectedUsers.remove(u);
                        u.selectedflag = false;

                        ldm.loadSearchUsers.add(u);
                        mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
                        mUserResultsAdapter.swapCursor(mUsersCursor);
                        mUserResultsAdapter.notifyDataSetChanged();
                        if (ldm.loadSearchUsers.size() > 0) {
                            addSearchedUsersToResult();
                        }
                    }
                }

                if (ApplicationModel.SearchModel.SearchResult.selectedRowIndexList.contains(position)) {
                    int index = ApplicationModel.SearchModel.SearchResult.selectedRowIndexList.indexOf(position);
                    ApplicationModel.SearchModel.SearchResult.selectedRowIndexList.remove(index);
                    ApplicationModel.SearchModel.SearchResult.selectedRowItemIdList.remove(index);
                }
            } else {
                resultsCheckbox.setVisibility(View.VISIBLE);
                resultsCheckbox.setChecked(true);

                if( position < shownUsers && position >= selectedUsers) {
                    User u = ldm.loadSearchUsers.get(position - selectedUsers);
                    ldm.loadSearchSelectedUsers.add(u);
                    u.selectedflag = true;

                    ldm.loadSearchUsers.remove(u);
                    mUsersCursor = ApplicationUtils.getCursorFromUserArray(ldm.loadSearchSelectedUsers);
                    mUserResultsAdapter.swapCursor(mUsersCursor);
                    mUserResultsAdapter.notifyDataSetChanged();
                    if( ldm.loadSearchUsers.size() > 0) {
                        addSearchedUsersToResult();
                    }

                }

                if (!ApplicationModel.SearchModel.SearchResult.selectedRowIndexList.contains(position)) {
                    ApplicationModel.SearchModel.SearchResult.selectedRowIndexList.add(position);
                    ApplicationModel.SearchModel.SearchResult.selectedRowItemIdList.add(rowItemId);
                }

            }
        }
        else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_USER &&
                ApplicationModel.SearchModel.isAddUserMode() == false)
        {
            /*Intent intent = new Intent(SearchActivity.this,StudentProfileActivity.class);
            intent.putExtra("title",ldm.loadSearchUsers.get(position).fname + " " + ldm.loadSearchUsers.get(position).lname);
            startActivity(intent);*/
            LoadDataModel.loadUserId = ldm.loadSearchUsers.get(position).userid;
            LoadDataModel.loadUserName = ldm.loadSearchUsers.get(position).fname + " " + ldm.loadSearchUsers.get(position).lname ;
            ldm.loadedUserForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_DETAILS,null).execute();
        }
        else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_THREAD )
        {
            /*Intent intent = new Intent(SearchActivity.this,DiscussionViewActivity.class);
            intent.putExtra("title",ldm.loadSearchThreads.get(position).title );
            startActivity(intent);*/
            LoadDataModel.loadThreadId = ldm.loadSearchThreads.get(position).id;
            LoadDataModel.loadThreadTitle = ldm.loadSearchThreads.get(position).title;
            ldm.loadedThreadForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_THREAD_DETAILS,null).execute();
        }
        else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_GROUP )
        {
            /*Intent intent = new Intent(SearchActivity.this,GroupViewActivity.class);
            intent.putExtra("title",ldm.loadSearchGroups.get(position).name );
            startActivity(intent);*/
            LoadDataModel.loadGroupId = ldm.loadSearchGroups.get(position).id;
            LoadDataModel.loadGroupName = ldm.loadSearchGroups.get(position).name;
            ldm.loadedGroupForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_GROUP_DETAILS,null).execute();
        }
        else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_DEPARTMENT )
        {
            /*Intent intent = new Intent(SearchActivity.this,DepartmentView.class);
            intent.putExtra("title", ldm.loadSearchDepartments.get(position).name);
            intent.putExtra("caller", getClass().toString());
            intent.putExtra("subtitle", LoadDataModel.loadDepartmentNumMembers);
            startActivity(intent);*/
            LoadDataModel.loadDepartmentId = ldm.loadSearchDepartments.get(position).id;
            LoadDataModel.loadDepartmentTitle = ldm.loadSearchDepartments.get(position).name;
            ldm.loadedDepartmentForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS,null).execute();
        }
        else if(ApplicationModel.SearchModel.getSearchContext() == ApplicationModel.SearchContext.SEARCH_POST )
        {
        }

    }

}