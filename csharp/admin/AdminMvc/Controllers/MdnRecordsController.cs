using System;
using System.Linq;
using System.Text;
using System.Web.Mvc;

using AutoMapper;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace Health.Direct.Admin.Console.Controllers
{
    public class MdnRecordsController : ControllerBase<Mdn, MdnModel, IMdnRecordRepository>
    {
        public MdnRecordsController(IMdnRecordRepository repository)
            : base(repository)
        {
        }

        [Authorize]
        public ActionResult Index(int? page)
        {
            //ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";
            var mdns = Repository.Query().ToList();
            return View(mdns.Select(record => Mapper.Map<Mdn, MdnModel>(record))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        protected override void SetStatus(Mdn item, EntityStatus status)
        {
        }
    }
}