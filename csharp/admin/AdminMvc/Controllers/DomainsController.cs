/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Web.Mvc;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Controllers
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

        [Authorize]
        public ActionResult Index(int? page)
        {
            return IndexBase(page);
        }

        [Authorize]
        public ActionResult Addresses(long id)
        {
            return RedirectToAction("Index", "Addresses", new {domainID = id});
        }

        [Authorize]
        public ActionResult Anchors(long id)
        {
            return RedirectToAction("Index", "Anchors", new {domainID = id});
        }

        [Authorize]
        public ActionResult Certificates(long id)
        {
            return RedirectToAction("Index", "Certificates", new {domainID = id});
        }

        public ActionResult Add()
        {
            return View(new DomainModel());
        }

        [Authorize]
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

        [Authorize]
        public ActionResult Details(long id)
        {
            var domain = Repository.Get(id);
            if (domain == null) return View("NotFound");

            return Json(Mapper.Map<Domain, DomainModel>(domain), "text/json", JsonRequestBehavior.AllowGet);
        }

        [Authorize]
        public JsonResult List(string q, int? max)
        {
            var query = (from d in Repository.Query()
                         where d.Name.StartsWith(q, StringComparison.OrdinalIgnoreCase)
                         select d.Name);
            if (max.HasValue)
            {
                query = query.Take(max.Value);
            }

            return Json(query.ToList(), "text/json", JsonRequestBehavior.AllowGet);
        }
    }
}
