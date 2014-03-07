﻿/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
    Joe Shook       jshook@krytiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class CertificateFacts : ConfigStoreTestBase
    {

        /// <summary>
        /// property to expose enumerable testing Certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCertificates
        {
            get
            {
                return ConfigStoreTestBase.TestCertificates;
            }
        }

        /// <summary>
        /// property to expose enumerable test certs extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCerts
        {
            get
            {
                return ConfigStoreTestBase.TestCerts;

            }
        }

        /// <summary>
        /// property to expose enumerable test cert bytpes extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static new IEnumerable<object[]> TestCertsBytes
        {
            get
            {
                return ConfigStoreTestBase.TestCertsBytes;

            }
        }

        /// <summary>
        ///A test for ValidStartDate
        ///</summary>
        [Fact]
        public void ValidStartDateTest()
        {
            Certificate target = new Certificate();
            DateTime expected = DateTime.UtcNow;
            target.ValidStartDate = expected;
            DateTime actual = target.ValidStartDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ValidEndDate
        ///</summary>
        [Fact]
        public void ValidEndDateTest()
        {
            Certificate target = new Certificate(); 
            DateTime expected = DateTime.UtcNow;
            target.ValidEndDate = expected;
            DateTime actual = target.ValidEndDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Thumbprint
        ///</summary>
        [Fact]
        public void ThumbprintTest()
        {
            Certificate target = new Certificate(); 
            const string expected = "somethumbprintteststring";
            target.Thumbprint = expected;
            string actual = target.Thumbprint;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for Status
        ///</summary>
        [Fact]
        public void StatusTest()
        {
            Certificate target = new Certificate();
            const EntityStatus expected = EntityStatus.New;
            target.Status = expected;
            EntityStatus actual = target.Status;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Owner
        ///</summary>
        [Fact]
        public void OwnerTest()
        {
            Certificate target = new Certificate();
            const string expected = "somevalidownerstring";
            target.Owner = expected;
            string actual = target.Owner;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void IDTest()
        {
            Certificate target = new Certificate();
            long expected = new Random().Next(1, MAXCERTPEROWNER * MAXDOMAINCOUNT);
            target.ID = expected;
            long actual = target.ID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for HasData
        ///</summary>
        [Fact]
        public void HasDataTest()
        {
            Certificate target = new Certificate(); 
            Assert.False(target.HasData);
            target.Data = System.Text.Encoding.UTF8.GetBytes("somerandomstring");
            Assert.True(target.HasData);

        }

        /// <summary>
        ///A test for Data
        ///</summary>
        [Fact]
        public void DataTest()
        {
            Certificate target = new Certificate();
            byte[] expected = System.Text.Encoding.UTF8.GetBytes("somerandomstring"); 
            target.Data = expected;
            byte[] actual = target.Data;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            Certificate target = new Certificate();
            DateTime expected = DateTime.UtcNow;
            target.CreateDate = expected;
            DateTime actual = target.CreateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ValidateHasData
        ///</summary>
        [Fact]
        public void ValidateHasDataTest()
        {
            Certificate target = new Certificate();
            try
            {
                target.ValidateHasData();
                Assert.True(false);
            }
            catch
            {
                Assert.True(true);
            }
            target.Data = System.Text.Encoding.UTF8.GetBytes("somerandomstring");
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
        ///A test for ToX509Collection
        ///</summary>
        [Fact]
        public void ToX509CollectionTest()
        {
            X509Certificate2Collection expected = new X509Certificate2Collection();
            List<Certificate> certs = new List<Certificate>(MAXDOMAINCOUNT);
            for (int i = 0; i < MAXDOMAINCOUNT; i++)
            {
                for (int t = 1; t <= MAXCERTPEROWNER; t++)
                {
                    expected.Add(GetDisposableTestCertFromPfx(i + 1, t));
                    certs.Add(GetCertificateFromTestCertPfx(i + 1, t));
                }
            }

            X509Certificate2Collection actual = Certificate.ToX509Collection(certs.ToArray());

            Assert.Equal(expected, actual);
            
            Assert.DoesNotThrow(() => actual.Close(true));
        }
        
        /// <summary>
        ///A test for ToX509Certificate
        ///</summary>
        [Theory]
        [PropertyData("TestCertificates")]
        public void ToX509CertificateTest(Certificate target)
        {
            X509Certificate2 expected = new DisposableX509Certificate2(target.ToX509Certificate().GetRawCertData());
            X509Certificate2 actual = target.ToX509Certificate();
            Assert.Equal(expected, actual);
            
        }

        [Theory]
        [PropertyData("TestCertificates")]
        public void TestForPrivateKeyExistence(Certificate cert)
        {
            X509Certificate2 cert2 =  cert.ToX509Certificate();
            Assert.True(cert2.HasPrivateKey);
        }
         
        /// <summary>
        ///A test for ToPublicX509Certificate
        ///</summary>
        [Theory]
        [PropertyData("TestCertificates")]
        public void ToPublicX509CertificateTest(Certificate target)
        {
            target.ExcludePrivateKey();
            X509Certificate2 cert = target.ToX509Certificate();
            Assert.False(cert.HasPrivateKey);            
        }

        /// <summary>
        ///A test for IsValid
        ///</summary>
        [Theory]
        [PropertyData("TestCertificates")]
        public void IsValidTest(Certificate target)
        {
            Assert.True(target.IsValid(DateTime.UtcNow));
            Assert.False(target.IsValid(target.ValidEndDate.AddSeconds(1)));
            Assert.False(target.IsValid(target.ValidStartDate.AddSeconds(-1)));
        }

        /// <summary>
        ///A test for Import
        ///</summary>
        [Theory]
        [PropertyData("TestCertsBytes")]
        public void ImportTest(byte[] sourceFileBytes)
        {
            string password = string.Empty; 
            X509Certificate2 expected = new X509Certificate2(sourceFileBytes, password); 
            X509Certificate2 actual = Certificate.Import(sourceFileBytes, password);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ExcludePrivateKey
        ///</summary>
        [Theory]
        [PropertyData("TestCertificates")]
        public void ExcludePrivateKeyTest(Certificate target)
        {
            target.ExcludePrivateKey();
            Assert.False(target.ToX509Certificate().HasPrivateKey);           
        }

        /// <summary>
        ///A test for ClearData
        ///</summary>
        [Theory]
        [PropertyData("TestCertificates")]
        public void ClearDataTest(Certificate target)
        {
            Assert.NotNull(target.Data);
            target.ClearData();
            Assert.Null(target.Data);
           
        }


    }
}