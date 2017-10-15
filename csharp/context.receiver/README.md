# Health.Direct.Context.Loopback.Receiver

## What is Health.Direct.Context.Loopback.Receiver
This is a plugin receiver test receiver.  
The LoopBack receiver receives messages and parses context if it exists.  Then is rebuilds the context and sends a reply to the sender with the same context.  The value of such a receiver is to allow testing partners to test a context implementation with with the .net RI context implemenation.  If no context exists a failure DSN with a description is returned.  All other failures will result failure DSN's with the best attempt to indicated the failure.


## Licesnse Information

Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 


## Installing
Included with Direct-1.3.0.7-NET45_Beta installer.

### Configuring
Ensure the following two assemblies are in the Direct Project .NET Gateway folder, typically in C:\Program Files\Direct Project .NET Gateway

- Health.Direct.Context.Loopback.Receiver.dll
- MimeKitLite.dll

In SmtpAgentConfig.xml add the Health.Direct.Context.Loopback.Receiver plugin reciever to the IncomingRoutes section. Notice the AddressType is LoopBackContext.  

```xml
<IncomingRoutes>
    <Route>
      <AddressType>SMTP</AddressType>
      <CopyFolder>C:\inetpub\mailroot\Gateway\incoming</CopyFolder>
    </Route>
    <PluginRoute>
      <AddressType>LoopBackContext</AddressType>
      <Receiver>
        <TypeName>Health.Direct.Context.Loopback.Receiver.LoopBackContext, Health.Direct.Context.Loopback.Receiver</TypeName>
        <Settings>
          <PickupFolder>c:\inetpub\mailroot\pickup</PickupFolder>
        </Settings>
      </Receiver>
    </PluginRoute>
  </IncomingRoutes>
```

Now run the cli tool, "ConfigConsole.exe".  The following two commands will add a route for only the PingPong@Direct.North.Hobo.Lab email address to the Health.Direct.Context.Loopback.Receiver plugin receiver and set it's status to enabled.  After this restart with IISReset. <br />

Address_Add  PingPong@Direct.North.Hobo.Lab LoopBackContext <br />
Address_Status_Set PingPong@Direct.North.Hobo.Lab Enabled <br />
