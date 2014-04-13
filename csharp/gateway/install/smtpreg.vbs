'THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT
'WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED,
'INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
'OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR
'PURPOSE

'------------------------------------------------------------------------------
'FILE DESCRIPTION: Script for managing SMTP Protocol and Transport Event Sink bindings.
'
'File Name: smtpreg.vbs
'
'
' Copyright (c) Microsoft Corporation 1993-1999. All rights reserved.
'------------------------------------------------------------------------------
Option Explicit

' The SMTP Source Type

Const GUID_SMTPSourceType          = "{fb65c4dc-e468-11d1-aa67-00c04fa345f6}"

' The base Source GUID for SMTP Service Event Sources
Const GUID_SmtpSvcSourceBase        = "{1b3c0666-e470-11d1-aa67-00c04fa345f6}"

' the Event type GUIDs (COM Categories) for SMTP Service Events
' Protocol Events
Const catidSmtpOnInboundCommand          = "{F6628C8D-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnServerResponse          = "{F6628C8E-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnSessionStart            = "{F6628C8F-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnMessageStart            = "{F6628C90-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnPerRecipient            = "{F6628C91-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnBeforeData              = "{F6628C92-0D5E-11d2-AA68-00C04FA35B82}"
Const catidSmtpOnSessionEnd              = "{F6628C93-0D5E-11d2-AA68-00C04FA35B82}"

' Transport Events
Const catidSmtpStoreDriver               = "{59175850-e533-11d1-aa67-00c04fa345f6}"
Const catidSmtpOnArrival                 = "{FF3CAA23-00B9-11d2-9DFB-00C04FA322BA}"
Const catidSmtpOnTransportSubmission     = "{FF3CAA23-00B9-11d2-9DFB-00C04FA322BA}"
Const catidSmtpOnPreCategorize              = "{A3ACFB0D-83FF-11d2-9E14-00C04FA322BA}"
Const catidSmtpOnCategorize            = "{960252A3-0A3A-11d2-9E00-00C04FA322BA}"
Const catidSmtpOnPostCategorize          = "{76719654-05A6-11D2-9DFD-00C04FA322BA}"
Const catidSmtpOnTransportRouter         = "{283430C9-1850-11d2-9E03-00C04FA322BA}"
Const catidSmtpMsgTrackLog               = "{c6df52aa-7db0-11d2-94f4-00c04f79f1d6}"
Const catidSmtpDnsResolver               = "{bd0b4366-8e03-11d2-94f6-00c04f79f1d6}"
Const catidSmtpMaxMsgSize                = "{ebf159de-a67e-11d2-94f7-00c04f79f1d6}"


' Create the GLOBAL SEO Objects used to manage the bindings.

'   The SEO oEventManager object.
'   This object is used to manage the event bindings database.
Dim oEventManager
Set oEventManager = CreateObject("Event.Manager")

'   The SEO COM Category Manager
'   This object is used to add Implements Category keys for registered sinks.
Dim oComCatMan
Set oComCatMan    = CreateObject("Event.ComCat")

'   SEO Utilities
'   This object is used to generate the necessary Source GUID for a particular
'   SMTP Service Virtual Service.
Dim oSEOUtil
Set oSEOUtil      = CreateObject("Event.Util")



' ** DisplayUsage **
' display usage information for this script
'
public sub DisplayUsage
     WScript.echo "usage: cscript smtpreg.vbs <Command> <Arguments>"
     WScript.echo "  Commands:"
     WScript.echo "    /add     <Instance> <Event> <DisplayName | Binding GUID> <SinkClass> <Rule>"
     WScript.echo "    /remove  <Instance> <Event> <DisplayName | Binding GUID>"
     WScript.echo "    /setprop <Instance> <Event> <DisplayName | Binding GUID> <PropertyBag> <PropertyName> "
     WScript.echo "             <PropertyValue>"
     WScript.echo "    /delprop <Instance> <Event> <DisplayName | Binding GUID> <PropertyBag> <PropertyName>"
     WScript.echo "    /enable  <Instance> <Event> <DisplayName | Binding GUID>"
     WScript.echo "    /disable <Instance> <Event> <DisplayName | Binding GUID>"
     WScript.echo "    /enum"
     WScript.echo "  Arguments:"
     WScript.echo "    <Instance> The SMTP virtual service instance"
     WScript.echo "    <Event>    The event name. Can be one of the following:"
     WScript.echo
     WScript.echo "       Transport Events:"
     WScript.echo "         StoreDriver"
     WScript.echo "         OnArrival (OnTransportSubmission)"
     WScript.echo "         OnTransportSubmission"
     WScript.echo "         OnPreCategorize"
     WScript.echo "         OnCategorize"
     WScript.echo "         OnPostCategorize"
     WScript.Echo "         OnTransportRouter"
     WScript.echo "         MsgTrackLog"
     WScript.echo "         DnsResolver "
     WScript.echo "         MaxMsgSize"
     WScript.echo "       Protocol Events:"
     WScript.echo "         OnInboundCommand"
     WScript.echo "         OnServerResponse"
     WScript.echo "         OnSessionStart"
     WScript.echo "         OnMessageStart"
     WScript.echo "         OnPerRecipient"
     WScript.echo "         OnBeforeData"
     WScript.echo "         OnSessionEnd"
     WScript.echo
     WScript.echo "    <DisplayName>   The display name of the event to edit"
     WScript.echo "    <SinkClass>     The sink Programmatic identifier"
     WScript.echo "    <Rule>          The protocol rule to use for the event (ehlo=*,mail from=*, etc)"
     WScript.echo "    <Binding GUID>  The event binding GUID in registry string format: {GUID}"
     WScript.echo "    <PropertyBag>   The ""Source"" or ""Sink"" property bag"
     WScript.echo "    <PropertyName>  The name of the property to edit"
     WScript.echo "    <PropertyValue> The value to assign to the property"
end sub



'
' register a new sink with event manager
'
' iInstance     - The SMTP virtual service instance for which to register the binding.
' szEventName   - Must be "OnArrival"
' szDisplayName - The display name for this new sink
' szProgID      - The ProgId of the event sink COM Class.
' szRule        - The protocol firing rule for the event sink.
' szBindingGUID - Optional. The Binding GUID to use for the binding.
'
public sub RegisterSink(iInstance, szEventName, szDisplayName, szBindingGUID, szProgID, szRule, szPrioVal)

     Dim oBindings
     Dim oBinding
     Dim PrioVal
     Dim catidEventType

     Set oBindings = GetBindings(iInstance, szEventName)
     catidEventType = GetEventTypeCatID(szEventName)

     ' Attempt to add the "ImplementsCategory" key for Sink using ProgID
     ' If the sink is not registered on this machine, this will fail.
        ' If this category is not registered, this method will fail.

     On Error Resume Next
     Dim fRegCompleteOrErr
     fRegCompleteOrErr = False
     Do Until fRegCompleteOrErr
          oComCatMan.RegisterClassImplementsCategory szProgID, catidEventType
          If Err.Number <> 0 Then
               Dim oldErrorNum
               Dim oldErrDesc
               oldErrorNum = Err.Number
               oldErrDesc  = Err.Description
               ' verify the COM category exists:
               Err.Clear
               Dim szCOMCatDesc
               szCOMCatDesc = ""
               szCOMCatDesc = oComCatMan.GetCategoryDescription(catidEventType,0)
               if Err.Number <> 0 Then
                    WScript.Echo "COM Category (EventType) is not registered..."
                    ' Attempt to run SMTPSEOSetup
                    Call ResetSMTPCatIDs()
               Else
                    Wscript.Echo "** Registration Failed **"
                    Wscript.Echo "    Err.Number (HRESULT) = 0x" & Hex(oldErrorNum)
                    WScript.Echo "    Err.Description      = " & oldErrDesc
                    WScript.Echo "    ProgID               = " & szProgID
                    WSCript.Echo "    COM Category         = " & catidEventType
                    WSCript.Echo "    Corresponding Event  = " & szEventName
                    WScript.Echo "** Have you registered your sink COM class on this machine?"
                    WScript.Quit
               End If
          Else
               fRegCompleteOrErr = True
          End If
     Loop

     ' Sink is registered...resume registration


        ' Generate a GUID for the binding if the caller did not specify one
     If szBindingGUID = "" Then
          szBindingGUID = oSEOUtil.GetNewGUID
     End If

     Set oBinding = oBindings.Add(szBindingGUID)
     ' set the binding properties
     oBinding.DisplayName = szDisplayName
     oBinding.SinkClass   = szProgID
     ' register a rule with the binding
     oBinding.SourceProperties.Add "Rule", szRule
     ' register a priority with the binding

     ' Assign the default priority if not specified
        If szPrioVal = "" Then
          szPrioVal = 24575  ' the default
          WScript.Echo "Assigning priority (" & szPrioVal & " in 32767)"
     End If
     oBinding.SourceProperties.Add "Priority", CInt(szPrioVal)

     ' save the binding
     oBinding.Save
     If Err.Number <> 0 Then
          WScript.Echo "Failed to save the binding " & GUID_Binding
          WScript.Echo Err.Number
          WScript.Echo Err.Description
          WScript.Quit
        End If

     WScript.Echo "** SUCCESS **"
     WScript.Echo "Registered Binding:"
     Wscript.ECho " Event Name  :" & oComCatMan.GetCategoryDescription(catidEventType,0)
     WScript.Echo " Display Name:" & szDisplayName
     WScript.Echo " Binding GUID:" & szBindingGUID
     WScript.Echo " ProgID      :" & szProgID
        WScript.Echo "   Rule      :" & szRule
        WScript.Echo "   Priority  :" & szPrioVal & " (0 - 32767, default: 24575)"
     WScript.Echo "   ComCatID  :" & catidEventType
end sub


' ** GetEventTypeCatID **
'  in:  szEventName
'  returns: COM Category ID for the Event (as a string)

Function GetEventTypeCatID(szEventName)
     select case LCase(szEventName)
                case "storedriver"
               GetEventTypeCatID = catidSmtpStoreDriver
          case "onarrival"
               GetEventTypeCatID = catidSmtpOnArrival
                case "ontransportsubmission"
                        GetEventTypeCatID = catidSmtpOnTransportSubmission
          case "onprecategorize"
               GetEventTypeCatID = catidSmtpOnPreCategorize
          case "oncategorize"
               GetEventTypeCatID = catidSmtpOnCategorize
          case "onpostcategorize"
               GetEventTypeCatID = catidSmtpOnPostCategorize
          case "ontransportrouter"
               GetEventTypeCatID = catidSmtpOnTransportRouter
          case "msgtracklog"
               GetEventTypeCatID = catidSmtpMsgTrackLog
          case "dnsresolver"
               GetEventTypeCatID = catidSmtpDnsResolver
          case "maxmsgsize"
               GetEventTypeCatID = catidSmtpMaxMsgSize
                case "oninboundcommand"
               GetEventTypeCatID = catidSmtpOnInBoundCommand
          case "onserverresponse"
               GetEventTypeCatID = catidSmtpOnServerResponse
                case "onsessionstart"
               GetEventTypeCatID = catidSmtpOnSessionStart
          case "onmessagestart"
               GetEventTypeCatID = catidSmtpOnMessageStart
          case "onperrecipient"
               GetEventTypeCatID = catidSmtpOnPerRecipient
          case "onbeforedata"
               GetEventTypeCatID = catidSmtpOnBeforeData
          case "onsessionend"
               GetEventTypeCatID = catidSmtpOnSessionEnd
          case else
               WScript.Echo "Unrecognized Event Name: " & szEventName
               ' This is fatal...quit!
               WScript.Quit(-1)
     end select
End Function


'
' ** UnregisterSink **
'   Unregister a previously registered sink
'
' iInstance     - the SMTP Virtual Service Instance
' szEventName   - The Event name
' szDisplayName - The display name of the event to remove
' szBindingGUID - Optional. The Binding GUID to use.

public sub UnregisterSink(iInstance, szEventName, szDisplayName, szBindingGUID)

     Dim oBindings
     dim catidEventType

     Set oBindings = GetBindings(iInstance,szEventName)
     catidEventType = GetEventTypeCatID(szEventName)

     ' If the Binding GUID was given, use it to remove the binding
     ' otherwise, get it using the display name

     If szBindingGUID = "" Then
          szBindingGUID = GetBindingGUIDFromDisplayName(szDisplayName,oBindings)
     End If

     oBindings.Remove szBindingGUID

     WScript.Echo "** SUCCESS **"
     WScript.Echo "Removed Binding:"
     Wscript.ECho " Event Name  :" & oComCatMan.GetCategoryDescription(catidEventType,0)
     WScript.Echo " Display Name:" & szDisplayName
     WScript.Echo " Binding GUID:" & szBindingGUID
     WScript.Echo "   ComCatID  :" & catidEventType
end sub


' ****************
' * EditProperty *
' ****************
' add or remove a property from the source or sink propertybag for an event binding
'
' iInstance       - The SMTP instance to edit
' szEvent         - The event name (OnArrival, OnMessageSubmission, etc)
' szDisplayName   - The display name of the event
' szBindingGUID   - Optional. The GUID for the binding. Display name is used if not supplied
' szPropertyBag   - The property bag to edit ("source" or "sink")
' szOperation     - "add" or "remove"
' szPropertyName  - The name to edit in the property bag
' szPropertyValue - The value to assign to the name (ignored for remove)
'
public sub EditProperty(iInstance, szEventName, szDisplayName, szBindingGUID, szPropertyBag, szOperation, szPropertyName, szPropertyValue)
     Dim oBinding
     Dim oPropertyBag

     Set oBinding = GetBinding(iInstance, szEventName, szDisplayName, szBindingGUID)

     select case LCase(szPropertyBag)
          case "source"
               set oPropertyBag = oBinding.SourceProperties
          case "sink"
               set oPropertyBag = oBinding.SinkProperties
          case else
               WScript.echo "invalid propertybag: " & szPropertyBag
               exit sub
          end select

          ' figure out what operation we want to perform
     select case LCase(szOperation)
          case "remove"
               ' they want to remove szPropertyName from the
               ' property bag
               oPropertyBag.Remove szPropertyName
               WScript.echo "removed property " & szPropertyName
          case "add"
               ' add szPropertyName to the property bag and
               ' set its value to szValue.  if this value
               ' already exists then this will change  the value
               ' it to szValue.
               oPropertyBag.Add szPropertyName, szPropertyValue
               WScript.echo "set property " & szPropertyName & " to " & szPropertyValue
          case else
               WScript.echo "invalid operation: " & szOperation
               exit sub
     end select
     ' save the binding
     oBinding.Save
end sub


' ******************
' * SetSinkEnabled *
' ******************
' Enable/disable a registered sink
'
' iInstance     - The instance to work against
' szEvent       - The event name
' szDisplayName - The display name for this sink
' szBindingGUID - The Binding GUID (optional)
'
public sub SetSinkEnabled(iInstance, szEvent, szDisplayName, szBindingGUID, szEnable)
     Dim oBinding
     Set oBinding = GetBinding(iInstance, szEvent, szDisplayName, szBindingGUID)
     Select Case(szEnable)
          case "True"
               oBinding.Enabled = True
               oBinding.Save
               wscript.echo "Success: Sink Binding Enabled"
          case "False"
               oBinding.Enabled = False
               oBinding.Save
               wscript.echo "Success: Sink Binding Disabled"
          case else
               Wscript.Echo "Error in SetSinkEnabled Routine: Invalid option."
               Wscript.Quit
     End Select
end sub


' ***********************
' * GetBindings Function *
' ***********************
'  Returns a reference to the SEO binding object for a particular binding
'
'  iInstance     - The SMTP Virtual Service Instance (> 0)
'  szEventName   - The Name of the Event (OnArrival, etc)

Function GetBindings(iInstance, szEventName)
     Dim oSourceType
     Dim catidEventType
     Dim oSource
     Dim GUID_SMTPInstanceSource

     catidEventType = GetEventTypeCatID(CStr(szEventName))

        ' Make sure iInstance is not less than 1.
        If iInstance < 1 Then
             WScript.Echo "Invalid SMTP service instance: " & CStr(iInstance)
             WScript.Quit
        End If

        ' Generate Source GUID using SMTP source base GUID and the instance number.
        ' Do this using the SEO Util object's GetIndexedGUID method.
        GUID_SMTPInstanceSource = oSEOUtil.GetIndexedGUID(GUID_SmtpSvcSourceBase,iInstance)


        ' Get the binding manager for this source
        Set oSourceType = oEventManager.SourceTypes(GUID_SMTPSourceType)
        Set oSource     = oSourceType.Sources(GUID_SMTPInstanceSource)

     If typename(oSource) = "Nothing" Then
          WScript.Echo "SMTP Virtual Service # " & iInstance & " Source not present...exiting."
                WScript.Quit(-1)
        End If

     Set GetBindings = oSource.GetBindingManager.Bindings(catidEventType)


End Function



' ***********************
' * GetBinding Function *
' ***********************
'  Returns a reference to the SEO binding object for a particular binding
'
'  iInstance     - The SMTP Virtual Service Instance (> 0)
'  szEventName   - The Name of the Event (OnArrival, etc)
'  szDisplayName - The Display Name for the binding. Used to retrieve binding GUID
'                  if is it not supplied
'  szBindingGUID - The GUID for the binding. If not "", this GUID is used.
'
Function GetBinding(iInstance, szEventName, szDisplayName,szBindingGUID)
     Dim oSourceType
     Dim catidEventType
     Dim oSource
     Dim oBindings
     Dim oBinding
     Dim GUID_SMTPInstanceSource

     catidEventType = GetEventTypeCatID(CStr(szEventName))

        ' Make sure iInstance is not less than 1.
        If iInstance < 1 Then
             WScript.Echo "Invalid SMTP service instance: " & CStr(iInstance)
             WScript.Quit
        End If

        ' Generate Source GUID using SMTP source base GUID and the instance number.
        ' Do this using the SEO Util object's GetIndexedGUID method.
        GUID_SMTPInstanceSource = oSEOUtil.GetIndexedGUID(GUID_SmtpSvcSourceBase,iInstance)

        ' Get the binding manager for this source
        Set oSourceType = oEventManager.SourceTypes(GUID_SMTPSourceType)
        Set oSource     = oSourceType.Sources(GUID_SMTPInstanceSource)

     If typename(oSource) = "Nothing" Then
          WScript.Echo "SMTP Virtual Service # " & iInstance & " Source not present...exiting."
                WScript.Quit(-1)
        End If

        Set oBindings   = oSource.GetBindingManager.Bindings(catidEventType)

     If szBindingGUID = "" Then
          szBindingGUID = GetBindingGUIDFromDisplayName(SzDisplayName, oBindings)
     End If

     Set oBinding = oBindings(szBindingGUID)
     If TypeName(oBinding) = "Nothing" Then
          WScript.Echo "** ERROR **"
          WScript.Echo "No binding present for GUID " & szBindingGUID
          WScript.Quit
     Else
          Set GetBinding = oBinding
     End If

End Function


'
' this helper function takes an IEventSource object and a event category
' and dumps all of the bindings for this category under the source
'
' Source - the IEventSource object to display the bindings for
' GUIDComCat - the event category to display the bindings for
'
public sub DisplayBindingHelper(oSource, oEventType)
     Dim Binding
     Dim PropName
     Dim Props

     ' walk each of the registered bindings for this component category
     dim strSpaces
     strSpaces = "                        "
     for each Binding in oSource.GetBindingManager.Bindings(oEventType.id)
          wscript.echo "                        ---------"
          wscript.echo "                       | Binding |"
          Wscript.Echo "                        ---------"
          WScript.Echo strSpaces & "           Event: " & oEventType.DisplayName
          WScript.echo strSpaces & "              ID: " & Binding.ID
          WScript.echo strSpaces & "            Name: " & Binding.DisplayName
          WScript.echo strSpaces & "       SinkClass: " & Binding.SinkClass
          WScript.echo strSpaces & "         Enabled: " & Binding.Enabled

          Set Props = Binding.SourceProperties
          If Props.Count > 0 Then
               WScript.echo strSpaces & "SourceProperties: {"
               for each PropName in Props
                    WScript.echo strSpaces & "                   " & propname & " = " & Binding.SourceProperties(propname)
               next
               WScript.echo strSpaces & "                  }"
          End If

          Set Props = Binding.SinkProperties
          If Props.Count > 0 Then
               WScript.echo strSpaces & "SinkProperties    {"
               for each Propname in Props
                    WScript.echo strSpaces & "                   " & PropName & " = " & Props(PropName)
               next
               WScript.echo strSpaces & "                  }"
          End If
     next
end sub

'
' dumps all of the information in the binding database related to SMTP
'
public sub DisplaySinks
     Dim SourceType
     Dim Source
     Dim eventtype
     On Error Resume Next
     For Each SourceType in oEventManager.SourceTypes
          wscript.echo " -------------"
          Wscript.Echo "| Source Type |"
          Wscript.Echo " -------------"
          Wscript.echo " Name: " & SourceType.DisplayName
          WScript.Echo "   ID: " & SourceType.ID
          for each eventtype in sourcetype.eventtypes
               wscript.echo "               ------------"
               wscript.echo "              | Event Type |"
               Wscript.Echo "               ------------"
               WScript.Echo "               Name: " & eventtype.DisplayName
               WSCript.Echo "                 ID: " & eventtype.ID
          next
          for each Source in SourceType.Sources
               ' display the source properties
               wscript.echo "               --------"
               wscript.echo "              | Source |"
               Wscript.Echo "               --------"
               WScript.echo "               Name: " & Source.DisplayName
               WScript.echo "                 ID: " & Source.ID
               for each eventtype in sourcetype.eventtypes
                    call DisplayBindingHelper(Source, eventtype)
               next
          next
     Next
end sub


' ************************
' * SetDisplayNameOrGUID *
' ************************
'  Examines the arguments to determine whether a GUID
'  or a display name was passed. All GUIDs must be passed in the form
'  "{871736C0-FD85-11D0-869A-00C04FD65616}"
'  and display names must not start with a left-bracket "{"
'

Sub SetDisplayNameOrGUID(ByRef szArg, ByRef szDisplayName, ByRef szBindingGUID)

  ' check for left bracked used for a GUID
  If (InStr(1,szArg, "{", 1) = 1) Then
     WScript.Echo "Binding GUID specified: " & szArg
     szBindingGUID = szArg
     szDisplayName = ""
  Else
     WScript.Echo "Binding Display Name Specified: " & szArg
     szBindingGUID = ""
     szDisplayName = szArg
  End If
End Sub


' *********************************
' * GetBindingGUIDFromDisplayName *
' *********************************
' Attempts to return the binding GUID for a binding
' based upon the binding display name
'
' szDisplayName  - [in] The display name for the binding
' oBindings      - [in] The SEO EventBindings Object
'
' Returns
'  If successful, returns the binding GUID for the binding.
'  The first matched display name is used. That is, if multiple bindings
'  have the same display name, the first found is used.

Function GetBindingGUIDFromDisplayName(SzDisplayName, oBindings)

     Dim oBinding

     for each oBinding in oBindings
          if oBinding.DisplayName = szDisplayName then
               GetBindingGUIDFromDisplayName = oBinding.ID
               exit function
          End If
     next

     WScript.Echo "Failed to find binding with display name:" & szDisplayName
     WScript.Quit

End Function



' *******************
' * ResetSMTPCatIDs *
' *******************
'  - Registers the various COM categories for SMTP transport and protocol
'  events.
'  - Adds each as an event type to the SMTP Service Source Type
'
'
Sub ResetSMTPCatIDs()

     WScript.Echo "Running ResetSMTPCatIDs...one moment"
     On Error Resume Next
     Err.Clear

     Dim oSourceType
     Set oSourceType = oEventManager.SourceTypes(GUID_SMTPSourceType)
     If TypeName(oSourceType) = "Nothing" Then
          WScript.Echo "** ERROR **"
          Wscript.Echo "No SMTP Source Type registered."
          WScript.Echo "Is the SMTP Service installed on this machine?"
          WScript.Quit
     End If

     oComCatMan.RegisterCategory catidSmtpStoreDriver,   "SMTP StoreDriver", 0
     oSourceType.EventTypes.Add  catidSmtpStoreDriver

     oComCatMan.RegisterCategory catidSmtpOnTransportSubmission, "SMTP OnTransportSubmission/OnArrival", 0
     oSourceType.EventTypes.Add  catidSmtpOnTransportSubmission

     oComCatMan.RegisterCategory catidSmtpOnPreCategorize, "SMTP OnPreCategorize",0
     oSourceType.EventTypes.Add  catidSmtpOnPreCategorize

     oComCatMan.RegisterCategory catidSmtpOnCategorize, "SMTP OnCategorize",0
     oSourceType.EventTypes.Add  catidSmtpOnCategorize

     oComCatMan.RegisterCategory catidSmtpOnPostCategorize, "SMTP OnPostCategorize",0
     oSourceType.EventTypes.Add  catidSmtpOnPostCategorize

     oComCatMan.RegisterCategory catidSmtpOnTransportRouter, "SMTP OnTransportRouter",0
     oSourceType.EventTypes.Add  catidSmtpOnTransportRouter

     oComCatMan.RegisterCategory catidSmtpMsgTrackLog,      "SMTP MsgTrackLog", 0
     oSourceType.EventTypes.Add  catidSmtpMsgTrackLog

     oComCatMan.RegisterCategory catidSmtpDnsResolver,      "SMTP DnsResolver", 0
     oSourceType.EventTypes.Add  catidSmtpDnsResolver

     oComCatMan.RegisterCategory catidSmtpMaxMsgSize,       "SMTP MaxMsgSize", 0
     oSourceType.EventTypes.Add  catidSmtpMaxMsgSize

     oComCatMan.RegisterCategory catidSmtpOnInBoundCommand, "SMTP OnInBoundCommand", 0
     oSourceType.EventTypes.Add  catidSmtpOnInBoundCommand

     oComCatMan.RegisterCategory catidSmtpOnServerResponse, "SMTP OnServerResponse", 0
     oSourceType.EventTypes.Add  catidSmtpOnServerResponse

     oComCatMan.RegisterCategory catidSmtpOnSessionStart, "SMTP OnSessionStart", 0
     oSourceType.EventTypes.Add  catidSmtpOnSessionStart

     oComCatMan.RegisterCategory catidSmtpOnMessageStart, "SMTP OnMessageStart", 0
     oSourceType.EventTypes.Add  catidSmtpOnMessageStart

     oComCatMan.RegisterCategory catidSmtpOnPerRecipient, "SMTP OnPerRecipient", 0
     oSourceType.EventTypes.Add  catidSmtpOnPerRecipient

     oComCatMan.RegisterCategory catidSmtpOnBeforeData, "SMTP OnBeforeData", 0
     oSourceType.EventTypes.Add  catidSmtpOnBeforeData

     oComCatMan.RegisterCategory catidSmtpOnSessionEnd,  "SMTP OnSessionEnd", 0
     oSourceType.EventTypes.Add  catidSmtpOnSessionEnd

     If Err.Number <> 0 Then
          WScript.Echo "** ERROR ** "
          Wscript.Echo "Error registering COM categories"
          WScript.Echo Err.Number
          WScript.Echo Err.Description
          Wscript.Echo "Make sure the SMTP Service is installed on the machine"
          WScript.Quit
     End If

     ' Remove the duplicate, redundant SMTP SourceType
     Const GUID_ExtraSMTPSourceType = "{4f803d90-fd85-11d0-869a-00c04fd65616}"
     EVentManager.SourceTypes.Remove(GUID_ExtraSMTPSourceType)

End Sub




' **********************
' * Main Routine Start *
' **********************

Dim iInstance
Dim szEvent
Dim szDisplayName
Dim szSinkClass
Dim szRule
Dim szPrio
Dim szBindingGUID
Dim szPropertyBag
Dim szPropertyName
Dim szPropertyValue
dim bCheck
Dim ArgCount
'
' this is the main body of our script.  it reads the command line parameters
' specified and then calls the appropriate function to perform the operation
'
if WScript.Arguments.Count = 0 then
     call DisplayUsage
else
     ArgCount = WScript.Arguments.Count
     Select Case LCase(WScript.Arguments(0))
          Case "/?"
               DisplayUsage
          Case "/add"
               if not ArgCount = 6 and not ArgCount = 7 then
                    call DisplayUsage
               else
                    iInstance   = WScript.Arguments(1)
                    szEvent     = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    szSinkClass = WScript.Arguments(4)
                    szRule      = WScript.Arguments(5)
                    If ArgCount = 7 Then
                         szPrio = WScript.Arguments(6)
                    Else
                         szPrio = ""
                    End If
                    call RegisterSink(iInstance, szEvent, szDisplayName, szBindingGUID, szSinkClass, szRule, szPrio)
               end if
          Case "/remove"
               if not ArgCount = 4 then
                    call DisplayUsage
               else
                    iInstance = WScript.Arguments(1)
                    szEvent = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    call UnregisterSink(iInstance, szEvent, szDisplayName, szBindingGUID)
               end if
          Case "/setprop"
               if not ArgCount = 7 then
                    call DisplayUsage
               else
                    iInstance = WScript.Arguments(1)
                    szEvent = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    szPropertyBag = WScript.Arguments(4)
                    szPropertyName = WScript.Arguments(5)
                    szPropertyValue = WScript.Arguments(6)
                    call EditProperty(iInstance, szEvent, szDisplayName, szBindingGUID, szPropertyBag, "add", szPropertyName, szPropertyValue)
               end if
          Case "/delprop"
               if not ArgCount = 6 then
                    call DisplayUsage
               else
                    iInstance = WScript.Arguments(1)
                    szEvent = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    szPropertyBag = WScript.Arguments(4)
                    szPropertyName = WScript.Arguments(5)
                    call EditProperty(iInstance, szEvent, szDisplayName, szBindingGUID, szPropertyBag, "remove", szPropertyName, "")
               end if
          Case "/enable"
               if not ArgCount = 4 then
                    call DisplayUsage
               else
                    iInstance = WScript.Arguments(1)
                    szEvent = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    call SetSinkEnabled(iInstance, szEvent, szDisplayName, szBindingGUID, "True")
               end if
          Case "/disable"
               if not ArgCount = 4 then
                    call DisplayUsage
               else
                    iInstance = WScript.Arguments(1)
                    szEvent = WScript.Arguments(2)
                    Call SetDisplayNameOrGUID(WScript.Arguments(3), szDisplayName, szBindingGUID)
                    call SetSinkEnabled(iInstance, szEvent, szDisplayName, szBindingGUID, "False")
               end if
          Case "/enum"
               if not ArgCount = 1 then
                    call DisplayUsage
               else
                    call DisplaySinks
               end if
          Case "/resetcatids"
               call ResetSMTPCatIDs
          Case Else
               call DisplayUsage
     End Select
end if
