#!/bin/sh

#kill -9 `cat /tmp/server.pid`

if [ "$1" = "force" ]; then
    for pid in `ps -ef | grep launcher | grep -v grep | awk 'BEGIN{OFS=FS=" "} {print $2}'` ;do kill $pid ; done
    exit
fi

./target/classes/scripts/launchers/sh/launcher.sh stopall
