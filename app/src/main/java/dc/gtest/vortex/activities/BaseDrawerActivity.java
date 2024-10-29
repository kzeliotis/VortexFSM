package dc.gtest.vortex.activities;

import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SCANNED_SERIAL;
import static dc.gtest.vortex.support.MyGlobals.KEY_ID_SEARCH;
import static dc.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_CHECK_LOCATION_SETTINGS;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.codeScanned;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import dc.gtest.vortex.R;
import dc.gtest.vortex.api.GetProducts;
import dc.gtest.vortex.support.MyLocalization;
import dc.gtest.vortex.support.MySliderMenu;
import dc.gtest.vortex.support.MySynchronize;
import dc.gtest.vortex.support.MyUtils;

@SuppressLint("Registered")
public class BaseDrawerActivity extends AppCompatActivity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Log.e(LOG_TAG, "-------------------------------------------- BaseDrawerActivity onCreate.");

//        Locale.setDefault(Locale.ENGLISH);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Log.e(LOG_TAG, "-------------------------------------------- BaseDrawerActivity onResume.");

        MyLocalization.setupLanguage(BaseDrawerActivity.this);
        new MySliderMenu(this).mySliderMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 49375:
                IntentResult result = IntentIntegrator.parseActivityResult(49374, resultCode, data);
                if (result != null) {
                    if (result.getContents() != null) {
                        String ScannedCode = result.getContents();
                        if(!ScannedCode.isEmpty()){
//                            if (MyUtils.isNetworkAvailable()) {
//                                GetProducts getProducts = new GetProducts(this, "", false, "0", false, ScannedCode);
//                                getProducts.execute();
//                            }
                            Intent intent = new Intent(BaseDrawerActivity.this, ProductsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(KEY_ID_SEARCH, true);
                            intent.putExtra(KEY_ID_SCANNED_SERIAL,  ScannedCode);
                            startActivity(intent);
                        }

                    }
                }
                break;
        }
    }
}
