using System.IO;
using Health.Direct.Context.Loopback.Receiver;
using Health.Direct.SmtpAgent;
using MimeKit;
using Xunit;

namespace Health.Direct.Context.Receiver.tests
{
    public class TestReceiver
    {
        private const string TestPickupFolder = @"C:\inetpub\mailroot\testpickup";

        public TestReceiver()
        {
            if (Directory.Exists(TestPickupFolder))
            {
                foreach (var enumerateFile in Directory.EnumerateFiles(TestPickupFolder))
                {
                    File.Delete(enumerateFile);
                }
            }
            else
            {
                Directory.CreateDirectory(TestPickupFolder);
            }
        }

        [Theory]
        [InlineData("ContextTestFiles\\ContextSimple1.txtDefault")]
        public void TestConstructContextSuccess(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            var mimeMessage = MimeMessage.Load(Path.Combine(
                settings.PickupFolder,
                testFileName));

            Assert.StartsWith("<",mimeMessage.Headers["X-Direct-Context"]);
            Assert.EndsWith(">", mimeMessage.Headers["X-Direct-Context"]);
        }

        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\MDN.eml")]
        public void TestConstructContextIgnoreMDN(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            Assert.False(File.Exists(Path.Combine(
                settings.PickupFolder,
                testFileName)));

        }

        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\DSN.eml")]
        public void TestConstructContextIgnorDSN(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            Assert.False(File.Exists(Path.Combine(
                settings.PickupFolder,
                testFileName)));

        }


        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\ContextMissing.eml")]
        public void TestConstructContextNoContext(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));
            
            var resultMessage = MimeMessage.Load(
                Path.Combine(
                    settings.PickupFolder,
                    testFileName));

           Assert.Equal("Object reference not set to an instance of an object.", resultMessage.TextBody);
            
        }

        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\NoDirectContextHeader.eml")]
        public void TestConstructContextNoContextId(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            var resultMessage = MimeMessage.Load(
                Path.Combine(
                    settings.PickupFolder,
                    testFileName));

            Assert.Equal("No Context found", resultMessage.TextBody);
        }


        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\ContextUnparsableType.eml")]
        public void TestConstructContextUnParsableType(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            var resultMessage = MimeMessage.Load(
                Path.Combine(
                    settings.PickupFolder,
                    testFileName));

            Assert.Equal("Context Error=InvalidType", resultMessage.TextBody);
        }

        [Theory]
        [InlineData("ContextTestFiles\\BadContext\\ContextUnparsablePatient.eml")]
        public void TestConstructContextUnParsablePatient(string file)
        {
            var smtpMessage = new CDOSmtpMessage(SmtpAgent.Extensions.LoadCDOMessage(file));
            var testFileName = SmtpAgent.Extensions.CreateUniqueFileName();

            var receiver = new LoopBackContext
            {
                TestFilename = testFileName
            };

            var settings = new PongContextSettings()
            {
                PickupFolder = TestPickupFolder
            };

            receiver.Settings = settings;
            Assert.True(receiver.Receive(smtpMessage));

            var resultMessage = MimeMessage.Load(
                Path.Combine(
                    settings.PickupFolder,
                    testFileName));

            Assert.Equal("Context Error=InvalidPatient", resultMessage.TextBody);
        }
    }
}
