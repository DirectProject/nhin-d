using System;
using Health.Direct.Policy;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// 
    /// </summary>
    public class CertPolicyParseValidator : ICertPolicyValidator
    {
        

        public Boolean IsValidLexicon(CertPolicy policy)
        {
            try
            {
                //might get parser from policy.Lexicon in the future
                var parser = new SimpleTextV1LexiconPolicyParser();
                parser.Parse(policy.Data.ToMemoryStream());
                return true;
            }
            catch (Exception)
            {
                return false;
            }
            
        }
    }
}