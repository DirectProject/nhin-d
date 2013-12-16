using System;
using System.Linq.Expressions;
using System.Reflection;
using System.Text;

namespace Health.Direct.Policy.Extensions
{
    public static class TypeExt
    {
        private static ConstructorInfo GetConstructor(Type type, params Type[] argumentTypes)
        {
            type.ThrowIfNull("type");
            argumentTypes.ThrowIfNull("argumentTypes");

            ConstructorInfo ci = type.GetConstructor(argumentTypes);
            if (ci == null)
            {
                StringBuilder sb = new StringBuilder();
                sb.Append(type.Name).Append(" has no constructor(");
                for (int i = 0; i < argumentTypes.Length; i++)
                {
                    if (i > 0)
                    {
                        sb.Append(',');
                    }
                    sb.Append(argumentTypes[i].Name);
                }
                sb.Append(')');
                throw new InvalidOperationException(sb.ToString());
            }
            return ci;
        }

        public static Func<TArg1, TResult> Ctor<TArg1, TResult>(this Type type)
        {
            ConstructorInfo ci = GetConstructor(type, typeof(TArg1));
            ParameterExpression
                param1 = Expression.Parameter(typeof(TArg1), "arg1");

            return Expression.Lambda<Func<TArg1, TResult>>(
                Expression.New(ci, param1), param1).Compile();
        }
    }
}
