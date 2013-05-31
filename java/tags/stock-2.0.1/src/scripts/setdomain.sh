#!/bin/sh -x
export DIRECT_DOMAIN=$1
ant -q -f $DIRECT_HOME/james-2.3.2/bin/set-domain.xml
echo "Domain set to $1"
echo "Postmaster set to postmaster@$1"
