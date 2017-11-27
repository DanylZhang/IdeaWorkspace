# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models
from django.contrib.auth.models import User

# Create your models here.

duration_choice = (
    (1, '5 minutes'), (2, '10 minutes'), (3, 'half hour'),
    (4, 'one hour'), (5, 'half day'), (6, 'one day'),
    (7, 'three days'), (8, 'one week'), (9, 'one month')
)


class Project(models.Model):
    project_id = models.AutoField(u'项目ID', primary_key=True)
    username = models.EmailField(u'用户ID', max_length=64)
    url = models.URLField(u'监控链接', max_length=512)
    notify_emails = models.CharField(u'通知Email', max_length=1024)
    duration = models.SmallIntegerField(u'间隔', choices=duration_choice)
    last_curl_time = models.DateTimeField(u'最近一次监控时间')
