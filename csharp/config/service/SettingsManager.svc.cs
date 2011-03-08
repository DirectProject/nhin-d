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
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class SettingsManager : ConfigServiceBase, IPropertyManager, IBlobManager
    {
        #region IPropertyManager Members
        
        public void AddProperty(Property property)
        {
            try
            {
                Store.Properties.Add(property);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddProperty", ex);
            }
        }
        
        public void AddProperties(Property[] properties)
        {
            try
            {
                Store.Properties.Add(properties);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddProperties", ex);
            }
        }
        
        public void SetProperty(Property property)
        {
            try
            {
                Store.Properties.Set(property);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetProperty", ex);
            }
        }
        
        public void SetProperties(Property[] properties)
        {
            try
            {
                Store.Properties.Set(properties);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetProperties", ex);
            }
        }

        public Property[] GetProperties(string[] propertyNames)
        {
            try
            {
                return Store.Properties.Get(propertyNames);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetProperties", ex);
            }
        }

        public Property[] GetPropertiesByPrefix(string propertyNamePrefix)
        {
            try
            {
                if (string.IsNullOrEmpty(propertyNamePrefix))
                {
                    //
                    // If no prefix provided, return all properties
                    //
                    return Store.Properties.ToArray();
                }
                
                return Store.Properties.GetStartsWith(propertyNamePrefix);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetPropertiesByPrefix", ex);
            }

        }

        public void RemoveProperty(string propertyName)
        {
            try
            {
                this.Store.Properties.Remove(propertyName);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveProperty", ex);
            }
        }

        public void RemoveProperties(string[] propertyNames)
        {
            try
            {
                this.Store.Properties.Remove(propertyNames);   
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveProperties", ex);
            }
        }

        #endregion

        #region IBlobManager Members

        public void AddBlob(NamedBlob blob)
        {
            try
            {
                this.Store.Blobs.Add(blob);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddBlob", ex);
            }
        }

        public void UpdateBlob(NamedBlob blob)
        {
            try
            {
                this.Store.Blobs.Update(blob);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateBlob", ex);
            }
        }

        public NamedBlob GetBlob(string blobName)
        {
            try
            {
                return this.Store.Blobs.Get(blobName);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBlob", ex);
            }
        }

        public NamedBlob[] GetBlobsByPrefix(string blobNamePrefix)
        {
            try
            {
                return this.Store.Blobs.GetNameStartsWith(blobNamePrefix);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBlobsByPrefix", ex);
            }
        }

        public void RemoveBlob(string blobName)
        {
            try
            {
                this.Store.Blobs.Remove(blobName);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveBlob", ex);
            }
        }

        #endregion
    }
}
