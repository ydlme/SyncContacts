#!/usr/bin/python
# -*- coding: UTF-8 -*-


import tornado.httpserver
import tornado.ioloop
import tornado.options
import tornado.web
import sys
from QuerySqlTask import celery_query_contacts
from QuerySqlTask import celery_query_msgs
from QuerySqlTask import celery_query_calllogs
import os
import datetime
from tornado.options import define, options

define('port', default=8001, help='running', type=int)


class WebContactsHandler(tornado.web.RequestHandler):

    def get(self):
        user = self.get_argument('user')
        passwd = self.get_argument('passwd')
        user_info = 'user=%s&passwd=%s' % (user, passwd)
        task = celery_query_contacts.delay(user, passwd)
        seconds = 0.005

        def poll_celery_task(self):
            if task.ready():
                result = task.result
                self.render('contacts.html', contacts=result, user_info=user_info)
            else:
                func = poll_celery_task(self)
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds), func)

        func = poll_celery_task(self)
        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds), func)


class WebMsgsHandler(tornado.web.RequestHandler):

    def get(self):
        user = self.get_argument('user')
        passwd = self.get_argument('passwd')
        user_info = 'user=%s&passwd=%s' % (user, passwd)
        task = celery_query_msgs.delay(user, passwd)
        seconds = 0.005

        def poll_celery_task(self):
            if task.ready():
                result = task.result
                self.render('msgs.html', contacts=result, user_info=user_info)
            else:
                func = poll_celery_task(self)
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds), func)

        func = poll_celery_task(self)
        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds), func)


class WebCalllogsHandler(tornado.web.RequestHandler):

    def get(self):
        user = self.get_argument('user')
        passwd = self.get_argument('passwd')
        user_info = 'user=%s&passwd=%s' % (user, passwd)
        task = celery_query_calllogs.delay(user, passwd, 1)
        seconds = 0.005

        def poll_celery_task(self):
            if task.ready():
                result = task.result
                self.render('calllogs.html', contacts=result, user_info=user_info)
            else:
                func = poll_celery_task(self)
                tornado.ioloop.IOLoop.instance().add_timeout(
                    datetime.timedelta(seconds=seconds), func)

        func = poll_celery_task(self)
        tornado.ioloop.IOLoop.instance().add_timeout(
            datetime.timedelta(seconds=seconds), func)

class LoginHandler(tornado.web.RequestHandler):

    def get(self):
        self.render('login.html')


if __name__ == '__main__':
    reload(sys)
    sys.setdefaultencoding('utf-8')
    print "starting server on 8001"
    tornado.options.parse_command_line()
    app = tornado.web.Application(handlers=[
        (r'/contacts', WebContactsHandler),
        (r'/msgs', WebMsgsHandler),
        (r'/calllogs', WebCalllogsHandler),
        (r'/', LoginHandler)],
        template_path=os.path.join(os.path.dirname(__file__), 'templates'),
        static_path=os.path.join(os.path.dirname(__file__), 'static'),
        debug=True
    )
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(options.port)
    tornado.ioloop.IOLoop.instance().start()
