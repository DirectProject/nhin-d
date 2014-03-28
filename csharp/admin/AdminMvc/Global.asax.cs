using System.Reflection;
using System.Web.Mvc;
using System.Web.Routing;
using System.Web.Security;
using Autofac.Integration.Mvc;
using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;

using Autofac;
using AutoMapper;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Diagnostics.NLog;

namespace Health.Direct.Admin.Console
{
    public class MvcApplication : System.Web.HttpApplication
    {
        

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

            DependencyResolver.SetResolver(new AutofacDependencyResolver(container));

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