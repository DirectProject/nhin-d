using System;
using System.IO;
using System.Linq;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Context;
using Health.Direct.Common.Mime;
using MimeKit;
using Xunit;
using MimeEntity = Health.Direct.Common.Mime.MimeEntity;

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
            // Headers
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
            // Metatdata PatientId
            //
            Assert.Equal("2.16.840.1.113883.19.999999:123456;2.16.840.1.113883.19.888888:75774", context.Metadata.PatientId);
            Assert.Equal(2, context.Metadata.PatientIdentifier.Count());
            var patientIdentifiers = context.Metadata.PatientIdentifier.ToList();
            Assert.Equal("2.16.840.1.113883.19.999999", patientIdentifiers[0].PidContext);
            Assert.Equal("123456", patientIdentifiers[0].LocalPatientId);
            Assert.Equal("2.16.840.1.113883.19.888888", patientIdentifiers[1].PidContext);
            Assert.Equal("75774", patientIdentifiers[1].LocalPatientId);

            //
            // Metatdata Type
            //
            Assert.Equal("radiology/report", context.Metadata.Type.ToString());
            Assert.Equal("radiology", context.Metadata.Type.Category.ToString());
            Assert.Equal("report", context.Metadata.Type.Action.ToString());

            //
            // Metatdata Purpose
            //
            Assert.Equal("research", context.Metadata.Purpose);

            //
            // Metadata Patient
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
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtBase64")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtBinary")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtDefault")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtEightBit")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtQuotedPrintable")]
        [InlineData("Mail\\ContextTestFiles\\ContextSimple1.txtSevenBit")]
        public void TestBuildContextRoundTrip(string file)
        {
            var text = File.ReadAllText(file);
            var directMessage = Message.Load(text);
            var context = directMessage.DirectContext();

            var contextBuilder = new ContextBuilder();

            contextBuilder
                .WithContentType(context.ContentType)
                .WithContentId(context.ContentID)
                .WithDisposition(context.ContentDisposition)
                .WithTransferEncoding(context.ContentTransferEncoding)
                .WithVersion(context.Metadata.Version)
                .WithId(context.Metadata.Id)
                .WithPatientId(context.Metadata.PatientId)
                .WithType(context.Metadata.Type.Category, context.Metadata.Type.Action)
                .WithPurpose(context.Metadata.Purpose)
                .WithPatient(
                    context.Metadata.Patient.GivenName,
                    context.Metadata.Patient.SurName,
                    context.Metadata.Patient.MiddleName,
                    context.Metadata.Patient.DateOfBirth,
                    context.Metadata.Patient.Gender,
                    context.Metadata.Patient.SocialSecurityNumber,
                    context.Metadata.Patient.TelephoneNumber,
                    context.Metadata.Patient.StreetAddress,
                    context.Metadata.Patient.PostalCode)
                .WithEncapsulation(context.Metadata.Encapsulation?.Type);

            var messageBuilt = contextBuilder.Build();

            //
            // Now build a message to contain the context.
            // No good api yet to work with just the MailPart
            //
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress("Jean", "Jean@opsstation.lab"));
            message.To.Add(new MailboxAddress("Joe", "Joe@hobo.lab"));
            message.Subject = "Need more memory";
            message.MessageId = $"<{Guid.NewGuid():N}@{Environment.MachineName}>";
            message.Headers.Add("X-Direct-Context", messageBuilt.ContentID);

            var body = new TextPart("plain")
            {
                Text = @"Simple Body"
            };

            var multipart = new Multipart("mixed");
            multipart.Add(body);
            multipart.Add(contextBuilder.BuildMimePart());
            message.Body = multipart;


            var directMessageRebuilt = Message.Load(message.ToString());
            var messageRebuilt = directMessageRebuilt.DirectContext();

            AssertEqual(context, messageRebuilt);
        }

        [Theory]
        [InlineData("Mail\\ContextTestFiles\\ContextHL7.Default.txtBase64")]
        public void TestBuildContextEncapsulationRoundTrip(string file)
        {
            var text = File.ReadAllText(file);
            var directMessage = Message.Load(text);
            var context = directMessage.DirectContext();

            //
            // Metadata
            //
            Assert.Equal("1.0", context.Metadata.Version);
            Assert.Equal("<2142848@direct.example.com>", context.Metadata.Id);
            Assert.Equal("hl7v2", context.Metadata.Encapsulation.Type);
            var encapsulations = directMessage.SelectEncapulations().ToList();
            Assert.Equal(1, encapsulations.Count());
            Assert.Equal(@"MSH |^ ~\&| SENDING_APPLICATION | SENDING_FACILITY | RECEIVING_APPLICATION | RECEIVING_FACILITY | 20110613083617 || ADT ^ A01 | 934576120110613083617 | P | 2.3 ||||
EVN | A01 | 20110613083617 |||
PID | 1 || 135769 || MOUSE ^ MICKEY ^|| 19281118 | M ||| 123 Main St.^^ Lake Buena Vista ^ FL ^ 32830 || (407)939 - 1289 ^^^ theMainMouse@disney.com ||||| 1719 | 99999999 ||||||||||||||||||||
PV1 | 1 | O |||||^^^^^^^^|^^^^^^^^",
                MimeEntity.DecodeBody(encapsulations.Single()).ToString());

            var contextBuilder = new ContextBuilder();

            contextBuilder
                .WithContentType(context.ContentType)
                .WithContentId(context.ContentID)
                .WithDisposition(context.ContentDisposition)
                .WithTransferEncoding(context.ContentTransferEncoding)
                .WithVersion(context.Metadata.Version)
                .WithId(context.Metadata.Id)
                .WithEncapsulation(context.Metadata.Encapsulation?.Type);

            var messageBuilt = contextBuilder.Build();

            //
            // Now build a message to contain the context.
            // No good api yet to work with just the MailPart
            //
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress("Jean", "Jean@opsstation.lab"));
            message.To.Add(new MailboxAddress("Joe", "Joe@hobo.lab"));
            message.Subject = "Need more memory";
            message.MessageId = $"<{Guid.NewGuid():N}@{Environment.MachineName}>";
            message.Headers.Add("X-Direct-Context", messageBuilt.ContentID);

            var body = new TextPart("plain")
            {
                Text = @"Simple Body"
            };

            var multipart = new Multipart("mixed");
            multipart.Add(body);
            multipart.Add(contextBuilder.BuildMimePart());
            message.Body = multipart;


            var directMessageRebuilt = Message.Load(message.ToString());
            var messageRebuilt = directMessageRebuilt.DirectContext();

            AssertEqual(context, messageRebuilt);
        }


        private void AssertEqual(Context context, Context messageRebuilt)
        {
            Assert.Equal(context.ContentType, messageRebuilt.ContentType);
            Assert.Equal(context.ContentID, messageRebuilt.ContentID);
            Assert.Equal(context.ContentDisposition, messageRebuilt.ContentDisposition);

            Assert.Equal(context.Metadata.Version, messageRebuilt.Metadata.Version);
            Assert.Equal(context.Metadata.Id, messageRebuilt.Metadata.Id);

            Assert.Equal(context.Metadata.PatientId, messageRebuilt.Metadata.PatientId);

            Assert.Equal(context.Metadata.Type?.Action, messageRebuilt.Metadata.Type?.Action);
            Assert.Equal(context.Metadata.Type?.Category, messageRebuilt.Metadata.Type?.Category);

            Assert.Equal(context.Metadata.Patient?.GivenName, messageRebuilt.Metadata.Patient?.GivenName);
            Assert.Equal(context.Metadata.Patient?.SurName, messageRebuilt.Metadata.Patient?.SurName);
            Assert.Equal(context.Metadata.Patient?.MiddleName, messageRebuilt.Metadata.Patient?.MiddleName);
            Assert.Equal(context.Metadata.Patient?.DateOfBirth, messageRebuilt.Metadata.Patient?.DateOfBirth);
            Assert.Equal(context.Metadata.Patient?.Gender, messageRebuilt.Metadata.Patient?.Gender);
            Assert.Equal(context.Metadata.Patient?.SocialSecurityNumber, messageRebuilt.Metadata.Patient?.SocialSecurityNumber);
            Assert.Equal(context.Metadata.Patient?.StreetAddress, messageRebuilt.Metadata.Patient?.StreetAddress);
            Assert.Equal(context.Metadata.Patient?.PostalCode, messageRebuilt.Metadata.Patient?.PostalCode);

            Assert.Equal(context.Metadata.Encapsulation?.Type, messageRebuilt.Metadata.Encapsulation?.Type);
            
        }
    }


}
