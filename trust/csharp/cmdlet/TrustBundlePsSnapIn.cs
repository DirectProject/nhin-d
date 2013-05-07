using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Management.Automation;
using System.Text;

namespace Health.Direct.Trust.Commandlet
{
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
                return "This is a PowerShell snap-in that includes the Export0Bundler cmdlet.";
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
                return "TrustBundlePsSnapIn,This is a PowerShell snap-in that includes the Export-Bundle cmdlet.";
            }
        }

    }
}
