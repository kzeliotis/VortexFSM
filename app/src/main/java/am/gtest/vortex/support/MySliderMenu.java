package am.gtest.vortex.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
//import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import am.gtest.vortex.BuildConfig;
import am.gtest.vortex.R;
import am.gtest.vortex.activities.ApiUrlSettingsActivity;
import am.gtest.vortex.activities.AssignmentsActivity;
import am.gtest.vortex.activities.CustomFieldsActivity;
import am.gtest.vortex.activities.LoginActivity;
import am.gtest.vortex.activities.MapsActivity;
import am.gtest.vortex.activities.NewAssignmentActivity;
import am.gtest.vortex.activities.NewCustomerActivity;
import am.gtest.vortex.activities.PhotoSettingsActivity;
import am.gtest.vortex.activities.SearchCustomersActivity;
import am.gtest.vortex.activities.ManualsActivity;
import am.gtest.vortex.application.MyApplication;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyGlobals.CONST_GR;
import static am.gtest.vortex.support.MyGlobals.KEY_DOWNLOAD_ALL_DATA;
import static am.gtest.vortex.support.MyGlobals.KEY_VORTEX_TABLE;
import static am.gtest.vortex.support.MyLocalization.localized_clear_cache_warning;
import static am.gtest.vortex.support.MyLocalization.localized_company_custom_fields;
import static am.gtest.vortex.support.MyLocalization.localized_company_website;
import static am.gtest.vortex.support.MyLocalization.localized_host_server_settings;
import static am.gtest.vortex.support.MyLocalization.localized_log_out;
import static am.gtest.vortex.support.MyLocalization.localized_manuals;
import static am.gtest.vortex.support.MyLocalization.localized_new_assignment;
import static am.gtest.vortex.support.MyLocalization.localized_new_customer;
import static am.gtest.vortex.support.MyLocalization.localized_photo_settings;
import static am.gtest.vortex.support.MyLocalization.localized_search_customers;
import static am.gtest.vortex.support.MyLocalization.localized_send_email;
import static am.gtest.vortex.support.MyLocalization.localized_shared_prefs_exported;
import static am.gtest.vortex.support.MyLocalization.localized_show_assignments;
import static am.gtest.vortex.support.MyLocalization.localized_show_map;
import static am.gtest.vortex.support.MyLocalization.localized_synchronize;
import static am.gtest.vortex.support.MyPrefs.PREF_DEVICE_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_IS_LOGGED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;
import static am.gtest.vortex.support.MyPrefs.PREF_USER_NAME;

public class MySliderMenu {

    //private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final Context ctx;
    private final Activity activity;
    private final Toolbar toolbar;
    private final DrawerLayout drawer;

    private final String userName;

    public MySliderMenu(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
        this.toolbar = activity.findViewById(R.id.toolbar);
        drawer = activity.findViewById(R.id.drawer_layout);
        userName = MyPrefs.getString(PREF_USER_NAME, "");
    }

