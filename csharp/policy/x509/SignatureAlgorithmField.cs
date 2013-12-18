using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Policy.X509
{
    public class SignatureAlgorithmField : X509Field<string>
    {
        public SignatureAlgorithmField()
        {

        }

        public override void InjectReferenceValue(X509Certificate2 value) //throws PolicyProcessException
        {
            Certificate = value;
            PolicyValue = PolicyValueFactory<string>.GetInstance(value.SignatureAlgorithm.Value); 
        }


        public override X509FieldType GetX509FieldType()
        {
            return X509FieldType.SignatureAlgorithm;
        }	
    }
}