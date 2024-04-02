package dc.gtest.vortex.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MySynchronize;

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
                new MySynchronize(this).mySynchronize(true);
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