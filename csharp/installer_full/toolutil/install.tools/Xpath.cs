using System.Runtime.InteropServices;
using System.Xml;

namespace install.tools
{
    [ComVisible(true), GuidAttribute("BA15EF13-A16D-4414-9203-53EF049185ED")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IPath
    {
        string XmlFilePath { get; set; }
        string SelectSingleAttribute(string xpath);
        void SetSingleAttribute(string xpath, string value);
    }

    [ComVisible(true), GuidAttribute("142E02A1-CEF8-4305-AB70-9A26F1ED0F41")]
    [ProgId("Direct.XpathTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class Xpath : IPath
    {
        private XmlDocument _document;
        private string _xmlFilePath;

        public string XmlFilePath
        {
            get { return _xmlFilePath; }
            set { _xmlFilePath = value;
                _document.Load(_xmlFilePath);
            }
        }
        

        public Xpath()
        {
            _document = new XmlDocument();
        }
        public string SelectSingleAttribute(string xpath)
        {
            XmlNode node = _document.SelectSingleNode(xpath);
            return node == null ? null : node.Value;
        }

        public void SetSingleAttribute(string xpath, string value)
        {
            XmlNode node = _document.SelectSingleNode(xpath);

            switch (node.NodeType)
            {
                case (XmlNodeType.Element):
                    node.InnerXml = value;
                    break;
                default:
                    node.Value = value;
                    break;
            }

            _document.Save(XmlFilePath);
        }
    }
}

