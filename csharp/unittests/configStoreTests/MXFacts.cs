/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class MXFacts
    {
        /// <summary>
        ///A test for UpdateDate
        ///</summary>
        [Fact]
        public void UpdateDateTest()
        {
            MX target = new MX(1, "testSMTPDomainName", 1);
            DateTime expected = DateTime.UtcNow;
            DateTime actual;
            target.UpdateDate = expected;
            actual = target.UpdateDate;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for SMTPDomainName
        ///</summary>
        [Fact]
        public void SMTPDomainNameTest()
        {
            MX target = new MX(1, "testSMTPDomainName", 1);
            string expected = "some.smtp.name.value";
            string actual;
            target.SMTPDomainName = expected;
            actual = target.SMTPDomainName;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for Preference
        ///</summary>
        [Fact]
        public void PreferenceTest()
        {
            MX target = new MX(1, "testSMTPDomainName", 1);
            int expected = 200;
            int actual;
            target.Preference = expected;
            actual = target.Preference;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void IDTest()
        {
            MX target = new MX(1, "testSMTPDomainName", 1);
            long expected = 777;
            long actual;
            target.ID = expected;
            actual = target.ID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            MX target = new MX(1, "testSMTPDomainName", 1);
            DateTime expected = DateTime.UtcNow;
            DateTime actual;
            target.CreateDate = expected;
            actual = target.CreateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CopyFixed
        ///</summary>
        [Fact]
        public void CopyFixedTest()
        {
            MX source = new MX(1, "testSMTPDomainName", 1);
            MX target = new MX();
            target.CopyFixed(source);
            Assert.Equal(source.ID, target.ID);
            Assert.Equal(source.CreateDate, target.CreateDate);
            Assert.Equal(source.DomainID, target.DomainID);
            Assert.Equal(source.SMTPDomainName, target.SMTPDomainName);
            Assert.Equal(source.UpdateDate, target.UpdateDate);
            Assert.Equal(source.Preference, target.Preference);

        }
        
        /// <summary>
        ///A test for ApplyChanges
        ///</summary>
        [Fact]
        public void ApplyChangesTest()
        {
            MX target = new MX(1, "target.smtp.name",1);

            Assert.Equal(1, target.DomainID);
            Assert.Equal(1, target.Preference);
            Assert.Equal("target.smtp.name", target.SMTPDomainName);
            
            MX source = new MX(1, "source.smtp.name",2);


            Assert.Equal(1, source.DomainID); 
            Assert.Equal(2, source.Preference);
            Assert.Equal("source.smtp.name", source.SMTPDomainName);
            
            target.ApplyChanges(source);
            Assert.Equal(1, target.DomainID);
            Assert.Equal(source.Preference, target.Preference);
            Assert.Equal(source.SMTPDomainName, target.SMTPDomainName);
        }

        /// <summary>
        ///A test for MX Constructor
        ///</summary>
        [Fact]
        public void MXConstructorTest2()
        {
            MX target = new MX();
            Assert.Equal(String.Empty, target.SMTPDomainName);
            Assert.Equal(0, target.Preference);
            Assert.Equal(target.CreateDate, target.UpdateDate);
        }

        /// <summary>
        ///A test for MX Constructor
        ///</summary>
        [Fact]
        public void MXConstructorTest1()
        {
            long domainID = 1;
            string SMTPDomainName = "some.smtp.name.here";
            MX target = new MX(domainID, SMTPDomainName);
            Assert.Equal(domainID, target.DomainID);
            Assert.Equal(SMTPDomainName, target.SMTPDomainName);
            Assert.Equal(0, target.Preference);
            Assert.Equal(target.CreateDate, target.UpdateDate);
        }

        /// <summary>
        ///A test for MX Constructor
        ///</summary>
        [Fact]
        public void MXConstructorTest()
        {

            long domainID = 1;
            string SMTPDomainName = "some.smtp.name.here";
            short preference = 777;
            MX target = new MX(domainID, SMTPDomainName, preference);
            Assert.Equal(domainID, target.DomainID);
            Assert.Equal(SMTPDomainName, target.SMTPDomainName);
            Assert.Equal(preference, target.Preference);
            Assert.Equal(target.CreateDate, target.UpdateDate);
            
        }
    }
}