#!/bin/bash

BASEDIR=`dirname $0`
USERDIR=`echo ~`

java -Xmx512m -splash:"$BASEDIR/resources/images/splash_screen.png" -Dcom.leclercb.taskunifier.resource_folder="$BASEDIR/resources" -Dcom.leclercb.taskunifier.data_folder="$USERDIR/.taskunifier" -jar "$BASEDIR/TaskUnifier.jar"

