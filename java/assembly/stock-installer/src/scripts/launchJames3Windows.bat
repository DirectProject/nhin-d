set JAVA_HOME=%1
cd %2
start james install
ping 123.45.67.89 -n 1 -w 5000 > nul
james start
exit 0