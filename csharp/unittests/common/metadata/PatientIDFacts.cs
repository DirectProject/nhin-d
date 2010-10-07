using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Metadata;
using Xunit;
using Xunit.Extensions;

namespace NHINDirect.Tests.metadata
{
    public class PatientIDFacts
    {
        [Fact]
        public void IdenticalPatientIDsAreEqual()
        {
            PatientID p1 = new PatientID("abc", "123", "foo");
            PatientID p2 = new PatientID("abc", "123", "foo");
            Assert.True(p1.Equals(p2));
            //TODO: why doesn't this work?
            // Assert.Equal(p1, p2);
        }
    }
}
