'<SCRIPT LANGUAGE="VBScript">
'----------------
' 
' Constants
'
'----------------
' CUSTOMIZE THIS FOR YOUR DOMAIN
const c_ThisDomain = "nhind.hsgincubator.com"
' MAKE SURE YOU COPY YOUR CONFIG FILE...
Const c_ConfigFile          = "C:\inetpub\nhinsmtp\AgentConfig.xml"
'
' Mail Storage Folders
'
Const c_MailRoot            = "C:\inetpub\mailroot\"
Const c_LogRoot             = "C:\inetpub\logs"
Const c_MailDirectory       = "C:\inetpub\mailroot\newmail\"
Const c_SendMailDirectory   = "C:\inetpub\mailroot\sendmail\"
Const c_BadMailDirectory    = "C:\inetpub\mailroot\badmail\"
'
' SMTP Eventing Constants
'
Const cdoRunNextSink = 0
Const cdoSkipRemainingSinks = 1
Const cdoStatAbortDelivery = 2 
Const cdoStatBadMail= 3 
Const cdoStatSuccess= 0
'
' ADODB Constants
'
Const stWriteChar = 0
Const stWriteLine = 1
'
' nhinSMTP constants
'
Const messageTypeReject = 0
Const messageTypeOutgoing = 1
Const messageTypeIncoming = 2

'----------------
' 
' Globals
'
'----------------
Dim g_agent

set g_agent = CreateObject("NHINDirect.ScriptAgent.SmtpAgentEventHandler")
g_agent.Init c_ThisDomain, c_ConfigFile

'Test

'----------------
' 
' SMTP Event Handler
'
'----------------

Sub IEventIsCacheable_IsCacheable() 
	'To implement the interface, and return S_OK implicitly 
End Sub

Sub ISMTPOnArrival_OnArrival(ByVal msg, status)    
    On Error Resume Next
    
    status = cdoSkipRemainingSinks
    ProcessMessage msg, status
    If Err.number <> 0 Then    
        g_agent.WriteLog Err.Description
        AbortMessage msg
    End If
    
End Sub

Sub ProcessMessage(ByVal msg, status)    
    On Error Resume Next
    
    Dim result
    Dim isIncoming
        
    result = False
    isIncoming = False    
    
    result = g_agent.ProcessCDOMessage((msg), isIncoming)
    
    If result = False Then
        RejectMessage msg
        Exit Sub
    End if    
    
    If isIncoming = True Then
        CopyMessage msg, c_MailDirectory
    Else
        CopyMessage msg, c_SendMailDirectory
    End If
    
End Sub

Sub Test()
    TestEndToEnd "C:\inetpub\mailroot\simple.eml"    
End Sub

Function TestEndToEnd(filePath)
    On Error Resume Next
    
    TestEndToEnd = False
    
    Dim result
    Dim isIncoming
        
    result = ""
    isIncoming = True    
    
    result = g_agent.ProcessCDOMessageFile(filePath, isIncoming)
    If Err.number <> 0 Then    
        g_agent.WriteLog Err.Description
        Exit Function
    End If
    
    If result = "" Then
        g_agent.WriteLog "Outgoing Failed"
        Exit Function
    End If
    
    If isIncoming = True Then
        g_agent.WriteLog "Should be Outgoing"
        Exit Function
    End If
    
    result = g_agent.ProcessMessageRaw(result, isIncoming)
    If Err.number <> 0 Then    
        g_agent.WriteLog Err.Description
        Exit Function
    End If
    
    If result = "" Then
        g_agent.WriteLog "Incoming Failed"
        Exit Function
    End If
    
    If isIncoming = False Then
        g_agent.WriteLog "Should be Incoming "
        Exit Function
    End If
    
    TestEndToEnd = True
    
End Function

Sub RejectMessage(ByVal msg)
    On Error Resume Next

    RouteMessageToDirectory msg, c_BadMailDirectory
    
End Sub

Sub CopyMessage(ByVal msg, directory)
    Dim guid
    Dim filePath
    Dim stream
    
    guid = CreateGuid
    filePath = directory + guid + ".eml"
    
    set stream = msg.GetStream
    stream.SaveToFile filePath, 2
    
End Sub

Sub AbortMessage(ByVal msg)
    Dim flds
    
    Set flds = msg.EnvelopeFields
    flds("http://schemas.microsoft.com/cdo/smtpenvelope/messagestatus") = cdoStatAbortDelivery
    flds.Update
    
End Sub

Sub RouteMessageToDirectory(ByVal msg, directory)
    On Error Resume Next
    
    CopyMessage msg, directory
    
    If Err.number <> 0 Then
        LogError
    End If
    
    AbortMessage msg
    
End Sub

Sub LogError()
    If Err.number <> 0 Then
        g_agent.WriteError Err.number & " " & Err.Description
        Err.Clear
    End If
End Sub

Set TypeLib = CreateObject("Scriptlet.TypeLib")
Function CreateGuid
    dim newGUID
    
    newGUID = TypeLib.Guid
    CreateGuid = Mid(newGUID, 2, 36)

End Function

Function ReadAllText(ByVal msg)
    Dim stream
    Set stream = msg.GetStream
    ReadAllText = stream.ReadText(stream.Size)
End Function

Sub WriteAllText(ByVal msg, text)
    Dim stream
    Set stream = msg.GetStream
    
    stream.Position = 0
    stream.WriteText text, stWriteChar
    stream.SetEOS
    stream.Flush    
End Sub

'</SCRIPT>
