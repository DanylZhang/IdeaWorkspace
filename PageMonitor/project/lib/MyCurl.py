#!/usr/bin/env python
# coding:utf-8
import re
import cookielib
import urllib2
import gzip
import zlib
from StringIO import StringIO
from bs4 import BeautifulSoup
from Utility import *


class MyCurl(object):
    url = ''
    enable_proxy = False
    proxy_list = []
    cookie = cookielib.CookieJar()

    def __init__(self, enable_proxy=False, sleep=True):
        self.enable_proxy = enable_proxy
        self.sleep = sleep
        if self.enable_proxy:
            self.__init_proxy_list()

    def __init_proxy_list(self):
        self.__init_proxy_list_xici()
        self.__init_proxy_list_zhangchenchen()

    def __init_proxy_list_xici(self):
        url = "http://www.xicidaili.com/"
        url = self.url_prefix(url)
        content = self.__do_get_content(url)
        soup = BeautifulSoup(content, 'lxml')

        trs = soup.select('tr[class=""]')
        trs.extend(soup.select('tr.odd'))

        for tr in trs:
            row = "".join(str(tr).split())
            matches = re.findall(
                r'<trclass=.+?><tdclass="country"><img.+?><td>(\d+\.\d+\.\d+\.\d+)</td><td>(\d+)</td><td>.+?</td><td.+?</td><td>http</td>.+?</tr>',
                row, re.I | re.S)
            if len(matches):
                (ip, port) = matches[0]
                ip_port = "{0}:{1}".format(ip, port)
                proxy_tmp = {'http': ip_port}
                self.proxy_list.append(proxy_tmp)

    def __init_proxy_list_zhangchenchen(self):
        url = "http://7xrnwq.com1.z0.glb.clouddn.com/proxy_list.txt?v=3000"
        url = self.url_prefix(url)
        content = self.__do_get_content(url)
        self.proxy_list.extend(
            [{'http': match.group(1)} for match in re.finditer(r'(\d+\.\d+\.\d+\.\d+:\d+)', content)])

    def __do_get_content(self, url, proxy_ip=None):
        if not url.startswith('http'):
            log('__do_get_content Wrong url: ', url)
            return None
        log('__do_get_content: url = ', url)
        if self.sleep:
            random_sleep()

        # 一堆handler
        handler_list = []
        cookie_handler = urllib2.HTTPCookieProcessor(self.cookie)
        handler_list.append(cookie_handler)

        if proxy_ip is not None:
            proxy_handler = urllib2.ProxyHandler(proxy_ip)
            handler_list.append(proxy_handler)

        opener = urllib2.build_opener(*handler_list)
        # 用一堆 handler构造好的 opener安装给 urllib2模块的默认global _opener对象
        urllib2.install_opener(opener)

        request = urllib2.Request(url)
        request.add_header('Accept-encoding', 'gzip,deflate')
        request.add_header('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8')
        request.add_header('User-Agent',
                           'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
        response = urllib2.urlopen(request, timeout=180)
        content = response.read()
        encoding = response.info().get('Content-Encoding')
        if encoding == 'gzip':
            content = self.__gzip(content)
        elif encoding == 'deflate':
            content = self.__deflate(content)
        if content is None or re.search(ur'无访问权限|无效用户|毒霸网址大全', content.decode('utf-8')):
            raise Exception('访问{0}时代理IP出错'.format(url))
        return content

    def get_content_by_url(self, url, enable_proxy=None):
        self.url = self.url_prefix(url)
        self.enable_proxy = bool(enable_proxy is None) and self.enable_proxy or enable_proxy
        proxy_ip = random.choice(self.proxy_list) if self.enable_proxy else None
        try:
            content = self.__do_get_content(self.url, proxy_ip)
        except Exception, e:
            log(Exception, ':', e)
            if proxy_ip is not None:
                self.proxy_list.remove(proxy_ip)
            log('proxy_list count: ', len(self.proxy_list))
            if len(self.proxy_list) <= 0 and self.enable_proxy:
                log('已无可用代理IP，正在重新获取代理IP...')
                self.__init_proxy_list()
            return self.get_content_by_url(self.url)
        return content

    def __gzip(self, data):
        buf = StringIO(data)
        f = gzip.GzipFile(fileobj=buf)
        return f.read()

    def __deflate(self, data):
        try:
            return zlib.decompress(data, -zlib.MAX_WBITS)
        except zlib.error:
            return zlib.decompress(data)

    def url_prefix(self, url):
        if url.startswith("http"):
            return url
        if not url.startswith("http"):
            if url.startswith("//"):
                return 'http:' + url
            else:
                return 'http://' + url
        else:
            log('url_prefix Wrong url: ', url)
