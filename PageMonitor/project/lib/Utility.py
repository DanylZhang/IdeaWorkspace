#!/usr/bin/env python
# coding:utf-8
import hashlib
import os
import random
import time
import math

from config import *


# 简单的测试一个字符串的MD5值
def get_str_md5(src):
    md5 = hashlib.md5()
    md5.update(src)
    return md5.hexdigest()


# 大文件的MD5值
def get_file_md5(filename):
    if not os.path.isfile(filename):
        return
    md5 = hashlib.md5()
    f = file(filename, 'rb')
    while True:
        b = f.read(1024 * 1024)
        if not b:
            break
        md5.update(b)
    f.close()
    return md5.hexdigest()


def get_filepath_sha1(filepath):
    with open(filepath, 'rb') as f:
        sha1 = hashlib.sha1()
        sha1.update(f.read())
        return sha1.hexdigest()


def get_filepath_md5(filepath):
    with open(filepath, 'rb') as f:
        md5 = hashlib.md5()
        md5.update(f.read())
        return md5.hexdigest()


# 控制台输出信息
def log(*info):
    print '[{time}] Info ['.format(time=time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())),
    for msg in info:
        print msg,
    print ']'


# 随机暂停n秒
def random_sleep():
    sleep_secs = random.randint(3, 10)
    log('random sleep {sleep_secs}s'.format(sleep_secs=sleep_secs))
    time.sleep(sleep_secs)
    return sleep_secs


def get_cursor(config, dict_cursor=False, auto_commit=True):
    connect = pymysql.connect(**config)
    if auto_commit is True:
        connect.autocommit(True)
    else:
        connect.autocommit(False)
    if dict_cursor is True:
        return connect.cursor(cursor=pymysql.cursors.DictCursor)
    else:
        return connect.cursor(cursor=pymysql.cursors.Cursor)


def query_scalar(config, sql):
    cursor = get_cursor(config=config)
    cursor.execute(sql)
    row = cursor.fetchone()
    scalar = row[0]
    return scalar


def query_column(config, sql):
    cursor = get_cursor(config=config)
    cursor.execute(sql)
    rows = cursor.fetchall()
    column = [(row[0]) for row in rows]
    return column


def query_one(config, sql):
    cursor = get_cursor(config=config)
    cursor.execute(sql)
    return cursor.fetchone()


def query_one_dict(config, sql):
    cursor = get_cursor(config=config, dict_cursor=True)
    cursor.execute(sql)
    return cursor.fetchone()


def query_all(config, sql):
    cursor = get_cursor(config=config)
    cursor.execute(sql)
    return list(cursor.fetchall())


def query_all_dict(config, sql):
    cursor = get_cursor(config=config, dict_cursor=True)
    cursor.execute(sql)
    return list(cursor.fetchall())


def update(config, sql):
    connect = pymysql.connect(**config)
    connect.autocommit(False)
    rows_affected = 0
    try:
        with connect.cursor() as cursor:
            """
            如果autocommit设置为False，则会执行sql，但不提交更改。
            即：如果sql对表有更改操作，execute会返回成功影响的行数rows_affected，相当于尝试执行sql
                但由于autocommit为False，数据库并不会真正保存sql对数据的更改结果，
                直到执行connect.commit()命令，才会保存更改，常用在事务提交。
            """
            rows_affected = cursor.execute(sql)
        connect.commit()
    except Exception, e:
        connect.rollback()
        log("update error:{exception}, sql = '{sql}'".format(exception=e, sql=sql))
    finally:
        connect.close()
        return rows_affected


def insert(config, sql, items):
    connect = pymysql.connect(**config)
    # 更新数据的操作最好禁用自动提交
    connect.autocommit(False)
    rows_affected = 0
    try:
        with connect.cursor() as cursor:
            rows_affected = cursor.executemany(sql, items)
        connect.commit()
    except Exception, e:
        connect.rollback()
        log("insert error:{exception}, sql = '{sql}' values = {values}".format(exception=e, sql=sql, values=items))
    finally:
        connect.close()
        return rows_affected


def chunk_list(array, size, is_chunk_number=False):
    array = list(array)
    list_size = len(array)
    if list_size == 0:
        raise Exception('ValueError: list_size={list_size}'.format(list_size=list_size))

    if is_chunk_number is True:
        size = math.ceil(list_size / float(math.ceil(size)))
    chunk_size = int(size)

    if chunk_size == 0:
        raise Exception('ValueError: chunk_size={chunk_size}'.format(chunk_size=chunk_size))

    result_list = list(zip(*[iter(array)] * chunk_size))

    mod = list_size % chunk_size
    if mod > 0:
        tail = tuple(array[-mod:])
        result_list.append(tail)
    return result_list
