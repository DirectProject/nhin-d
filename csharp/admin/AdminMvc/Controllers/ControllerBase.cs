using System.Web.Mvc;

using MvcContrib.Pagination;

using AdminMvc.Models;

namespace AdminMvc.Controllers
{
    public class ControllerBase<T,TRepository> : Controller
        where T : class
        where TRepository : Repository<T>, new() 
    {
        protected const int DefaultPageSize = 10;

        private readonly TRepository m_repository;

        protected ControllerBase()
        {
            m_repository = new TRepository();
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

        public ActionResult Details(long id)
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