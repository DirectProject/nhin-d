using System;
using Health.Direct.Config.Store.Entity;

namespace Health.Direct.Config.Store
{
    public interface ICertPolicyValidator
    {
        Boolean IsValidLexicon(CertPolicy policy);
    }
}