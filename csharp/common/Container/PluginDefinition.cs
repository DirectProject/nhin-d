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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Reflection;

namespace Health.Direct.Common.Container
{
    /// <summary>
    /// If your plugin implements this interface, will get initialized...
    /// </summary>
    public interface IPlugin
    {
        /// <summary>
        /// Initialize the plugin
        /// </summary>
        /// <param name="pluginDef">plugin definition</param>
        void Init(PluginDefinition pluginDef);
    }
    
    /// <summary>
    /// Some features such as message routing may require loading one or more plugins. 
    /// This simple class lets you define them via Xml Config files
    /// </summary>
    public class PluginDefinition
    {
        /// <summary>
        /// New plugin definition
        /// </summary>
        public PluginDefinition()
        {
        }
        
        /// <summary>
        /// Optional settings for this plugin
        /// </summary>
        [XmlAnyElement]
        public XmlNode Settings
        {
            get; set;
        }
        
        /// <summary>
        /// Are optional settings specified?
        /// </summary>
        [XmlIgnore]
        public bool HasSettings
        {
            get 
            { 
                return (this.Settings != null); 
            }
        }

        /// <summary>
        /// The .NET type that implements this plugin. Should be a qualified type name in the following format:
        ///     TypeName, Assembly Name
        ///     Health.Direct.SmtpAgent.SmtpMessageForwarder, Health.Direct.SmtpAgent
        /// </summary>
        [XmlElement]
        public string TypeName
        {
            get;
            set;
        }
        
        /// <summary>
        /// Was a type name actually provided?
        /// </summary>        
        [XmlIgnore]
        public bool HasTypeName
        {
            get 
            {
                return !(string.IsNullOrEmpty(this.TypeName));
            }
        }
        
        /// <summary>
        /// Create an instance of the plugin 
        /// </summary>
        /// <returns>object</returns>                
        public object Create()
        {
            if (!this.HasTypeName)
            {
                throw new NotSupportedException("No type name specified");
            }
            
            object obj = Activator.CreateInstance(Type.GetType(this.TypeName, true));
            IPlugin plugin = obj as IPlugin;
            if (plugin != null)
            {
                plugin.Init(this);
            }
            
            return obj;
        }
        
        /// <summary>
        /// Create a typed instance of the plugin
        /// </summary>
        /// <typeparam name="T">Type of plugin to create</typeparam>
        /// <returns>plugin instance</returns>
        public T Create<T>()
            where T : class
        {
            return this.Create() as T;
        }
        
        /// <summary>
        /// Deserialize the Settings XmlNode into a typed object
        /// Uses the XmlSerializer.
        /// </summary>
        /// <typeparam name="T">type to deserialize to</typeparam>
        /// <returns>strongly typed settings object</returns>
        public T DeserializeSettings<T>()
            where T : class
        {
            if (!this.HasSettings)
            {
                return null;
            }
            using (XmlNodeReader reader = new XmlNodeReader(this.Settings))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(T), new XmlRootAttribute(this.Settings.LocalName));
                return (T)serializer.Deserialize(reader);
            }
        }
    }
}
