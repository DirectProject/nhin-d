/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.ComponentModel;
using System.Management.Automation;

namespace Health.Direct.Trust.Commandlet
{

    /// <summary>
    /// Powershell SnapIn.
    /// </summary>
    [RunInstaller(true)]
    public class TrustBundlePsSnapIn : PSSnapIn
    {
        /// <summary>
        /// Create PowerShell snap-in instance.
        /// </summary>
        public TrustBundlePsSnapIn()
            : base()
        {
        }

        /// <summary>
        /// Name of PowerShell snap-in.
        /// </summary>
        public override string Name
        {
            get
            {
                return "TrustBundlePsSnapIn";
            }
        }

        /// <summary>
        /// Name of PowerShell snap-in vendor.
        /// </summary>
        public override string Vendor
        {
            get
            {
                return "Direct Project";
            }
        }

        /// <summary>
        /// Localization resource information for vendor.
        /// Use format: resourceBaseName,vendor name.
        /// </summary>
        public override string VendorResource
        {
            get
            {
                return "TrustBundlePsSnapIn,Direct Project";
            }
        }

        /// <summary>
        /// Description of the PowerShell snap-in.
        /// </summary>
        public override string Description
        {
            get
            {
                return "This is a PowerShell snap-in that includes all Trust Bundle cmdlets.";
            }
        }

        /// <summary>
        /// Localization resource information for the description. 
        /// Use the format: resourceBaseName,description. 
        /// </summary>
        public override string DescriptionResource
        {
            get
            {
                return "TrustBundlePsSnapIn,This is a PowerShell snap-in that includes the Bundle-Anchors cmdlet.";
            }
        }

    }
}
