# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django import forms
from django.forms import fields
from django.forms import widgets
from django.core.exceptions import ValidationError
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from models import duration_choice


# Create your forms here.

class RegisterForm(forms.Form):
    username = fields.EmailField(
        label=u'用户邮箱',
        required=True,
        widget=widgets.EmailInput(attrs={'class': "form-control", 'placeholder': '请输入邮箱账号'}),
        error_messages={'required': '邮箱不能为空',
                        'invalid': '请输入正确的邮箱格式'},
    )
    password = fields.CharField(
        label=u'密码',
        required=True,
        widget=widgets.PasswordInput(attrs={'class': "form-control", 'placeholder': '请输入6-12位密码'}, render_value=True),
        min_length=6,
        max_length=12,
        strip=True,
        error_messages={'required': '密码不能为空!',
                        'min_length': '密码最少为6个字符',
                        'max_length': '密码最多不超过为12个字符!', },
    )
    password_again = fields.CharField(
        # render_value会对于PasswordInput，错误是否清空密码输入框内容，默认为清除，我改为不清除
        label=u'确认密码',
        widget=widgets.PasswordInput(attrs={'class': "form-control", 'placeholder': '请再次输入密码!'}, render_value=True),
        required=True,
        strip=True,
        error_messages={'required': '请再次输入密码!', }
    )

    def clean_username(self):
        # 对username的扩展验证，查找用户是否已经存在
        username = self.cleaned_data.get('username')
        users = User.objects.filter(username=username).count()
        if users:
            raise ValidationError('用户已经存在！')
        return username

    def _clean_new_password2(self):  # 查看两次密码是否一致
        password1 = self.cleaned_data.get('password')
        password2 = self.cleaned_data.get('password_again')
        if password1 and password2:
            if password1 != password2:
                # self.error_dict['pwd_again'] = '两次密码不匹配'
                raise ValidationError('两次密码不匹配！')

    def clean(self):
        # 是基于form对象的验证，字段全部验证通过会调用clean函数进行验证
        self._clean_new_password2()  # 简单的调用而已


class LoginForm(forms.Form):
    username = fields.EmailField(
        label=u'用户邮箱',
        required=True,
        widget=widgets.EmailInput(attrs={'class': "form-control", 'placeholder': '请输入用户名'}),
        error_messages={'required': '用户名不能为空!', }
    )

    password = fields.CharField(
        label=u'密码',
        widget=widgets.PasswordInput(attrs={'class': "form-control", 'placeholder': '请输入密码'}),
        required=True,
        min_length=6,
        max_length=12,
        strip=True,
        error_messages={'required': '密码不能为空!', }
    )

    def clean(self):
        username = self.cleaned_data.get('username')
        password = self.cleaned_data.get('password')
        if authenticate(username=username, password=password):
            pass
        elif User.objects.filter(username=username).first():
            raise ValidationError('密码不正确！')
        else:
            raise ValidationError('用户名不存在！')


class ProjectForm(forms.Form):
    url = forms.URLField(label='URL', max_length=512, min_length=1, widget=forms.URLInput)
    notify_emails = forms.CharField(label=u'通知邮箱', max_length=1024)
    duration = forms.ChoiceField(label=u'监控频率', widget=forms.Select, choices=duration_choice)
