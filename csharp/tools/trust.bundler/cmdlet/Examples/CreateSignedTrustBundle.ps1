add-pssnapin TrustBundlePsSnapIn  -ErrorVariable snapinError
if($snapinError -ne $null)
{
    Write-Host 'Install TrustBundle Commandlet.  InstallBundleSnap-in.ps1 installer script will peform the registration'
    EXIT 1            
} 


# Named resources to ignore
$ignoreArray = "Direct.Drhisp.Com Root CAKey.der"

# The -AsPlainText option is not safe.  Your password is not protected.  
# Use this if you want to protect the password in memory
# $secureString = Read-Host -AsSecureString 
$secureString = ConvertTo-SecureString "passw0rd!" -AsPlainText -Force    

Bundle-Anchors '..\..\..\..\..\certs\anchors' `
	-Ignore $ignoreArray  `
	-ErrorVariable bundleError  `
	-ErrorAction SilentlyContinue `
		| Sign-Bundle  '..\..\unittests\bundle.tests\bin\Debug\Certificates\redmond\Private\redmond.pfx' -PassKey $secureString `
		| Set-Content psSignedTestBundle.p7m  -enc Byte
    

    if($? -ne $true){
        Write-Host $bundleError
        EXIT 1
    }
    else{
        $currentPath = Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path
        Write-Host 'Successfuly exported to ' $currentPath'\psSignedTestBundle.p7m'
    }


EXIT 0 # nice to have exit codes if called from batch file.