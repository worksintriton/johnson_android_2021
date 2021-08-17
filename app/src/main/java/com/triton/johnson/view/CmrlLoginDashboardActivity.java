package com.triton.johnson.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.triton.johnson.R;

import com.triton.johnson.admin.AdminSelectedDepartmentScreen;
import com.triton.johnson.cmrllogin.CmrlLoginDashaboardScreen;
import com.triton.johnson.session.SessionManager;
import com.triton.johnson.sweetalertdialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Objects;

public class CmrlLoginDashboardActivity extends AppCompatActivity {

    String TAG = "CmrlLoginDashboardActivity";
    public static NavigationView nvDrawer;
    public static DrawerLayout drawerLayout;
    SessionManager sessionManager;
    String code = "";
    private Fragment fragment;
    private Class fragmentClass;
    public static String type;
    public static String tabSelects;
    private FragmentTransaction transaction;
    private Dialog dialog;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.w(TAG,"onCreate--->");
        sessionManager = new SessionManager(CmrlLoginDashboardActivity.this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        code = hashMap.get(SessionManager.KEY_STATION_CODE);
        Intent intent = getIntent();
        if (intent.getStringExtra("Tabselects") != null) {
            tabSelects = intent.getStringExtra("Tabselects");
        } else {
            tabSelects = "0";
        }
        if (intent.getStringExtra("TypeForm") != null) {
            if (Objects.requireNonNull(intent.getStringExtra("TypeForm")).equalsIgnoreCase("1")) {
                fragmentClass = CmrlLoginDashaboardScreen.class;

                try {

                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {

                    e.printStackTrace();

                }
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();
            } else if (Objects.requireNonNull(intent.getStringExtra("TypeForm")).equalsIgnoreCase("2")) {
                fragmentClass = AdminSelectedDepartmentScreen.class;

                try {

                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {

                    e.printStackTrace();

                }
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();
            }
        } else {
            fragmentClass = CmrlLoginDashaboardScreen.class;

            try {

                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {

                e.printStackTrace();

            }
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();
        }

        Button logOutButton = findViewById(R.id.logout_button);

        nvDrawer = findViewById(R.id.nvView);

        drawerLayout =  findViewById(R.id.main_content);
        logOutButton.setOnClickListener(view -> new SweetAlertDialog(CmrlLoginDashboardActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.app_name))
                .setContentText("Do you want to logout?")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setCancelClickListener(Dialog::dismiss)
                .setConfirmClickListener(sDialog -> {
                    getSharedPreferences("Station", 0).edit().clear().commit();
                    sessionManager.logoutUser();
                    sDialog.dismiss();
                })
                .show());
        setupDrawerContent(nvDrawer);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    @SuppressLint("NonConstantResourceId")
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        switch (menuItem.getItemId()) {

            case R.id.home_fragment:
                fragmentClass = CmrlLoginDashaboardScreen.class;

                try {

                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {

                    e.printStackTrace();

                }
                tabSelects = "0";
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();
                break;
            /*case R.id.favorites_fragment:
                fragmentClass = AdminSelectedDepartmentScreen.class;

                try {

                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {

                    e.printStackTrace();

                }
                tabSelects = "0";
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();
                break;
            case R.id.change_password:

                fragmentClass = ChangePasswordForAdminFragment.class;

                try {

                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {

                    e.printStackTrace();

                }

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("NotificationFragment").setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left).replace(R.id.flContent, fragment).commit();

                break;*/
            default:

        }

        // Highlight the selected item has been done by NavigationView

        menuItem.setChecked(true);

        // Set action bar title
        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        showExitAppAlert();
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showExitAppAlert() {
        try {

            dialog = new Dialog(CmrlLoginDashboardActivity.this);
            dialog.setContentView(R.layout.alert_exit_layout);
            Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
            Button btn_exit = dialog.findViewById(R.id.btn_exit);

            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    CmrlLoginDashboardActivity.this.finishAffinity();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }




    }



}
