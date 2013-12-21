using System.Collections.Generic;

namespace Health.Direct.Policy.OpCode
{
    public class Code
    {
        public string Text;
        public string Token;

        
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

    public class Equals : Code
    {
        public Equals()
        {
            Text = "equals";
            Token = "=";
        }
    }

    public class NotEquals : Code
    {
        public NotEquals()
        {
            Text = "not equals";
            Token = "!=";
        }
    }

    public class Greater : Code
    {
        public Greater()
        {
            Text = "greater than";
            Token = ">";
        }
    }
    
    public class Less : Code
    {
        public Less()
        {
            Text = "less than";
            Token = "<";
        }
    }
    
    public class Reg_Ex : Code
    {
        public Reg_Ex()
        {
            Text = "matches";
            Token = "$";
        }
    }

    public class Contains : Code
    {
        public Contains()
        {
            Text = "contains";
            Token = "{?}";
        }
    }

    public class NotContains : Code
    {
        public NotContains()
        {
            Text = "not contains";
            Token = "{?}!";
        }
    }

    public class ContainsRegEx : Code
    {
        public ContainsRegEx()
        {
            Text = "contains match";
            Token = "{}$";
        }
    }
        
    public class Size : Code
    {
        public Size()
        {
            Text = "size";
            Token = "^";
        }
    }

    public class Empty : Code
    {
        public Empty()
        {
            Text = "empty";
            Token = "{}";
        }
    }
    
    public class NotEmpty : Code
    {
        public NotEmpty()
        {
            Text = "not empty";
            Token = "{}!";
        }
    }

    public class Intersection : Code
    {
        public Intersection()
        {
            Text = "intersection";
            Token = "{}&";
        }
    }

    public class LogicalOr : Code
    {
        public LogicalOr()
        {
            Text = "or";
            Token = "||";
        }
    }

    public class Logical_And : Code
    {
        public Logical_And()
        {
            Text = "and";
            Token = "&&";
        }
    }

    public class Logical_Not : Code
    {
        public Logical_Not()
        {
            Text = "not";
            Token = "!";
        }
    }

    public class Bitwise_And : Code
    {
        public Bitwise_And()
        {
            Text = "bitand";
            Token = "&";
        }
    }

    public class Bitwise_Or : Code
    {
        public Bitwise_Or()
        {
            Text = "bitor";
            Token = "|";
        }
    }

    public class Uri_Valid : Code
    {
        public Uri_Valid()
        {
            Text = "uri valid";
            Token = "@@";
        }
    }

}