#!/bin/sh

if [ $# > 1 ]
then
	echo "Usage: $0 [-a]"
	exit 1
fi

MACAPPSTORE=0

while getopts ":a" opt; do
	case $opt in
		a)
			MACAPPSTORE=1
			;;
		\?)
			MACAPPSTORE=0
			;;
	esac
done

BASEDIR=`dirname $0`
BASEDIR="$BASEDIR/.."
APPFILE="$BASEDIR/temp/TaskUnifier.app"
PKGFILE="$BASEDIR/temp/TaskUnifier.app"

if [ $MACAPPSTORE == 0 ]
then

	echo "Codesign application file ""$APPFILE""..."
	
	codesign -v -f -s "Developer ID Application"  $APPFILE
	
	if [ $? != 0 ]
	then
		exit $?
	fi
	
	echo "Codesign sign all libraries..."
	
	find $APPFILE/Contents/ -type f \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose -f -s "Developer ID Application" {} \;
	
	echo "Verify all libraries have been signed..."
	
	find $APPFILE/Contents/ -type f \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose --verify {} \;
	
	exit 0
	
fi

if [ $MACAPPSTORE == 1 ]
then

	echo "Codesign application file ""$APPFILE""..."
	
	codesign -v -f -s "Developer ID Application" --entitlements mac/entitlements.plist $APPFILE
	
	if [ $? != 0 ]
	then
		exit $?
	fi
	
	echo "Codesign sign all libraries..."
	
	find $APPFILE/Contents/ -type f \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose -f -s "Developer ID Application" --entitlements mac/entitlements.plist {} \;
	
	echo "Verify all libraries have been signed..."
	
	find $APPFILE/Contents/ -type f \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose --verify {} \;
	
	exit 0
	
fi
