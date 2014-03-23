/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Text;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class CertPolicyFacts : ConfigStoreTestBase
    {
        
        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void IDTest()
        {
            CertPolicy target = new CertPolicy();
            long expected = new Random().Next(1, 10);
            target.ID = expected;
            long actual = target.ID;
            Assert.Equal(expected, actual);
        }


        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            CertPolicy target = new CertPolicy();
            DateTime expected = DateTime.UtcNow;
            target.CreateDate = expected;
            DateTime actual = target.CreateDate;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for HasData
        ///</summary>
        [Fact]
        public void HasDataTest()
        {
            CertPolicy target = new CertPolicy();
            Assert.False(target.HasData);
            target.Data = Encoding.UTF8.GetBytes("somerandomstring");
            Assert.True(target.HasData);
        }

        /// <summary>
        ///A test for Data
        ///</summary>
        [Fact]
        public void DataTest()
        {
            CertPolicy target = new CertPolicy();
            byte[] expected = Encoding.UTF8.GetBytes("somerandomstring");
            target.Data = expected;
            byte[] actual = target.Data;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for ValidateHasData
        ///</summary>
        [Fact]
        public void ValidateHasDataTest()
        {
            CertPolicy target = new CertPolicy();
            try
            {
                target.ValidateHasData();
                Assert.True(false);
            }
            catch
            {
                Assert.True(true);
            }
            target.Data = Encoding.UTF8.GetBytes("somerandomstring");
            try
            {
                target.ValidateHasData();
                Assert.True(true);
            }
            catch
            {
                Assert.True(false);
            }
        }

        

        /// <summary>
        ///A test for Name
        ///</summary>
        [Fact]
        public void NameTest()
        {
            CertPolicy target = new CertPolicy();
            string expected = "Policy_1";
            target.Name = expected;
            string actual = target.Name;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        /// Add Child test
        ///</summary>
        [Fact]
        public void AddCertPolicyGroupTest()
        {
            CertPolicy target = new CertPolicy();
            target.ID = 99;
            CertPolicyGroupMap targetChild = new CertPolicyGroupMap();
            target.CertPolicyGroupMap.Add(targetChild);
        }

        

    }
}
