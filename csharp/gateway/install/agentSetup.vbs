Dim g_agentSetup

Set g_agentSetup = CreateObject("NHINDirect.SmtpAgent.AgentSetup")

Sub EnsureStores()
    g_agentSetup.EnsureStandardMachineStores
End Sub