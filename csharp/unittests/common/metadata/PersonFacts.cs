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
            Assert.Contains("PID-7|" + p.Dob.Value.ToHL7Date(), p.ToSourcePatientInfoValues(null));
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
            Person p = new Person { First = "John", Last = "Jacob", Address = new Address { City = "Wiesbaden", State = "Hesse", Street = "Meadow Bath Ave", Zip="00000"} };
            Assert.Contains("PID-11|" + p.Address.Value.ToHL7Ad(), p.ToSourcePatientInfoValues(null));
        }
    }
}