    public void mySliderMenu() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);

        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }

        try {
            toggle.syncState();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NavigationView navigationView = activity.findViewById(R.id.nav_view);

        if (navigationView != null) {

            View header = navigationView.getHeaderView(0);
            TextView tvVersion = header.findViewById(R.id.tvVersion);
            TextView tvDeviceId = header.findViewById(R.id.tvDeviceId);
            RadioButton rbEnglish = header.findViewById(R.id.rbEnglish);
            RadioButton rbGreek = header.findViewById(R.id.rbGreek);

            navigationView.getMenu().findItem(R.id.nav_manuals).setTitle(localized_manuals);
            navigationView.getMenu().findItem(R.id.nav_map).setTitle(localized_show_map);
            navigationView.getMenu().findItem(R.id.nav_new_assignment).setTitle(localized_new_assignment);
            navigationView.getMenu().findItem(R.id.nav_show_assignments).setTitle(localized_show_assignments);
            navigationView.getMenu().findItem(R.id.nav_new_customer).setTitle(localized_new_customer);
            navigationView.getMenu().findItem(R.id.nav_search_customers).setTitle(localized_search_customers);
            navigationView.getMenu().findItem(R.id.nav_synchronize).setTitle(localized_synchronize);
            navigationView.getMenu().findItem(R.id.nav_send_email).setTitle(localized_send_email);
            navigationView.getMenu().findItem(R.id.nav_photo_settings).setTitle(localized_photo_settings);
            navigationView.getMenu().findItem(R.id.nav_api_url_settings).setTitle(localized_host_server_settings);
            navigationView.getMenu().findItem(R.id.nav_website).setTitle(localized_company_website);
            navigationView.getMenu().findItem(R.id.nav_logout).setTitle(localized_log_out + " - " + userName);
            navigationView.getMenu().findItem(R.id.nav_company_custom_fields).setTitle(localized_company_custom_fields);

            tvVersion.setText(BuildConfig.VERSION_NAME);

            String deviceIdText = "Device ID: " + MyPrefs.getDeviceId(PREF_DEVICE_ID, "");
            tvDeviceId.setText(deviceIdText);

            if (MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN).equals(CONST_GR)) {
                rbEnglish.setChecked(false);
                rbGreek.setChecked(true);
            } else {
                rbEnglish.setChecked(true);
                rbGreek.setChecked(false);
            }

            navigationView.setNavigationItemSelectedListener(item -> {

                Intent intent = null;

                switch (item.getItemId()) {

                    case R.id.nav_clearCache:
                        new AlertDialog.Builder(activity)
                                .setMessage(localized_clear_cache_warning)
                                .setPositiveButton(R.string.yes, (dialog, which) -> {
                                    dialog.dismiss();

                                    deleteCache(ctx);
                                    clearApplicationData(ctx);
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx); //ctx.getSharedPreferences("com.vortex.app", Context.MODE_PRIVATE);
                                    prefs.edit().clear().commit();

                                })
                                .setNegativeButton(R.string.no, null)
                                .show();

                        break;

                    case R.id.nav_exportSharedPref:
                        File sourceLocation = new File("/data/data/am.gtest.vortex/shared_prefs");
                        //String RootPath = Environment.getExternalStorageDirectory().toString();
                        File targetLocation = new File(ctx.getExternalFilesDir(null) + File.separator + "Shared_Pref_BackUp");
                        MyUtils.checkMakeDir(targetLocation);

                        try {
                            copyDirectory(sourceLocation, targetLocation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            Toast.makeText(MyApplication.getContext(), localized_shared_prefs_exported, Toast.LENGTH_LONG).show();
                        }
                        break;

                    case R.id.nav_manuals:
                        intent = new Intent(ctx, ManualsActivity.class);
                        break;

                    case R.id.nav_map:
                        intent = new Intent(ctx, MapsActivity.class);
                        break;

                    case R.id.nav_new_assignment:
                        intent = new Intent(ctx, NewAssignmentActivity.class);
                        break;

                    case R.id.nav_show_assignments:
                        if (!ctx.getClass().getSimpleName().equalsIgnoreCase("AssignmentsActivity")) {
                            intent = new Intent(ctx, AssignmentsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(KEY_DOWNLOAD_ALL_DATA, false);
                            ctx.startActivity(intent);
                        }
                        break;

                    case R.id.nav_new_customer:
                        intent = new Intent(ctx, NewCustomerActivity.class);
                        break;

                    case R.id.nav_search_customers:
                        intent = new Intent(ctx, SearchCustomersActivity.class);
                        break;

                    case R.id.nav_synchronize:
                        new MySynchronize(ctx).mySynchronize();
                        break;

                    case R.id.nav_send_email:
                        intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

                        if (intent.resolveActivity(ctx.getPackageManager()) == null) {
                            intent = null;
                        }
                        break;

                    case R.id.nav_photo_settings:
                        intent = new Intent(ctx, PhotoSettingsActivity.class);
                        break;

                    case R.id.nav_api_url_settings:
                        intent = new Intent(ctx, ApiUrlSettingsActivity.class);
                        break;

                    case R.id.nav_website:
                        Uri website = Uri.parse(item.toString());
                        intent = new Intent(Intent.ACTION_VIEW, website);
                        break;

                    case R.id.nav_logout:
                        MyPrefs.setBoolean(PREF_KEY_IS_LOGGED_IN, false);
                        activity.finishAffinity();

                        intent = new Intent(ctx, LoginActivity.class);
                        break;

                    case R.id.nav_company_custom_fields:
                        intent = new Intent(ctx, CustomFieldsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(KEY_VORTEX_TABLE, "Company");
                }

                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                }

                closeDrawer();

                return true;
            });
        }
    }

    public void mySwitchSliderMenuLanguage(View view) {

        NavigationView navigationView = activity.findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_manuals).setTitle(localized_manuals);
        navigationView.getMenu().findItem(R.id.nav_map).setTitle(localized_show_map);
        navigationView.getMenu().findItem(R.id.nav_new_assignment).setTitle(localized_new_assignment);
        navigationView.getMenu().findItem(R.id.nav_show_assignments).setTitle(localized_show_assignments);
        navigationView.getMenu().findItem(R.id.nav_new_customer).setTitle(localized_new_customer);
        navigationView.getMenu().findItem(R.id.nav_search_customers).setTitle(localized_search_customers);
        navigationView.getMenu().findItem(R.id.nav_synchronize).setTitle(localized_synchronize);
        navigationView.getMenu().findItem(R.id.nav_send_email).setTitle(localized_send_email);
        navigationView.getMenu().findItem(R.id.nav_photo_settings).setTitle(localized_photo_settings);
        navigationView.getMenu().findItem(R.id.nav_api_url_settings).setTitle(localized_host_server_settings);
        navigationView.getMenu().findItem(R.id.nav_website).setTitle(localized_company_website);
        navigationView.getMenu().findItem(R.id.nav_logout).setTitle(localized_log_out + " - " + userName);

        closeDrawer();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}


    }


    public static void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {


        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public void clearApplicationData(Context context) {
        File cacheDirectory = context.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }


    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    private void closeDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
