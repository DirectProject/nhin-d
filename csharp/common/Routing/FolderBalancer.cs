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
using System.IO;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Routing
{
    /// <summary>
    /// A load balancer where the receivers are folders/directories
    /// </summary>
    /// <typeparam name="T">type of data to forward to receivers</typeparam>
    public class FolderBalancer<T> : LoadBalancer<T, string>
    {
        /// <summary>
        /// Create a new load balancer
        /// </summary>
        /// <param name="dataCopier">function that actually copies source data to the receiver</param>
        public FolderBalancer(Func<T, string, bool> dataCopier)
            : base(null, dataCopier)
        {
        }

        /// <summary>
        /// Create a load balancer that will distribute file data over a set of sub-folders of baseFolder
        /// </summary>
        /// <param name="baseFolder">base folder</param>
        /// <param name="receiverFolderCount">create these many child folders, with names from 0 - receiveFolderCount - 1</param>
        /// <param name="dataCopier">function that actually copies source data to the receiver</param>
        public FolderBalancer(string baseFolder, int receiverFolderCount, Func<T, string, bool> dataCopier)
            : base(CreateFolderPaths(baseFolder, null, receiverFolderCount), dataCopier)
        {
        }
        
        /// <summary>
        /// Create a new load balancer
        /// </summary>
        /// <param name="targetFolderPaths">paths to receiving folders.</param>
        /// <param name="dataCopier">function that actually copies source data to the receiver</param>
        public FolderBalancer(string[] targetFolderPaths, Func<T, string, bool> dataCopier)
            : base(targetFolderPaths, dataCopier)
        {
        }
        
        /// <summary>
        /// Get/Set receivers
        /// </summary>
        public override string[] Receivers
        {
            get
            {
                return base.Receivers;
            }
            set
            {
                base.Receivers = value;
                this.EnsureReceivers();
            }
        }
        
        /// <summary>
        /// Ensure that all receiver folders have been created
        /// </summary>        
        public void EnsureReceivers()
        {
            if (this.Receivers.IsNullOrEmpty())
            {
                return;
            }
            
            foreach (string folderPath in this.Receivers)
            {
                try
                {
                    if (!Directory.Exists(folderPath))
                    {
                        Directory.CreateDirectory(folderPath);
                    }
                }
                catch
                {
                }
            }
        }    
        
        /// <summary>
        /// Frequently, you will load balance over a set of numbered child sub folders...
        /// This simple helper makes it easier to create those. 
        /// </summary>
        /// <param name="basePath">base path for where your child folders live</param>
        /// <param name="namePrefix">name prefix for your folder (optional)</param>
        /// <param name="count">number of child folders to create</param>
        /// <returns>Array of created paths</returns>
        public static string[] CreateFolderPaths(string basePath, string namePrefix, int count)
        {
            //
            // Delegate argument checking to Path
            //
            string[] folderPaths = new string[count];
            bool hasPrefix = !string.IsNullOrEmpty(namePrefix);
            for (int i = 0; i < count; ++i)
            {
                folderPaths[i] = Path.Combine(basePath,
                                        hasPrefix ? Path.Combine(namePrefix, i.ToString()) : i.ToString());
            }
                
            return folderPaths;
        }    
    }
}
