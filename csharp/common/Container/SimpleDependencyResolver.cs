using System;
using System.Collections.Generic;

namespace NHINDirect.Container
{
	///<summary>
	/// SimpleDependencyResolver provides a simple (non-external) container
	/// so that dependencies can be isolated and other implementors of an
	/// agent or gateway can slide in their own container library.
	///</summary>
	public class SimpleDependencyResolver : IDependencyResolver
	{
		private readonly Dictionary<Type, Func<object>> m_types;

		///<summary>
		/// A simple <see cref="Dictionary{TKey,TValue}"/> based dependency
		/// resolver. All dependencies need to be initialized on startup and
		/// prior to calling to <see cref="IoC.Initialize"/>.
		///</summary>
		public SimpleDependencyResolver()
		{
			m_types = new Dictionary<Type, Func<object>>();
		}

		///<summary>
		/// Given a specific type, return an instance of that type.
		///</summary>
		///<typeparam name="T">The type to attempt resolution</typeparam>
		///<returns>An instance of type <typeparamref name="T"/></returns>
		public T Resolve<T>()
		{
			return (T) m_types[typeof (T)]();
		}

		/// <summary>
		/// Registers a specific instance <paramref name="obj"/> type <typeparamref name="T"/>
		/// with the container.
		/// </summary>
		/// <typeparam name="T">The type to register</typeparam>
		/// <param name="obj">The specific instance to register</param>
		/// <returns>An instance of self so the Register calls can be chained.</returns>
		public SimpleDependencyResolver Register<T>(T obj)
		{
			return Register<T>(() => obj);
		}

		///<summary>
		/// Registers a function, <paramref name="functor"/>, that returns an instance of type <typeparamref name="T"/>
		///</summary>
		///<param name="functor">The function that produces a new instance of <typeparamref name="T"/></param>
		///<typeparam name="T">The type to register</typeparam>
		///<returns>An instance of self so the Register calls can be chained.</returns>
		public SimpleDependencyResolver Register<T>(Func<object> functor)
		{
			m_types.Add(typeof(T), functor);
			return this;
		}
	}
}