﻿/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook     
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Linq;
using System.Runtime.InteropServices;
using System.Xml;

namespace Health.Direct.Install.Tools
{
    [ComVisible(true), GuidAttribute("BA15EF13-A16D-4414-9203-53EF049185ED")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IPath
    {
        /// <summary>
        /// Path to document.
        /// </summary>
        string XmlFilePath { get; set; }
        /// <summary>
        /// Get a single attribute
        /// </summary>
        /// <param name="xpath"></param>
        /// <returns></returns>
        string SelectSingleAttribute(string xpath);
        /// <summary>
        /// Set a single attribute
        /// </summary>
        /// <param name="xpath"></param>
        /// <param name="value"></param>
        void SetSingleAttribute(string xpath, string value);
        /// <summary>
        /// Get xml fragment by xpath
        /// </summary>
        /// <param name="xpath"></param>
        string GetFragment(string xpath);
        /// <summary>
        /// Adds a simple leaf node to the xml document
        /// </summary>
        /// <remarks>
        /// Xpath should not contain attributes.
        /// </remarks>
        /// <param name="xpath"></param>
        void CreateFragment(string xpath);

        /// <summary>
        /// Adds an xml fragment to the xml document
        /// </summary>
        /// <remarks>
        /// Xpath should not contain attributes.
        /// </remarks>
        /// <param name="xpathParent"></param>
        /// <param name="xmlFragment"></param>
        void CreateFullFragment(string xmlFragment, string xpathParent);

        /// <summary>
        /// Add xml fragment at a position in the document.
        /// </summary>
        /// <param name="xmlFragment"></param>
        /// <param name="xPathBefore">Position to be placed before xPathBefore XmlNode</param>
        void CreateFragmentBefore(string xmlFragment, string xPathBefore);

        /// <summary>
        /// Replace a xml fragment in the document.
        /// This is different then SetSingleAttribute in that it is a full xml node.
        /// </summary>
        /// <param name="xpath"></param>
        /// <param name="xmlFragment"></param>
        void ReplaceFragment(string xpath, string xmlFragment);

        /// <summary>
        /// Delete a leaf node in the xml document
        /// </summary>
        /// <param name="xpath"></param>
        void DeleteFragment(string xpath);

        /// <summary>
        /// Delete the node at xpath
        /// </summary>
        /// <param name="xpath"></param>
        void DeleteNode(string xpath);
    }

    [ComVisible(true), GuidAttribute("142E02A1-CEF8-4305-AB70-9A26F1ED0F41")]
    [ProgId("Direct.Installer.XPathTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class XPath : IPath
    {
        private readonly XmlDocument _document;
        private string _xmlFilePath;

        /// <inheritdoc />
        public string XmlFilePath
        {
            get { return _xmlFilePath; }
            set
            {
                _xmlFilePath = value;
                _document.Load(_xmlFilePath);
            }
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public XPath()
        {
            _document = new XmlDocument();
        }

        /// <inheritdoc />
        public string SelectSingleAttribute(string xpath)
        {
            var node = _document.SelectSingleNode(xpath);

            if (node is XmlElement)
            {
                return node.InnerText;
            }
            return node?.Value;
        }

        /// <inheritdoc />
        public void SetSingleAttribute(string xpath, string value)
        {
            var node = _document.SelectSingleNode(xpath);

            if (node != null)
            {
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

        /// <inheritdoc />
        public string GetFragment(string xpath)
        {
            var node = _document.SelectSingleNode(xpath);
            var nodeText = node?.OuterXml;

            return nodeText;
        }

        /// <inheritdoc />
        public void CreateFragment(string xpath)
        {
            var nodes = xpath.Split('/').ToList();
            nodes = nodes.FindAll(n => n.Trim().Length > 0);

            var element = _document.DocumentElement as XmlNode;

            if (element == null)
            {
                throw new Exception("Cannot find XML document!");
            }

            string buildPath = string.Empty;

            foreach (string node in nodes)
            {
                var  buildPathPrevious = buildPath;
                buildPath = buildPath + "/" + node;
                
                if (_document.SelectSingleNode(buildPath) != null)
                {
                    continue;
                }

                var xmlNode = _document.SelectSingleNode(buildPathPrevious);
                var newNode = _document.CreateElement(node);
                xmlNode?.AppendChild(newNode);
            }

            _document.Save(XmlFilePath);
        }
        /// <inheritdoc />
        public void CreateFullFragment(string xmlFragment, string xpathParent)
        {
            var node = _document.SelectSingleNode(xpathParent);
            if (node == null) return;

            var newNode = _document.CreateDocumentFragment();
            newNode.InnerXml = xmlFragment;
            if (_document.DocumentElement != null)
            {
                node.AppendChild(newNode);
                _document.Save(XmlFilePath);
            }
        }

        /// <inheritdoc />
        public void ReplaceFragment(string xpath, string xmlFragment)
        {
            var node = _document.SelectSingleNode(xpath);
            var newNode = _document.CreateDocumentFragment();
            newNode.InnerXml = xmlFragment;

            if (_document.DocumentElement != null)
            {
                if (node != null) _document.DocumentElement.RemoveChild(node);
                _document.DocumentElement.AppendChild(newNode);
            }
            _document.Save(XmlFilePath);
        }


        /// <inheritdoc />
        public void DeleteFragment(string xpath)
        {
            var node = _document.SelectSingleNode(xpath);

            if (_document.DocumentElement != null)
            {
                if (node != null) _document.DocumentElement.RemoveChild(node);
                _document.Save(XmlFilePath);
            }
        }
        /// <inheritdoc />
        public void DeleteNode(string xpath)
        {
            var node = _document.SelectSingleNode(xpath);
            if (node == null) return;

            if (_document.DocumentElement != null)
            {
                node.ParentNode?.RemoveChild(node);
                _document.Save(XmlFilePath);
            }
        }

        public void CreateFragmentBefore(string xmlFragment, string xPathBefore)
        {
            var newNode = _document.CreateDocumentFragment();
            newNode.InnerXml = xmlFragment;
            var beforeNode = _document.SelectSingleNode(xPathBefore);

            if (_document.DocumentElement != null)
            {
                _document.DocumentElement.InsertBefore(newNode, beforeNode);
                _document.Save(XmlFilePath);
            }
        }
    }
}

