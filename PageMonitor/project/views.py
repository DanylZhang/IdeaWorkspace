# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render, redirect
from django.contrib import auth
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
# 第四个是 auth中用户权限有关的类。auth可以设置每个用户的权限。
from django.views.decorators.csrf import csrf_exempt

from forms import UserForm, ProjectForm
from models import Project


# Create your views here.


# 注册
@csrf_exempt
def register(req):
    context = {}
    if req.method == 'POST':
        form = UserForm(req.POST)
        if form.is_valid():
            # 获得表单数据
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']

            # 判断用户是否存在
            user = auth.authenticate(username=username, password=password)
            if user:
                context['userExit'] = True
                return render(req, 'register.html', context)

            # 添加到数据库（还可以加一些字段的处理）
            user = User.objects.create_user(username=username, password=password, email=username)
            user.save()

            # 添加到session
            req.session['username'] = username
            # 调用auth登录
            auth.login(req, user)
            # 重定向到首页
            return redirect('/login')
    else:
        context = {'isLogin': False}
    # 将req 、页面 、以及context{}（要传入html文件中的内容包含在字典里）返回
    return render(req, 'register.html', context)


# 登陆
@csrf_exempt
def login(req):
    context = {}
    if req.method == 'POST':
        form = UserForm(req.POST)
        if form.is_valid():
            # 获取表单用户密码
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']

            # 获取的表单数据与数据库进行比较
            user = authenticate(username=username, password=password)
            if user:
                # 比较成功，跳转index
                auth.login(req, user)
                req.session['username'] = username
                return redirect('/list_all')
            else:
                # 比较失败，还在login
                context = {'isLogin': False, 'pawd': False}
                return render(req, 'login.html', context)
    else:
        context = {'isLogin': False, 'pswd': True}
    return render(req, 'login.html', context)


# 登出
@login_required
def logout(req):
    # 清理cookie里保存username
    auth.logout(req)
    return redirect('/login')


# index
@login_required(login_url='/login')
def index(req):
    if req.user.is_authenticated():
        return redirect('/list_all')
    else:
        return redirect('/login')


# project list
@login_required(login_url='/login')
def list_all(req):
    # 清理cookie里保存username
    if req.user.is_authenticated():
        username = req.user
        projects = Project.objects.filter(username=username)
        return render(req, 'project/list.html', {'projects': projects})
    else:
        return redirect('/login')


# add project
@login_required(login_url='/login')
def add_project(req):
    username = req.user
    if req.method == 'POST':
        project = ProjectForm(req.POST, initial={'username': username})
        if project.is_valid():
            p = Project(username=username, url=project.cleaned_data['url'],
                        duration=project.cleaned_data['duration'], notify_emails=project.cleaned_data['notify_emails'],
                        last_curl_time='1970-01-01 00:00:00')
            p.save()
            return redirect('/list_all')
        else:
            project = ProjectForm(req.POST)
            return render(req, 'project/add_project.html', {'project': project})
    else:
        project = ProjectForm(initial={'notify_emails': username})
        return render(req, 'project/add_project.html', {'project': project})
