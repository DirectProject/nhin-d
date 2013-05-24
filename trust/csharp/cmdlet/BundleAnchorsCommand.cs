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
                Metadata = "TrustBundleMetaData.xml";
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
                    , null
                    , Ignore);
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
