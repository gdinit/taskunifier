#!/bin/sh

if [ $# != 1 ]
then
	echo "Usage: $0 version"
	exit 1
fi

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
DMGFILE="$BASEDIR/binaries/TaskUnifier_$1/TaskUnifier_$1_mac.dmg"

echo "Creating DMG file $DMGFILE"

rm -f $DMGFILE

hdiutil create -quiet -srcfolder $APPFILE $DMGFILE 2> /dev/null
hdiutil internet-enable -quiet -yes $DMGFILE 2> /dev/null

exit 0

