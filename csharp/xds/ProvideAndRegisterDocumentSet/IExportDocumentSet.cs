using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel;

namespace NHINDirect.XDS
{
    public interface IExportDocumentSet
    {
        ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(string xdsMetadata, string xdsDocument, string endpointUrl, string certThumbprint);
        ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(XmlDocument xdsMetadata, XmlDocument xdsDocument, string endpointUrl, string certThumbprint);
        ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(XmlDocument xdsMetadata, XmlDocument xdsDocument, EndpointAddress endpointAddress, string certThumbprint);
        ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(XmlDocument xdsMetadata, XmlDocument xdsDocument, EndpointAddress endpointAddress, X509Certificate2 clientCert);
        
    }
}
