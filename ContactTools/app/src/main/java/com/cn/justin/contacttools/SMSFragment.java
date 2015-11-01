package com.cn.justin.contacttools;


import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cn.justin.contacttools.adapter.SMSAdapter;
import com.cn.justin.contacttools.utils.SMSItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SMSFragment extends BaseFragment {

    private ArrayList<SMSItem> mSMSItem = new ArrayList<SMSItem>();
    private ListView listView;
    SMSAdapter adapter = null;
    private final String API = "sync_msgs";

    private String encodeSmsString() {
        StringBuffer sb = new StringBuffer();
        String item = String.format("[{\"from_user\":\"%s\", \"msg\":\"%s\",\"long_date\":%d}",
                mSMSItem.get(0).name, mSMSItem.get(0).msg, mSMSItem.get(0).date);
        sb.append(item);
        for (int i = 1; i < mSMSItem.size(); i++) {
            item = String.format(",{\"from_user\":\"%s\", \"msg\":\"%s\",\"long_date\":%d}",
                    mSMSItem.get(i).name, mSMSItem.get(i).msg, mSMSItem.get(i).date);
            sb.append(item);
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public void syncToServer() {
        if (mSMSItem.isEmpty())
            return;
        RequestParams params = new RequestParams();
        int id = ((AppConf)getActivity().getApplication()).getUid();
        params.put("uid", id);
        String strSms = encodeSmsString();
        params.put("msgs", strSms);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = String.format("%s%s", ((AppConf)getActivity().getApplication()).getHostIP(), API);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                getActivity().setTitle("同步完成");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                getActivity().setTitle("同步失败");
            }
        });
    }


    public static SMSFragment newInstance(String param1, String param2) {
        SMSFragment fragment = new SMSFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SMSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    private void loadSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";
        final String SMS_URI_OUTBOX = "content://sms/outbox";
        final String SMS_URI_FAILED = "content://sms/failed";
        final String SMS_URI_QUEUED = "content://sms/queued";

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
            Cursor cur = getActivity().getContentResolver().query(uri, projection, null, null, "date desc");		// 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    mSMSItem.add(new SMSItem(strAddress, strbody, intType, longDate));
                    /*
                    intType == 1 "接收"; intType == 2 "发送";
                    */
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);
        listView = (ListView)rootView.findViewById(R.id.fragment_listview_sms);

        if (mSMSItem.isEmpty()) {
            loadSmsInPhone();
        }

        if (adapter == null) {
            adapter = new SMSAdapter(getActivity(), mSMSItem);
        }

        listView.setAdapter(adapter);

        return rootView;
    }


}
