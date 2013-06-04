add-pssnapin TrustBundlePsSnapIn  -ErrorVariable snapinError
if($snapinError -ne $null)
{
    Write-Host 'Install TrustBundle Commandlet.  InstallBundleSnap-in.ps1 installer script will peform the registration'
    EXIT 1            
} 

    #Named resources to ignore
    $ignoreArray = "Direct.Drhisp.Com Root CAKey.der"

    Bundle-Anchors '..\..\..\..\..\certs\anchors' `
		-Ignore $ignoreArray `
		-Metadata "<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>" `
		-ErrorVariable bundleError  `
		-ErrorAction SilentlyContinue `
		    | Set-Content psTestBundleWithMetadata.p7b  -enc Byte
    

    if($? -ne $true){
        Write-Host $bundleError
        EXIT 1
    }
    else{
        $currentPath = Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path
        Write-Host 'Successfuly expored to ' $currentPath'\psTestBundleWithMetadata.p7b'
    }


EXIT 0 # nice to have exit codes if called from batch file.



