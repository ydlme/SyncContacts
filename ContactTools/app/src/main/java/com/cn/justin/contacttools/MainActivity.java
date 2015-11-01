package com.cn.justin.contacttools;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ImageButton imageButtonContact;
    private ImageButton imageButtonSMS;
    private ImageButton imageButtonCall;
    private int uid = 0;
    private RelativeLayout layoutContactBar;
    private RelativeLayout layoutCallBar;
    private RelativeLayout layoutSMSBar;
    private BaseFragment fragmentContact;
    private BaseFragment fragmentSMS;
    private BaseFragment fragmentCall;
    private BaseFragment currentFragment;
    FragmentManager fragmentManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);
        if (intent == null)
            return;
        int id = intent.getIntExtra("uid", 1);
        AppConf appConf = (AppConf)getApplication();
        appConf.setUid(id);
        uid = id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_bar :{
                if (uid != 0) {
                    setTitle("正在同步...");
                    currentFragment.syncToServer();
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButtonCall = (ImageButton)findViewById(R.id.call_bar);
        imageButtonContact = (ImageButton)findViewById(R.id.contact_bar);
        imageButtonSMS = (ImageButton)findViewById(R.id.sms_bar);

        layoutCallBar = (RelativeLayout)findViewById(R.id.layout_call_bar);
        layoutContactBar = (RelativeLayout)findViewById(R.id.layout_contact_bar);
        layoutSMSBar = (RelativeLayout)findViewById(R.id.layout_sms_bar);

        initImageButton();

        fragmentManager = getFragmentManager();
        fragmentContact = new ContactFragment();
        fragmentSMS = new SMSFragment();
        fragmentCall = new CallFragment();
        currentFragment = fragmentContact;
        fragmentManager.beginTransaction().replace(R.id.fragment_content, fragmentContact).commit();
    }

    private void switchFragment(BaseFragment fragment) {
        if (fragment == currentFragment)
            return;

        if (!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .add(R.id.fragment_content, fragment).commit();
        } else {
            fragmentManager.beginTransaction()
                    .hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }


    private void resetImageButton() {
        Drawable btnDrawable = getResources().getDrawable(R.drawable.bar_contact);
        imageButtonContact.setBackgroundDrawable(btnDrawable);
        btnDrawable = getResources().getDrawable(R.drawable.bar_call);
        imageButtonCall.setBackgroundDrawable(btnDrawable);
        btnDrawable = getResources().getDrawable(R.drawable.bar_sms);
        imageButtonSMS.setBackgroundDrawable(btnDrawable);
    }


    private void initImageButton() {

        imageButtonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_sms_clicked);
                imageButtonSMS.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentSMS);
                setTitle("短信");
            }
        });

        imageButtonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_contact_clicked);
                imageButtonContact.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentContact);
                setTitle("通讯录");
            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_call_clicked);
                imageButtonCall.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentCall);
                setTitle("通话记录");
            }
        });

        layoutSMSBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_sms_clicked);
                imageButtonSMS.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentSMS);
                setTitle("短信");
            }
        });

        layoutContactBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_contact_clicked);
                imageButtonContact.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentContact);
                setTitle("通讯录");
            }
        });

        layoutCallBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageButton();
                Resources resources = getResources();
                Drawable btnDrawable = resources.getDrawable(R.drawable.bar_call_clicked);
                imageButtonCall.setBackgroundDrawable(btnDrawable);
                switchFragment(fragmentCall);
                setTitle("通话记录");
            }
        });
    }
}
