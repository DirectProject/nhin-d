using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Health.Direct.Config.Store
{
    /// <summary>
     /// Security standard used.
     /// </summary>
    public enum SecurityStandard : byte
    {
        /// <summary>
        /// Original ConfigStore software based cryptography and storage
        /// </summary>
        Software = 0,
        /// <summary>
        /// Federal Health Architecture required security level.
        /// </summary>
        Fips1402 = 1
    }
}
