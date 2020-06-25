package am.gtest.vortex.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyLocalization;
import am.gtest.vortex.support.MySynchronize;

@SuppressLint("Registered")
public class BaseCoordinatorActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Log.e(LOG_TAG, "-------------------------------------------- onCreate.");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(LOG_TAG, "-------------------------------------------- onResume.");

        MyLocalization.setupLanguage(BaseCoordinatorActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.itemSynchronize:
                new MySynchronize(this).mySynchronize();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}