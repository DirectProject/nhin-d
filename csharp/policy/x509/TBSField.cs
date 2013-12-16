using System;

namespace Health.Direct.Policy.X509
{
    public abstract class TBSField<T>: X509Field<T> , ITBSField<T>
    {
     
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="required">Indicates if the field is required to be present in the certificate to be compliant with the policy.</param>
        protected TBSField(bool required)
        {
            Required = required;
        }
	
        /// <inheritdoc />
        public override X509FieldType GetX509FieldType()
        {
            return X509FieldType.TBS;
        }

        virtual public TBSFieldName<T> GetFieldName()
        {
            return null;
        }
   

        /// <inheritdoc />
        public override String ToString()
        {
            if (PolicyValue == null)
            {
                return "Unevaluated TBS field: " + GetFieldName();
            }
            return PolicyValue.ToString();
        }
    }
}