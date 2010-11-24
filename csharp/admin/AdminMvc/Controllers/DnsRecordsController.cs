using System.Linq;
using System.Web.Mvc;

using AutoMapper;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;
using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace Health.Direct.Admin.Console.Controllers
{
    public class DnsRecordsController : ControllerBase<DnsRecord, DnsRecordModel, IDnsRecordRepository>
    {
        public DnsRecordsController(IDnsRecordRepository repository) : base(repository)
        {
        }

        [Authorize]
        public ActionResult Index(int? page)
        {
            //ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            return View(Repository.Query()
                            .Select(record => Mapper.Map<DnsRecord, DnsRecordModel>(record))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        [Authorize]
        public ActionResult AddMx()
        {
            return View(new MxRecordModel());
        }

        [Authorize]
        [HttpPost]
        public ActionResult AddMx(FormCollection formValues)
        {
            return Add<MxRecordModel>();
        }

        [Authorize]
        public ActionResult AddAname()
        {
            return View(new AddressRecordModel());
        }

        [Authorize]
        [HttpPost]
        public ActionResult AddAname(FormCollection formValues)
        {
            return Add<AddressRecordModel>();
        }

        [Authorize]
        public ActionResult AddSoa()
        {
            return View(new SoaRecordModel());
        }

        [Authorize]
        [HttpPost]
        public ActionResult AddSoa(FormCollection formValues)
        {
            return Add<SoaRecordModel>();
        }

        private ActionResult Add<T>()
            where T : DnsRecordModel, new()
        {
            var model = new T();

            if (TryUpdateModel(model))
            {
                Repository.Add(Mapper.Map<T, DnsRecord>(model));
                return RedirectToAction("Index");
            }

            return View(model);

        }

        //public ActionResult Details(long id)
        //{
        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    return Json(Mapper.Map<Address, AddressModel>(address), "text/json", JsonRequestBehavior.AllowGet);
        //}

        //public ActionResult Edit(long id)
        //{
        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    return View(Mapper.Map<Address,AddressModel>(address));
        //}

        //[HttpPost]
        //public ActionResult Edit(FormCollection formValues)
        //{
        //    long id;
        //    long.TryParse(formValues["id"], out id);

        //    var address = Repository.Get(id);
        //    if (address == null) return View("NotFound");

        //    var model = Mapper.Map<Address, AddressModel>(address);

        //    if (TryUpdateModel(model))
        //    {
        //        address.DisplayName = model.DisplayName;
        //        Repository.Update(address);
        //        return RedirectToAction("Index", new { domainID = address.DomainID });
        //    }

        //    return View(model);
        //}

        protected override void SetStatus(DnsRecord item, EntityStatus status)
        {
        }
    }
}