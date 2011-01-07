using System;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// A factory to localize access to <see cref="DateTime"/> objects. If we wish to store
    /// <see cref="DateTime"/> objects in UTC then those changes only need to be made here.
    /// </summary>
    public static class DateTimeHelper
    {
        public static DateTime Now
        {
            get
            {
                return DateTime.Now;
            }
        }
    }
}