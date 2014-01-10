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
using System.IO;
using System.Management.Automation;
using System.Security;

namespace Health.Direct.Trust.Commandlet
{
    [Cmdlet("Sign", "Bundle")]
    public class SignBundleCommand : Cmdlet
    {
        private string _name;
        private byte[] _bundle;

        /// <summary>
        /// Signing Cert path.
        /// </summary>
        [Parameter(Position = 0,
            ValueFromPipelineByPropertyName = false,
            HelpMessage = "Signing cert path"), ValidateNotNullOrEmpty]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        
        [Parameter(Position = 1,
            Mandatory = true,
            ValueFromPipeline = true,
            ValueFromPipelineByPropertyName = true,
            HelpMessage = "Trust bundle as byte array"), ValidateNotNullOrEmpty]
        public byte[] Bundle
        {
            get { return _bundle; }
            set { _bundle = value; }
        }


        [Parameter(Position = 2, HelpMessage = "SecureString", Mandatory = true)]
        public SecureString PassKey { get; set; }


        protected override void BeginProcessing()
        {
            SetDefaults();
            WriteVerbose(String.Format("Begin Sign-Bundle with {0}", Name));
            base.BeginProcessing();
        }

        private void SetDefaults()
        {
            if (string.IsNullOrEmpty(_name))
            {
                Name = Path.Combine(Directory.GetCurrentDirectory(), "Signatures");
            }
            
        }

        protected override void ProcessRecord()
        {
            try
            {

                Bundler bundle = new Bundler();

                ISignProvider signProvider =
                    new FileSignerProvider(
                        Name,
                        PassKey);


                byte[] p7BData = _bundle as byte[];
                byte[] p7MData = bundle.Sign(p7BData, signProvider);

                WriteObject(p7MData);
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

        protected override void EndProcessing()
        {
            WriteVerbose(String.Format("End Processing {0}", Name));
            base.EndProcessing();
        }

    }
}