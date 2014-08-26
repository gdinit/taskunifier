#!/bin/sh

VERSION="1.0"
MACAPPBUNDLE=0

while getopts ":v::b" opt; do
	case $opt in
	    v)
	        VERSION="${OPTARG}"
	        ;;
		b)
			MACAPPBUNDLE=1
			;;
		\?)
			echo "Usage: $0 -v version [-b]"
            exit 1
            ;;
	esac
done

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
DMGFILE="$BASEDIR/binaries/TaskUnifier_${VERSION}/TaskUnifier_${VERSION}_mac.dmg"

if [ $MACAPPBUNDLE == 1 ]
then
    DMGFILE="$BASEDIR/binaries/TaskUnifier_${VERSION}/TaskUnifier_${VERSION}_mac_bundle.dmg"
fi

echo "Creating DMG file $DMGFILE"

rm -f $DMGFILE

hdiutil create -quiet -srcfolder $APPFILE $DMGFILE 2> /dev/null
hdiutil internet-enable -quiet -yes $DMGFILE 2> /dev/null

exit 0

