package com.cn.justin.contacttools.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.justin.contacttools.R;
import com.cn.justin.contacttools.utils.CallLogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class CallAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private ArrayList<CallLogItem> mCallLogs = new ArrayList<CallLogItem>();

    public CallAdapter(Activity activity, ArrayList<CallLogItem> logItems) {
        this.activity = activity;
        mCallLogs = logItems;
    }
    @Override
    public int getCount() {
        return mCallLogs.size();
    }
    @Override
    public String getItem( int location ) {
        return  mCallLogs.get(location).name;
    }
    @Override
    public long getItemId(int position ) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( inflater == null ) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        ViewHolder viewHolder = null;
        if ( convertView == null ) {
            convertView = inflater.inflate(R.layout.listrow_call, null);
            viewHolder = new ViewHolder();
            viewHolder.call_number = (TextView) convertView.findViewById(R.id.call_number);
            viewHolder.call_name = (TextView) convertView.findViewById(R.id.call_name);
            viewHolder.call_date = (TextView) convertView.findViewById(R.id.call_date);
            viewHolder.call_type = (ImageView) convertView.findViewById(R.id.call_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.call_name.setText(mCallLogs.get(position).name);
        viewHolder.call_number.setText(mCallLogs.get(position).phone);
        Integer type = mCallLogs.get(position).type;
        int callType;
        switch (type) {
            case CallLog.Calls.OUTGOING_TYPE:
                callType = R.drawable.call_make;
                break;
            case CallLog.Calls.INCOMING_TYPE:
                callType = R.drawable.call_recv;
                break;
            default:
                callType = R.drawable.call_miss;
                break;
        }
        Drawable btnDrawable = activity.getResources().getDrawable(callType);
        viewHolder.call_type.setBackgroundDrawable(btnDrawable);

        Date date = new Date(mCallLogs.get(position).date);
        String strDate = dateFormat.format(date);
        viewHolder.call_date.setText(strDate);

        return convertView;
    }

    public class ViewHolder {
        TextView call_number;
        TextView call_name;
        ImageView call_type;
        TextView call_date;
    }
}