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