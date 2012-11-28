#!/bin/sh

if [ $# != 1 ]
then
	echo "Usage: $0 version"
	exit 1
fi

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
PKGFILE="$BASEDIR/binaries/TaskUnifier_$1/TaskUnifier_$1_mac.pkg"

echo "Creating PKG file $PKGFILE"

rm -f $PKGFILE

productbuild --component $APPFILE /Applications --sign "3rd Party Mac Developer Installer: Benjamin Leclerc" $PKGFILE

exit 0

