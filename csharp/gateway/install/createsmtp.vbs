'
' **SAMPLE** VBS to Setup SMTP Server
' PLEASE PLEASE improve and extend
'
' Author: Umesh Madan umeshma@microsoft.com
'       

WScript.Echo "*****************************************"
WScript.Echo ""
WScript.Echo " Setting up Sample Direct Smtp Server"
WScript.Echo " NOTE: You must install the Direct Gateway Separately"
WScript.Echo ""
WScript.Echo "*****************************************"

Set setupObj = new SmtpSetup
setupObj.ParseArgs()

WScript.Echo "Setting up"
setupObj.Setup()
WScript.Echo "Done"

'
' Simple class to do setup
' Not much defensive coding, but please embrace & extend
' To get a FULL list of IIS properties you can set, you can:
'  a. Search the web :-) "IIS metabase script"
'  b. Use 'adsutil.vbs' to dump all known properties
'      cscript adsutil.vbs ENUM smtpsvc/1
'  A copy of adsutil is checked into csharp\gateway\install
'
Class SmtpSetup

    Public Instance
    Public Domain
    Public MaxMessageSize
    Public DataRoot

    Private Sub Class_Initialize()
        Instance = 1                ' Default SMTP Server instance
        Domain = ""
        MaxMessageSize = 10485760   ' 10 MB
        DataRoot = "C:\inetpub\MailRoot"                
    End Sub

    Public Sub ParseArgs()
        
        Set args = WScript.Arguments.Named
        If (args.Count = 0) Then
            PrintUsage
            Exit Sub
        End If
        
        Domain = CStr(GetArg(args, "domain", ""))
        if Domain = "" Then                    
            Err.Raise 5, "Missing domain"
        End If                

        Instance = CInt(GetArg(args, "instance", Instance))                        
        DataRoot = CStr(GetArg(args, "dataRoot", DataRoot))        
        MaxMessageSize = CLng(GetArg(args, "maxSize", MaxMessageSize))
        
    End Sub

    Public Sub PrintUsage()
        WScript.Echo "/domain: /dataRoot: /instance: /maxSize:"
        WScript.Echo "    domain:   (required)"
        WScript.Echo "    instance: (optional) virtual server number [1]"
        WScript.Echo "    dataRoot: (optional) Root directory for mail storage (C:\inetpub\DataRoot)"
        WScript.Echo "    maxSize:  (optional) Max message size (10 MB)"
        WScript.Echo "smtp.vbs /domain:direct.example.com"
    End Sub

    Public Sub Setup()
        On Error Resume Next
	Set smtpSvc = Nothing
        Set smtpSvc = GetSmtpNode(Instance)
	If smtpSvc Is Nothing Then
		Err.Raise 5, "Smtp Server not installed"
		Exit Sub
	End If
        
        Me.DataRoot = FixDirPath(Me.DataRoot)
                
        smtpSvc.ServerComment = "Direct Gateway"
        smtpSvc.FullyQualifiedDomainName = Me.Domain
        smtpSvc.DefaultDomain = Me.Domain
        smtpSvc.MaxMessageSize = Me.MaxMessageSize
        
        smtpSvc.LogFileDirectory = Me.DataRoot & "Logs"
        smtpSvc.LogFilePeriod = 1 ' 24 hrs
        smtpSvc.DropDirectory = Me.DataRoot & "Drop"
        smtpSvc.BadMailDirectory = Me.DataRoot & "Badmail"
        smtpSvc.PickupDirectory = Me.DataRoot & "Pickup"
        smtpSvc.QueueDirectory = Me.DataRoot & "Queue"
        
        smtpSvc.SetInfo ' Save settings to metabase
        
	    If Err.Number <> 0 Then
		    WScript.Echo "Error commiting information to metabase"
		    WScript.Echo Err.Description
	    End If        
    End Sub

    Function GetSmtpNode(instanceNumber)
        Set GetSmtpNode = GetObject("IIS://localhost/smtpsvc/" + CStr(instanceNumber))
    End Function

    Function FixDirPath(path)
        If (Right(path, 1) <> "\") Then
            FixDirPath = path & "\"
        Else
            FixDirPath = path
        End If
    End Function           

    Function GetArg(args, name, defaultValue)
        GetArg = defaultValue
        value = args(name)
        If value <> "" Then
            GetArg = value
        End If
    End Function
End Class 