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
        public override X509FieldType X509FieldType
        {
            get { return X509FieldType.TBS; }
        }

        public virtual TBSFieldName Name
        {
            get { return null; }
        }


        /// <inheritdoc />
        public override String ToString()
        {
            if (PolicyValue == null)
            {
                return "Unevaluated TBS field: " + Name;
            }
            return PolicyValue.ToString();
        }
    }
}