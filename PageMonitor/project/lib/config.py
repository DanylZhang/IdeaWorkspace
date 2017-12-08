#!/usr/bin/env python
# coding:utf-8
import pymysql

default_config = {
    'host': '139.196.96.149',
    'port': 13306,
    'user': 'dataway-rw',
    'password': 'QqHVMhmN*8',
    'db': 'jumei',
    'charset': 'utf8mb4'
}
apollo_config = {
    'host': '127.0.0.1',
    'port': 11306,
    'user': 'apollo-rw',
    'password': 'QBT094bt',
    'db': 'apollo',
    'charset': 'utf8mb4',
    'autocommit': True
}
allsite_config = {
    'host': '127.0.0.1',
    'port': 15306,
    'user': 'apollo-rw',
    'password': 'QBT094bt',
    'db': 'all_site',
    'charset': 'utf8mb4'
}
dataway_config = {
    'host': '139.196.96.149',
    'port': 13306,
    'user': 'dataway-rw',
    'password': 'QqHVMhmN*8',
    'db': 'jumei',
    'charset': 'utf8mb4'
}
dw_entity_config = {
    'host': '127.0.0.1',
    'port': 18306,
    'user': 'qbt',
    'password': 'QBT094bt',
    'db': 'dw_entity',
    'charset': 'utf8mb4',
    'autocommit': True
}
channel_config = {
    'host': 'channel.ecdataway.com',
    'port': 3306,
    'user': 'comment_catcher',
    'password': 'cc33770880',
    'db': 'monitor',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}
