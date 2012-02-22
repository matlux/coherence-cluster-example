#!/bin/sh


DEPLOY_ENV=PROD
TMPz=`pwd`/`dirname $0`
APP_HOME=`echo $TMPz | sed s:/bin::`

echo APP_HOME=$APP_HOME

. $APP_HOME/conf/common.properties

if [ "$1" = "stopall" ]; then
	LIST_PID_FILES=`find $TARGETDIR/tmp/pids -name "*.pid"`
	LIST_PIDS=`find target/tmp/pids/ -name "*.pid" -exec cat {} \;`
	echo LIST_PIDS=$LIST_PIDS;
	for pid in $LIST_PIDS ; do
		kill -9 $pid
	done
	
	for file in $LIST_PID_FILES ; do
		rm $file
	done
	
	
	exit
fi

if [ "$2" = "stop" ]; then
	kill -9 `cat $TARGETDIR/tmp/pids/$1.pid`
	exit
fi


. $APP_HOME/conf/$1.properties

echo OPTIONS=$OPTIONS

echo CLASSPATH=$CLASSPATH




if [ "$DAEMON" = "true" ]; then
	mkdir -p $TARGETDIR/tmp/pids
	java -cp $CLASSPATH $OPTIONS $CLASS2LAUNCH $ARGS &
	echo $! > $TARGETDIR/tmp/pids/$1.pid

else

	java -cp $CLASSPATH $OPTIONS $CLASS2LAUNCH $ARGS
fi


