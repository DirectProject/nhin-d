using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Utility methods for the policy engine.
    /// </summary>
    public class PolicyUtils
    {

        /// <summary>
        /// Creates a string representation of a byte array.
        /// <param name="bytes">The byte array to convert to a string representation.</param>
        /// <returns>A string represention of the byte array.</returns> 
        /// </summary>
        public static String CreateByteStringRep(byte[] bytes)
        {
            var c = new char[bytes.Length*2];
            for (var i = 0; i < bytes.Length; i++)
            {
                var b = bytes[i] >> 4;
                c[i*2] = (char) (55 + b + (((b - 10) >> 31) & -7));
                b = bytes[i] & 0xF;
                c[i*2 + 1] = (char) (55 + b + (((b - 10) >> 31) & -7));
            }
            return new string(c);

        }
    }
}