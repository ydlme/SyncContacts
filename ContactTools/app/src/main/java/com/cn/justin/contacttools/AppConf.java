package com.cn.justin.contacttools;
import android.app.Application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by justin on 10/28/15.
 */
public class AppConf extends Application {
    private int uid = 0;
    private String mHostIP = null;
    @Override
    public void onCreate() {
        super.onCreate();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/sdcard/sync_host_ip.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String line = br.readLine();
            mHostIP = new String(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getHostIP() {
        return  mHostIP;
    }

    public int getUid() {
        return uid;
    }
}
