# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import datetime
import threading

from models import Project
from lib.AppMonitor import do_monitor


def monitor():
    project_list = Project.objects.filter(enable=True).all()
    tasks = []
    for project in project_list:
        project_id = project.project_id
        url = project.url
        notify_emails = project.notify_emails.split(';')
        duration = project.duration
        last_monitor_time = project.last_monitor_time
        # django框架的datetime带有时区信息，转换为不带时区信息的naive类型
        last_monitor_time = last_monitor_time.replace(tzinfo=None)
        now = datetime.datetime.now()
        delta = now - last_monitor_time
        delta_minutes = delta.seconds / 60.0
        if delta_minutes > duration:
            thread = threading.Thread(target=do_monitor,
                                      kwargs={'project_id': project_id, 'url': url, 'notify_emails': notify_emails})
            tasks.append(thread)
            thread.start()
            project.last_monitor_time = datetime.datetime.now()
            project.save()

    for task in tasks:
        task.join()
