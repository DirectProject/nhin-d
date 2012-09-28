using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    public class TimelyAndReliableFacts
    {
        [Fact]
        public void TestIsTimelyAndRequired_nullMimeMessage_assertFalse()
        {
            Message message = new Message();
            Assert.False(message.IsTimelyAndReliable());
        }

        [Fact]
        public void TestIsTimelyAndRequired_NoMNDOptionDetails_assertFalse()
        {
            Message message = new Message();
            message.From = new Header("from", "me@test.com");
            Assert.False(message.IsTimelyAndReliable());
        }

        [Fact]
        public void TestIsTimelyAndRequired_MDNOptionNotForTimely_assertFalse()
        {
            Message message = new Message("bob@nhind.hsgincubator.com", "toby@redmond.hsgincubator.com", "Test message");
            message.Headers.Add(MDNStandard.Headers.DispositionNotificationOptions, "X-NOT-SO-TIMELY");
            Assert.False(message.IsTimelyAndReliable());
        }

        [Fact]
        public void TestIsTimelyAndRequired_MDNOptionForTimely_assertTrue()
        {
            Message message = new Message("bob@nhind.hsgincubator.com", "toby@redmond.hsgincubator.com", "Test message");
            message.Headers.Add(MDNStandard.Headers.DispositionNotificationOptions, MDNStandard.DispositionOption_TimelyAndReliable);
            Assert.True(message.IsTimelyAndReliable());
        }
    }
}