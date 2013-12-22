///* 
// Copyright (c) 2013, Direct Project
// All rights reserved.

// Authors:
//    Joe Shook      jshook@kryptiq.com
  
//Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

//Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
//*/

//namespace Health.Direct.Policy.X509{

///// <summary>
///// Subject public key info field of TBS section of certificate
///// <para>
///// The policy value of this extension is returned as an integer containing size in bit of the public key.
///// </para>
///// </summary>
//public class SubjectPublicKeySizeField : TBSField<int>
//{

//    /// <summary>
//    /// Create new instance.
//    /// </summary>
//    public SubjectPublicKeySizeField():base(true)
//    {
//    }
	

//           /// <inheritdoc />
//    public override TBSFieldName GetFieldName() 
//    {
//        return TBSFieldName.SUBJECT_PUBLIC_KEY_INFO;
//    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void injectReferenceValue(X509Certificate value) throws PolicyProcessException 
//    {
//        int retVal = 0;
		
//        this.certificate = value;
		
//        final PublicKey pubKey = this.certificate.getPublicKey();
		
//        if (pubKey instanceof RSAKey)
//        {
//            retVal = ((RSAKey)pubKey).getModulus().bitLength();
//        }
//        else if (pubKey instanceof DSAKey)
//        {
//            retVal = ((DSAKey)pubKey).getParams().getP().bitLength();
//        }
//        else
//        {
//            // undertermined
//            retVal = 0;
//        }
		
//        this.policyValue = PolicyValueFactory.getInstance(retVal);
//    }
//}
