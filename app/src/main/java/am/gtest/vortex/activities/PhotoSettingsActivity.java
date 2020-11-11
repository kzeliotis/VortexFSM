/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyLocalization.localized_photo_settings;
import static am.gtest.vortex.support.MyPrefs.PREF_IMAGE_SIZE;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_ONLY_WIFI;

public class PhotoSettingsActivity extends AppCompatActivity {

    //private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvImageSizeTitle;
    private TextView tvImageSendingNetworkTitle;
    private RadioButton rbSmall;
    private RadioButton rbMedium;
    private RadioButton rbLarge;
    private RadioButton rbActual;
    private RadioButton rbWiFiAndMobile;
    private RadioButton rbOnlyWiFi;

    private String language;

    private String imageSizeTitle;
    private String networkTypeTitle;
    private String small;
    private String medium;
    private String large;
    private String actual;
    private String wifiAndMobile;
    private String onlyWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FrameLayout flCoordinatorLayout = findViewById(R.id.flBaseCoordinatorLayout);
        getLayoutInflater().inflate(R.layout.content_photo_settings, flCoordinatorLayout, true);

        tvImageSizeTitle = findViewById(R.id.tvImageSizeTitle);
        tvImageSendingNetworkTitle = findViewById(R.id.tvImageSendingNetworkTitle);
        rbSmall = findViewById(R.id.rbSmall);
        rbMedium = findViewById(R.id.rbMedium);
        rbLarge = findViewById(R.id.rbLarge);
        rbActual = findViewById(R.id.rbActual);
        rbWiFiAndMobile = findViewById(R.id.rbWiFiAndMobile);
        rbOnlyWiFi = findViewById(R.id.rbOnlyWiFi);

        switch (MyPrefs.getInt(PREF_IMAGE_SIZE, 360)) {
            case 360:
                rbSmall.setChecked(true);
                break;
            case 720:
                rbMedium.setChecked(true);
                break;
            case 1280:
                rbLarge.setChecked(true);
                break;
            case 0:
                rbActual.setChecked(true);
                break;
        }

        rbWiFiAndMobile.setChecked(!MyPrefs.getBoolean(PREF_ONLY_WIFI, false));
        rbOnlyWiFi.setChecked(MyPrefs.getBoolean(PREF_ONLY_WIFI, false));

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);
        changeTextLanguage();
        setViewsTexts();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        if (menuItemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPhotoImageSize(View view) {
        switch (view.getId()) {
            case R.id.rbSmall:
                MyPrefs.setInt(PREF_IMAGE_SIZE, 360);
                break;

            case R.id.rbMedium:
                MyPrefs.setInt(PREF_IMAGE_SIZE, 720);
                break;

            case R.id.rbLarge:
                MyPrefs.setInt(PREF_IMAGE_SIZE, 1280);
                break;

            case R.id.rbActual:
                MyPrefs.setInt(PREF_IMAGE_SIZE, 0);
                break;
        }
    }

    public void onNetworkType(View view) {
        switch (view.getId()) {
            case R.id.rbWiFiAndMobile:
                MyPrefs.setBoolean(PREF_ONLY_WIFI, false);
                break;

            case R.id.rbOnlyWiFi:
                MyPrefs.setBoolean(PREF_ONLY_WIFI, true);
                break;
        }
    }

    private void setViewsTexts() {
        tvImageSizeTitle.setText(imageSizeTitle);
        tvImageSendingNetworkTitle.setText(networkTypeTitle);
        rbSmall.setText(small);
        rbMedium.setText(medium);
        rbLarge.setText(large);
        rbActual.setText(actual);
        rbWiFiAndMobile.setText(wifiAndMobile);
        rbOnlyWiFi.setText(onlyWifi);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_photo_settings);
        }
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            imageSizeTitle = getString(R.string.photo_image_size_gr);
            networkTypeTitle = getString(R.string.image_sending_network_type_gr);
            small = getString(R.string.small_gr);
            medium = getString(R.string.medium_gr);
            large = getString(R.string.large_gr);
            actual = getString(R.string.actual_gr);
            wifiAndMobile = getString(R.string.wifi_and_mobile_gr);
            onlyWifi = getString(R.string.only_wifi_gr);
        } else {
            imageSizeTitle = getString(R.string.photo_image_size);
            networkTypeTitle = getString(R.string.image_sending_network_type);
            small = getString(R.string.small);
            medium = getString(R.string.medium);
            large = getString(R.string.large);
            actual = getString(R.string.actual);
            wifiAndMobile = getString(R.string.wifi_and_mobile);
            onlyWifi = getString(R.string.only_wifi);
        }
    }
}