package com.cn.justin.contacttools.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;


import com.cn.justin.contacttools.R;
import com.cn.justin.contacttools.utils.SMSItem;

import java.lang.ref.PhantomReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SMSAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<SMSItem> mSMSItem = new ArrayList<SMSItem>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");

    public SMSAdapter(Activity activity, ArrayList<SMSItem> smsItems) {
        this.activity = activity;
        mSMSItem = smsItems;
    }
    @Override
    public int getCount() {
        return mSMSItem.size();
    }
    @Override
    public String getItem( int location ) {
        return  mSMSItem.get(location).name;
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
            convertView = inflater.inflate(R.layout.listrow_sms, null);
            viewHolder = new ViewHolder();
            viewHolder.sms_content = (TextView) convertView.findViewById(R.id.sms_content);
            viewHolder.sms_name = (TextView) convertView.findViewById(R.id.sms_name);
            viewHolder.sms_date = (TextView) convertView.findViewById(R.id.sms_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Date d = new Date(mSMSItem.get(position).date);
        String strDate = dateFormat.format(d);

        viewHolder.sms_name.setText(mSMSItem.get(position).name);
        viewHolder.sms_content.setText(mSMSItem.get(position).msg);
        viewHolder.sms_date.setText(strDate);
        return convertView;
    }

    public class ViewHolder {
        TextView sms_content;
        TextView sms_name;
        TextView sms_date;
    }
}