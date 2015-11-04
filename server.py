#!/usr/bin/python
# -*- coding: UTF-8 -*-


import tornado.httpserver
import tornado.ioloop
import tornado.options
import tornado.web
from QuerySqlTask import celery_sync_contacts
from QuerySqlTask import celery_sync_calllogs
from QuerySqlTask import celery_sync_msgs
from QuerySqlTask import celery_user_register
from QuerySqlTask import celery_user_login
import sys
import datetime
from tornado.options import define, options

define('port', default=8000, help='running', type=int)


class SyncContactsHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    def post(self):
        uid = self.get_argument('uid')
        contacts = self.get_argument('contacts')
        task = celery_sync_contacts.delay(uid, contacts)
        seconds = 0.005

        def poll_celery_task():
            if task.ready():
                result = task.result
                self.on_sync_finish(result)
            else:
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds),
                    poll_celery_task)

        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds),
            poll_celery_task)

    def on_sync_finish(self, response):
        result = '{\"contacts\": %s}' % response
        print result
        self.write(result)
        self.finish()


class UserRegisterHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    def post(self):
        name = self.get_argument('username')
        passwd = self.get_argument('passwd')
        task = celery_user_register.delay(name, passwd)
        seconds = 0.005

        def poll_celery_task():
            if task.ready():
                result = task.result
                self.on_register_finish(result)
            else:
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds),
                    poll_celery_task)

        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds),
            poll_celery_task)

    def on_register_finish(self, response):
        res = '{\'uid\':%s}' % (response)
        print res
        self.write(res)
        self.finish()


class UserLoginHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    def post(self):
        name = self.get_argument('username')
        passwd = self.get_argument('passwd')
        task = celery_user_login.delay(name, passwd)
        seconds = 0.005

        def poll_celery_task():
            if task.ready():
                result = task.result
                self.on_login_finish(result)
            else:
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds),
                    poll_celery_task)

        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds),
            poll_celery_task)

    def on_login_finish(self, response):
        res = '{\'uid\':%s}' % (response)
        print res
        self.write(res)
        self.finish()


class SyncMsgsHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    def post(self):
        uid = self.get_argument('uid')
        msgs = self.get_argument('msgs')
        s = msgs.replace('\r', '\\r').replace('\n', '\\n')
        task = celery_sync_msgs.delay(uid, s)
        seconds = 0.005

        def poll_celery_task():
            if task.ready():
                result = task.result
                self.on_sync_finish(result)
            else:
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds),
                    poll_celery_task)

        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds),
            poll_celery_task)

    def on_sync_finish(self, response):
        self.write(str(response))
        self.finish()


class SyncCalllogsHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    def post(self):
        uid = self.get_argument('uid')
        calllogs = self.get_argument('calllogs')
        task = celery_sync_calllogs.delay(uid, calllogs)
        seconds = 0.005

        def poll_celery_task():
            if task.ready():
                result = task.result
                self.on_sync_finish(result)
            else:
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds),
                    poll_celery_task)

        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds),
            poll_celery_task)

    def on_sync_finish(self, response):
        self.write(str(response))
        self.finish()


if __name__ == '__main__':
    reload(sys)
    sys.setdefaultencoding('utf-8')
    print "starting server on 8000"
    tornado.options.parse_command_line()
    app = tornado.web.Application(handlers=[
        (r'/login', UserLoginHandler),
        (r'/register', UserRegisterHandler),
        (r'/sync_contacts', SyncContactsHandler),
        (r'/sync_calllogs', SyncCalllogsHandler),
        (r'/sync_msgs', SyncMsgsHandler)])
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(options.port)
    tornado.ioloop.IOLoop.instance().start()
