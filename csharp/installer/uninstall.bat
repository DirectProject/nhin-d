@echo off

@echo Stopping services...
net stop DirectDnsResponderSvc
@echo Dropping the DNS Responder Service...
sc delete DirectDnsResponderSvc
iisreset

@echo Unregistering the Gateway...
call unregisterGateway.bat
