#!/bin/sh

if [ $# != 1 ]
then
	echo "Usage: $0 version [-b]"
	exit 1
fi

MACAPPBUNDLE=0

while getopts ":b" opt; do
	case $opt in
		b)
			MACAPPBUNDLE=1
			;;
		\?)
			MACAPPBUNDLE=0
			;;
	esac
done

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
DMGFILE="$BASEDIR/binaries/TaskUnifier_$1/TaskUnifier_$1_mac.dmg"

if [ $MACAPPSTORE == 1 ]
then
    DMGFILE="$BASEDIR/binaries/TaskUnifier_$1/TaskUnifier_$1_mac_bundle.dmg"
fi

echo "Creating DMG file $DMGFILE"

rm -f $DMGFILE

hdiutil create -quiet -srcfolder $APPFILE $DMGFILE 2> /dev/null
hdiutil internet-enable -quiet -yes $DMGFILE 2> /dev/null

exit 0

