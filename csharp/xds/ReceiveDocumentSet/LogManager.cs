using Health.Net.Diagnostics.NLog;

using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace NHINDirect.XDS
{
	/// <summary>
	/// This is a helper class to hide the IoC container from us for now.
	/// </summary>
	/// <remarks>
	/// When we know how this will be used in the gateway, then we can remove
	/// this class, or at least remove the <see cref="IoC.Initialize{T}"/> call.
	/// </remarks>
	internal static class LogManager
	{
		static LogManager()
		{
			IoC.Initialize(new SimpleDependencyResolver())
				.Register<ILogFactory>(new NLogFactory());
		}

		/// <summary>
		/// Gets a logger with the given name.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		internal static ILogger GetLogger(string name)
		{
			return IoC.Resolve<ILogFactory>().GetLogger(name);
		}
	}
}