using System;
using System.Web.Mvc;

using AdminMvc.Models;
using AdminMvc.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

namespace AdminMvc.Controllers
{
    public class DomainsController : ControllerBase<Domain, DomainModel, IDomainRepository>
    {
        public DomainsController(IDomainRepository repository)
            : base(repository)
        {
        }

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
            // pick up the default values from the real model
            var model = Mapper.Map<Domain,DomainModel>(new Domain());

            if (TryUpdateModel(model))
            {
                Repository.Add(Mapper.Map<DomainModel,Domain>(model));
                return RedirectToAction("Index");
            }

            return View(model);
        }

        [HttpPost]
        public string Delete(long id)
        {
            try
            {
                var domain = Repository.Get(id);
                if (domain == null) return "NotFound";

                Repository.Delete(domain);

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
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            domain.Status = status;
            Repository.Update(domain);

            return Json(Mapper.Map<Domain, DomainModel>(domain), "text/json");
        }

        public override ActionResult Details(long id)
        {
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            return Json(Mapper.Map<Domain, DomainModel>(domain), "text/json", JsonRequestBehavior.AllowGet);
        }
    }
}
