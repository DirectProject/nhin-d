using System;
using System.Linq;
using System.Web.Mvc;

using AdminMvc.Models;
using AdminMvc.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace AdminMvc.Controllers
{
    public class AnchorsController : ControllerBase<Anchor, AnchorModel, IAnchorRepository>
    {
        public AnchorsController(IAnchorRepository repository) : base(repository)
        {
        }

        protected override void SetStatus(Anchor item, EntityStatus status)
        {
            item.Status = status;
        }

        public ActionResult Index(long? domainID, int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            Func<Anchor, bool> filter = anchor => true;
            if (domainID.HasValue)
            {
                var domain = Mapper.Map<Domain, DomainModel>(new DomainRepository().Get(domainID.Value));
                ViewData["Domain"] = domain;
                filter = anchor => anchor.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase);
            }

            return View(Repository.FindAll()
                            .Where(filter)
                            .Select(anchor => Mapper.Map<Anchor, AnchorModel>(anchor))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        public ActionResult Details(long id)
        {
            var anchor = Repository.Get(id);
            if (anchor == null) return View("NotFound");

            return Json(Mapper.Map<Anchor, AnchorModel>(anchor), "text/json", JsonRequestBehavior.AllowGet);
        }

        //public ActionResult Add(long domainID)
        //{
        //    return View(new AddressModel {DomainID = domainID});
        //}

        //[HttpPost]
        //public ActionResult Add(FormCollection formValues)
        //{
        //    var address = new AddressModel();

        //    if (TryUpdateModel(address))
        //    {
        //        var newAddress = Repository.Add(address);

        //        return RedirectToAction("Details", new { id = newAddress.ID });
        //    }

        //    return View(address);
        //}

        //public ActionResult Delete(long id)
        //{
        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    return View(address);
        //}

        //[HttpPost]
        //public ActionResult Delete(long id, string confirmButton)
        //{
        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    Repository.Delete(address);

        //    return View("Deleted", address);
        //}

        protected override ActionResult EnableDisable(long id, EntityStatus status)
        {
            var anchor = Repository.Get(id);
            if (anchor == null) return View("NotFound");

            anchor = Repository.ChangeStatus(anchor, status);

            return Json(Mapper.Map<Anchor, AnchorModel>(anchor), "text/json");
        }
    }
}