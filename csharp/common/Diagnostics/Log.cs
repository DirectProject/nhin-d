using System;

using Health.Direct.Common.Container;

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// A static gateway to ease access to the LogFactory.
    ///</summary>
    public static class Log
    {
        private static ILogFactory s_logFactory;
        private static readonly object s_factorySync = new object();

        private static ILogFactory LogFactory
        {
            get
            {
                lock (s_factorySync)
                {
                    if (s_logFactory == null)
                    {
                        s_logFactory = IoC.Resolve<ILogFactory>();
                    }

                    return s_logFactory;
                }
            }
        }

        ///<summary>
        /// Get the logger for the generic parameter type <typeparamref name="T"/>.
        ///</summary>
        ///<typeparam name="T">The type to create a logger for</typeparam>
        ///<returns>The logger for type <typeparamref name="T"/></returns>
        public static ILogger For<T>()
        {
            return For(typeof (T));
        }

        ///<summary>
        /// Get the logger for the object's type
        ///</summary>
        ///<param name="obj">The object to create a logger for</param>
        ///<returns>The logger for the type <see cref="object.GetType"/></returns>
        ///<exception cref="ArgumentNullException">If the <paramref name="obj"/> was null</exception>
        public static ILogger For(object obj)
        {
            if (obj == null)
            {
                throw new ArgumentNullException("obj");
            }
            return For(obj.GetType());
        }

        ///<summary>
        /// Get the logger for the given <paramref name="type"/>
        ///</summary>
        ///<param name="type">The type to create a logger for</param>
        ///<returns>The logger for the type <paramref name="type"/></returns>
        public static ILogger For(Type type)
        {
            return LogFactory.GetLogger(type);
        }
    }
}