# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django import forms
from models import duration_choice


# Create your forms here.
class UserForm(forms.Form):
    username = forms.EmailField(label=u'邮箱账号', max_length=32, min_length=3, widget=forms.EmailInput)
    password = forms.CharField(label=u'密码', max_length=32)


class ProjectForm(forms.Form):
    url = forms.URLField(label='URL', max_length=512, min_length=1, widget=forms.URLInput)
    notify_emails = forms.CharField(label=u'通知邮箱', max_length=1024)
    duration = forms.ChoiceField(label=u'监控频率', widget=forms.Select, choices=duration_choice)
