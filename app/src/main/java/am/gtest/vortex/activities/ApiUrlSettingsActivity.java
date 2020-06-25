package am.gtest.vortex.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyLocalization.localized_cancel;
import static am.gtest.vortex.support.MyLocalization.localized_fill_host_server;
import static am.gtest.vortex.support.MyLocalization.localized_host_server_settings;
import static am.gtest.vortex.support.MyLocalization.localized_host_url_title;
import static am.gtest.vortex.support.MyLocalization.localized_remove_url;
import static am.gtest.vortex.support.MyLocalization.localized_save;
import static am.gtest.vortex.support.MyLocalization.localized_user;
import static am.gtest.vortex.support.MyPrefs.PREF_ALTERNATIVE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_BASE_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_FIRST_HOST_URL;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class ApiUrlSettingsActivity extends BaseCoordinatorActivity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private TextView tvHostUrlTitle;
    private RadioGroup radioGroup;

    private RadioButton rbUrl_1;

    private int myCustomId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout flCoordinatorLayout = findViewById(R.id.flBaseCoordinatorLayout);
        getLayoutInflater().inflate(R.layout.content_api_url_settings, flCoordinatorLayout, true);

        FloatingActionButton fabAddUrl = findViewById(R.id.fabAddUrl);
        radioGroup = findViewById(R.id.radioGroup);
        tvHostUrlTitle = findViewById(R.id.tvHostUrlTitle);
        rbUrl_1 = findViewById(R.id.rbUrl_1);

        rbUrl_1.setText(MyPrefs.getString(PREF_FIRST_HOST_URL, ""));

        addPreviouslyDefinedUrls();

        fabAddUrl.setOnClickListener(v -> {
            final EditText editText = new EditText(ApiUrlSettingsActivity.this);
            editText.setPadding(40, 20, 40, 20);

            new AlertDialog.Builder(ApiUrlSettingsActivity.this)
                    .setView(editText)
                    .setTitle(localized_fill_host_server)
                    .setPositiveButton(localized_save, (dialog, which) -> {
                        dialog.dismiss();
                        String hostServerUrl = editText.getText().toString().trim();

                        if (!hostServerUrl.equals("")) {
                            MyPrefs.setString(PREF_BASE_HOST_URL, hostServerUrl);

                            String alternativeHostUrls = MyPrefs.getString(PREF_ALTERNATIVE_HOST_URL, "");
                            alternativeHostUrls = alternativeHostUrls + hostServerUrl + ",";
                            MyPrefs.setString(PREF_ALTERNATIVE_HOST_URL, alternativeHostUrls);

                            addRadioButtonView(hostServerUrl);
                        }
                    })
                    .setNegativeButton(localized_cancel, null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUiTexts();
    }

    private void addPreviouslyDefinedUrls() {
        String alternativeHostUrls = MyPrefs.getString(PREF_ALTERNATIVE_HOST_URL, "");

        if (!alternativeHostUrls.equals("")) {
            List<String> urlsList = Arrays.asList(alternativeHostUrls.split("\\s*,\\s*"));

            for (int i = 0; i < urlsList.size(); i++) {
                addRadioButtonView(urlsList.get(i));
            }
        }
    }

    private void addRadioButtonView(final String text) {
        myCustomId ++;

        final RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button, radioGroup, false);
        radioButton.setId(myCustomId);
        radioButton.setText(text);
        radioGroup.addView(radioButton);

        radioButton.setChecked(text.equals(MyPrefs.getString(PREF_BASE_HOST_URL, "")));

        radioButton.setOnLongClickListener(v -> {
            new AlertDialog.Builder(ApiUrlSettingsActivity.this)
                    .setMessage(localized_remove_url)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        radioButton.setVisibility(View.GONE);

                        String alternativeHostUrls = MyPrefs.getString(PREF_ALTERNATIVE_HOST_URL, "");
                        alternativeHostUrls = alternativeHostUrls.replace(text + ",", "");
                        MyPrefs.setString(PREF_ALTERNATIVE_HOST_URL, alternativeHostUrls);

                        rbUrl_1.setChecked(true);

                        MyPrefs.setString(PREF_BASE_HOST_URL, MyPrefs.getString(PREF_FIRST_HOST_URL, ""));

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return true;
        });
    }

    public void onUrlRadioButtonClick(View view) {
        RadioButton radioButton = (RadioButton) view;
        MyPrefs.setString(PREF_BASE_HOST_URL, radioButton.getText().toString());

        Log.e(LOG_TAG, "--------------- base host: " + MyPrefs.getString(PREF_BASE_HOST_URL, ""));
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(localized_host_server_settings);
            getSupportActionBar().setSubtitle(localized_user + ": " + MyPrefs.getString(PREF_USER_NAME, ""));
        }

        tvHostUrlTitle.setText(localized_host_url_title);
    }
}
