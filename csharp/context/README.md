# Health.Direct.Context

## What is Direct.Health.Context

This is a Microsoft .NET reference implementation library for Expressing Context in Direct Messaging.  The implementation guid in draft for trial use is maintained at [www.directproject.org](http://wiki.directproject.org/file/detail/Implementation+Guide+for+Expressing+Context+in+Direct+Messaging+v1.0-DRAFT-2016122901.docx).

### Design of Context
The Context library uses the excellent [MimeKit](https://github.com/jstedfast/MimeKit) library as the base for creating and parsing mime messages.  The Direct Project libraries are primarily parsers and not builders. The current implementation is targeted for [Direct Context]((http://wiki.directproject.org/file/detail/Implementation+Guide+for+Expressing+Context+in+Direct+Messaging+v1.0-DRAFT-2016122901.docx)) version 1.0 compliance.

## Licesnse Information

Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

## Installing via NuGet

The easiest way to install MimeKit is via [NuGet](https://www.nuget.org/packages/DirectProjectDotNetContext/).

In Visual Studio's [Package Manager Console](http://docs.nuget.org/docs/start-here/using-the-package-manager-console),
simply enter the following command:

    Install-Package DirectProject.DotNet.Context


### Building Context

Creating a MimeMessage with Context

```csharp
//
// Context
//
var contextBuilder = new ContextBuilder();

contextBuilder
    .WithContentId(MimeUtils.GenerateMessageId())
    .WithDisposition("metadata.txt")
    .WithTransferEncoding(ContentEncoding.Base64)
    .WithVersion("1.0")
    .WithId(MimeUtils.GenerateMessageId())
    .WithPatientId(
        new PatientInstance
        {
            PidContext = "2.16.840.1.113883.19.999999",
            LocalPatientId = "123456"
        }.ToList()
    )
    .WithType(ContextStandard.Type.CategoryRadiology, ContextStandard.Type.ActionReport)
    .WithPurpose(ContextStandard.Purpose.PurposeResearch)
    .WithPatient(
        new Patient
        {
            GivenName = "John",
            SurName = "Doe",
            MiddleName = "Jacob",
            DateOfBirth = "1961-12-31",
            Gender = "M",
            PostalCode = "12345"
        }
    );

var context = contextBuilder.Build();

//
// Mime message and simple body
//
var message = new MimeMessage();
message.From.Add(new MailboxAddress("HoboJoe", "hobojoe@hsm.DirectInt.lab"));
message.To.Add(new MailboxAddress("Toby", "toby@redmond.hsgincubator.com"));
message.Subject = "Sample message with pdf and context attached";
message.Headers.Add(MailStandard.Headers.DirectContext, context.ContentId);

var body = new TextPart("plain")
{
    Text = @"Simple Body"
};

//
// Represent PDF attachment
// 
var pdf = new MimePart("application/pdf")
{
    ContentDisposition = new ContentDisposition(ContentDisposition.Attachment),
    FileName = "report.pdf",
    ContentTransferEncoding = ContentEncoding.Base64
};

var byteArray = Encoding.UTF8.GetBytes("Fake PDF (invalid)");
var stream = new MemoryStream(byteArray);
pdf.ContentObject = new ContentObject(stream);

//
// Multi part construction
//
var multiPart = new Multipart("mixed")
{
    body,
    contextBuilder.BuildMimePart(),
    pdf
};

message.Body = multiPart;            
```
### Parsing Context

Parsing context is very simple from a file or from a stream.  Using the message from the previous example the following code shows most examples of accessing context and the metadata.

```csharp
//
// Assert context can be serialized and parsed.
//
var messageParsed = MimeMessage.Load(message.ToString().ToStream());
Assert.True(messageParsed.ContainsDirectContext());
Assert.Equal(context.ContentId, messageParsed.DirectContextId());
var contextParsed = message.DirectContext();
Assert.NotNull(contextParsed);

//
// Headers
//
Assert.Equal("text", contextParsed.ContentType.MediaType);
Assert.Equal("plain", contextParsed.ContentType.MediaSubtype);
Assert.Equal("attachment", contextParsed.ContentDisposition.Disposition);
Assert.Equal("metadata.txt", contextParsed.ContentDisposition.FileName);
Assert.Equal(context.ContentId, contextParsed.ContentId);

//
// Metadata
//
Assert.Equal("1.0", contextParsed.Metadata.Version);
Assert.Equal(context.Metadata.Id, contextParsed.Metadata.Id);

//
// Metatdata PatientId
//
Assert.Equal("2.16.840.1.113883.19.999999:123456", contextParsed.Metadata.PatientId);
Assert.Equal(1, contextParsed.Metadata.PatientIdentifier.Count());
var patientIdentifiers = Enumerable.ToList(contextParsed.Metadata.PatientIdentifier);
Assert.Equal("2.16.840.1.113883.19.999999", patientIdentifiers[0].PidContext);
Assert.Equal("123456", patientIdentifiers[0].LocalPatientId);

//
// Metatdata Type
//
Assert.Equal("radiology/report", contextParsed.Metadata.Type.ToString());
Assert.Equal("radiology", contextParsed.Metadata.Type.Category);
Assert.Equal("report", contextParsed.Metadata.Type.Action);

//
// Metatdata Purpose
//
Assert.Equal("research", contextParsed.Metadata.Purpose);

//
// Metadata Patient
//
Assert.Equal("givenName=John; surname=Doe; middleName=Jacob; dateOfBirth=1961-12-31; gender=M; postalCode=12345", 
    contextParsed.Metadata.Patient.ToString());

Assert.Equal("John", contextParsed.Metadata.Patient.GivenName);
Assert.Equal("Doe", contextParsed.Metadata.Patient.SurName);
Assert.Equal("1961-12-31", contextParsed.Metadata.Patient.DateOfBirth);
```

The above code can also be ran from the context.tests project.
