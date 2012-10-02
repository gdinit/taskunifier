#!/bin/sh

if [ $# != 0 ]
then
	echo "Usage: $0"
	exit 1
fi

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"

echo "Codesign application file $APPFILE"

codesign -f -s "Developer ID Application" $APPFILE

exit $?

