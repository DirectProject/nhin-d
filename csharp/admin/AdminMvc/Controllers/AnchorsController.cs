using System;
using System.Linq;
using System.Web.Mvc;

using AdminMvc.Models.Repositories;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace AdminMvc.Controllers
{
    public class AnchorsController : ControllerBase<Anchor, Anchor, IAnchorRepository>
    {
        public AnchorsController(IAnchorRepository repository) : base(repository)
        {
        }

        public ActionResult Show(long domainID, int? page)
        {
            var domain = new DomainRepository().Get(domainID);
            if (domain == null) return View("NotFound");

            ViewData["Domain"] = domain;

            return View(
                (from anchor in Repository.FindAll()
                 where anchor.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase)
                 select anchor)
                    .AsPagination(page ?? 1, DefaultPageSize));
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

        //public ActionResult Disable(long id)
        //{
        //    return EnableDisable(id, EntityStatus.Disabled);
        //}

        //public ActionResult Enable(long id)
        //{
        //    return EnableDisable(id, EntityStatus.Enabled);
        //}

        //private ActionResult EnableDisable(long id, EntityStatus status)
        //{
        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    address.Status = status;
        //    Repository.Update(address);

        //    return View("Details", address);
        //}
    }
}