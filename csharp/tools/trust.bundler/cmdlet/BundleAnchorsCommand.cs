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

using System;
using System.Collections;
using System.IO;
using System.Linq;
using System.Management.Automation;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Xml;

namespace Health.Direct.Trust.Commandlet
{

    [Cmdlet("Bundle", "Anchors")]
    public class BundleAnchorsCommand : Cmdlet
    {
        /// <summary>
        /// Source folder
        /// </summary>
        [Parameter(Position = 0), ValidateNotNullOrEmpty]
        public string Name { get; set; }

        [Parameter(Position = 1), ValidateNotNullOrEmpty]
        public string[] Ignore { get; set; }

        [Parameter(Position = 2), ValidateNotNullOrEmpty]
        public string Metadata { get; set; }

        /// <summary>
        /// Output file path.
        /// </summary>
        [Parameter(Position = 3), ValidateNotNullOrEmpty]
        public string Output { get; set; }

        protected override void BeginProcessing()
        {
            SetDefaults();
            WriteVerbose(String.Format("Begin Processing anchors in {0}.", Name));
            if (Ignore != null && Ignore.Length > 1)
            {
                WriteVerbose(String.Format("Filtered: {0}.", String.Join(",", Ignore)));
            }
            if (Ignore != null && Ignore.Length > 1)
            {
                WriteVerbose(String.Format("Included medatdata: {0}.", String.Join(",", Ignore)));
            }
            base.BeginProcessing();
        }

        private void SetDefaults()
        {
            if (string.IsNullOrEmpty(Name))
            {
                Name = Path.Combine(Directory.GetCurrentDirectory(), "Anchors");
            }

            if (string.IsNullOrEmpty(Metadata))
            {
                if (File.Exists(@".\TrustBundleMetaData.xml"))
                {
                    Metadata = "TrustBundleMetaData.xml";
                }
            }
        }

        protected override void ProcessRecord()
        {
            try
            {
                Bundler bundle = new Bundler();

                IResourceProvider resourceProvider =
                new FileResourceProvider(
                    Name
                    , Output
                    , Ignore
                    , Metadata);
                bundle.Create(resourceProvider);

                byte[] p7BData = bundle.Create(resourceProvider);

                WriteObject(p7BData);
            }
            catch (Exception e)
            {
                WriteError(
                    new ErrorRecord(
                        e,
                        "Export-Bundle",
                        ErrorCategory.NotSpecified,
                        Name
                        )
                 );
            }
        }


    }
}
