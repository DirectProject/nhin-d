/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Xml.Serialization;
using System.Reflection;
using System.IO;

namespace Health.Direct.SmtpAgent
{
    public class ExtensibleXmlSerializer
    {
        XmlAttributeOverrides m_overrides;
        
        public ExtensibleXmlSerializer()
        {
            m_overrides = new XmlAttributeOverrides();
        }
        
        public XmlAttributeOverrides Overrides
        {
            get
            {
                return m_overrides;
            }
        }
        
        public void AddElementOption<T>(string propertyName, string elementName, Type tNew)
        {
            this.AddElementOption<T>(propertyName, new XmlElementAttribute(elementName, tNew));
        }
        
        public void AddElementOption<T>(string propertyName, params XmlElementAttribute[] elements)
        {
            XmlAttributes attributes = new XmlAttributes();
            Type type = typeof(T);
            
            this.Reflect(type, propertyName, attributes.XmlElements);
            for (int i = 0; i < elements.Length; ++i)
            {
                attributes.XmlElements.Add(elements[i]);
            }
            
            m_overrides.Add(type, propertyName, attributes);
        }
        
        public void Serialize<T>(TextWriter writer, T item)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T), m_overrides);
            serializer.Serialize(writer, item);
        }
        
        public void Serialize<T>(Stream stream, T item)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T), m_overrides);
            serializer.Serialize(stream, item);
        }

        public T Deserialize<T>(TextReader reader)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T), m_overrides);
            return (T)serializer.Deserialize(reader);
        }
        
        public T Deserialize<T>(Stream stream)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T), m_overrides);
            return (T) serializer.Deserialize(stream);
        }
        
        void Reflect(Type type, string propertyName, XmlElementAttributes xmlElements)
        {
            PropertyInfo property = type.GetProperty(propertyName);
            object[] attributes = property.GetCustomAttributes(true);

            for (int i = 0; i < attributes.Length; ++i)
            {
                XmlElementAttribute element = attributes[i] as XmlElementAttribute;
                if (element != null)
                {
                    xmlElements.Add(element);
                }
            }
        }
    }
}