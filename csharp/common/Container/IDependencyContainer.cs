using System;

namespace Health.Direct.Common.Container
{
    ///<summary>
    /// The <see cref="IDependencyContainer"/> contains the container register methods.
    ///</summary>
    public interface IDependencyContainer : IDependencyResolver
    {
        /// <summary>
        /// Registers a specific instance <paramref name="obj"/> type <typeparamref name="T"/>
        /// with the container.
        /// </summary>
        /// <typeparam name="T">The type to register</typeparam>
        /// <param name="obj">The specific instance to register</param>
        /// <returns>An instance of self so the Register calls can be chained.</returns>
        IDependencyContainer Register<T>(T obj);

        ///<summary>
        /// Registers a function, <paramref name="functor"/>, that returns an instance of type <typeparamref name="T"/>
        ///</summary>
        ///<param name="functor">The function that produces a new instance of <typeparamref name="T"/></param>
        ///<typeparam name="T">The type to register</typeparam>
        ///<returns>An instance of self so the Register calls can be chained.</returns>
        IDependencyContainer Register<T>(Func<object> functor);

        ///<summary>
        /// Registers a function of type <paramref name="serviceType"/> that is instatiated by <paramref name="functor"/>.
        ///</summary>
        ///<param name="serviceType">The type to register</param>
        ///<param name="functor">The function that produces a new instance of <paramref name="functor"/></param>
        ///<returns>A newly created container</returns>
        IDependencyContainer Register(Type serviceType, Func<object> functor);

        ///<summary>
        /// Register all of the components found in the <see cref="SimpleContainerSection"/>
        ///</summary>
        ///<returns>An instance of self so the Register calls can be chained.</returns>
        IDependencyContainer RegisterFromConfig();
    }
}