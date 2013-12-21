/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Policy.X509
{

    /// <summary>
    /// Key usage extension field. 
    /// <para>
    /// The policy value of this extension is returned as an integer that is a "logical or" combination of all key usage bits.
    /// </para>
    /// <para>
    /// If the extension does not exist, the policy value returned by this class evaluates to 0.
    /// </para>
    /// </summary>
    public class KeyUsageExtensionField : ExtensionField<int>
    {
        /// <summary>
	    /// Create new instance
	    /// <param name="required">
	    /// Indicates if the field is required to be present in the certificate to be compliant with the policy.
	    /// </param>
	    /// </summary>
	    public KeyUsageExtensionField(bool required) : base(required)
	    {
	    }

	    /// <inheritdoc />
	    public override void InjectReferenceValue(X509Certificate2 value) 
	    {
		    Certificate = value;

            DerObjectIdentifier exValue = GetExtensionValue(value);
		
		    if (exValue == null)
		    {
			    if (IsRequired())
				    throw new PolicyRequiredException("Extention " + GetExtentionIdentifier().GetDisplay() + " is marked as required by is not present.");
		        PolicyValue = new PolicyValue<int>(0);
		        return;
		    }
		
		    var derBitString = new DerBitString(exValue);
		    var keyUsage = new KeyUsage(derBitString.IntValue);
		    byte[] data = keyUsage.GetBytes();
		
		    int intValue = (data.Length == 1) ? data[0] & 0xff : (data[1] & 0xff) << 8 | (data[0] & 0xff);
		
		    PolicyValue = new PolicyValue<int>(intValue);
	    }

        /// <inheritdoc />
	    public override ExtensionIdentifier GetExtentionIdentifier() 
	    {
		    return ExtensionIdentifier.KeyUsage;
	    }	
    }
}
