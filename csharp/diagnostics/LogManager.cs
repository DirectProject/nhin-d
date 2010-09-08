/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using NHINDirect.Diagnostics;

namespace Health.Net.Diagnostics.NLog
{
	/// <summary>
	/// This substitutes for a container managing the loading of a different
	/// logging library. If you make use of a container, then do not reference
	/// this class, but configure your container so it is able to create an
	/// instance of ILogger for your logging framework.
	/// 
	/// If we were using autofac it would look something like this:
	/// 
	/// <example>
	///		container.RegisterType<NLogFactory>().As<ILogFactory>();
	/// 
	///		ILogFactory factory = container.Resolve<ILogFactory>();
	///		ILogger logger = factory.GetLogger<MyType>();
	/// </example>
	/// 
	/// If you are not using a container, then make changes as necessary
	/// to this class so that it returns an instance of your ILogFactory.
	/// 
	/// </summary>
	public static class LogManager
	{
		private static readonly ILogFactory m_factory = new NLogFactory();

		/// <summary>
		/// Create a logger that is named using the <typeparamref name="T"/> full name as the key.
		/// </summary>
		/// <typeparam name="T">The type's full name to use as a key to obtain a logger.</typeparam>
		/// <returns>A new instance of a logger.</returns>
		public static ILogger GetLogger<T>()
		{
			return GetLogger(typeof (T).FullName);
		}

		/// <summary>
		/// Create a logger that is named using the <paramref name="name"/> full name as the key.
		/// </summary>
		/// <param name="name">The name to use as a key when finding the logger instance.</param>
		/// <returns>A new instance of a logger.</returns>
		public static ILogger GetLogger(string name)
		{
			return m_factory.GetLogger(name);
		}
	}
}