<#
.SYNOPSIS
    Provides integrated build/deploy/test script.
#>

param
(
    [ValidateSet('Build', 'Deploy', 'Test', 'Policy', 'ConfigData', 'HSM', IgnoreCase = $true)]
    [string[]] $Include = @('Build', 'Deploy', 'Test'),

    [string] $Solution = '.\build\DirectProject.sln',
    
    [string] $Database = 'DirectConfig',
    [string] $DbServer = '(localdb)\Projects',
    
    [string] $DefaultWebSiteName = 'Default Web Site',
    [string] $ConfigServiceName = 'ConfigService',

    [string] $Domain = 'hsgincubator.com',
    [string] $GatewayName = 'NHINDGateway',
    [string] $MailRootFolder = "$env:SystemDrive\inetpub\MailRoot",
    [string] $ConfigFile = (Join-Path (Get-Item 'gateway\smtpEventHandler').FullName 'bin\x64\Debug\SmtpAgentConfig.xml'),

    [int]    $InstanceNumber = 1,
    [string] $SmtpBindingId = '{21312143-C790-4698-94BF-5BDE9B574C19}',
    [string] $EventSinkProgId = 'NHINDirectGateway.MessageArrivalSink',
	[string] $Platform = 'ANY CPU', 
	[string] $Rule = 'mail from=*',
	[int]    $Priority = 24575
)


#####################################################################
# some computers have the Platform Environment variable set 
# such as x86.  
#####################################################################
$env:Platform = $Platform


#####################################################################
# Prevent bugs
#####################################################################

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

#######################################################################
# Import modules - optional for IIS Express with LocalDB
#######################################################################

# Free download: https://msdn.microsoft.com/en-us/library/mt238290.aspx
$sqlModule = Import-Module 'SqlServer' -ErrorAction SilentlyContinue -PassThru

# Available via Install-WindowsFeature 'Web-Mgmt-Console' command
$webModule = Import-Module 'WebAdministration' -ErrorAction SilentlyContinue -PassThru

#######################################################################
# Configuring IIS and SMTP requires administrator role
#######################################################################

function Test-AdministratorRole
{
    # Get the ID and security principal of the current user account
    $windowsID = [System.Security.Principal.WindowsIdentity]::GetCurrent()
    $windowsPrincipal = New-Object System.Security.Principal.WindowsPrincipal($windowsID)
  
    # Get the security principal for the Administrator role
    $adminRole = [System.Security.Principal.WindowsBuiltInRole]::Administrator
  
    # Check to see if we are currently running "as Administrator"
    [bool] ($windowsPrincipal.IsInRole($adminRole))
}

$isAdministrator = Test-AdministratorRole

$isServer = (Get-CimInstance Win32_OperatingSystem).ProductType -eq 3

#######################################################################
# Build the whole solution first
#######################################################################

function Invoke-Msbuild
{
    param
    (
        [string] $Project,
        [string] $LogFile,
        [string[]] $Options
    )

    #
    # Build the project use separate log files for simplification.
    #

    $arguments = @(
        # Project to build.
        $project,

        # Verbosity:minimal - this is the output to console
        '/v:m',

        # Log all errors to a file
        '/fl1', "/flp1:logfile=$LogFile.err;errorsonly",

        # Log all warnings to a file
        '/fl2', "/flp2:logfile=$LogFile.wrn;warningsonly",

        # Log all warnings to a file
        '/fl3', "/flp3:logfile=$LogFile.log;detailed"
    ) + $Options

    & msbuild.exe $arguments

    #
    # Delete empty log files to simplify detection of failed builds
    #
    
    @("$LogFile.log", "$LogFile.wrn", "$LogFile.err") | 
        Get-Item -ErrorAction SilentlyContinue | 
        Where-Object {$_.Length -eq 0} | 
        Remove-Item -ErrorAction SilentlyContinue

    if (Test-Path "$LogFile.err")
    {
        # Build failed do not continue.
        Exit
    }
}

