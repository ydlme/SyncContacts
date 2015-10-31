#!/usr/bin/python
# -* coding: UTF-8 -*-

from MysqlUtils import mysql_client_connect
from celery import Celery
import json

app = Celery(backend='amqp', broker='amqp://')


def encode_contacts_json(diff):
    if len(diff) == 0:
        return str(diff)
    str_json = '[{\'fphone\':\'%s\', \'fname\':\'%s\'}' % (diff[0][0], diff[0][1])
    for idx in (1, len(diff)):
        item = ',{\'fphone\':\'%s\', \'fname\':\'%s\'}' % (diff[idx][0], diff[idx][1])
        str_json = str_json + item
    str_json = str_json + ']'
    return str_json


def encode_calllogs_json(diff):
    if len(diff) == 0:
        return str(diff)
    str_json = '[{\'fphone\':\'%s\', \'fname\':\'%s\', \'type\':%d, \
    \'long_date\':%d}' % (diff[0][0], diff[0][1], diff[0][2], diff[0][3])
    for idx in (1, len(diff)):
        item = ',{\'fphone\':\'%s\', \'fname\':\'%s\', \'type\':%d, \
                \'long_date\':%d}' % (diff[idx][0], diff[idx][1], diff[idx][2], diff[idx][3])
        str_json = str_json + item
    str_json = str_json + ']'
    return str_json


def encode_msg_json(diff):
    if len(diff) == 0:
        return str(diff)
    str_json = '[{\'long_date\':%d, \'from_user\':\'%s\', \
            \'msg\':\'%s\'}' % (diff[0][0], diff[0][1], diff[0][2])
    for idx in range(1, len(diff)):
        item = ',{\'long_date\':%d, \'from_user\':\'%s\', \
                \'msg\':\'%s\'}' % (diff[idx][0], diff[idx][1], diff[idx][2])
        str_json = str_json + item
    str_json = str_json + ']'
    return str_json


@app.task
def celery_sync_contacts(uid, contacts):
    json_contacts = json.loads(contacts)
    size = len(json_contacts)
    conn = mysql_client_connect()
    cursor = conn.cursor()
    hashtbl = {}
    sql = 'select fphone, fname from tbl_contacts \
            where uid={uid}'.format(uid=int(uid))
    cursor.execute(sql)
    row = cursor.fetchall()
    contacts_db = []
    for i in range(len(row)):
        fphone = row[i][0]
        fname = row[i][1]
        contacts_db.append((fphone, fname))
        hashtbl[fphone] = fname

    contacts_user = []
    for i in range(size):
        item = json_contacts[i]
        fphone = item['fphone']
        fname = item['fname']
        contacts_user.append((fphone, fname))
        if fphone in hashtbl:
            continue
        else:
            hashtbl[fphone] = fname
        sql = 'insert into tbl_contacts(uid, fname, fphone) \
        values({uid}, \'{fname}\', \'{fphone}\')'.format(uid=int(uid), fname=fname, fphone=fphone)
        cursor.execute(sql)
    conn.commit()
    cursor.close()
    conn.close()
    diff = []
    if not contacts_db:
        return str(diff)
    diff.extend(list(set(contacts_db).difference(set(contacts_user))))
    return encode_contacts_json(diff)


