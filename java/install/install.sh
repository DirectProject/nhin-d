#!/bin/sh -x
if [ $# -gt 0 ] ; then
	export DIRECT_INSTALL_DIR=$1
else
	export DIRECT_INSTALL_DIR=build
fi
if [ ! -d $DIRECT_INSTALL_DIR ]; then
	echo "Installation directory $DIRECT_INSTALL_DIR does not exist"
	exit 1
fi
if [ -d $DIRECT_INSTALL_DIR/james-2.3.2 -o -e $DIRECT_INSTALL_DIR/james-2.3.2 ]; then
	echo "Can't install to $DIRECT_INSTALL_DIR - file or directory exists"
	exit 1
else
	echo "Installing Direct in $1"
	export DIRECT_INSTALL_CD=`pwd`
	ant -f src/scripts/build.xml
fi
