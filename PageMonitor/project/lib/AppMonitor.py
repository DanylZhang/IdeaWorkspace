#!/usr/bin/env python
# coding:utf-8
import difflib
from lxml.html.diff import htmldiff, html_annotate
from MyCurl import *
import Utility
import SendEmail


# 得到指定路径下最新一次爬取的html文件名
def get_last_html_name(path='./', project_id=0):
    file_name_list = os.listdir(path)
    file_name_list = filter(
        lambda file_name: re.search(r'^' + str(project_id) + r'-\d+(-\d+){5}\.html', file_name),
        file_name_list)
    file_name_list.sort()
    if len(file_name_list) > 0:
        return file_name_list.pop()
    else:
        log('首次执行，无法比较，请再次执行')
        exit(0)


# 得到指定路径下最近两次爬取的html文件名
def get_last2_html_names(path='./', project_id=0):
    file_name_list = os.listdir(path)
    file_name_list = filter(
        lambda file_name: re.search(r'^' + str(project_id) + r'-\d+(-\d+){5}\.html', file_name),
        file_name_list)
    file_name_list.sort()
    if len(file_name_list) > 1:
        return {'current_html_name': file_name_list[-1], 'last_html_name': file_name_list[-2]}
    else:
        log('首次执行，无法比较，请再次执行')
        exit(0)


def do_monitor(project_id=0, url='', notify_emails=[]):
    # 必须根据脚本运行时的BASE_DIR动态获取绝对路径，否则可能没有写文件权限
    # 此时BASE_DIR在app project文件夹下，
    path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '/tmp/'

    # step 1:将本次爬取的html保存到文档
    my_curl = MyCurl(enable_proxy=False, sleep=False)
    html = my_curl.get_content_by_url(url)
    # 先解析成soup对象
    current_soup = BeautifulSoup(html, 'lxml')
    # 保存到html文件
    current_html_file = open(
        path + "{project_id}-{time}.html".format(project_id=project_id, time=time.strftime('%Y-%m-%d-%H-%M-%S')),
        'wb')
    if current_html_file is not None:
        current_html_file.write(current_soup.prettify(encoding='UTF-8'))
        current_html_file.close()

    # step 2:得到最近两次爬取的html文件
    tmp_file_names = get_last2_html_names(path=path, project_id=project_id)
    log('将要进行对比的两个文件名：', tmp_file_names)
    current_html_name = tmp_file_names['current_html_name']
    last_html_name = tmp_file_names['last_html_name']

    # step 3:本次爬取文件md5和上次作对比，如果md5不同再进行差异分析
    current_md5 = Utility.get_file_md5(path + current_html_name)
    last_md5 = Utility.get_file_md5(path + last_html_name)
    if current_md5 == last_md5:
        log('两次爬取的 project_id={project_id},url={url} 内容相同'.format(project_id=project_id, url=url))
    else:
        log('两次爬取的 project_id={project_id},url={url} 不同'.format(project_id=project_id, url=url))
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
        diff_html_file_name1 = 'project_{project_id}_diff_html1.html'.format(project_id=project_id)
        with open(path + diff_html_file_name1, 'wb') as f:
            f.write(diff_html1.encode('UTF-8'))
            f.close()
        diff_html_file_name2 = 'project_{project_id}_diff_html2.html'.format(project_id=project_id)
        with open(path + diff_html_file_name2, 'wb') as f:
            f.write(diff_html2.encode('UTF-8'))
            f.close()
        diff_html_file_name3 = 'project_{project_id}_diff_html3.html'.format(project_id=project_id)
        with open(path + diff_html_file_name3, 'wb') as f:
            f.write(diff_html3.encode('UTF-8'))
            f.close()

        # step 5:发送提醒邮件
        html = u'<a href="{url}">{url2}</a><br/> {diff_table}'.format(url=url, url2=url, diff_table=diff_table)
        status = SendEmail.send_mail(to_email=notify_emails, html=html, attachments=[path + diff_html_file_name1,
                                                                                     path + diff_html_file_name2,
                                                                                     path + diff_html_file_name3])
        if status is True:
            log("内容不同，提醒邮件发送成功")
        else:
            log(status)
