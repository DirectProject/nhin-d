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
using System.Net.Mail;
using System.Web.Mvc;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace Health.Direct.Admin.Console.Controllers
{
    public class AddressesController : ControllerBase<Address, AddressModel, IAddressRepository>
    {
        private readonly IDomainRepository m_domainRepository;

        public AddressesController(IAddressRepository repository, IDomainRepository domainRepository) : base(repository)
        {
            m_domainRepository = domainRepository;
        }

        protected override void SetStatus(Address item, EntityStatus status)
        {
            item.Status = status;
        }

        [Authorize]
        public ActionResult Index(long? domainID, int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            Func<Address, bool> filter = address => true;
            if (domainID.HasValue && domainID.Value > 0)
            {
                var domain = Mapper.Map<Domain, DomainModel>(m_domainRepository.Get(domainID.Value));
                ViewData["Domain"] = domain;
                filter = address => address.DomainID == domain.ID;
            }

            return View(Repository.Query()
                            .Where(filter)
                            .Select(address => Mapper.Map<Address, AddressModel>(address))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        [Authorize]
        public ActionResult Add(long domainID)
        {
            return View(new AddressModel {DomainID = domainID});
        }

        [Authorize]
        [HttpPost]
        public ActionResult Add(FormCollection formValues)
        {
            var model = Mapper.Map<Address, AddressModel>(new Address());

            if (LocalTryUpdateModel(model))
            {
                var address = Mapper.Map<AddressModel, Address>(model);
                Repository.Add(address);
                return RedirectToAction("Index", new { domainID = address.DomainID });
            }

            return View(model);
        }

        private bool LocalTryUpdateModel(AddressModel model)
        {
            if (!TryUpdateModel(model)) return false;

            var domain = m_domainRepository.GetByDomainName(new MailAddress(model.EmailAddress).Host);
            if (domain == null)
            {
                ModelState.AddModelError("EmailAddress", "Domain does not exist for email address");
                return false;
            }

            model.DomainID = domain.ID;
            return true;
        }

        [Authorize]
        public ActionResult Details(long id)
        {
            var address = Repository.Get(id);
            if (address == null) return View("NotFound");

            return Json(Mapper.Map<Address, AddressModel>(address), "text/json", JsonRequestBehavior.AllowGet);
        }

        [Authorize]
        public ActionResult Edit(long id)
        {
            var address = Repository.Get(id);
            if (address == null) return View("NotFound");

            return View(Mapper.Map<Address,AddressModel>(address));
        }

        [Authorize]
        [HttpPost]
        public ActionResult Edit(FormCollection formValues)
        {
            long id;
            long.TryParse(formValues["id"], out id);

            var address = Repository.Get(id);
            if (address == null) return View("NotFound");

            var model = Mapper.Map<Address, AddressModel>(address);

            if (TryUpdateModel(model))
            {
                address.DisplayName = model.DisplayName;
                Repository.Update(address);
                return RedirectToAction("Index", new { domainID = address.DomainID });
            }

            return View(model);
        }
    }
}