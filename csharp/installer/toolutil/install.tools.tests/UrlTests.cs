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


using System.Net.Sockets;
using Xunit;

namespace Health.Direct.Install.Tools.tests
{
    public class UrlTests
    {
        const string urlHost = "http://localhost/DnsService/RecordRetrievalService.svc/Records";
        const string urlHost2 = "http://localhost:80/DnsService/RecordRetrievalService.svc/Records";
        const string urlHost3 = "http://localhost:8080/DnsService/RecordRetrievalService.svc/Records";
        const string urlSecureHost = "https://localhost/DnsService/RecordRetrievalService.svc/Records";
        const string urlSecureHost2 = "https://localhost:443/DnsService/RecordRetrievalService.svc/Records";
        const string urlSecureHost3 = "https://localhost:444/DnsService/RecordRetrievalService.svc/Records";
        const string urlHostSecurePort = "https://localhost.lab:6693/DnsService/RecordRetrievalService.svc/Records";
  
        [Fact]
        public void UrlHostTest()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlHost));
            Assert.Equal("80", url.Port(urlHost));
            Assert.Equal("localhost:80", url.HostPort(urlHost));
            Assert.Equal("http", url.Scheme(urlHost));
        }

        [Fact]
        public void UrlHost2Test()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlHost2));
            Assert.Equal("80", url.Port(urlHost2));
            Assert.Equal("localhost:80", url.HostPort(urlHost2));
            Assert.Equal("http", url.Scheme(urlHost2));
        }

        [Fact]
        public void UrlHost3Test()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlHost3));
            Assert.Equal("8080", url.Port(urlHost3));
            Assert.Equal("localhost:8080", url.HostPort(urlHost3));
            Assert.Equal("http", url.Scheme(urlHost3));
        }

        [Fact]
        public void UrlSecureHostTest()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlSecureHost));
            Assert.Equal("443", url.Port(urlSecureHost));
            Assert.Equal("localhost:443", url.HostPort(urlSecureHost));
            Assert.Equal("https", url.Scheme(urlSecureHost));
        }

        [Fact]
        public void UrlSecureHost2Test()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlSecureHost2));
            Assert.Equal("443", url.Port(urlSecureHost2));
            Assert.Equal("localhost:443", url.HostPort(urlSecureHost2));
            Assert.Equal("https", url.Scheme(urlSecureHost2));
        }

        [Fact]
        public void UrlSecureHost3Test()
        {
            Url url = new Url();
            Assert.Equal("localhost", url.Host(urlSecureHost3));
            Assert.Equal("444", url.Port(urlSecureHost3));
            Assert.Equal("localhost:444", url.HostPort(urlSecureHost3));
            Assert.Equal("https", url.Scheme(urlSecureHost3));
        }

        [Fact]
        public void UrlHostPortTest()
        {
            Url url = new Url();
            Assert.Equal("localhost.lab", url.Host(urlHostSecurePort));
            Assert.Equal("6693", url.Port(urlHostSecurePort));
            Assert.Equal("localhost.lab:6693", url.HostPort(urlHostSecurePort));
            Assert.Equal("https", url.Scheme(urlHostSecurePort));
            
            
        }

        [Fact]
        public void ReplaceHostTest()
        {
            Url url = new Url();
            Assert.Equal("https://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab:6693").FullUrl);

            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab").FullUrl);


            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost2, "North.Hobo.Lab").FullUrl);

            Assert.Equal("http://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHost2, "North.Hobo.Lab").FullUrl);

            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost3, "North.Hobo.Lab").FullUrl);

            Assert.Equal("https://north.hobo.lab:444/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost3, "North.Hobo.Lab:444").FullUrl);


            Assert.Equal("http://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHost3, "North.Hobo.Lab").FullUrl);




            Assert.Equal("http://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHost2, "North.Hobo.Lab:80").FullUrl);

            Assert.Equal("http://north.hobo.lab:8080/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlHost2, "North.Hobo.Lab:8080").FullUrl);

            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost2, "North.Hobo.Lab:443").FullUrl);

            Assert.Equal("https://north.hobo.lab:444/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost2, "North.Hobo.Lab:444").FullUrl);


            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                , url.UpdateUrlHost(urlSecureHost3, "North.Hobo.Lab").FullUrl);



        }

        [Fact]
        public void ReplaceHostSchemeTest()
        {
            Url url = new Url();
            Assert.Equal("http://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc/Records"
                         , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab:6693")
                         .UpdateScheme("http").FullUrl);

            Assert.Equal("https://north.hobo.lab:6693/DnsService/RecordRetrievalService.svc/Records"
                         , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab:6693")
                         .UpdateScheme("https").FullUrl);

            Assert.Equal("https://north.hobo.lab/DnsService/RecordRetrievalService.svc/Records"
                         , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab:443")
                         .UpdateScheme("https").FullUrl);

            Assert.Equal("https://north.hobo.lab:444/DnsService/RecordRetrievalService.svc/Records"
                         , url.UpdateUrlHost(urlHostSecurePort, "North.Hobo.Lab:444")
                         .UpdateScheme("https").FullUrl);

            Assert.Equal("https://localhost/DnsService/TestService.aspx"
                         , url.UpdateUrlHost("http://localhost:443/DnsService/TestService.aspx"
                                             , "localhost:443").UpdateScheme("https").FullUrl);

        }

        [Fact]
        public void ReplaceHostPathAndQueryTest()
        {
            Url url = new Url();
            Assert.Equal("https://localhost.lab:6693/DnsService/test.aspx"
                         , url.UpdateUrlPathAndQuery(urlHostSecurePort, "DnsService/test.aspx").FullUrl);


            
            Assert.Equal("http://localhost.lab:6693/DnsService/test.aspx?myvar=1"
                         , url.UpdateUrlPathAndQuery("http://localhost.lab:6693/DnsService/RecordRetrievalService.svc/Records?myvar=1"
                         , "DnsService/test.aspx").FullUrl);


        }

        [Fact]
        public void BadUrlTest()
        {
            Url url = new Url();
            Assert.Equal("", url.Host("http://lajdf;/DnsService/TestService.aspx"));

            Assert.Equal("", url.Port("http://lajdf:99;99/DnsService/TestService.aspx"));
            
            Assert.Equal("", url.Scheme("ht;tp://lajdf/DnsService/TestService.aspx"));

            url.UpdateUrlHost("http://localhost/DnsService/TestService.aspx", "locahost:");
            
            
        }


        [Fact]
        public void ValidURlTest()
        {
            Url url = new Url();
            Assert.False(url.ValidUrl("http://lajdf;/DnsService/TestService.aspx"));

            Assert.False(url.ValidUrl("http://lajdf:99;99/DnsService/TestService.aspx"));
            
            Assert.False(url.ValidUrl("ht;tp://lajdf/DnsService/TestService.aspx"));

            Assert.True(url.ValidUrl(urlHost));
            Assert.True(url.ValidUrl(urlHostSecurePort));

        }
    }
}
