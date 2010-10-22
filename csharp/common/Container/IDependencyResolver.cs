namespace Health.Direct.Common.Container
{
    ///<summary>
    /// Works in conjuction with the <see cref="IoC"/> global class to isolate
    /// container implementations from the agent and gateway implementations.
    ///</summary>
    public interface IDependencyResolver
    {
        ///<summary>
        /// Given a specific type, return an instance of that type.
        ///</summary>
        ///<typeparam name="T">The type to attempt resolution</typeparam>
        ///<returns>An instance of type <typeparamref name="T"/></returns>
        T Resolve<T>();
    }
}