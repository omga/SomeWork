package se.anyro.nfc_reader;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.otentico.android.model.DrawerItem;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragmentActivity extends ActionBarActivity {
    private ListView mDrawerList;
    private List<DrawerItem> mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Toolbar mToolbar;

    ImageView cubeHomeIcon;

    //return the layout of activity that extends this class
    protected abstract int getActivityLayout();

    //return the drawer layout id of activity that extends this class (make sure your activity layout have android.support.v4.widget.DrawerLayout)
    protected abstract int getDrawerLayout();

    protected void setToolbarVisibility(int alpha) {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(0x00ffffff);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(getDrawerLayout());
        mTitle = mDrawerTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.list_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        prepareNavigationDrawerItems();
        setAdapter();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        cubeHomeIcon = (ImageView) findViewById(R.id.image_app_cube);
        cubeHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(BaseFragmentActivity.this instanceof MainScreen)) {
                    Intent i = new Intent(BaseFragmentActivity.this, MainScreen.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void prepareNavigationDrawerItems() {
        mDrawerItems = new ArrayList<>();
        mDrawerItems.add(
                new DrawerItem(
                        R.drawable.history_ic,
                        R.string.scan_history_text,
                        DrawerItem.DRAWER_ITEM_TAG_SCAN_HISTORY));
        mDrawerItems.add(
                new DrawerItem(
                        R.drawable.bookmark_ic,
                        R.string.term_of_use_text,
                        DrawerItem.DRAWER_ITEM_TAG_TERMS_OF_USE));
        mDrawerItems.add(
                new DrawerItem(
                        R.drawable.floppy_disk_ic,
                        R.string.version_text,
                        DrawerItem.DRAWER_ITEM_TAG_VERSION));

    }

    private void setAdapter() {

        View headerView = null;
        headerView = prepareHeaderView(R.layout.header_navigation_drawer_1,
                "http://pengaja.com/uiapptemplate/avatars/0.jpg",
                "dev@csform.com");


        BaseAdapter adapter = new DrawerAdapter(this, mDrawerItems, true);

        mDrawerList.addHeaderView(headerView);//Add header before adapter (for pre-KitKat)
        mDrawerList.setAdapter(adapter);
    }

    private View prepareHeaderView(int layoutRes, String url, String email) {
        View headerView = getLayoutInflater().inflate(layoutRes, mDrawerList, false);
        //ImageView iv = (ImageView) headerView.findViewById(R.id.image);
        //TextView tv = (TextView) headerView.findViewById(R.id.email);
        //tv.setText(email);
        return headerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position/*, mDrawerItems.get(position - 1).getTag()*/);
        }
    }

    private void selectItem(int position/*, int drawerTag*/) {
        // minus 1 because we have header that has 0 position
        if (position < 1) { //because we have header, we skip clicking on it
            return;
        }
        //String drawerTitle = getString(mDrawerItems.get(position - 1).getTitle());
        //Toast.makeText(this, "You selected " + drawerTitle + " at position: " + position, Toast.LENGTH_SHORT).show();

        mDrawerList.setItemChecked(position, false);
//        setTitle(mDrawerItems.get(position - 1).getTitle());
        mDrawerLayout.closeDrawer(mDrawerList);
        if (mDrawerItems.get(position - 1).getTag() == DrawerItem.DRAWER_ITEM_TAG_TERMS_OF_USE) {
            if (!(BaseFragmentActivity.this instanceof TermsOfUseActivity)) {
                Intent i = new Intent(BaseFragmentActivity.this, TermsOfUseActivity.class);
                startActivity(i);
            }
        } else if (mDrawerItems.get(position - 1).getTag() == DrawerItem.DRAWER_ITEM_TAG_SCAN_HISTORY) {
            if (!(BaseFragmentActivity.this instanceof RecentHistoryActivity)) {
                Intent i = new Intent(BaseFragmentActivity.this, RecentHistoryActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
