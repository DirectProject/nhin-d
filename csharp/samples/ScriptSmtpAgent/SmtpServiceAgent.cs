using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Net.Mail;
using NHINDirect.Agent;
using NHINDirect.Agent.Config;
using NHINDirect.Certificates;
using NHINDirect.Diagnostics;
using System.Diagnostics;
using CDO;
using ADODB;

namespace NHINDirect.ScriptAgent
{    
    public class SmtpServiceAgent
    {
        string m_name;
        string m_configFilePath; 
        NHINDAgent m_agent;
        LogFile m_log;
        
        public SmtpServiceAgent(string serviceName, string configFilePath)
        {
            if (string.IsNullOrEmpty(serviceName))
            {
                throw new ArgumentException();
            }

            m_name = serviceName;
            m_configFilePath = configFilePath;

            this.Init(configFilePath);
        }

        public string Name
        {
            get
            {
                return m_name;
            }
        }
        
        public NHINDAgent Agent
        {
            get
            {
                this.VerifyInitialized();
                return m_agent;
            }
        }
        
        public LogFile Log
        {
            get
            {
                return m_log;
            }
        }
        
        public string ConfigFilePath
        {
            get
            {
                return m_configFilePath;
            }
        }
        
        void Init(string configFilePath)
        {
            SmtpAgentSettings settings = SmtpAgentSettings.LoadFile(configFilePath);
            m_log = new LogFile(settings.LogSettings.CreateWriter());
            
            m_log.WriteLine("Init_Begin");
            try
            {
                m_log.WriteLine("Creating Agent");
                m_agent = settings.CreateAgent();
                m_log.WriteLine("Agent Creation Complete");
                this.SubscribeToResolvers();
                
                m_log.WriteLine("Init_Complete");
            }
            catch (Exception error)
            {
                m_log.WriteLine("Init_Failed");
                m_log.WriteError(error);
                throw;
            }
        }

        void VerifyInitialized()
        {
            if (m_agent == null)
            {
                throw new InvalidOperationException("Not initialized");
            }
        }

        public bool ProcessMessage(CDO.Message message, ref bool isIncoming)
        {
            try
            {
                NHINDAddressCollection recipientAddresses = null;
                NHINDAddress senderAddress = null;

                string messageText = message.GetMessageText();

                if (this.ExtractEnvelopeFields(message, ref recipientAddresses, ref senderAddress))
                {
                    messageText = this.ProcessMessage(messageText, recipientAddresses, senderAddress, ref isIncoming);
                }
                else
                {
                    messageText = this.ProcessMessage(messageText, ref isIncoming);
                }
                message.SetMessageText(messageText, true);  

                return true;
            }
            catch (Exception ex)
            {
                m_log.WriteError(ex);
            }

            return false;
        }
        
        //
        // rcpto, mailfrom are formatted like SMTP server formats them
        //
        public string ProcessMessage(string messageText, string rcpto, string mailFrom, ref bool isIncoming)
        {
            this.VerifyInitialized();

            isIncoming = false;

            NHINDAddressCollection recipientAddresses = NHINDAddressCollection.ParseSmtpServerEnvelope(rcpto);
            NHINDAddress senderAddress = new NHINDAddress(mailFrom);

            MessageEnvelope processed = m_agent.Process(messageText, recipientAddresses, senderAddress, ref isIncoming);
            m_log.WriteLine(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            //
            // TODO: Generate Bounces for rejected recipients
            //
            return processed.SerializeMessage();
        }

        public string ProcessMessage(string messageText, NHINDAddressCollection recipients, NHINDAddress sender, ref bool isIncoming)
        {
            this.VerifyInitialized();

            isIncoming = false;
            MessageEnvelope processed = m_agent.Process(messageText, recipients, sender, ref isIncoming);
            m_log.WriteLine(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            //
            // TODO: Generate Bounces for rejected recipients
            //
            return processed.SerializeMessage();
        }

        public string ProcessMessage(string messageText, ref bool isIncoming)
        {
            this.VerifyInitialized();

            isIncoming = false;
            MessageEnvelope processed = m_agent.Process(messageText, ref isIncoming);
            m_log.WriteLine(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            //
            // TODO: Generate Bounces for rejected recipients
            //
            return processed.SerializeMessage();
        }

        public const string EnvelopeField_Recipients = @"http://schemas.microsoft.com/cdo/smtpenvelope/recipientlist";
        public const string EnvelopeField_Sender = @"http://schemas.microsoft.com/cdo/smtpenvelope/senderemailaddress";
                
        bool ExtractEnvelopeFields(CDO.Message message, ref NHINDAddressCollection recipientAddresses, ref NHINDAddress senderAddress)
        {
            recipientAddresses = null;
            senderAddress = null;
            
            Fields fields = null;
            try
            {
                fields = message.EnvelopeFields;
            }
            catch
            {
            }
            
            if (fields == null || fields.Count == 0)
            {
                return false;
            }

            string recipients = fields.GetStringValue(EnvelopeField_Recipients);
            if (string.IsNullOrEmpty(recipients))
            {
                throw new NotSupportedException("Recipients required");
            }
            string sender = fields.GetStringValue(EnvelopeField_Sender);
            if (string.IsNullOrEmpty(sender))
            {
                throw new NotSupportedException("Sender required");
            }

            recipientAddresses = NHINDAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new NHINDAddress(sender);
            
            return true;
        }
                
        void SubscribeToResolvers()
        {
            DnsCertResolver dnsResolver = m_agent.PublicCertResolver as DnsCertResolver;
            if (dnsResolver != null)
            {
                dnsResolver.Error += this.WriteDnsError;
            }
        }
        
        void WriteDnsError(DnsCertResolver service, Exception error)
        {
            m_log.WriteError(error);
        }
    }
}
