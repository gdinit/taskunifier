#!/bin/sh

if [ $# != 1 ]
then
	echo "Usage: $0 version"
	exit 1
fi

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
TARFILE="$BASEDIR/binaries/TaskUnifier_$1/mas/TaskUnifier_$1_mac.tar"
PKGFILE="$BASEDIR/binaries/TaskUnifier_$1/mas/TaskUnifier_$1_mac.pkg"

echo "Creating DMG file $DMGFILE"

mkdir $BASEDIR/temp

tar -C $BASEDIR/temp -xf $TARFILE

rm -f $DMGFILE

productbuild --component $APPFILE /Applications --sign "Developer ID Application" $PKGFILE

rm -rf $BASEDIR/temp

exit 0

