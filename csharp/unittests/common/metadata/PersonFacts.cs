using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Metadata;
using Xunit;

namespace NHINDirect.Tests.metadata
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
        public void MarcusWelbyToXCN()
        {
            Person p = new Person { First = "Marcus", Last = "Welby", Degree = "M.D.", Prefix = "Dr." };
            Assert.Equal("^Welby^Marcus^^^Dr.^M.D.", p.ToXCN());
        }
    }
}
