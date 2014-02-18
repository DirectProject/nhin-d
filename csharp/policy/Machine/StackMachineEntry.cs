using System;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Policy.Operators;
using System.Text;

namespace Health.Direct.Policy.Machine
{
    //public class LiteralMachineEntry<T> : StackMachineEntry<T> 
    //{
    //    public LiteralMachineEntry(object policyValue)
    //        : base(EntryType.Value, policyValue)
    //    {
    //    }
    //}

    
    //public class ReferenceMachineEntry<T> : StackMachineEntry<T> 
    //{
    //    public ReferenceMachineEntry(object policyValue)
    //        : base(EntryType.Value, policyValue)
    //    {
    //    }
    //}


    

    public class StackMachineEntry<T> : IOpCode
    {
        readonly OperatorBase m_policyOperator;
        readonly EntryType m_entryType;
        readonly object m_policyValue;
        readonly Type m_opCodeType;

        public EntryType EntryType { get { return m_entryType; } }
        public OperatorBase PolicyOperator { get { return m_policyOperator; } }
        public object PolicyValue { get { return m_policyValue; } }

        public StackMachineEntry(OperatorBase policyOperator)
        {
            m_policyOperator = policyOperator;
            m_entryType = EntryType.Operator;
            m_policyValue = null;
            m_opCodeType = typeof(T);
        }

        public StackMachineEntry(EntryType entryType,  object policyValue)
        {
            m_entryType = entryType;
            m_policyValue = policyValue;
            m_opCodeType = typeof (T);
        }
       
        
        protected StackMachineEntry(){}

        
        public OperatorBase GetOperator()
        {
            return PolicyOperator;
        }
        

        public override string ToString()
        {
            StringBuilder builder = new StringBuilder("Entry Type: ")
                    .Append(EntryType).Append("\r\n");
            switch (EntryType)
            {
                case EntryType.Operator:
                    builder.Append("Operator: " + GetOperator().GetOperatorText() + "\r\n");
                    break;
                case EntryType.Value:
                    builder.Append("Value: " + PolicyValue + "\r\n");
                    break;
            }

            return builder.ToString();
        }
    }

    public enum EntryType
    {
        Operator,
        Value
    }
}
