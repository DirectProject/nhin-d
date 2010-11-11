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

        public ActionResult Add(string owner)
        {
            return View(new AnchorUploadModel { Owner = owner });
        }

        [HttpPost]
        public ActionResult Add(FormCollection formValues)
        {
            var model = new AnchorUploadModel();

            if (TryUpdateModel(model))
            {
                var bytes = GetFileFromRequest("certificateFile");
                var anchor = new Anchor(model.Owner, bytes, model.Password)
                                 {
                                     ForIncoming = (model.Purpose & PurposeType.Incoming) == PurposeType.Incoming,
                                     ForOutgoing = (model.Purpose & PurposeType.Outgoing) == PurposeType.Outgoing
                                 };
                Repository.Add(anchor);

                var domain = new DomainRepository().GetByDomainName(model.Owner);
                if (domain == null) return View("NotFound");

                return RedirectToAction("Index", new { domainID = domain.ID });
            }

            return View(model);
        }

        protected override ActionResult EnableDisable(long id, EntityStatus status)
        {
            var anchor = Repository.Get(id);
            if (anchor == null) return View("NotFound");

            anchor = Repository.ChangeStatus(anchor, status);

            return Json(Mapper.Map<Anchor, AnchorModel>(anchor), "text/json");
        }
    }
}