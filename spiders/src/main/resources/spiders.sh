#!/bin/sh
while :
do
	chmod 777 /data/brand/Spider/yhd/spiders.jar
	count=`ps -ef | grep -i spiders.jar | grep -v grep | wc -l`
	if [ 0 == $count ];then
		nohup ./spiders.jar -Xms1g -Xmx1g -Xss128k --spring.profiles.active=prod >> /data/brand/Spider/yhd/log/all.log 2>&1 &
	fi
	rm -f /data/brand/Spider/yhd/log/dump.bin
	pid=`jps | grep -i spiders | awk '{print $1}'`
	jmap -dump:format=b,file=/data/brand/Spider/yhd/log/dump.bin $pid
	sleep 5m
done
