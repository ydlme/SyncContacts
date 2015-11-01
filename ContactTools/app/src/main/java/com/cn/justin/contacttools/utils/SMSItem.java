package com.cn.justin.contacttools.utils;

public class SMSItem {
    public String name;
    public String msg;
    public long date;
    public int type;

    public SMSItem(String name, String msg, Integer type, long date) {
        this.name = name;
        this.msg = msg;
        this.date = date;
    }

    public SMSItem() {

    }
}