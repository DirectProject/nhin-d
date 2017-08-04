using System.IO;
using System.Linq;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Context;
using Health.Direct.Common.Mime;
using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    public class TestContext
    {
        [Theory]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtBase64")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtBinary")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtDefault")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtEightBit")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtQuotedPrintable")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtSevenBit")]
        //UUEncode not supported.   
        //[InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtUUEncode")]
        public void TestParseContext(string file)
        {
            var text = File.ReadAllText(file);
            var message = Message.Load(text);
            Assert.Equal("<2ff6eaec83894520bbb872e5671ff49e@hobo.lab>", message.DirectContextID.Value);
            Assert.True(message.ContainsDirectContext());
            var context = message.DirectContext();
            Assert.NotNull(context);

            //
            // Metadata Headers
            //
            Assert.Equal("text/plain", context.ContentType);
            Assert.Equal("attachment; filename=metadata.txt", context.ContentDisposition);
            Assert.Equal("<2ff6eaec83894520bbb872e5671ff49e@hobo.lab>", context.ContentID);

            //
            // Metadata
            //
            Assert.Equal("1.0", context.Metadata.Version);
            Assert.Equal("<2142848@direct.example.com>", context.Metadata.Id);

            //
            // PatientId
            //
            Assert.Equal("2.16.840.1.113883.19.999999:123456;2.16.840.1.113883.19.888888:75774", context.Metadata.PatientId);
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
            Assert.Equal("radiology", context.Metadata.Type.Category.ToString());
            Assert.Equal("report", context.Metadata.Type.Action.ToString());

            //
            // Purpose
            //
            Assert.Equal("research", context.Metadata.Purpose);

            //
            // Patient
            //
            Assert.Equal("givenName=John; surname=Doe; middleName=Jacob; dateOfBirth=1961-12-31; gender=M; postalCode=12345", context.Metadata.Patient.ToString());
            Assert.Equal("John", context.Metadata.Patient.GivenName);
            Assert.Equal("Doe", context.Metadata.Patient.SurName);
            Assert.Equal("1961-12-31", context.Metadata.Patient.DateOfBirth);
        }


        [Fact]
        public void TestConstructContext()
        {
            var context = new Context();
            Assert.NotNull(context.ContentID);
            Assert.Equal(MimeStandard.MediaType.TextPlain, context.ContentType);
            Assert.Equal($"attachment; filename={Context.FileName}", context.ContentDisposition);
        }

        [Theory]
        [InlineData("")]
        public void TestBuildContext(string encoding)
        {
            
        }
    }
}
