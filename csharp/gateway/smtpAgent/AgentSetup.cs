using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using NHINDirect.Certificates;

namespace NHINDirect.SmtpAgent
{
    /// <summary>
    /// COM object used by scripting components. Aids in Agent setup
    /// </summary>
    [ComVisible(true)]
    [Guid("A1F86888-959C-49e1-B786-C17465668972")]
    [ClassInterface(ClassInterfaceType.AutoDispatch)]
    public class AgentSetup
    {
        public AgentSetup()
        {
        }
        
        public void EnsureStandardMachineStores()
        {
            SystemX509Store.CreateAll();
        }
    }
}
