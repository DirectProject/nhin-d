using System;
using System.Linq;
using System.Web.Mvc;

using AdminMvc.Models;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace AdminMvc.Controllers
{
    public class CertificatesController : ControllerBase<Certificate, CertificateRepository>
    {
        public ActionResult Show(long domainID, int? page)
        {
            var domain = new DomainRepository().Get(domainID);
            if (domain == null) return View("NotFound");

            ViewData["Domain"] = domain;

            return View(
                (from certificate in Repository.FindAll()
                 where certificate.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase)
                 select certificate)
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