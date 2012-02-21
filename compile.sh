#!/bin/sh

mvn clean package dependency:copy-dependencies
chmod +x ./target/classes/scripts/launchers/sh/launcher.sh