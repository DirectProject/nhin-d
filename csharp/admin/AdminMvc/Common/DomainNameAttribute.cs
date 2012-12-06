using System.ComponentModel.DataAnnotations;

namespace Health.Direct.Admin.Console.Common
{
    public class DomainNameAttribute : RegularExpressionAttribute
    {
        private const string DomainPattern = @"^([A-Za-z0-9\-]{1,63}\.)+[A-Za-z]{2,}$";

        public DomainNameAttribute() : base(DomainPattern)
        {
        }
    }
}