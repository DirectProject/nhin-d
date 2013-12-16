using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Policy.X509
{

    /// <summary>
    /// Key usage extension field. 
    /// The policy value of this extension is returned as an integer that is a "logical or" combination of all key usage bits.
    /// If the extension does not exist, the policy value returned by this class evaluates to 0.
    /// </summary>
    public class KeyUsageExtensionField : ExtensionField<int>, IExtensionField<int>
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
			    else
			    {
				    PolicyValue = new PolicyValue<int>(0);
				    return;
			    }
		    }
		
		    DerBitString derBitString = new DerBitString(exValue);
		    KeyUsage keyUsage = new KeyUsage(derBitString.IntValue);
		    byte[] data = keyUsage.GetBytes();
		
		    int intValue = (data.Length == 1) ? data[0] & 0xff : (data[1] & 0xff) << 8 | (data[0] & 0xff);
		
		    PolicyValue = new PolicyValue<int>(intValue);
	    }

        /// <inheritdoc />
	    public override ExtensionIdentifier<int> GetExtentionIdentifier() 
	    {
		    return ExtensionIdentifier<int>.KeyUsage;
	    }	
    }
}
