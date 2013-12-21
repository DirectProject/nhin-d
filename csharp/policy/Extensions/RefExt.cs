using System;

namespace Health.Direct.Policy.Extensions
{
    public static class RefExt
    {
        /// <summary>
        /// Throws an ArgumentNullException <paramref name="data"/> is null.
        /// </summary>
        /// <param name="data">The item being checked.</param>
        /// <param name="name">Name of item being checked.</param>
        public static void ThrowIfNull<T>(this T data, string name) where T : class
        {
            if (data == null)
            {
                throw new ArgumentNullException(name);
            }
        }
    }
}
