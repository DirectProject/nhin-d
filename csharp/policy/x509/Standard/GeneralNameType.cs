/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


namespace Health.Direct.Policy.X509.Standard
{
    /// <summary>
    /// General name types as describe in section 4.2.1.6 of RFC5280
    /// <remarks>
    /// <![CDATA[
    /// SubjectAltName ::= GeneralNames<br/>
    ///  
    /// GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName<br/>
    ///  
    /// GeneralName ::= CHOICE {<br/>
    ///      otherName                       [0]     OtherName,<br/>
    ///      rfc822Name                      [1]     IA5String,<br/>
    ///      dNSName                         [2]     IA5String,<br/>
    ///      x400Address                     [3]     ORAddress,<br/>
    ///      directoryName                   [4]     Name,<br/>
    ///      ediPartyName                    [5]     EDIPartyName,<br/>
    ///      uniformResourceIdentifier       [6]     IA5String,<br/>
    ///      iPAddress                       [7]     OCTET STRING,<br/>
    ///      registeredID                    [8]     OBJECT IDENTIFIER }<br/>
    /// ]]>
    /// </remarks>
    /// </summary>
    public enum GeneralNameType
    {
        OtherName = 0,
        RFC822Name = 1,
        DNSName = 2,
        X400Address = 3,
        DirectoryName = 4,
        EdiPartyName = 5,
        UniformResourceIdentifier = 6,
        IPAddress = 7,
        RegisteredId = 8

    }
}