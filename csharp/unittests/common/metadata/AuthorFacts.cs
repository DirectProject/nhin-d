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
using System.Linq;

using Health.Direct.Common.Metadata;

using Xunit;

namespace Health.Direct.Common.Tests.Metadata
{
    public class AuthorFacts
    {


        [Fact]
        public void SimpleAuthor()
        {
            Author a = new Author();
            a.Person = new Person { First = "Tom", Last = "Jones" };
            Assert.Equal("Tom Jones", a.Person.ToString());
            Assert.Equal("^Jones^Tom^^^^", a.Person.ToXCN());
        }

        [Fact]
        public void AuthorWithMI()
        {
            Author a = new Author();
            a.Person = new Person { First="Tom", Last="Jones", MI="A" };
            Assert.Equal("Tom A Jones", a.Person.ToString());
            Assert.Equal("^Jones^Tom^A^^^", a.Person.ToXCN());
        }

        [Fact]
        public void MD()
        {
            Author a = new Author();
            a.Person = new Person { First = "Tom", Last = "Jones", Degree = "M.D." };
            Assert.Equal("Tom Jones, M.D.", a.Person.ToString());
            Assert.Equal("^Jones^Tom^^^^M.D.", a.Person.ToXCN());
        }

        [Fact]
        public void FullMonty()
        {
            Author a = new Author();
            a.Person = new Person { Prefix = "Sir", First = "Tom", MI = "A", Last = "Jones", Suffix = "Jr.", Degree = "M.D., PhD, JD, PharmD" };
            Assert.Equal("Sir Tom A Jones, Jr., M.D., PhD, JD, PharmD", a.Person.ToString());
            Assert.Equal("^Jones^Tom^A^Jr.^Sir^M.D., PhD, JD, PharmD", a.Person.ToXCN());
        }

        [Fact]
        public void Celebrity()
        {
            Author a = new Author();
            a.Person = new Person { Prefix = "Lady", First = "Gaga", Degree = "M.A." };
            Assert.Equal("Lady Gaga, M.A.", a.Person.ToString());
            Assert.Equal("^^Gaga^^^Lady^M.A.", a.Person.ToXCN());
        }

        [Fact]
        public void OnlyLastName()
        {
            Author a = new Author();
            a.Person = new Person { Last = "Bond", Degree = "Licensed to Kill" };
            Assert.Equal("Bond, Licensed to Kill", a.Person.ToString());
            Assert.Equal("^Bond^^^^^Licensed to Kill", a.Person.ToXCN());
        }

        [Fact]
        public void Institutions()
        {
            Author a = new Author();
            a.Institutions.Add(new Institution("ONC-U"));
            Assert.Contains("ONC-U", a.Institutions.Select( i => i.ToXON()));
        }
    }
}