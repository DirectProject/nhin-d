using System;
using System.Linq;
using System.Web.Mvc;

using AdminMvc.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace AdminMvc.Controllers
{
    public abstract class ControllerBase<T,TModel,TRepository> : Controller
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

        protected abstract void SetStatus(T item, EntityStatus status);

        protected ActionResult IndexBase(int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            new DateTime().ToString("");

            var paginatedItems = (from item in Repository.FindAll()
                        select Mapper.Map<T, TModel>(item))
                        .AsPagination(page ?? 1, DefaultPageSize);

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

        [HttpPost]
        public string Delete(long id)
        {
            try
            {
                var item = Repository.Get(id);
                if (item == null) return "NotFound";

                Repository.Delete(item);

                return Boolean.TrueString;
            }
            catch (Exception ex)
            {
                return ex.GetBaseException().Message;
            }
        }

        public ActionResult Disable(long id)
        {
            return EnableDisable(id, EntityStatus.Disabled);
        }

        public ActionResult Enable(long id)
        {
            return EnableDisable(id, EntityStatus.Enabled);
        }

        private ActionResult EnableDisable(long id, EntityStatus status)
        {
            var item = Repository.Get(id);
            if (item == null) return View("NotFound");

            SetStatus(item, status);
            Repository.Update(item);

            return Json(Mapper.Map<T, TModel>(item), "text/json");
        }
    }
}