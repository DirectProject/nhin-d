#!/bin/sh -x

sed -i "s/<servername>localhost<\/servername>/<servername>$1<\/servername>/g" $DIRECT_HOME/james-2.3.2/apps/james/SAR-INF/config.xml
sed -i "s/<postmaster>Postmaster@localhost<\/postmaster>/<postmaster>postmaster@$1<\/postmaster>/g" $DIRECT_HOME/james-2.3.2/apps/james/SAR-INF/config.xml

echo "Domain set to $1"
echo "Postmaster set to postmaster@$1"
