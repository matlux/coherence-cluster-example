#!/bin/sh

./target/classes/scripts/launchers/sh/launcher.sh cache-server
./target/classes/scripts/launchers/sh/launcher.sh cache-server2

./target/classes/scripts/launchers/sh/launcher.sh proxy
./target/classes/scripts/launchers/sh/launcher.sh proxy2
./target/classes/scripts/launchers/sh/launcher.sh mbean-connector
