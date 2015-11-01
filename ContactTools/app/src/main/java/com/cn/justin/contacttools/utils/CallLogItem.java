package com.cn.justin.contacttools.utils;

import java.util.Comparator;

/**
 * Created by justin on 10/26/15.
 */


public class CallLogItem {
    public String name;
    public String phone;
    public Integer type;
    public long date;

    public CallLogItem(String name, String phone, Integer type, long date) {
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.date = date;
    }

    public CallLogItem() {

    }
}
