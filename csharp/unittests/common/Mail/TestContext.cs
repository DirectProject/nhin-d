using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Context;
using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    public class TestContext
    {
        [Fact]
        public void TestParseContext()
        {
            var text = File.ReadAllText("Mail\\ContextTestFiles\\ContextSimple1.txtDefault");
            var message = Message.Load(text);
            var context = message.DirectContext();
            Assert.NotNull(context);

            //
            // Metadata Headers
            //
            Assert.Equal("text/plain", context.ContentType);
            Assert.Equal("attachment; filename=ContextSimple1.txt", context.ContentDisposition);
            Assert.Equal("2ff6eaec83894520bbb872e5671ff49e", context.ContentID);

            //
            // Metadata
            //
            Assert.Equal("1.0", context.Metadata.Version);
            Assert.Equal("2ff6eaec83894520bbb872e5671ff49e", context.Metadata.Id);

            //
            // PatientId
            //
            Assert.Equal("2.16.840.1.113883.19.999999:123456; 2.16.840.1.113883.19.888888:75774", context.Metadata.PatientId);
            Assert.Equal(2, context.Metadata.PatientIdentifier.Count());
            var patientIdentifiers = context.Metadata.PatientIdentifier.ToList();
            Assert.Equal("2.16.840.1.113883.19.999999", patientIdentifiers[0].PidContext);
            Assert.Equal("123456", patientIdentifiers[0].LocalPatientId);
            Assert.Equal("2.16.840.1.113883.19.888888", patientIdentifiers[1].PidContext);
            Assert.Equal("75774", patientIdentifiers[1].LocalPatientId);

            //
            // Type
            //
            Assert.Equal("radiology/report", context.Metadata.Type.ToString());
            Assert.Equal("radiology", context.Metadata.Type.Category);
            Assert.Equal("report", context.Metadata.Type.Action);

            //
            // Purpose
            //
            Assert.Equal("research", context.Metadata.Purpose);

            //
            // Patient
            //
            Assert.Equal("givenName=John; surname=Doe; dateOfBirth=1961-12-31", context.Metadata.Patient.ToString());
            Assert.Equal("John", context.Metadata.Patient.GivenName);
            Assert.Equal("Doe", context.Metadata.Patient.SurName);
            Assert.Equal("1961-12-31", context.Metadata.Patient.DateOfBirth);
        }
    }
}
