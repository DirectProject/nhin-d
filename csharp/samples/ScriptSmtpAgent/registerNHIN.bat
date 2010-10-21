call regasm.bat SmtpServiceAgent.dll
cscript smtpreg.vbs /add 1 onArrival NHINHandler CDO.SS_SMTPOnArrivalSink "mail from=*"
cscript smtpreg.vbs /setprop 1 onarrival NHINHandler Sink ScriptName "%~dp0nhinHandler.vbs"