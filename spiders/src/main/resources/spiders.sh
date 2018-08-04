#!/bin/sh
while :
do
	chmod 777 /data/brand/Spider/yhd/spiders.jar
	count=`ps -ef | grep -i spiders.jar | grep -v grep | wc -l`
	if [ 0 == ${count} ];then
		nohup ./spiders.jar -Xms1g -Xmx1g -Xss128k --spring.profiles.active=prod >> /data/brand/Spider/yhd/log/all.log 2>&1 &
	fi
	pid=`jps | grep -i spiders | awk '{print $1}'`
	# prevent linux oom killer
	echo -17 > /proc/${pid}/oom_adj
	rm -f /data/brand/Spider/yhd/log/${pid}.hprof
	jmap -dump:live,format=b,file=/data/brand/Spider/yhd/log/${pid}.hprof ${pid}
	sleep 3m
done
