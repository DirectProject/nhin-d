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

        protected override void SetStatus(Domain item, EntityStatus status)
        {
            item.Status = status;
        }

        public ActionResult Index(int? page)
        {
            return IndexBase(page);
        }

        public ActionResult Addresses(long id)
        {
            return RedirectToAction("Index", "Addresses", new {domainID = id});
        }

        public ActionResult Anchors(long id)
        {
            return RedirectToAction("Index", "Anchors", new {domainID = id});
        }

        public ActionResult Certificates(long id)
        {
            return RedirectToAction("Index", "Certificates", new {domainID = id});
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

        public override ActionResult Details(long id)
        {
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            return Json(Mapper.Map<Domain, DomainModel>(domain), "text/json", JsonRequestBehavior.AllowGet);
        }
    }
}
