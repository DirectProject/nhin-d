#!/bin/sh -x
if [ $# -gt 0 ] ; then
	export DIRECT_INSTALL_DIR_REL=$1
else
	export DIRECT_INSTALL_DIR_REL=build
	if [ ! -d $DIRECT_INSTALL_DIR_REL -a ! -e $DIRECT_INSTALL_DIR_REL ]; then
		mkdir $DIRECT_INSTALL_DIR_REL
	else
		if [ ! -d $DIRECT_INSTALL_DIR_REL ]; then
			echo "Can't make directory $DIRECT_INSTALL_DIR_REL"
			exit 1
		fi
	fi
fi
if [ ! -d $DIRECT_INSTALL_DIR_REL ]; then
	echo "Installation directory $DIRECT_INSTALL_DIR_REL does not exist"
	exit 1
fi
export DIRECT_INSTALL_DIR=`cd $DIRECT_INSTALL_DIR_REL; pwd`
export DIRECT_INSTALL_PACKAGE_DIR=${DIRECT_INSTALL_DIR}/james-2.3.2
if [ -d $DIRECT_INSTALL_PACKAGE_DIR -o -e $DIRECT_INSTALL_PACKAGE_DIR ]; then
	echo "Can't install to $DIRECT_INSTALL_PACKAGE_DIR - file or directory exists"
	exit 1
else
	echo "Installing Direct in $DIRECT_INSTALL_PACKAGE_DIR"
	export DIRECT_INSTALL_CD=`pwd`
	ant -f src/scripts/build.xml
fi
