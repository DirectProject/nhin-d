using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Config.Store
{
    public enum EntityStatus : byte
    {
        New = 0,
        Enabled = 1,
        Disabled = 2,
        Deleted = 3
    }
}
