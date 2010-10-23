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
    public class PersonFacts
    {

        [Fact]
        public void EmptyPerson()
        {
            Person p = new Person();
            Assert.Equal("", p.ToString());
            Assert.Equal("^^^^^^", p.ToXCN());
        }

        [Fact]
        public void NullEquals()
        {
            Assert.True(new Person().Equals(new Person()));
        }

        [Fact]
        public void NameEquals()
        {
            Person p1 = new Person { First = "Bob", Last = "Smith" };
            Person p2 = new Person { First = "Bob", Last = "Smith" };
            Assert.True(p1.Equals(p2));
        }

        [Fact]
        public void MarcusWelbyToXCN()
        {
            Person p = new Person { First = "Marcus", Last = "Welby", Degree = "M.D.", Prefix = "Dr." };
            Assert.Equal("^Welby^Marcus^^^Dr.^M.D.", p.ToXCN());
        }

        [Fact]
        public void ToSourcePatientInfoReturnsCorrectNameValue()
        {
            Person p = new Person { First = "Bob", Last = "Smith", MI = "A" };
            Assert.Contains("PID-5|" + p.ToXCN(), p.ToSourcePatientInfoValues(null));
        }

        [Fact]
        public void ToSourcePatientInfoResturnsCorrectDobValue()
        {
            Person p = new Person { First = "John", Last = "Jacob", Dob = new DateTime(1969, 06, 20) };
            Assert.Contains("PID-7|" + p.Dob.ToHL7Date(), p.ToSourcePatientInfoValues(null));
        }

        [Fact]
        public void ToSourcePatientInfoResturnsCorrectSexValue()
        {
            Person p = new Person { First = "John", Last = "Jacob", Sex = Sex.Male };
            Assert.Contains("PID-8|M", p.ToSourcePatientInfoValues(null));
        }

        [Fact]
        public void ToSourcePatientInfoResturnsCorrectAddressValue()
        {
            Person p = new Person { First = "John", Last = "Jacob", Address = new PostalAddress { City = "Wiesbaden", State = "Hesse", Street = "Meadow Bath Ave", Zip="00000"} };
            Assert.Contains("PID-11|" + p.Address.Value.ToHL7Ad(), p.ToSourcePatientInfoValues(null));
        }
    }
}