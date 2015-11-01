package com.cn.justin.contacttools;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.cn.justin.contacttools.adapter.CallAdapter;
import com.cn.justin.contacttools.utils.CallLogItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class CallFragment extends BaseFragment {


    private ArrayList<CallLogItem> mCallItems = new ArrayList<CallLogItem>();
    private ListView listView;
    CallAdapter adapter = null;
    private final String API = "sync_calllogs";

    public CallFragment() {
        // Required empty public constructor
    }

    private String encodeLogsString() {
        StringBuffer sb = new StringBuffer();
        String item = String.format("[{\"fname\":\"%s\", \"fphone\":\"%s\", \"long_date\":%d, \"type\":%d}",
                mCallItems.get(0).name, mCallItems.get(0).phone, mCallItems.get(0).date, mCallItems.get(0).type);
        sb.append(item);
        for (int i = 1; i < mCallItems.size(); i++) {
            item = String.format(",{\"fname\":\"%s\", \"fphone\":\"%s\", \"long_date\":%d, \"type\":%d}",
                    mCallItems.get(i).name, mCallItems.get(i).phone, mCallItems.get(i).date, mCallItems.get(i).type);
            sb.append(item);
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public void syncToServer() {
        if (mCallItems.isEmpty())
            return;
        RequestParams params = new RequestParams();
        AppConf appConf = (AppConf)getActivity().getApplication();
        int uid = appConf.getUid();
        params.put("uid", uid);
        String strCallLogs = encodeLogsString();
        params.put("calllogs", strCallLogs);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_call, container, false);
        listView = (ListView)rootView.findViewById(R.id.fragment_listview_call);

        if (mCallItems.isEmpty()) {
            Cursor cursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI,
                    new String[] {"name", "type", "number", CallLog.Calls.DATE},
                    CallLog.Calls.DATE + " > " + 1, null, null);
            if (cursor.moveToNext()) {
                do {
                    String strName = cursor
                            .getString(cursor.getColumnIndex("name"));
                    String strNumber = cursor.getString(cursor
                            .getColumnIndex("number"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    long date = cursor.getLong(cursor
                            .getColumnIndex(CallLog.Calls.DATE));

                    if (strName == null) {
                        strName = "陌生人"; //PhoneNumberUtils.formatNumber(strNumber);
                    }

                    if ((strNumber == null) || (strNumber.equalsIgnoreCase("-1"))) {
                        strNumber = "Unknown";
                    }

                    mCallItems.add(new CallLogItem(strName, strNumber, type, date));

                } while (cursor.moveToNext());
            }
        }

        Collections.sort(mCallItems, new Comparator<CallLogItem>() {
            @Override
            public int compare(CallLogItem o1, CallLogItem o2) {
                    return o1.date > o2.date ? -1 :
                            o1.date < o2.date ? 1 : 0;
            }
        });

        if (adapter == null) {
            adapter = new CallAdapter(getActivity(), mCallItems);
        }

        listView.setAdapter(adapter);

        return rootView;
    }


}
