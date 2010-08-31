using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;

namespace NHINDirect.Config.Client
{
    internal static class BindingFactory
    {
        public static BasicHttpBinding CreateBasic()
        {
            return CreateBasic(int.MaxValue, false);
        }
        
        public static BasicHttpBinding CreateBasic(int maxReceivedMessageSize, bool secure)
        {
            BasicHttpBinding binding = new BasicHttpBinding(BasicHttpSecurityMode.None);
            binding.AllowCookies = false;
            binding.UseDefaultWebProxy = true;
            binding.TransferMode = TransferMode.Buffered;
            binding.MessageEncoding = WSMessageEncoding.Text;
            binding.TextEncoding = Encoding.UTF8;
            binding.ReaderQuotas = System.Xml.XmlDictionaryReaderQuotas.Max;
            binding.MaxReceivedMessageSize = maxReceivedMessageSize;

            if (secure)
            {
                binding.Security.Mode = BasicHttpSecurityMode.Transport;
                binding.Security.Transport.ClientCredentialType = HttpClientCredentialType.Windows;
            }
            
            return binding;
        }
    }
}
