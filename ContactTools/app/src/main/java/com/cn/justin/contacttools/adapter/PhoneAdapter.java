package com.cn.justin.contacttools.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;


import com.cn.justin.contacttools.R;
import com.cn.justin.contacttools.utils.ContactItem;

import java.util.ArrayList;

public class PhoneAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<ContactItem> mContactItem = new ArrayList<ContactItem>();
    
    public PhoneAdapter(Activity activity, ArrayList<ContactItem> contactItems) {
        this.activity = activity;
        mContactItem = contactItems;
    }
    @Override
    public int getCount() {
        return mContactItem.size();
    }
    @Override
    public String getItem( int location ) {
        return  mContactItem.get(location).name;
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
            convertView = inflater.inflate(R.layout.listrow_phone, null);
            viewHolder = new ViewHolder();
            viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mContactItem.get(position).name);
        viewHolder.phone.setText(mContactItem.get(position).phone);
        return convertView;
    }

    public class ViewHolder {
        TextView phone;
        TextView name ;
    }
}