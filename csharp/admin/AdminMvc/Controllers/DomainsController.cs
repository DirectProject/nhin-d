using System.Web.Mvc;

using AdminMvc.Models;

using Health.Direct.Config.Store;

namespace AdminMvc.Controllers
{
    public class DomainsController : ControllerBase<Domain, DomainRepository>
    {
        public ActionResult Addresses(long id)
        {
            return RedirectToAction("Show", "Addresses", new {domainID = id});
        }

        public ActionResult Anchors(long id)
        {
            return RedirectToAction("Show", "Anchors", new {domainID = id});
        }

        public ActionResult Certificates(long id)
        {
            return RedirectToAction("Show", "Certificates", new {domainID = id});
        }

        public ActionResult Add()
        {
            return View(new DomainModel());
        }

        [HttpPost]
        public ActionResult Add(FormCollection formValues)
        {
            var domain = new DomainModel();

            if (TryUpdateModel(domain))
            {
                var newDomain = Repository.Add(new Domain(domain.Name));

                return RedirectToAction("Details", new {id = newDomain.ID});
            }

            return View(domain);
        }

        public ActionResult Delete(long id)
        {
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            ViewData["ReturnUrl"] = Request.UrlReferrer.PathAndQuery;
            return View(domain);
        }
        
        [HttpPost]
        public ActionResult Delete(long id, string confirmButton)
        {
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            Repository.Delete(domain);

            return View("Deleted");
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
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            domain.Status = status;
            Repository.Update(domain);

            return View("Details", domain);
        }
    }
}
