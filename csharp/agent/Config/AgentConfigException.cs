using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Agent.Config
{
    public enum AgentConfigError
    {
        Unknown = 0,
        InvalidDomainList,
        MissingPrivateCertSettings,
        MissingPublicCertSettings,
        MissingAnchorSettings,
        MissingResolver,
        MissingPrivateCertResolver,
        MissingPublicCertResolver,
        MissingAnchorResolverSettings,
        MissingIncomingAnchors,
        MissingOutgoingAnchors,
        MissingMachineStoreName,
        MissingDnsServerIP
    }
    
    public class AgentConfigException : NHINDException<AgentConfigError>
    {
        public AgentConfigException(AgentConfigError error)
            : base(error)
        {
        }
    }
}
