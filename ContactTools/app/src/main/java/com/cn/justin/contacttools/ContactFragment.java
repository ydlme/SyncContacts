package com.cn.justin.contacttools;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cn.justin.contacttools.adapter.PhoneAdapter;
import com.cn.justin.contacttools.utils.ContactItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.Header;

public class ContactFragment extends BaseFragment {


    private ArrayList<ContactItem> mContactItem = new ArrayList<ContactItem>();
    private ListView listView;
    PhoneAdapter adapter = null;
    private final String API = "sync_contacts";


    private String encodeContactString() {
        StringBuffer sb = new StringBuffer();
        String item = String.format("[{\"fname\":\"%s\", \"fphone\":\"%s\"}",
                mContactItem.get(0).name, mContactItem.get(0).phone);
        sb.append(item);
        for (int i = 1; i < mContactItem.size(); i++) {
            item = String.format(",{\"fname\":\"%s\", \"fphone\":\"%s\"}",
                    mContactItem.get(i).name, mContactItem.get(i).phone);
            sb.append(item);
        }
        sb.append("]");

        return sb.toString();
    }

    private void addContacts(JSONArray contacts){
        if (contacts == null || contacts.length() == 0)
            return;
        ContentResolver resolver = getActivity().getContentResolver();

        for (int i = 0; i< contacts.length(); i++) {
            ContentValues values = new ContentValues();
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            long contactId = ContentUris.parseId(resolver.insert(uri, values));
            try {
                uri = Uri.parse("content://com.android.contacts/data");
                JSONObject obj = contacts.getJSONObject(i);
                String name = obj.getString("fname");
                String phone = obj.getString("fphone");
                values.put("raw_contact_id", contactId);
                values.put("mimetype", "vnd.android.cursor.item/name");
                values.put("data2", name);
                resolver.insert(uri, values);

                // 添加电话
                values.clear();
                values.put("raw_contact_id", contactId);
                values.put("mimetype", "vnd.android.cursor.item/phone_v2");
                values.put("data2", "2");
                values.put("data1", phone);
                resolver.insert(uri, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean decodeContactsJson(JSONArray contacts) {
        if (contacts == null || contacts.length() == 0) {
            return false;
        }

        for (int i = 0; i < contacts.length(); i++) {
            try {
                JSONObject obj = contacts.getJSONObject(i);
                String name = obj.getString("fname");
                String phone = obj.getString("fphone");
                mContactItem.add(new ContactItem(name, phone));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @Override
    public void syncToServer() {
        if (mContactItem.isEmpty())
            return;
        RequestParams params = new RequestParams();
        AppConf appConf = (AppConf)getActivity().getApplication();
        int uid = appConf.getUid();
        params.put("uid", uid);
        String strContact = encodeContactString();
        params.put("contacts", strContact);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = String.format("%s%s", ((AppConf)getActivity().getApplication()).getHostIP(), API);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                getActivity().setTitle("同步完成");
                String str = null;
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                JSONObject obj = null;
                try {
                    obj = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (decodeContactsJson(obj.getJSONArray("contacts"))) {
                        adapter.notifyDataSetChanged();
                        addContacts(obj.getJSONArray("contacts"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                getActivity().setTitle("同步失败");
            }
        });
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = (ListView)rootView.findViewById(R.id.fragment_listview_contact);
        if (mContactItem.isEmpty()) {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                mContactItem.add(new ContactItem(name, phoneNumber));
            }
            phones.close();
        }

        if (adapter == null) {
            adapter = new PhoneAdapter(getActivity(), mContactItem);
        }

        listView.setAdapter(adapter);

        return rootView;
    }


}
