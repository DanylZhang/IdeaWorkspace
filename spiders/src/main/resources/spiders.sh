#!/bin/bash

path=/data/brand/Spider/yhd/
jar=spiders.jar
file=${path}${jar}

if [ -f ${file} ]; then
    chmod 777 ${file}
	count=`ps -ef | grep -i ${jar} | grep -v grep | wc -l`
	if [ 0 == ${count} ];then
		nohup ./${jar} -Xms512m -Xmx512m -Xss128k --spring.profiles.active=prod >> ${path}log/all.log 2>&1 &
	fi
	pid=`jps | grep -i spiders | awk '{print $1}'`
	# prevent linux oom killer
	echo -17 > /proc/${pid}/oom_adj
	rm -f ${path}log/${pid}.hprof
	# jmap -dump:live,format=b,file=${path}log/${pid}.hprof ${pid}
fi