using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using MimeKit;
using Xunit;

namespace Health.Direct.Context.Tests
{
    public class TestContext
    {
        [Theory]
        [InlineData("ContextTestFiles\\ContextSimple1.txtBase64")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtBinary")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtDefault")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtEightBit")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtQuotedPrintable")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtSevenBit")]
        //UUEncode not supported.   
        //[InlineData("ContextTestFiles\\ContextSimple1.txtUUEncode")]
        public void TestParseContext(string file)
        {
            var message = MimeMessage.Load(file);
            Assert.Equal("2ff6eaec83894520bbb872e5671ff49e@hobo.lab", message.DirectContextId());
            Assert.True(message.ContainsDirectContext());
            var context = message.DirectContext();
            Assert.NotNull(context);

            //
            // Headers
            //
            Assert.Equal("text", context.ContentType.MediaType);
            Assert.Equal("plain", context.ContentType.MediaSubtype);
            Assert.Equal("attachment", context.ContentDisposition.Disposition);
            Assert.Equal("metadata.txt", context.ContentDisposition.FileName);
            Assert.Equal("2ff6eaec83894520bbb872e5671ff49e@hobo.lab", context.ContentId);

            //
            // Metadata
            //
            Assert.Equal("1.0", context.Metadata.Version);
            Assert.Equal("<2142848@direct.example.com>", context.Metadata.Id);

            //
            // Metatdata PatientId
            //
            Assert.Equal("2.16.840.1.113883.19.999999:123456; 2.16.840.1.113883.19.888888:75774", context.Metadata.PatientId);
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
            Assert.Equal("radiology", context.Metadata.Type.Category);
            Assert.Equal("report", context.Metadata.Type.Action);

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
            Assert.NotNull(context.ContentId);
            Assert.Equal("text", context.ContentType.MediaType);
            Assert.Equal("plain", context.ContentType.MediaSubtype);
            Assert.Equal("attachment", context.ContentDisposition.Disposition);
            Assert.Equal(Context.FileNameValue, context.ContentDisposition.FileName);
        }


        [Theory]
        [InlineData("ContextTestFiles\\ContextSimple1.txtBase64")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtBinary")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtDefault")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtEightBit")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtQuotedPrintable")]
        [InlineData("ContextTestFiles\\ContextSimple1.txtSevenBit")]
        public void TestBuildContextRoundTrip(string file)
        {
            var directMessage = MimeMessage.Load(file);
            var context = directMessage.DirectContext();

            var contextBuilder = new ContextBuilder();

            contextBuilder
                .WithContentType(context.ContentType.MediaType, context.ContentType.MediaSubtype)
                .WithContentId(context.ContentId)
                .WithDisposition(context.ContentDisposition.FileName)
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
            message.Headers.Add("X-Direct-Context", messageBuilt.ContentId);

            var body = new TextPart("plain")
            {
                Text = @"Simple Body"
            };

            var multipart = new Multipart("mixed");
            multipart.Add(body);
            multipart.Add(contextBuilder.BuildMimePart());
            message.Body = multipart;


            var directMessageRebuilt = MimeMessage.Load(message.ToString().ToStream());
            var messageRebuilt = directMessageRebuilt.DirectContext();

            AssertEqual(context, messageRebuilt);
        }

        [Theory]
        [InlineData("ContextTestFiles\\ContextHL7.Default.txtBase64")]
        public void TestBuildContextEncapsulationRoundTrip(string file)
        {
            var directMessage = MimeMessage.Load(file);
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
                encapsulations.Single().DecodeBody());

            var echoMessage = EchoContext.Process(directMessage);
            var messageRebuilt = echoMessage.DirectContext();

            AssertEqual(context, messageRebuilt);
            AssertEqual(directMessage.SelectEncapulations().ToList(), echoMessage.SelectEncapulations().ToList());
        }

        private void AssertEqual(List<MimePart> expected, List<MimePart> actual)
        {
            if (expected == null) throw new ArgumentNullException(nameof(expected));
            if (actual == null) throw new ArgumentNullException(nameof(actual));

            Assert.Equal(expected.Count, actual.Count);

            for(int i = 0; i < expected.Count ; i++)
            {
                Assert.True(actual[i].DecodeBody().Trim().Length > 5);  // just ensure content exits
                Assert.Equal(expected[i].DecodeBody(), actual[i].DecodeBody());
            }
        }

        private void AssertEqual(Context context, Context messageRebuilt)
        {
            if (context == null) throw new ArgumentNullException(nameof(context));
            if (messageRebuilt == null) throw new ArgumentNullException(nameof(messageRebuilt));

            Assert.Equal(context.ContentType.MediaType, messageRebuilt.ContentType.MediaType);
            Assert.Equal(context.ContentType.MediaSubtype, messageRebuilt.ContentType.MediaSubtype);
            Assert.Equal(context.ContentId, messageRebuilt.ContentId);
            Assert.Equal(context.ContentDisposition.Disposition, messageRebuilt.ContentDisposition.Disposition);
            Assert.Equal(context.ContentDisposition.FileName, messageRebuilt.ContentDisposition.FileName);

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


    public static class Extensions
    {
        public static Stream ToStream(this string str)
        {
            var stream = new MemoryStream();
            var writer = new StreamWriter(stream);
            writer.Write(str);
            writer.Flush();
            stream.Position = 0;
            return stream;
        }
    }
}
