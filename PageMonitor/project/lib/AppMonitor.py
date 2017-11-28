#!/usr/bin/env python
# coding:utf-8
import difflib
from lxml.html.diff import htmldiff, html_annotate
from MyCurl import *
import Utility
import SendEmail


# 得到指定路径下最新一次爬取的html文件名
def get_last_html_name(path='./'):
    file_name_list = os.listdir(path)
    file_name_list = filter(
        lambda file_name: re.search(r'App-Store-Review-Guidelines\d+(-\d+){5}\.html', file_name),
        file_name_list)
    file_name_list.sort()
    if len(file_name_list) > 0:
        return file_name_list.pop()
    else:
        log('首次执行，无法比较，请再次执行')
        exit(0)


# 得到指定路径下最近两次爬取的html文件名
def get_last2_html_names(path='./'):
    file_name_list = os.listdir(path)
    file_name_list = filter(
        lambda file_name: re.search(r'App-Store-Review-Guidelines\d+(-\d+){5}\.html', file_name),
        file_name_list)
    file_name_list.sort()
    if len(file_name_list) > 1:
        return {'current_html_name': file_name_list[-1], 'last_html_name': file_name_list[-2]}
    else:
        log('首次执行，无法比较，请再次执行')
        exit(0)


def do_monitor(url='', notify_email=[]):
    print url
    print notify_email
    path = './'

    # step 1:将本次爬取的html保存到文档
    my_curl = MyCurl(enable_proxy=False)
    html = my_curl.get_content_by_url(url)
    # 先解析成soup对象
    current_soup = BeautifulSoup(html, 'lxml')
    # 找到《指南》的正文部分
    # current_main = current_soup.select_one('#main')
    current_main = current_soup
    # 保存到html文件
    current_html_file = open("./App-Store-Review-Guidelines{time}.html".format(time=time.strftime('%Y-%m-%d-%H-%M-%S')),
                             'wb')
    if current_html_file is not None:
        current_html_file.write(current_main.prettify(encoding='UTF-8'))
        current_html_file.close()

    # step 2:得到最近两次爬取的html文件
    tmp_file_names = get_last2_html_names(path)
    log('将要进行对比的两个文件名：', tmp_file_names)
    current_html_name = tmp_file_names['current_html_name']
    last_html_name = tmp_file_names['last_html_name']

    # step 3:本次爬取文件md5和上次作对比，如果md5不同再进行差异分析
    current_md5 = Utility.get_file_md5(path + current_html_name)
    last_md5 = Utility.get_file_md5(path + last_html_name)
    if current_md5 == last_md5:
        log('两次爬取的《App-Store-Review-Guidelines》内容相同')
    else:
        log('两次爬取的《App-Store-Review-Guidelines》内容不同')
        last_main = BeautifulSoup(open(path + last_html_name), 'lxml')
        current_main = BeautifulSoup(open(path + current_html_name), 'lxml')

        # 差异对比表格
        diff_table = difflib.HtmlDiff().make_table(last_main.prettify(),
                                                   current_main.prettify(), last_html_name, current_html_name,
                                                   context=True)
        # 差异表格导出为html文档
        diff_html1 = difflib.HtmlDiff().make_file(last_main.prettify(),
                                                  current_main.prettify(), last_html_name, 'current',
                                                  context=True)
        # 未更改内容显示 灰色，更新内容部分显示为 红色
        diff_html2 = html_annotate([(last_main.prettify(), '#707070'), (current_main.prettify(), '#9932CC')],
                                   markup=lambda text, version: '<span title="%s" style="color:%s">%s</span>' % (
                                       version, version, text))
        # 对更新内容添加标记线对比显示
        diff_html3 = htmldiff(last_main.prettify(), current_main.prettify())

        # step 4:将对比结果保存为html文档
        diff_html_file_name1 = 'diff_html1.html'
        with open(path + diff_html_file_name1, 'wb') as f:
            f.write(diff_html1.encode('UTF-8'))
            f.close()
        diff_html_file_name2 = 'diff_html2.html'
        with open(path + diff_html_file_name2, 'wb') as f:
            f.write(diff_html2.encode('UTF-8'))
            f.close()
        diff_html_file_name3 = 'diff_html3.html'
        with open(path + diff_html_file_name3, 'wb') as f:
            f.write(diff_html3.encode('UTF-8'))
            f.close()

        # step 5:发送提醒邮件
        html = u'<a href="https://developer.apple.com/app-store/review/guidelines/cn/">App Store 审核指南</a><br/>' + diff_table
        status = SendEmail.send_mail(to_email=notify_email, html=html, attachments=[path + diff_html_file_name1,
                                                                                    path + diff_html_file_name2,
                                                                                    path + diff_html_file_name3])
        if status is True:
            print "内容不同，提醒邮件发送成功"
        else:
            print status
