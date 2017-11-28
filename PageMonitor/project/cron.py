# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import time

from models import Project
from lib.AppMonitor import do_monitor
from gevent import monkey

#monkey.patch_all()
# 使用gevent实现并发,底层是libev并发库
import gevent


def monitor():
    project_list = Project.objects.all()
    f = open('./test.log', 'wb')
    f.writelines(project_list)
    f.close()
    task = []
    for project in project_list:
        url = project.url
        notify_email = project.notify_emails.split(',')
        duration = project.duration
        last_monitor_time = project.last_monitor_time
        last_monitor_time_stamp = time.mktime(time.strptime(last_monitor_time, "%Y-%m-%d %H:%M:%S"))
        if time.time() - last_monitor_time_stamp / 60 > duration:
            task.append(gevent.spawn(do_monitor, url, notify_email))

    gevent.joinall(task)
