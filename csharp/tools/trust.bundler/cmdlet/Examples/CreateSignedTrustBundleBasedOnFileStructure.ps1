add-pssnapin TrustBundlePsSnapIn  -ErrorVariable snapinError
if($snapinError -ne $null)
{
    Write-Host 'Install TrustBundle Commandlet.'
	Write-Host 'Example...'
	Write-Host 'set-alias installutil $env:windir\Microsoft.NET\Framework64\v2.0.50727\installutil.exe'
	Write-Host 'installutil Health.Direct.Trust.Commandlet.dll'

    EXIT 1            
} 

# This is the password: "passw0rd!"
$secureString = Read-Host -AsSecureString  


Bundle-Anchors `
	-ErrorVariable bundleError  `
	-ErrorAction SilentlyContinue `
        | Sign-Bundle  -PassKey $secureString `
		| Set-Content psTestBundleWithMetadataFileStructure.p7m  -enc Byte
    

    if($? -ne $true){
        Write-Host $bundleError
        EXIT 1
    }
    else{
        $currentPath = Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path
        Write-Host 'Successfuly exported to ' $currentPath'\psTestBundleWithMetadataFileStructure.p7m'
    }


EXIT 0 # nice to have exit codes if called from batch file.

# To pretty print trust bundle:
# openssl cms -print -cmsout -in demoSignedWithMetadata.p7m -inform der

