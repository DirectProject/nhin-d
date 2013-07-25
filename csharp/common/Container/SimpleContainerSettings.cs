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
using System.Xml.Serialization;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Container
{
    /// <summary>
    /// The XmlSerializer equivalent of classes in SmtpContainerSection
    /// </summary>
    public class SimpleContainerSettings
    {
        ///<summary>
        /// The list of components that are registered with the container.
        ///</summary>
        [XmlElement("Component")]
        public SimpleComponentSettings[] Components
        {
            get;
            set;
        }
        
        /// <summary>
        /// Are any components actually defined? 
        /// </summary>
        [XmlIgnore]
        public bool HasComponents
        {
            get
            {
                return (!this.Components.IsNullOrEmpty());
            }
        }
    }

    /// <summary>
    /// The XmlSerializer equivalent of classes in SmtpContainerElement
    /// </summary>
    public class SimpleComponentSettings
    {
        ///<summary>
        /// The interface that the component wants registered with the container.
        ///</summary>
        [XmlElement]
        public string Service
        {
            get;
            set;
        }

        ///<summary>
        /// The concrete implementation that implements the interface type specified in the <see cref="Service"/> type.
        ///</summary>
        [XmlElement]
        public string Type
        {
            get;
            set;
        }

        ///<summary>
        /// The scope of the instance that is registered with the container. <see cref="InstanceScope"/> provides
        /// either <see cref="InstanceScope.Singleton"/> or <see cref="InstanceScope.Transient"/>.
        ///</summary>
        public InstanceScope Scope
        {
            get;
            set;
        }

        ///<summary>
        /// A convience property that loads a the type specified in <see cref="Service"/>.
        ///</summary>
        [XmlIgnore]
        public Type ServiceType
        {
            get
            {
                return LoadType(Service);
            }
        }
        
        /// <summary>
        /// Validate the settings..
        /// </summary>
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Service))
            {
                throw new ArgumentException("Service");
            }
            if (string.IsNullOrEmpty(this.Type))
            {
                throw new ArgumentException("Type");
            }
        }
        ///<summary>
        /// Create an instance of the type <see cref="Type"/>.
        ///</summary>
        ///<returns>A new instance of the type specified by <see cref="Type"/></returns>
        public object CreateInstance()
        {
            return Activator.CreateInstance(LoadType(Type));
        }

        private static Type LoadType(string typeName)
        {
            return System.Type.GetType(typeName, true);
        }
    }
}
