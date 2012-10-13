/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook     
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.IO;
using Health.Direct.Install.Tools;
using Xunit;

namespace install.tools.tests
{
    public class EndPointTests
    {
        /// <summary>
        /// This grabs the service part of the url and appends ?wsdl and requests the wsdl,
        /// then it interogates the Location address for the expected parameter.  
        /// So http://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc?wsdl
        /// would validates successfully when it found:
        /// <soap:address location="http://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc/Records"/>
        /// in the wsdl.  All of our services publish the location.
        /// Sometimes the in the wsdl the fully qualified name is no used but rather the netbios name.  
        /// So this would pass also.
        /// <soap:address location="http://north:6693/DnsService/RecordRetrievalService.svc/Records"/>
        /// </summary>
        [Fact]
        public void Test()
        {
            EndPoint endPoint = new EndPoint();
            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/CertificateService.svc/Certificates"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/CertificateService.svc/Anchors"));

            Assert.False(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/CertificateService.svc/Addresses"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/DomainManagerService.svc/Addresses"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/DomainManagerService.svc/Domains"));
            
            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/DomainManagerService.svc/DnsRecords"));
            
            Assert.False(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/DomainManagerService.svc/Authentication"));
            
            Assert.True(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/AuthManagerService.svc/Authentication"));

            Assert.False(endPoint.TestWcfSoapConnection(
                "http://DirectGateway.South.Hobo.Lab/ConfigService/AuthManagerService.svc/Certificates"));

            Assert.False(endPoint.TestWcfSoapConnection(
               "http://badhostname/ConfigService/AuthManagerService.svc/Certificates"));
            
        }

        /// <summary>
        /// This grabs the service part of the url and appends ?wsdl and requests the wsdl,
        /// then it interogates the Location address for the expected parameter.  
        /// So http://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc?wsdl
        /// would validates successfully when it found:
        /// <soap:address location="http://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc/Records"/>
        /// in the wsdl.  All of our services publish the location.
        /// Sometimes the in the wsdl the fully qualified name is no used but rather the netbios name.  
        /// So this would pass also.
        /// <soap:address location="http://north:6693/DnsService/RecordRetrievalService.svc/Records"/>
        /// </summary>
        [Fact]
        public void TestFqdnToNetBios()
        {
            EndPoint endPoint = new EndPoint();
            Assert.True(endPoint.TestWcfSoapConnection(
                "http://engr-dir-be.engr.kryptiq.com/dnsservice/recordretrievalservice.svc/Records"));
        }

         

        [Fact]
        public void TestLocalhost()
        {
            EndPoint endPoint = new EndPoint();
            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/CertificateService.svc/Certificates"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/CertificateService.svc/Anchors"));

            Assert.False(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/CertificateService.svc/Addresses"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/DomainManagerService.svc/Addresses"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/DomainManagerService.svc/Domains"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/DomainManagerService.svc/DnsRecords"));

            Assert.False(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/DomainManagerService.svc/Authentication"));

            Assert.True(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/AuthManagerService.svc/Authentication"));

            Assert.False(endPoint.TestWcfSoapConnection(
                "http://localhost/ConfigService/AuthManagerService.svc/Certificates"));

            Assert.False(endPoint.TestWcfSoapConnection(
               "http://badhostname/ConfigService/AuthManagerService.svc/Certificates"));

        }


        [Fact]
        public void TestEmpty()
        {
            EndPoint endPoint = new EndPoint();
            Assert.False(endPoint.TestWcfSoapConnection(String.Empty));

            endPoint = new EndPoint();
            Assert.False(endPoint.TestWcfSoapConnection("  "));

        }


    }
}
