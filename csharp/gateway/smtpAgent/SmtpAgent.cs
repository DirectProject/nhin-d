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

namespace NHINDirect.SmtpAgent
{    
    public class SmtpAgent
    {
        string m_configFilePath; 
        SmtpAgentSettings m_settings;
        NHINDAgent m_agent;
        LogFile m_log;
                
        public SmtpAgent(string configFilePath)
        {
            m_configFilePath = configFilePath;
            this.Init(configFilePath);
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
            m_settings = SmtpAgentSettings.LoadFile(configFilePath);
            m_log = new LogFile(m_settings.LogSettings.CreateWriter());
            
            m_log.WriteLine("Init_Begin");
            try
            {
                this.InitAgent();
                
                m_log.WriteLine("Init_End");
            }
            catch (Exception error)
            {
                m_log.WriteLine("Init_Failed");
                m_log.WriteError(error);
                throw;
            }
        }
        
        void InitAgent()
        {
            m_log.WriteLine("CreateAgent_Begin");
            m_agent = m_settings.CreateAgent();
            m_log.WriteLine("CreateAgent_End");
            
            m_log.WriteLine("EnsureFolder_Begin");
            
            m_settings.RawMessage.EnsureFolders();
            m_settings.Incoming.EnsureFolders();
            m_settings.Outgoing.EnsureFolders();
            m_settings.BadMessage.EnsureFolders();
            
            m_log.WriteLine("EnsureFolder_End");
                        
            m_log.WriteLine("SubscribingToEvents_Begin");
            this.SubscribeToResolvers();
            m_log.WriteLine("SubscribingToEvents_End");
        }

        void SubscribeToResolvers()
        {
            DnsCertResolver dnsResolver = m_agent.PublicCertResolver as DnsCertResolver;
            if (dnsResolver != null)
            {
                dnsResolver.Error += this.WriteDnsError;
            }
        }
                
        void VerifyInitialized()
        {
            if (m_agent == null)
            {
                throw new InvalidOperationException("Not initialized");
            }
        }

        public void ProcessMessage(CDO.Message message)
        {            
            try
            {
                this.VerifyInitialized();
                
                this.CopyMessage(message, m_settings.RawMessage);
                                
                bool isIncoming = false;
                
                MessageEnvelope envelope = this.ProcessMessage(message, ref isIncoming);
                message.SetMessageStatus(CdoMessageStat.cdoStatSuccess);
                
                if (isIncoming)
                {
                    this.CopyMessage(message, m_settings.Incoming);
                }
                else
                {
                    this.CopyMessage(message, m_settings.Outgoing);
                }
                //
                // Generate Bounces
                //
                this.GenerateBounces(envelope);                
            }
            catch (Exception ex)
            {
                this.RejectMessage(message);
                m_log.WriteError(ex);
                throw;
            }
        }
        
        MessageEnvelope ProcessMessage(CDO.Message message, ref bool isIncoming)
        {
            MessageEnvelope envelope = this.CreateEnvelope(message);

            string messageText = this.ProcessMessage(envelope, ref isIncoming);
            if (string.IsNullOrEmpty(messageText))
            {
                throw new InvalidOperationException("Agent returned empty message");
            }
            
            message.SetMessageText(messageText, true);
            
            return envelope;
        }
                
        string ProcessMessage(MessageEnvelope envelope, ref bool isIncoming)
        {
            isIncoming = false;
                        
            envelope = this.Agent.Process(envelope, ref isIncoming);
            if (envelope == null)
            {
                throw new InvalidOperationException("Agent returned null envelope");
            }
            
            m_log.WriteLine(isIncoming ? "ProcessedIncoming" : "ProcessedOutgoing");
            
            return envelope.SerializeMessage();
        }

        MessageEnvelope CreateEnvelope(CDO.Message message)
        {
            NHINDAddressCollection recipientAddresses = null;
            NHINDAddress senderAddress = null;
            MessageEnvelope envelope;

            string messageText = message.GetMessageText();

            if (this.ExtractEnvelopeFields(message, ref recipientAddresses, ref senderAddress))
            {
                envelope = new MessageEnvelope(messageText, recipientAddresses, senderAddress);
            }
            else
            {
                envelope = new MessageEnvelope(messageText);
            }
            
            return envelope;
        }

        const string EnvelopeField_Recipients = @"http://schemas.microsoft.com/cdo/smtpenvelope/recipientlist";
        const string EnvelopeField_Sender = @"http://schemas.microsoft.com/cdo/smtpenvelope/senderemailaddress";

        bool ExtractEnvelopeFields(CDO.Message message, ref NHINDAddressCollection recipientAddresses, ref NHINDAddress senderAddress)
        {
            recipientAddresses = null;
            senderAddress = null;

            Fields fields = message.GetEnvelopeFields();
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
            //
            // In SMTP Server, the sender address can be empty if the message is from the postmaster
            //
            if (sender == "<>")
            {
                return false;
            }
            
            recipientAddresses = NHINDAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new NHINDAddress(sender);

            return true;
        }
        
        void RejectMessage(CDO.Message message)
        {
            try
            {
                message.AbortMessage();
                m_log.WriteLine("Rejected Message");
                
                this.CopyMessage(message, m_settings.BadMessage);
            }
            catch
            {
            }
        }
        
        void CopyMessage(CDO.Message message, MessageProcessingSettings settings)
        {
            if (!settings.HasCopyFolder)
            {
                return;
            }
            
            this.CopyMessage(message, settings.CopyFolder);
        }
        
        void CopyMessage(CDO.Message message, string folderPath)
        {
            try
            {            
                string fileName = Guid.NewGuid().ToString("D") + ".eml";
                message.SaveToFile(System.IO.Path.Combine(folderPath, fileName));            
            }
            catch(Exception ex)
            {
                m_log.WriteError(ex);
            }
        }
                                
        void GenerateBounces(MessageEnvelope envelope)
        {
            //
            // TODO
            //
        }
                                
        void WriteDnsError(DnsCertResolver service, Exception error)
        {
            m_log.WriteError(error);
        }
    }
}
