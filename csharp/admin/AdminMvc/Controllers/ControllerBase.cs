using System.Web.Mvc;

using AdminMvc.Models.Repositories;

using AutoMapper;

using MvcContrib.Pagination;

namespace AdminMvc.Controllers
{
    public class ControllerBase<T,TRepository> : Controller
        where T : class
        where TRepository : IRepository<T>
    {
        protected const int DefaultPageSize = 10;

        private readonly TRepository m_repository;

        protected ControllerBase(TRepository repository)
        {
            m_repository = repository;
        }

        protected TRepository Repository
        {
            get { return m_repository; }
        }

        public ActionResult Index(int? page)
        {
            var paginatedItems = Repository.FindAll().AsPagination(page ?? 1, DefaultPageSize);
            return View(paginatedItems);
        }

        public virtual ActionResult Details(long id)
        {
            var item = Repository.Get(id);

            if (item == null)
            {
                return View("NotFound");
            }

            return View("Details", item);
        }
    }
}