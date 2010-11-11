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
    public class CertificatesController : ControllerBase<Certificate, CertificateModel, ICertificateRepository>
    {
        public CertificatesController(ICertificateRepository repository) : base(repository)
        {
        }

        protected override void SetStatus(Certificate item, EntityStatus status)
        {
            item.Status = status;
        }

        public ActionResult Index(long? domainID, int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            Func<Certificate, bool> filter = certificate => true;
            if (domainID.HasValue)
            {
                var domain = Mapper.Map<Domain, DomainModel>(new DomainRepository().Get(domainID.Value));
                ViewData["Domain"] = domain;
                filter = certificate => certificate.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase);
            }

            return View(Repository.FindAll()
                            .Where(filter)
                            .Select(certificate => Mapper.Map<Certificate, CertificateModel>(certificate))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        public ActionResult Details(long id)
        {
            var certificate = Repository.Get(id);
            if (certificate == null) return View("NotFound");

            return Json(Mapper.Map<Certificate, CertificateModel>(certificate), "text/json", JsonRequestBehavior.AllowGet);
        }

        public ActionResult Add(string owner)
        {
            return View(new CertificateUploadModel {Owner = owner});
        }

        [HttpPost]
        public ActionResult Add(FormCollection formValues)
        {
            var model = new CertificateUploadModel();

            if (TryUpdateModel(model))
            {
                var bytes = GetFileFromRequest("certificateFile");
                var cert = new Certificate(model.Owner, bytes, model.Password);
                Repository.Add(cert);

                var domain = new DomainRepository().GetByDomainName(model.Owner);
                if (domain == null) return View("NotFound");

                return RedirectToAction("Index", new { domainID = domain.ID });
            }

            return View(model);
        }

        protected override ActionResult EnableDisable(long id, EntityStatus status)
        {
            var certificate = Repository.Get(id);
            if (certificate == null) return View("NotFound");

            certificate = Repository.ChangeStatus(certificate, status);

            return Json(Mapper.Map<Certificate, CertificateModel>(certificate), "text/json");
        }
    }
}