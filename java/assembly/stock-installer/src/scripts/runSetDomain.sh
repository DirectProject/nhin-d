export DIRECT_HOME=$1
export DIRECT_DOMAIN=$2
java -classpath $3 $4 $5
#echo "export DIRECT_HOME=$DIRECT_HOME" >> ~/.bashrc
#sh $DIRECT_HOME/$2/bin/setdomain.sh $3