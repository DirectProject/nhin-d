using System;
using System.IO;
using System.Management.Automation;
using System.Security.Cryptography.X509Certificates;
using System.Xml;

namespace Health.Direct.Trust.Commandlet
{

    [Cmdlet(VerbsData.Export, "Bundle")]
    public class BundleCommand : Cmdlet
    {
        private string folderNames;
        private string[] ignore;
        private string metadata;
        private X509Certificate2 signature;

        [Parameter(Position = 0)]
        [ValidateNotNullOrEmpty]
        public string Name
        {
            get { return folderNames; }
            set { folderNames = value; }
        }

        [Parameter(Position = 1)]
        [ValidateNotNullOrEmpty]
        public string[] Ignore
        {
            get { return ignore; }
            set { ignore = value; }
        }

        [Parameter(Position = 2)]
        [ValidateNotNullOrEmpty]
        public string Metadata
        {
            get { return metadata; }
            set { metadata = value; }
        }

       
        protected override void ProcessRecord()
        {
            try
            {
                Bundle bundle = new Bundle();
                byte[] p7bData = bundle.Create(folderNames, ignore, metadata);
                WriteObject(p7bData);
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
