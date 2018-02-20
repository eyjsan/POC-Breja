package com.example.ah398699.poc_deeplink;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FrameLayout mFrameBackground;
    private TextView mNameMessage;
    Menu optionsMenu;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mFrameBackground.setBackgroundColor(getResources().getColor(R.color.gold));
                    return true;
                case R.id.navigation_dashboard:
                    mFrameBackground.setBackgroundColor(getResources().getColor(R.color.black));
                    return true;
                case R.id.navigation_notifications:
                    mFrameBackground.setBackgroundColor(getResources().getColor(R.color.platinum));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCenter.start(getApplication(), "fa88d187-5c8e-4427-a8ea-ef4a4f24ff43", Analytics.class, Crashes.class);

        // Branch logging for debugging
        Branch.enableLogging();
        // Initialize the Branch object
        Branch.getAutoInstance(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        mFrameBackground = (FrameLayout) findViewById(R.id.backgroundcolor);
        mNameMessage = (TextView) findViewById(R.id.member_name);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        //  store the menu to var when creating options menu
        optionsMenu = menu;
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Branch init
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // option 1: log data
                    Log.i("BRANCH SDK", referringParams.toString());
                    String name = "";
                    String color = "";
                    String page = "";

                    try {

                        page = referringParams.getString("navigate_page");
                        if (page.equalsIgnoreCase("1")) {

                            Intent i = new Intent(MainActivity.this, Main2Activity.class);
                            startActivity(i);

                        }else{

                            name =  referringParams.getString("member_name");
                            color = referringParams.getString("member_color");

                            if (color.equalsIgnoreCase("gold")) {
                                mOnNavigationItemSelectedListener.onNavigationItemSelected(optionsMenu.getItem(0));

                            } else if (color.equalsIgnoreCase("black")) {
                                mOnNavigationItemSelectedListener.onNavigationItemSelected(optionsMenu.getItem(1));

                            } else if (color.equalsIgnoreCase("platinum")) {
                                mOnNavigationItemSelectedListener.onNavigationItemSelected(optionsMenu.getItem(2));
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mNameMessage.setText(name);
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

}
