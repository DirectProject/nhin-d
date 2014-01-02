using System.Collections.Generic;

namespace Health.Direct.Policy.OpCode
{
    public class Code
    {
        public string Text;
        public string Token;
        public OpCodeType OpCodeType;
        
        public static List<Code> Map;
        static Code()
        {
            Map = new List<Code>();
            Map.Add(new Equals());
            Map.Add(new NotEquals());
            Map.Add(new Greater());
            Map.Add(new Less());
            Map.Add(new Reg_Ex());
            Map.Add(new Contains());
            Map.Add(new NotContains());
            Map.Add(new ContainsRegEx());
            Map.Add(new Size());
            Map.Add(new Empty());
            Map.Add(new NotEmpty());
            Map.Add(new Intersection());
            Map.Add(new LogicalOr());
            Map.Add(new Logical_And());
            Map.Add(new Logical_Not());
            Map.Add(new Bitwise_And());
            Map.Add(new Bitwise_Or());
            Map.Add(new Uri_Valid());
        }
    }

    public enum OpCodeType
    {
        Unary,
        Binary
    }

    public class Equals : Code
    {
        public Equals()
        {
            Text = "equals";
            Token = "=";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class NotEquals : Code
    {
        public NotEquals()
        {
            Text = "not equals";
            Token = "!=";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Greater : Code
    {
        public Greater()
        {
            Text = "greater than";
            Token = ">";
            OpCodeType = OpCodeType.Binary;
        }
    }
    
    public class Less : Code
    {
        public Less()
        {
            Text = "less than";
            Token = "<";
            OpCodeType = OpCodeType.Binary;
        }
    }
    
    public class Reg_Ex : Code
    {
        public Reg_Ex()
        {
            Text = "matches";
            Token = "$";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Contains : Code
    {
        public Contains()
        {
            Text = "contains";
            Token = "{?}";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class NotContains : Code
    {
        public NotContains()
        {
            Text = "not contains";
            Token = "{?}!";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class ContainsRegEx : Code
    {
        public ContainsRegEx()
        {
            Text = "contains match";
            Token = "{}$";
            OpCodeType = OpCodeType.Binary;
        }
    }
        
    public class Size : Code
    {
        public Size()
        {
            Text = "size";
            Token = "^";
            OpCodeType = OpCodeType.Unary;
        }
    }

    public class Empty : Code
    {
        public Empty()
        {
            Text = "empty";
            Token = "{}";
            OpCodeType = OpCodeType.Unary;
        }
    }
    
    public class NotEmpty : Code
    {
        public NotEmpty()
        {
            Text = "not empty";
            Token = "{}!";
            OpCodeType = OpCodeType.Unary;
        }
    }

    public class Intersection : Code
    {
        public Intersection()
        {
            Text = "intersection";
            Token = "{}&";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class LogicalOr : Code
    {
        public LogicalOr()
        {
            Text = "or";
            Token = "||";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Logical_And : Code
    {
        public Logical_And()
        {
            Text = "and";
            Token = "&&";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Logical_Not : Code
    {
        public Logical_Not()
        {
            Text = "not";
            Token = "!";
            OpCodeType = OpCodeType.Unary;
        }
    }

    public class Bitwise_And : Code
    {
        public Bitwise_And()
        {
            Text = "bitand";
            Token = "&";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Bitwise_Or : Code
    {
        public Bitwise_Or()
        {
            Text = "bitor";
            Token = "|";
            OpCodeType = OpCodeType.Binary;
        }
    }

    public class Uri_Valid : Code
    {
        public Uri_Valid()
        {
            Text = "uri valid";
            Token = "@@";
            OpCodeType = OpCodeType.Unary;
        }
    }

}