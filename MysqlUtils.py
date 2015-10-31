#!/usr/bin/python
# -*- coding: UTF-8 -*-

import MySQLdb
import sys


def mysql_client_connect():
    conn = None
    reload(sys)
    sys.setdefaultencoding('utf-8')
    try:
        conn = MySQLdb.connect(host='localhost', user='admin', charset='utf8',
                               passwd='mysqladmin', db='db_contacts'
                               )
    except MySQLdb.Error, e:
        print e.args
        try:
            conn.rollback()
        except MySQLdb.Error, e:
            print e.args
    finally:
        return conn
