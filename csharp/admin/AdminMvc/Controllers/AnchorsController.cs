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
    public class AnchorsController : ControllerBase<Anchor, Anchor, IAnchorRepository>
    {
        public AnchorsController(IAnchorRepository repository) : base(repository)
        {
        }

        protected override void SetStatus(Anchor item, EntityStatus status)
        {
            item.Status = status;
        }

        public ActionResult Index(long domainID, int? page)
        {
            var domain = new DomainRepository().Get(domainID);
            if (domain == null) return View("NotFound", "Shared", "Anchor");

            ViewData["Domain"] = Mapper.Map<Domain, DomainModel>(domain);

            return View(
                (from anchor in Repository.FindAll()
                 where anchor.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase)
                 select Mapper.Map<Anchor,AnchorModel>(anchor))
                    .AsPagination(page ?? 1, DefaultPageSize));
        }

        public ActionResult Details(string owner, string thumbprint)
        {
            var anchor = Repository.Get(owner, thumbprint);
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
    }
}