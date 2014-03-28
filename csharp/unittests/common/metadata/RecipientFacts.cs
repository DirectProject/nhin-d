/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Metadata;

using Xunit;

namespace Health.Direct.Common.Tests.Metadata
{
    public class RecipientFacts
    {

        [Fact]
        public static void FromXonXcnWorksForSimpleCase()
        {
            Person p = new Person { First = "Bob", Last = "Smith", Degree = "MD" };
            Institution i = new Institution("Abc", "123");
            string xonxcn = String.Format("{0}|{1}", i.ToXON(), p.ToXCN());
            Recipient r = Recipient.FromXONXCNXTN(xonxcn);
            Assert.Equal(p, r.Person);
            Assert.Equal(i, r.Institution);
        }
        [Fact]
        public static void FromXonXcnMissingInstition()
        {
            Person p = new Person { First = "Bob", Last = "Smith", Degree = "MD" };
            string xonxcn = String.Format("|{0}", p.ToXCN());
            Recipient r = Recipient.FromXONXCNXTN(xonxcn);
            Assert.Equal(p, r.Person);
            Assert.Null(r.Institution);
        }
        [Fact]
        public static void FromXonXcnMissingPerson()
        {
            Institution i = new Institution("Abc", "123");
            string xonxcn = String.Format("{0}|", i.ToXON());
            Recipient r = Recipient.FromXONXCNXTN(xonxcn);
            Assert.Null(r.Person);
            Assert.Equal(i, r.Institution);
        }
        [Fact]
        public static void FromXonXcnMissingPersonAlternateFormat()
        {
            Institution i = new Institution("Abc", "123");
            string xonxcn = String.Format("{0}", i.ToXON());
            Recipient r = Recipient.FromXONXCNXTN(xonxcn);
            Assert.Null(r.Person);
            Assert.Equal(i, r.Institution);
        }
    }
}