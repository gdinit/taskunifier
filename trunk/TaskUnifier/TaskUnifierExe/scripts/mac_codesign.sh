#!/bin/sh

if [ $# -gt 1 ]
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

export CODESIGN_ALLOCATE="/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/codesign_allocate"

if [ $MACAPPSTORE == 0 ]
then

    echo "NORMAL MAC VERSION"

    echo "Codesign application file ""$APPFILE""..."
	
	codesign -v -f -s "Developer ID Application: Benjamin Leclerc"  $APPFILE
	
	if [ $? != 0 ]
	then
		exit $?
	fi
	
	echo "Codesign sign all libraries..."
	
	find $APPFILE/Contents/ \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose -f -s "Developer ID Application: Benjamin Leclerc" {} \;
	
	echo "Verify all libraries have been signed..."
	
	find $APPFILE/Contents/ \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose --verify {} \;
	
	exit 0
	
fi

if [ $MACAPPSTORE == 1 ]
then

    echo "MAC APP STORE VERSION"

	echo "Codesign application file ""$APPFILE""..."
	
	codesign -v -f -s "3rd Party Mac Developer Application: Benjamin Leclerc" --entitlements $BASEDIR/scripts/mac/entitlements.plist $APPFILE
	
	if [ $? != 0 ]
	then
		exit $?
	fi
	
	echo "Codesign sign all libraries..."
	
	find $APPFILE/Contents/ \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose -f -s "3rd Party Mac Developer Application: Benjamin Leclerc" --entitlements $BASEDIR/scripts/mac/entitlements.plist {} \;
	
	echo "Verify all libraries have been signed..."
	
	find $APPFILE/Contents/ \( -name "*.jar" -or -name "*.dylib" \) -exec codesign --verbose --verify {} \;
	
	exit 0
	
fi
