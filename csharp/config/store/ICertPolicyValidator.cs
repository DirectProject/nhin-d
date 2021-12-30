using System;

namespace Health.Direct.Config.Store
{
    public interface ICertPolicyValidator
    {
        Boolean IsValidLexicon(CertPolicy policy);
    }
}