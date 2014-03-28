using System.Reflection;
using System.Web.Mvc;
using System.Web.Routing;
using System.Web.Security;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;

using Autofac;
using Autofac.Integration.Web;
using Autofac.Integration.Web.Mvc;

using AutoMapper;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Diagnostics.NLog;

namespace Health.Direct.Admin.Console
{
    public class MvcApplication : System.Web.HttpApplication, IContainerProviderAccessor
    {
        // Provider that holds the application container.
        static IContainerProvider _containerProvider;

        // Instance property that will be used by Autofac HttpModules
        // to resolve and inject dependencies.
        public IContainerProvider ContainerProvider
        {
            get { return _containerProvider; }
        }

        protected void Application_Start()
        {
            InitializeContainer();
            ConfigureContainer();
            ConfigureAutoMapper();

            AreaRegistration.RegisterAllAreas();
            RegisterRoutes(RouteTable.Routes);
        }

        private static void ConfigureAutoMapper()
        {
            Mapper.Initialize(x => x.AddProfile<ModelProfiles>());
        }

        // Build up your application container and register your dependencies.
        private static void ConfigureContainer()
        {
            var builder = new ContainerBuilder();
            builder.RegisterModule(new RepositoryModule());
            builder.RegisterControllers(Assembly.GetExecutingAssembly());

            builder.Register(c => Membership.Provider).ExternallyOwned();
            var container = builder.Build();

            // Once you're done registering things, set the container
            // provider up with your registrations.
            _containerProvider = new ContainerProvider(container);

            ControllerBuilder.Current.SetControllerFactory(new AutofacControllerFactory(_containerProvider));

            // inject properties into the Membership Provider
            container.InjectUnsetProperties(Membership.Provider);
        }

        private static void InitializeContainer()
        {
            LogFileSettings settings = LogFileSection.GetAsSettings();

            // setup the container here... grrr... we're duplicating this!
            IoC.Initialize(new SimpleDependencyResolver()
                               .Register<ILogFactory>(new NLogFactory(settings))
                );
        }
        public static void RegisterRoutes(RouteCollection routes)
        {
            routes.IgnoreRoute("{resource}.axd/{*pathInfo}");

            routes.MapRoute(
                "Domains",
                "Domains/Page/{page}",
                new { controller = "Domains", action = "Index" }
                );

            routes.MapRoute(
                "Default", // Route name
                "{controller}/{action}/{id}", // URL with parameters
                new { controller = "Home", action = "Index", id = UrlParameter.Optional } // Parameter defaults
                );

        }
    }
}