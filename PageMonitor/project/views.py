# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render, redirect
from django.contrib import auth
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
from django.views.decorators.csrf import csrf_exempt

from forms import RegisterForm, LoginForm, ProjectForm
from models import Project
import datetime


# Create your views here.

# 注册
@csrf_exempt
def register(req):
    if req.method == 'GET':
        form = RegisterForm()
    elif req.method == 'POST':
        form = RegisterForm(req.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            # 判断用户是否存在
            user = auth.authenticate(username=username, password=password)
            if user:
                return render(req, 'register.html', {'form': form})

            # 添加到数据库（还可以加一些字段的处理）
            user = User.objects.create_user(username=username, password=password, email=username)
            user.save()
            # 添加到session
            req.session['username'] = username
            # 调用auth登录
            auth.login(req, user)
            # 重定向到首页
            return redirect('/')
    return render(req, 'register.html', {'form': form})


# 登陆
@csrf_exempt
def login(req):
    if req.method == 'POST':
        form = LoginForm(req.POST)
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
                return render(req, 'login.html', {'form': form})
    else:
        form = LoginForm()
    return render(req, 'login.html', {'form': form})


# 登出
@login_required
def logout(req):
    try:
        del req.session['username']
    except KeyError:
        pass
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
def edit_project(req, project_id):
    username = req.user
    # 刚进入编辑状态
    if req.method == 'GET' and project_id > 0:
        project = Project.objects.get(project_id=project_id, username=username)
        project_form = ProjectForm(
            initial={'url': project.url, 'notify_emails': project.notify_emails, 'duration': project.duration,
                     'enable': project.enable})
        return render(req, 'project/edit_project.html', {'project': project_form, 'project_id': project_id})
    # 编辑后POST提交
    elif req.method == 'POST' and project_id > 0:
        project = ProjectForm(req.POST, initial={'username': username})
        if project.is_valid():
            Project.objects.filter(project_id=project_id, username=username).update(
                url=project.cleaned_data.get('url'),
                duration=project.cleaned_data.get('duration'),
                enable=project.cleaned_data.get('enable'),
                notify_emails=project.cleaned_data.get('notify_emails')
            )
            return redirect('/list_all')
        else:
            return render(req, 'project/edit_project.html', {'project': project})
    # 新增项目POST提交
    elif req.method == 'POST':
        project = ProjectForm(req.POST, initial={'username': username})
        if project.is_valid():
            p = Project(username=username, url=project.cleaned_data['url'],
                        duration=project.cleaned_data.get('duration'),
                        enable=project.cleaned_data.get('enable'),
                        notify_emails=project.cleaned_data.get('notify_emails'),
                        last_monitor_time=datetime.datetime.now())
            p.save()
            return redirect('/list_all')
        else:
            project = ProjectForm(req.POST)
            return render(req, 'project/edit_project.html', {'project': project})
    # 新增项目
    else:
        project = ProjectForm(initial={'notify_emails': username})
        return render(req, 'project/edit_project.html', {'project': project})


# delete project
@login_required(login_url='/login')
def delete_project(req, project_id):
    username = req.user
    if project_id > 0:
        Project.objects.filter(project_id=project_id, username=username).delete()
        return redirect('/list_all')
