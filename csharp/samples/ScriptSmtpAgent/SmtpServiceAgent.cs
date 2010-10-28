using System;

using ADODB;

using Health.Direct.Agent;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Diagnostics;

namespace Health.Direct.Sample.ScriptAgent
{
    public class SmtpServiceAgent
    {
        string m_name;
        string m_configFilePath; 
        DirectAgent m_agent;
        ILogger m_logger;
        
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
        
        public DirectAgent Agent
        {
            get
            {
                this.VerifyInitialized();
                return m_agent;
            }
        }
        
        public ILogger Logger
        {
            get
            {
                return m_logger;
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
            m_logger = Log.For(this);
            
            m_logger.Info("Init_Begin");
            try
            {
                m_logger.Info("Creating Agent");
                m_agent = settings.CreateAgent();
                m_logger.Info("Agent Creation Complete");
                this.SubscribeToResolvers();
                
                m_logger.Info("Init_Complete");
            }
            catch (Exception error)
            {
                m_logger.Error("Init_Failed");
                m_logger.Error(error);
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
                DirectAddressCollection recipientAddresses = null;
                DirectAddress senderAddress = null;

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
                m_logger.Error(ex);
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

            DirectAddressCollection recipientAddresses = DirectAddressCollection.ParseSmtpServerEnvelope(rcpto);
            DirectAddress senderAddress = new DirectAddress(mailFrom);

            MessageEnvelope processed = m_agent.Process(messageText, recipientAddresses, senderAddress, ref isIncoming);
            m_logger.Info(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            //
            // TODO: Generate Bounces for rejected recipients
            //
            return processed.SerializeMessage();
        }

        public string ProcessMessage(string messageText, DirectAddressCollection recipients, DirectAddress sender, ref bool isIncoming)
        {
            this.VerifyInitialized();

            isIncoming = false;
            MessageEnvelope processed = m_agent.Process(messageText, recipients, sender, ref isIncoming);
            m_logger.Info(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
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
            m_logger.Info(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            //
            // TODO: Generate Bounces for rejected recipients
            //
            return processed.SerializeMessage();
        }

        public const string EnvelopeField_Recipients = @"http://schemas.microsoft.com/cdo/smtpenvelope/recipientlist";
        public const string EnvelopeField_Sender = @"http://schemas.microsoft.com/cdo/smtpenvelope/senderemailaddress";
                
        bool ExtractEnvelopeFields(CDO.Message message, ref DirectAddressCollection recipientAddresses, ref DirectAddress senderAddress)
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

            recipientAddresses = DirectAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new DirectAddress(sender);
            
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
        
        void WriteDnsError(ICertificateResolver resolver, Exception error)
        {
            m_logger.Error(error);
        }
    }
}