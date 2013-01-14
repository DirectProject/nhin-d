//---------------------------
//	Introduction
//---------------------------
An anchor bundle is a PKCS7 FILE (*.p7b) file that contains certificates. Bundles are published at Bundle Urls. 

You can find Blue Button bundles at:
https://secure.bluebuttontrust.org/

//---------------------------
//	Setup
//---------------------------
To use bundles, you must get a fresh clean build of the C# tree and redeploy:
- Database Schema
- Config Service
- Gateway 
- ConfigConsole

==Database ==
Update your Database schema to support bundles. 
   * csharp\config\store\Schema.sql
   * Run either the Bundle Specific statements only (bottom of the file) OR the entire script

==Config Service==
Redeploy your Config Service with the updated bits. 

Update WebConfig. Add the "Bundles" endpoint to your CertificateServiceBehavior, as shown below.
    <system.serviceModel>
        <services>
            <service behaviorConfiguration="Health.Direct.Config.Service.CertificateServiceBehavior"
                name="Health.Direct.Config.Service.CertificateService">
                <endpoint address="Certificates" binding="basicHttpBinding" contract="Health.Direct.Config.Service.ICertificateStore" />
                <endpoint address="Anchors" binding="basicHttpBinding" contract="Health.Direct.Config.Service.IAnchorStore" />
                <endpoint address="Bundles" binding="basicHttpBinding" contract="Health.Direct.Config.Service.IBundleStore" />
            </service>

==Plugin Resolvers==
The .NET RI includes 2 new Plugin Anchor Resolvers:
- BundleResolver
- MultiSourceAnchorResolver

Your Gateway resolves Anchor bundles using these plugin resolvers. 

Both resolvers are implemented in csharp\resolverPlugin
They are compiled into Health.Direct.ResolverPlugins.dll.

Copy Health.Direct.ResolverPlugins.dll to your Gateway directory. 

==Gateway===
Configure the Gateway to pull Anchors from bundles AND your existing Anchor store. 

<SmtpAgentConfig>
  .....
  <Anchors>
    <PluginResolver>
      <!-- NEW Resolver that COMBINES Anchors from multiple sources into a single list-->
      <Definition>
        <TypeName>Health.Direct.ResolverPlugins.MultiSourceAnchorResolver, Health.Direct.ResolverPlugins</TypeName>
        <Settings>
		  <!-- New Bundle Resolver -->
          <BundleResolver>
            <ClientSettings>
              <Url>http://localhost/ConfigService/CertificateService.svc/Bundles</Url>
            </ClientSettings>
            <CacheSettings>
              <Cache>true</Cache>
              <NegativeCache>true</NegativeCache>
              <!-- Set cache to longer duration in production -->
              <CacheTTLSeconds>60</CacheTTLSeconds>
            </CacheSettings>
            <MaxRetries>1</MaxRetries>
            <Timeout>30000</Timeout> <!-- In milliseconds -->
            <VerifySSL>true</VerifySSL>
          </BundleResolver>
		  <!-- Standard Resolver that pulls from Anchor store -->
          <ServiceResolver>
            <ClientSettings>
              <Url>http://localhost/ConfigService/CertificateService.svc/Anchors</Url>
            </ClientSettings>
            <CacheSettings>
              <Cache>true</Cache>
              <NegativeCache>true</NegativeCache>
              <CacheTTLSeconds>60</CacheTTLSeconds>
            </CacheSettings>
          </ServiceResolver>
        </Settings>
      </Definition>
    </PluginResolver>
  </Anchors>
....

//---------------------------
//	INSTALLING BUNDLES
//---------------------------
ConfigConsole.exe contains new command to support bundles. 
 * Type "commands bundle" to get a list.

To configure your DOMAIN to use a specific bundle, ADD the bundle with your domain as "owner". 
- Like Anchors, you can mark a bundle as "Incoming" and/or "Outgoing". 
- Make sure your Bundle is "Enabled". Else it will not be used. 

BUNDLE_ADD
Add a new bundle definition into the config store - if it doesn't already exist.
owner url forIncoming forOutgoing [status]
	owner: domain or address
    url: url for bundle
    forIncoming: (true/false) Use bundle to verify trust of INCOMING messages
	forOutgoing: (true/false) use bundle to verify trust of OUTGOING messages
	status: (new/enabled/disabled, default new) status field

E.g. 
BUNDLE_ADD YOURDOMAIN BundleUrl true true Enabled

The Gateway bundle cache must expire before your newly configured bundle is picked up. 
Or you can restart the Gateway. 

##BLUE BUTTON BUNDLES##
ConfigConsole has a command that sets up Blue Button Bundles for you. 

BUNDLE_ADD_BLUEBUTTON
Ensure that blue button bundles are registered for the given owner.

owner bundleType forIncoming forOutgoing [status]
	owner: domain or address
    bundleType: Provider | Patient | ProviderTest | PatientTest
	forIncoming: (true/false) Use bundle to verify trust of INCOMING messages
    forOutgoing: (true/false) use bundle to verify trust of OUTGOING messages
    status: (new/enabled/disabled, default new) status field

E.g.
// 
// Set up the domain nhind.hsgincubator.com to use the BlueButton Provider Bundle for Incoming Messages
// Meaning - if the incoming message was SIGNED by a certificate issued by a CA whose certificate is in the Provider bundle...
// ... ACCEPT THE MESSAGE
//
BUNDLE_ADD_BLUEBUTTON nhind.hsgincubator.com Provider true false Enabled
