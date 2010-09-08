using System;

namespace NHINDirect.Container
{
	///<summary>
	/// This is the global dispatch class for plugging in an Inversion of Control
	/// container. 
	///</summary>
	public static class IoC
	{
		private static IDependencyResolver m_resolver;

		///<summary>
		/// Initialize the global inversion of control reference with the <paramref name="resolver"/>.
		/// <see cref="IoC"/> allows us to drop in a different inversion of control container
		/// that will vary between different companies' implementations of the gateway and agent.
		///</summary>
		///<param name="resolver">The container to use as the resolver</param>
		///<returns>Returns a reference to the resolver so it can be used in method chaining.</returns>
		///<exception cref="ArgumentNullException">Throw if <paramref name="resolver"/> was null</exception>
		public static T Initialize<T>(T resolver)
			where T : class, IDependencyResolver
		{
			if (resolver == null)
			{
				throw new ArgumentNullException("resolver");
			}

			m_resolver = resolver;

			return resolver;
		}

		///<summary>
		/// Returns an instance of type <typeparamref name="T"/>.
		///</summary>
		///<typeparam name="T"></typeparam>
		///<returns></returns>
		///<exception cref="InvalidOperationException">Is throws if <see cref="Initialize{T}"/> was </exception>
		public static T Resolve<T>()
		{
			if (m_resolver == null)
			{
				throw new InvalidOperationException("Resolve was called before Initialize");
			}

			return m_resolver.Resolve<T>();
		}
	}
}