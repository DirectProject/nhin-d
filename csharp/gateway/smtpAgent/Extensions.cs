using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CDO;
using ADODB;

namespace NHINDirect.SmtpAgent
{
    //
    // Extensions on CDO & ADODB
    //
    public static class Extensions
    {
        public static CDO.Message LoadCDOMessage(string messageFile)
        {
            return LoadCDOMessageFromText(System.IO.File.ReadAllText(messageFile));
        }

        public static CDO.Message LoadCDOMessageFromText(string text)
        {
            CDO.Message message = new CDO.Message();

            ADODB._Stream stream = message.GetStream();

            stream.Position = 0;
            stream.WriteText(text, StreamWriteEnum.stWriteChar);
            stream.SetEOS();
            stream.Flush();

            return message;
        }
        
        public static void SaveToFile(this CDO.Message message, string filePath)
        {
            ADODB._Stream stream = message.GetStream();
            stream.SaveToFile(filePath, SaveOptionsEnum.adSaveCreateOverWrite);
        }
        
        const string EnvelopeField_Status = "http://schemas.microsoft.com/cdo/smtpenvelope/messagestatus";
        public static void SetMessageStatus(this CDO.Message message, CdoMessageStat status)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields == null || fields.Count == 0)
            {
                return;
            }
            fields.SetValue(EnvelopeField_Status, status);
        }

        public static void AbortMessage(this CDO.Message message)
        {
            message.SetMessageStatus(CdoMessageStat.cdoStatAbortDelivery);
        }

        public static void BadMessage(this CDO.Message message)
        {
            message.SetMessageStatus(CdoMessageStat.cdoStatBadMail);
        }
        
        public static string GetMessageText(this CDO.Message message)
        {
            ADODB._Stream stream = message.GetStream();
            return stream.ReadText(stream.Size);
        }

        public static void SetMessageText(this CDO.Message message, string messageText, bool save)
        {
            ADODB._Stream stream = message.GetStream();

            stream.Position = 0;
            stream.WriteText(messageText, StreamWriteEnum.stWriteChar);
            stream.SetEOS();
            stream.Flush();
            if (save)
            {
                try
                {
                    message.DataSource.Save();
                }
                catch
                {
                }
            }
        }
        
        public static string GetStringValue(this Fields fields, string name)
        {
            return (string) fields.GetValue(name);
        }

        public static object GetValue(this Fields fields, string name)
        {
            Field field = fields[name];
            if (field == null)
            {
                return null;
            }

            return field.Value;
        }

        public static void SetValue(this Fields fields, string name, object value)
        {
            Field field = fields[name];
            if (field != null)
            {
                field.Value = value;
            }
        }
        
        public static Fields GetEnvelopeFields(this CDO.Message message)
        {
            Fields fields = null;
            try
            {
                fields = message.EnvelopeFields;
            }
            catch
            {
            }
            
            return fields;
        }
    }
}
