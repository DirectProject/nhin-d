using System.Reflection;
using System.Web.Mvc;
using System.Web.Routing;

using AdminMvc.Models;
using AdminMvc.Models.Repositories;

using Autofac;
using Autofac.Integration.Web;
using Autofac.Integration.Web.Mvc;

using AutoMapper;

namespace AdminMvc
{
    // Note: For instructions on enabling IIS6 or IIS7 classic mode, 
    // visit http://go.microsoft.com/?LinkId=9394801

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
            ConfigureContainer();
            ConfigureAutoMapper();

            AreaRegistration.RegisterAllAreas();
            RegisterRoutes(RouteTable.Routes);
        }

        private static void ConfigureAutoMapper()
        {
            Mapper.Initialize(x =>
                x.AddProfile<ModelProfiles>());
        }

        // Build up your application container and register your dependencies.
        private static void ConfigureContainer()
        {
            var builder = new ContainerBuilder();
            builder.RegisterModule(new RepositoryModule());
            builder.RegisterControllers(Assembly.GetExecutingAssembly());

            // Once you're done registering things, set the container
            // provider up with your registrations.
            _containerProvider = new ContainerProvider(builder.Build());

            ControllerBuilder.Current.SetControllerFactory(new AutofacControllerFactory(_containerProvider));
        }

        public static void RegisterRoutes(RouteCollection routes)
        {
            routes.IgnoreRoute("{resource}.axd/{*pathInfo}");

            routes.MapRoute(
                "Domains",
                "Domains/Page/{page}",
                new {controller = "Domains", action = "Index"}
                );

            routes.MapRoute(
                "Default", // Route name
                "{controller}/{action}/{id}", // URL with parameters
                new { controller = "Home", action = "Index", id = UrlParameter.Optional } // Parameter defaults
            );

        }
    }
}