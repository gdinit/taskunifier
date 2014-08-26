#!/bin/sh

if [ $# -gt 2 ]
then
	echo "Usage: $0 -v=version [-b]"
	exit 1
fi

VERSION="1.0"
MACAPPBUNDLE=0

while getopts ":v::b" opt; do
	case $opt in
	    v)
	        VERSION=$OPTARG
	        ;;
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
DMGFILE="$BASEDIR/binaries/TaskUnifier_$VERSION/TaskUnifier_$VERSION_mac.dmg"

if [ $MACAPPBUNDLE == 1 ]
then
    DMGFILE="$BASEDIR/binaries/TaskUnifier_$VERSION/TaskUnifier_$VERSION_mac_bundle.dmg"
fi

echo "Creating DMG file $DMGFILE"

rm -f $DMGFILE

hdiutil create -quiet -srcfolder $APPFILE $DMGFILE 2> /dev/null
hdiutil internet-enable -quiet -yes $DMGFILE 2> /dev/null

exit 0