if ($Include -contains 'Build')
{
    #
    # Restore all NuGet packages required by the solution.
    #

    & .\build\.nuget\NuGet.exe restore $solution

    #
    # Build the whole solution
    #
    
    Invoke-Msbuild -Project $solution -LogFile 'build' -Options @("/p:Platform=$Platform")
}

#######################################################################
# Detect current IIS configuration
#######################################################################

$defaultSite = $null
$appPoolName = $null

if ($Include -contains 'Deploy' -and $isAdministrator -and $webModule)
{
    #
    # Assuming the default web site exist in IIS already.
    # Recreating it is not hard, but the details are different
    # depending on the version of IIS and .NET Framework.
    # http://stackoverflow.com/questions/750632/recreate-the-default-website-in-iis
    #
    
    $defaultSite = Get-WebSite $DefaultWebSiteName
    
    if ($defaultSite)
    {
        $appPoolName = $defaultSite.ApplicationPool
    }
}

#######################################################################
# Recreate the LocalDB database
#######################################################################

if ($Include -contains 'Deploy')
{
    $SqlLocalDb = (Get-Item "$env:ProgramFiles\Microsoft SQL Server\*\Tools\Binn\SqlLocalDB.exe")[-1].FullName
    $SqlCommand = (Get-Item "$env:ProgramFiles\Microsoft SQL Server\*\Tools\Binn\sqlcmd.exe")[-1].FullName

    #
    # Start LocalDB instance - it is not required each time, but it guarantees a consistent behavior.
    #

    & $SqlLocalDb stop Projects
    & $SqlLocalDb create Projects -s

    #
    # Sharing is required to make the database available to IIS and SMTP processes
    #
    
    if ($isAdministrator)
    {
        & $SqlLocalDb share Projects ProjectsShare
    }

    #
    # Use msbuild to recreate the database
    #
    
    Invoke-Msbuild -Project .\database\DirectConfig.sqlproj -LogFile 'DirectConfig' -Options @('/t:Publish', '/p:SqlPublishProfilePath=LocalDB.publish.xml')
    
    #
    # Add the IIS accounts as users
    #

    & $SqlCommand -S $DbServer -E -v DBName=$database -i .\gateway\devInstall\CreateUser.sql -v 'DBUSER="NT AUTHORITY\NETWORK SERVICE"'
    
    if ($isAdministrator -and $appPoolName)
    {
        & $SqlCommand -S $DbServer -E -v DBName=$database -i .\gateway\devInstall\CreateUser.sql -v "DBUSER=`"IIS APPPOOL\$appPoolName`""
    }
}

#######################################################################
# Configure IIS Express
#######################################################################

if ($Include -contains 'Deploy')
{
    $iisexpress = "$env:ProgramFiles\IIS Express\iisexpress.exe"

    if (Test-Path $iisexpress)
    {
        Stop-Process -Name iisexpress -ErrorAction SilentlyContinue

        $path = (Get-Item .\config\service).FullName
        Start-Process $iisexpress -ArgumentList @('/port:6692', "/path:$path") -WindowStyle Hidden

        $path = (Get-Item .\dnsresponder.service).FullName
        Start-Process $iisexpress -ArgumentList @('/port:6693', "/path:$path") -WindowStyle Hidden
    }
}

#######################################################################
# Configure full IIS
#######################################################################

if ($Include -contains 'Deploy')
{
    if ($isAdministrator -and $defaultSite)
    {
		#
		# If you receive access control list not in cononical form then
		# http://stackoverflow.com/questions/32648654/publishing-website-error-this-access-control-list-is-not-in-canonical-form-and
		#
        Invoke-Msbuild -Project .\config\service\config.service.csproj -LogFile 'ConfigService' -Options @('/p:DeployOnBuild=true', '/p:PublishProfile=Local-IIS.pubxml')
        Invoke-Msbuild -Project .\dnsresponder.service\dnsResponder.service.csproj -LogFile 'DnsService' -Options @('/p:DeployOnBuild=true', '/p:PublishProfile=Local-IIS.pubxml')
    }
}

#######################################################################
# Configure test data
#######################################################################

if ($Include -contains 'Deploy' -or $Include -contains 'ConfigData')
{
    Push-Location .\config\console\bin\Debug
    & .\ConfigConsole.exe Test_Certs_Install ..\..\..\..\unittests\agent
    & .\ConfigConsole.exe batch ..\..\..\..\gateway\devInstall\setupdomains.txt
    & .\ConfigConsole.exe batch ..\..\..\..\gateway\devInstall\setupdns.txt
    & .\ConfigConsole.exe test_certs_InstallInService ..\..\..\..\unittests\agent
	Pop-Location

    Push-Location .\tools\admin.console\bin\Debug
    & .\AdminConsole.exe USER_REMOVE admin
    & .\AdminConsole.exe USER_ADD admin admin
    & .\AdminConsole.exe USER_STATUS_SET admin Enabled
    Pop-Location
}

#######################################################################
# Configure Policy test data
#######################################################################
if ($Include -contains 'Deploy' -or $Include -contains 'Policy')
{
	Push-Location .\config\console\bin\Debug
	& .\ConfigConsole.exe batch ..\..\..\..\gateway\devInstall\Policy.txt
	Pop-Location
}

#######################################################################
# Configure HSM test data
#######################################################################
if ($Include -contains 'HSM')
{
	Push-Location .\config\console\bin\Debug
	& .\ConfigConsole.exe batch ..\..\..\..\gateway\devInstall\HSM.txt
	Pop-Location
}

#######################################################################
# Configure SMTP
#######################################################################

function CreateSubfolder
{
    param
    (
        [string] $Path,
        [string] $Name
    )

    $folder = New-Item -Force -ItemType directory -Path (Join-Path $Path $Name)
    $folder.FullName
}

if ($Include -contains 'Deploy' -and $isAdministrator -and ($isServer) -and (Get-WindowsFeature 'SMTP-Server'))
{
    # Register the C# version
    $regasm = Join-Path ([System.Runtime.InteropServices.RuntimeEnvironment]::GetRuntimeDirectory()) 'RegAsm.exe'
    & $regasm '.\gateway\smtpAgent\bin\Debug\Health.Direct.SmtpAgent.dll' /tlb:'Health.Direct.SmtpAgent.tlb' /codebase

    # Register the C++ version
    & regsvr32.exe /s .\gateway\smtpEventHandler\bin\x64\Debug\Health.Direct.SmtpEventHandler.dll

    # Configure SMTP service
    $smtp = [System.Runtime.InteropServices.Marshal]::BindToMoniker("IIS://localhost/smtpsvc/$InstanceNumber")
    $smtp.ServerComment = 'Direct Gateway'
    $smtp.FullyQualifiedDomainName = $Domain
    $smtp.DefaultDomain = $Domain
    $smtp.MaxMessageSize = 10485760   # 10 MB
    $smtp.MaxSessionSize = 52428800   # 50 MB
    $smtp.MaxBatchedMessages = 5
    $smtp.LogType = "1"
    $smtp.LogFilePeriod = 1           # 24 hours
    $smtp.LogExtFileDate = 'True'
    $smtp.LogExtFileTime = 'True'
    $smtp.LogExtFileClientIp = 'True'
    $smtp.LogExtFileUserName = 'True'
    $smtp.LogExtFileServerIp = 'True'
    $smtp.LogExtFileServerPort = 'True'
    $smtp.LogExtFileMethod = 'True'
    $smtp.LogExtFileUriStem = 'True'
    $smtp.LogExtFileWin32Status = 'True'
    $smtp.LogExtFileUserAgent = 'True'
    $smtp.LogFileDirectory = CreateSubfolder -Path $MailRootFolder -Name 'Logs'
    $smtp.DropDirectory = CreateSubfolder -Path $MailRootFolder -Name 'Drop'
    $smtp.BadMailDirectory = CreateSubfolder -Path $MailRootFolder -Name 'Badmail'
    $smtp.PickupDirectory = CreateSubfolder -Path $MailRootFolder -Name 'Pickup'
    $smtp.QueueDirectory = CreateSubfolder -Path $MailRootFolder -Name 'Queue'
    $smtp.SetInfo()

    # Server Extension Object
    [string] $SmtpSourceType = '{fb65c4dc-e468-11d1-aa67-00c04fa345f6}'

    # The base Source GUID for SMTP Service Event Sources
    [string] $SmtpSvcSourceBase = '{1b3c0666-e470-11d1-aa67-00c04fa345f6}'

    # SMTP OnTransportSubmission/OnArrival event category
    [string] $SmtpOnArrival = '{FF3CAA23-00B9-11d2-9DFB-00C04FA322BA}'

    # Server Extension Objects - Used to manage the event bindings database.
    $eventManager = New-Object -ComObject 'Event.Manager'
    $sourceType = $eventManager.SourceTypes($SmtpSourceType)

    # Server Extension Objects - Used to add Implements Category keys for registered sinks.
    $categoryManager = New-Object -ComObject 'Event.ComCat'

    # Server Extension Objects - Used to generate the necessary Source GUID for a particular SMTP Service Virtual Service.
    $utilities = New-Object -ComObject 'Event.Util'

    # Generate Source GUID using SMTP source base GUID and the instance number.
    $smtpInstanceSource = $utilities.GetIndexedGUID($SmtpSvcSourceBase, $InstanceNumber)

    try
    {
        # This is expected to fail.
        $categoryDescription = $categoryManager.GetCategoryDescription($SmtpOnArrival, 0)
    }
    catch
    {
        # This will restore SMTP event categories if they are missing.
        [string] $SmtpOnInBoundCommand = "{F6628C8D-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnInBoundCommand, "SMTP OnInBoundCommand", 0)
        $sourceType.EventTypes.Add($SmtpOnInBoundCommand)

        [string] $SmtpOnServerResponse = "{F6628C8E-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnServerResponse, "SMTP OnServerResponse", 0)
        $sourceType.EventTypes.Add($SmtpOnServerResponse)

        [string] $SmtpOnSessionStart = "{F6628C8F-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnSessionStart, "SMTP OnSessionStart", 0)
        $sourceType.EventTypes.Add($SmtpOnSessionStart)

        [string] $SmtpOnMessageStart = "{F6628C90-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnMessageStart, "SMTP OnMessageStart", 0)
        $sourceType.EventTypes.Add($SmtpOnMessageStart)

        [string] $SmtpOnPerRecipient = "{F6628C91-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnPerRecipient, "SMTP OnPerRecipient", 0)
        $sourceType.EventTypes.Add($SmtpOnPerRecipient)

        [string] $SmtpOnBeforeData = "{F6628C92-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnBeforeData, "SMTP OnBeforeData", 0)
        $sourceType.EventTypes.Add($SmtpOnBeforeData)

        [string] $SmtpOnSessionEnd = "{F6628C93-0D5E-11d2-AA68-00C04FA35B82}"
        $categoryManager.RegisterCategory($SmtpOnSessionEnd, "SMTP OnSessionEnd", 0)
        $sourceType.EventTypes.Add($SmtpOnSessionEnd)

        // Transport Events
        [string] $SmtpStoreDriver = "{59175850-e533-11d1-aa67-00c04fa345f6}"
        $categoryManager.RegisterCategory($SmtpStoreDriver, "SMTP StoreDriver", 0)
        $sourceType.EventTypes.Add($SmtpStoreDriver)

        [string] $SmtpOnTransportSubmission = $SmtpOnArrival
        $categoryManager.RegisterCategory($SmtpOnTransportSubmission, "SMTP OnTransportSubmission/OnArrival", 0)
        $sourceType.EventTypes.Add($SmtpOnTransportSubmission)

        [string] $SmtpOnPreCategorize = "{A3ACFB0D-83FF-11d2-9E14-00C04FA322BA}"
        $categoryManager.RegisterCategory($SmtpOnPreCategorize, "SMTP OnPreCategorize", 0)
        $sourceType.EventTypes.Add($SmtpOnPreCategorize)

        [string] $SmtpOnCategorize = "{960252A3-0A3A-11d2-9E00-00C04FA322BA}"
        $categoryManager.RegisterCategory($SmtpOnCategorize, "SMTP OnCategorize", 0)
        $sourceType.EventTypes.Add($SmtpOnCategorize)

        [string] $SmtpOnPostCategorize = "{76719654-05A6-11D2-9DFD-00C04FA322BA}"
        $categoryManager.RegisterCategory($SmtpOnPostCategorize, "SMTP OnPostCategorize", 0)
        $sourceType.EventTypes.Add($SmtpOnPostCategorize)

        [string] $SmtpOnTransportRouter = "{283430C9-1850-11d2-9E03-00C04FA322BA}"
        $categoryManager.RegisterCategory($SmtpOnTransportRouter, "SMTP OnTransportRouter", 0)
        $sourceType.EventTypes.Add($SmtpOnTransportRouter)

        [string] $SmtpMsgTrackLog = "{c6df52aa-7db0-11d2-94f4-00c04f79f1d6}"
        $categoryManager.RegisterCategory($SmtpMsgTrackLog, "SMTP MsgTrackLog", 0)
        $sourceType.EventTypes.Add($SmtpMsgTrackLog)

        [string] $SmtpDnsResolver = "{bd0b4366-8e03-11d2-94f6-00c04f79f1d6}"
        $categoryManager.RegisterCategory($SmtpDnsResolver, "SMTP DnsResolver", 0)
        $sourceType.EventTypes.Add($SmtpDnsResolver)

        [string] $SmtpMaxMsgSize = "{ebf159de-a67e-11d2-94f7-00c04f79f1d6}"
        $categoryManager.RegisterCategory($SmtpMaxMsgSize, "SMTP MaxMsgSize", 0)
        $sourceType.EventTypes.Add($SmtpMaxMsgSize)

        # Remove the duplicate, redundant SMTP SourceType
        $eventManager.SourceTypes.Remove("{4f803d90-fd85-11d0-869a-00c04fd65616}")

        $categoryDescription = $categoryManager.GetCategoryDescription($SmtpOnArrival, 0)
    }

    # Attempt to add the "ImplementsCategory" key for Sink using ProgID
    # If the sink is not registered on this machine, this will fail.
    $categoryManager.RegisterClassImplementsCategory($EventSinkProgId, $SmtpOnArrival)

    # Get the binding manager for this source
    $source = $sourceType.Sources($smtpInstanceSource)
    $manager = $source.GetBindingManager()
    $bindings = $manager.GetType().InvokeMember("Bindings", [System.Reflection.BindingFlags]::GetProperty, $null, $manager, $SmtpOnArrival)

    # Remove old bindings left over by previous version
    $remove = @($bindings | ForEach-Object { $_.ID })
    $remove | ForEach-Object {
        $bindings.GetType().InvokeMember("Remove", [System.Reflection.BindingFlags]::InvokeMethod, $null, $bindings, $_)
    }

    $binding = $bindings.GetType().InvokeMember("Add", [System.Reflection.BindingFlags]::InvokeMethod, $null, $bindings, $SmtpBindingId)
    $binding.DisplayName = $GatewayName
    $binding.SinkClass = $EventSinkProgId

    $sourceProperties = $binding.SourceProperties
    $sourceProperties.Add('Rule', $Rule)
    $sourceProperties.Add('Priority', $Priority)

    $sinkProperties = $binding.SinkProperties
    $sinkProperties.Add('ConfigFilePath', $ConfigFile)

    $binding.Save()
}

#######################################################################
# Test
#######################################################################

