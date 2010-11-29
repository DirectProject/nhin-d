Dim g_agentSetup

Set g_agentSetup = CreateObject("Health.Direct.SmtpAgent.AgentSetup")

EnsureStores
ValidateConfig WScript.Arguments(0)

Sub EnsureStores()
    WScript.Echo "Ensuring standard machine stores"
    g_agentSetup.EnsureStandardMachineStores
    WScript.Echo "Done"
End Sub

Sub ValidateConfig(path)
    WScript.Echo "Validating " & path
    g_agentSetup.ValidateConfig path
    WScript.Echo "Done"
End Sub