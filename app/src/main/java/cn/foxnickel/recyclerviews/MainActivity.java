package cn.foxnickel.recyclerviews;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import cn.foxnickel.recyclerviews.fragment.GridRecyclerFragment;
import cn.foxnickel.recyclerviews.fragment.LinearRecyclerFragment;
import cn.foxnickel.recyclerviews.fragment.StaggeredRecyclerFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setDefaultPage();
    }

    private void setDefaultPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main,new LinearRecyclerFragment());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_linear_layout_manager) {
            Snackbar.make(mDrawerLayout,"LinearRecycler",Snackbar.LENGTH_SHORT).show();
            LinearRecyclerFragment linearRecyclerFragment = LinearRecyclerFragment.newInstance();
            transaction.replace(R.id.content_main,linearRecyclerFragment);
            transaction.commit();
        } else if (id == R.id.nav_grid_layout_manager) {
            Snackbar.make(mDrawerLayout,"GridRecycler",Snackbar.LENGTH_SHORT).show();
            transaction.replace(R.id.content_main, GridRecyclerFragment.newInstance());
            transaction.commit();
        } else if (id == R.id.nav_staggered_grid_layout_manager) {
            Snackbar.make(mDrawerLayout,"StaggeredRecycler",Snackbar.LENGTH_SHORT).show();
            transaction.replace(R.id.content_main, StaggeredRecyclerFragment.newInstance());
            transaction.commit();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
