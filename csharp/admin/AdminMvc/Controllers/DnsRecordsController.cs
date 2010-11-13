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

            return View(Repository.FindAll()
                            .Select(record => Mapper.Map<DnsRecord, DnsRecordModel>(record))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        //public ActionResult Add(long domainID)
        //{
        //    return View(new AddressModel {DomainID = domainID});
        //}

        //[HttpPost]
        //public ActionResult Add(FormCollection formValues)
        //{
        //    var model = Mapper.Map<Address, AddressModel>(new Address());

        //    if (TryUpdateModel(model))
        //    {
        //        Repository.Add(Mapper.Map<AddressModel, Address>(model));
        //        return RedirectToAction("Index", new { domainID = model.DomainID });
        //    }

        //    return View(model);
        //}

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