@app.task
def celery_sync_calllogs(uid, calllogs):
    json_calllogs = json.loads(calllogs)
    size = len(json_calllogs)
    conn = mysql_client_connect()
    cursor = conn.cursor()
    hashtbl = {}
    sql = 'select fphone, fname, long_date, type from tbl_calllogs \
            where uid={uid}'.format(uid=int(uid))
    cursor.execute(sql)
    row = cursor.fetchall()
    calllogs_db = []
    for i in range(len(row)):
        fphone = row[i][0]
        fname = row[i][1]
        long_date = row[i][2]
        ftype = row[i][3]
        calllogs_db.append((fphone, fname, ftype, long_date))
        k = ''.join([str(fphone), str(long_date)])
        hashtbl[k] = 1

    calllogs_user = []
    for i in range(size):
        item = json_calllogs[i]
        fphone = item['fphone']
        fname = item['fname']
        long_date = item['long_date']
        ftype = item['type']
        calllogs_user.append((fphone, fname, ftype, long_date))
        if fphone in hashtbl:
            continue
        else:
            k = ''.join([str(fphone), str(long_date)])
            hashtbl[k] = 1
        sql = 'insert into tbl_calllogs(uid, fname, fphone, type, long_date) \
                values({uid}, \'{fname}\', \'{fphone}\', \
                {ftype}, {long_date})'.format(uid=int(uid), fname=fname, fphone=fphone, ftype=ftype, long_date=long_date)
        cursor.execute(sql)
    conn.commit()
    cursor.close()
    conn.close()
    diff = []
    if not calllogs_db:
        return str(diff)
    diff.extend(list(set(calllogs_db).difference(set(calllogs_user))))
    return encode_calllogs_json(diff)


@app.task
def celery_sync_msgs(uid, msgs):
    json_msgs = json.loads(msgs)
    size = len(json_msgs)
    conn = mysql_client_connect()
    cursor = conn.cursor()
    sql = 'select long_date, from_user, msg from \
    tbl_msgs where uid={uid}'.format(uid=int(uid))
    cursor.execute(sql)
    row = cursor.fetchall()
    hashtbl = {}
    msgs_db = []
    for i in range(len(row)):
        long_date = row[i][0]
        from_user = row[i][1]
        msgs_db.append((long_date, from_user))
        hashtbl[''.join([str(long_date), from_user])] = row[i][2]

    msgs_user = []
    for i in range(size):
        item = json_msgs[i]
        from_user = item['from_user']
        long_date = item['long_date']
        msgs_user.append((long_date, from_user))
        k = ''.join([str(long_date), from_user])
        if k in hashtbl:
            continue
        else:
            hashtbl[k] = item['msg']
        msg = item['msg']
        sql = 'insert into tbl_msgs(uid, from_user, msg, long_date) \
        values( \
        {uid}, \'{from_user}\', \'{msg}\', {long_date} \
        )'.format(uid=int(uid), from_user=from_user, msg=msg, long_date=long_date)
        cursor.execute(sql)
    conn.commit()
    cursor.close()
    conn.close()
    diffk = []
    if not msgs_db:
        return str(diffk)
    diffk.extend(list(set(msgs_db).difference(set(msgs_user))))
    diff = []
    for item in diffk:
        k = ''.join([str(item[0]), item[1]])
        diff.append((item[0], item[1], hashtbl[k]))
    return encode_msg_json(diff)


@app.task
def celery_user_register(name, passwd):
    sql = 'select uid from tbl_user_info \
    where username=\'{name}\' limit 1;'.format(name=name)
    conn = mysql_client_connect()
    cursor = conn.cursor()
    cursor.execute(sql)
    row = cursor.fetchone()
    if row and row[0]:
        return 0
    sql = 'insert into tbl_user_info(username, passwd) \
    values(\'{name}\',\'{passwd}\');'.format(name=name, passwd=passwd)
    cursor.execute(sql)
    conn.commit()
    sql = 'select uid from tbl_user_info where \
    username=\'{name}\' limit 1;'.format(name=name)
    cursor.execute(sql)
    row = cursor.fetchone()
    cursor.close()
    conn.close()
    return row[0]


@app.task
def celery_user_login(name, passwd):
    sql = 'select uid from tbl_user_info \
    where username=\'{name}\' and passwd=\'{passwd}\''.format(name=name, passwd=passwd)
    conn = mysql_client_connect()
    cursor = conn.cursor()
    cursor.execute(sql)
    row = cursor.fetchone()
    uid = 0
    if row and row[0]:
        uid = row[0]
    cursor.close()
    conn.close()
    return uid
