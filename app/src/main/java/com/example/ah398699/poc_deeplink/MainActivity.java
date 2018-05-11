package com.example.ah398699.poc_deeplink;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FrameLayout mFrameBackground;
    private TextView mNameMessage;
    Menu optionsMenu;
    Button btn;

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


        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    executeRemoteCommand("coinfo", "App7C1*0413", "54.68.88.236", 2222, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);

    }
    public static String executeRemoteCommand(String username, String password, String hostname, int port, Integer btnValue)
        throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        //Command List
        String cmdString = "";

        switch (btnValue) {
            case 0 :
                cmdString ="TaskAdmin show AppServer SyCoGate";
                break;
            case 1 :
                cmdString ="TaskAdmin stop AppServer SyCoGate";
                break;
            case 3 :
                cmdString="TaskAdmin start AppServer SyCoGate";
                break;
            default:
                break;
        }
            // Execute command
        channelssh.setCommand(cmdString);
        channelssh.connect();
        channelssh.disconnect();
        System.out.println(channelssh);
        return baos.toString();
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
