using ADODB;

namespace Health.Direct.Sample.ScriptAgent
{
    public static class Extensions
    {
        public static CDO.Message LoadCDOMessage(string messageFile)
        {
            CDO.Message message = new CDO.Message();

            ADODB._Stream stream = message.GetStream();

            stream.Position = 0;
            stream.WriteText(System.IO.File.ReadAllText(messageFile), StreamWriteEnum.stWriteChar);
            stream.SetEOS();
            stream.Flush();

            return message;
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
                message.DataSource.Save();
            }
        }
        
        public static string GetStringValue(this Fields fields, string name)
        {
            Field field = fields[name];
            if (field == null)
            {
                return null;
            }
            
            return (string) field.Value;
        }
    }
}