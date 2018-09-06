#!/bin/bash

path=/data/brand/Spider/yhd/
jar=spiders.jar
file=${path}${jar}

function start_program(){
    # 首先切换当前执行目录，因为crontab的默认执行目录是家目录
    cd ${path}
    if [ -f ${file} ]; then
        chmod 777 ${file}
    	count=`ps -ef | grep -i ${jar} | grep -v grep | wc -l`
    	if [ 0 == ${count} ]; then
    		nohup java -jar ${jar} -Xms512m -Xmx512m -Xss128k --spring.profiles.active=prod >> ${path}log/all.log 2>&1 &
    		echo "${jar} started"
    	else
    	    echo "the ${jar} process already exist!"
    	fi
    	pid=`jps -l | grep -i spiders | awk '{print $1}'`
    	# prevent linux oom killer
    	echo -17 > /proc/${pid}/oom_adj
    	rm -f ${path}log/${pid}.hprof
    	# jmap -dump:live,format=b,file=${path}log/${pid}.hprof ${pid}
    fi
}

function stop_program(){
	echo "Stopping program ..."

	count=`jps -l | grep -i ${jar} | grep -v grep | wc -l`
	if [ 0 == ${count} ]; then
	    echo "no ${jar} to stop"
	else
	    jps -l | grep -i ${jar} | grep -v grep | awk '{print $1}' | xargs kill -9
	    echo "stop ${count} ${jar}"
	fi

    count=`ps aux | grep -i 'phantomjs.*phantomjsdriver.log' | grep -v grep | wc -l`
    if [ 0 == ${count} ]; then
        echo "no phantomjs to stop"
    else
        ps aux | grep -i 'phantomjs.*phantomjsdriver.log' | grep -v grep | awk '{print $2}' | xargs kill -9
        echo "stop ${count} phatomjs"
    fi

    echo "Stop finish"
}

ACTION=$1
case "${ACTION}" in
    start)
        start_program
    ;;
    stop)
        stop_program
    ;;
    restart)
        stop_program
        start_program
    ;;
    *)
        start_program
    ;;
esac

exit 0