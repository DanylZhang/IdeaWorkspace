# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models
from django.contrib.auth.models import User

# Create your models here.

duration_choice = (
    (1, 'one minute'), (5, 'five minutes'), (10, 'ten minutes'), (30, 'half hour'),
    (60, 'one hour'), (720, 'half day'), (1440, 'one day'),
    (4320, 'three days'), (10080, 'one week'), (43200, 'one month')
)

enable_choice = ((0, '关闭'), (1, '开启'))


class Project(models.Model):
    project_id = models.AutoField(u'项目ID', primary_key=True)
    username = models.EmailField(u'用户ID', max_length=64)
    url = models.URLField(u'监控链接', max_length=512)
    notify_emails = models.CharField(u'通知Email', max_length=1024)
    duration = models.SmallIntegerField(u'监控频率', choices=duration_choice)
    enable = models.BooleanField(u'项目状态', choices=enable_choice)
    last_monitor_time = models.DateTimeField(u'最近一次监控时间')
