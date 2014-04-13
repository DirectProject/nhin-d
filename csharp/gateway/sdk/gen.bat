@echo off
set bin=C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include

midl "%bin%\msado15.idl" /I "C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include"
midl "%bin%\cdosys.idl" /I "C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include"
midl "%bin%\seo.idl" /I "C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include"