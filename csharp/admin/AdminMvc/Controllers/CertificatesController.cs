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
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Web.Mvc;

using Health.Direct.Admin.Console.Models;
using Health.Direct.Admin.Console.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;
using System.Net.Mail;

namespace Health.Direct.Admin.Console.Controllers
{
    public class CertificatesController : ControllerBase<Certificate, CertificateModel, ICertificateRepository>
    {
        private readonly IDomainRepository m_domainRepository;

        public CertificatesController(ICertificateRepository repository, IDomainRepository domainRepository) : base(repository)
        {
            m_domainRepository = domainRepository;
        }

        protected override void SetStatus(Certificate item, EntityStatus status)
        {
            item.Status = status;
        }

        [Authorize]
        public ActionResult Index(long? domainID, int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            Func<Certificate, bool> filter = certificate => true;
            if (domainID.HasValue && domainID.Value > 0)
            {
                var domain = Mapper.Map<Domain, DomainModel>(m_domainRepository.Get(domainID.Value));
                if (domain != null)
                {
                    ViewData["Domain"] = domain;
                    filter = certificate => certificate.Owner.Equals(domain.Name, StringComparison.OrdinalIgnoreCase);
                }
            }

            return View(Repository.Query()
                            .Where(filter)
                            .Select(certificate => Mapper.Map<Certificate, CertificateModel>(certificate))
                            .AsPagination(page ?? 1, DefaultPageSize));
        }

        [Authorize]
        public ActionResult Details(long id)
        {
            var certificate = Repository.Get(id);
            if (certificate == null) return View("NotFound");

            return Json(Mapper.Map<Certificate, CertificateModel>(certificate), "text/json", JsonRequestBehavior.AllowGet);
        }

        [Authorize]
        public ActionResult Add(long domainID)
        {
            var domain = m_domainRepository.Get(domainID);
            return View(new CertificateUploadModel(Mapper.Map<Domain,DomainModel>(domain)));
        }

        [Authorize]
        [HttpPost]
        public ActionResult Add(FormCollection formValues)
        {
            var model = new CertificateUploadModel();

            if (LocalTryUpdateModel(model))
            {
                byte[] bytes = GetFileFromRequest("certificateFile");

                if (bytes.Length < 1)
                {
                    ModelState.AddModelError("certificateFile", "No file provided");
                    return View(model);
                }
                
                var domain = m_domainRepository.GetByDomainName(model.Owner);
				if (domain == null)
				{
					// Check the address host
					try
					{
						domain = m_domainRepository.GetByDomainName(new MailAddress(model.Owner).Host);
						if (domain == null)
						{
							return View("NotFound");
						}
					}
					catch
					{
						return View("NotFound");
					}
				}

                try
                {
                    var cert = new Certificate(model.Owner, bytes, model.Password);
                    Repository.Add(cert);
                }
                catch (CryptographicException ex)
                {
                    ModelState.AddModelError("certificateFile",
                                             "Invalid certificate file - " + ex.GetBaseException().Message);
                    return View(model);
                }

                return RedirectToAction("Index", new {domainID = domain.ID});
            }

            return View(model);
        }

        public ActionResult Resolve(ResolveModel model)
        {
            return View(model);
        }

        [Authorize]
        [HttpPost]
        public ActionResult Resolve(FormCollection formValues)
        {
            var model = new ResolveModel();

            if (TryUpdateModel(model))
            {
                var certs = Repository.Resolve(model.Owner, model.ShowData);

                if (certs.Count() > 0)
                {
                    return View("ResolveResult",
                                Mapper.Map<IEnumerable<Certificate>, IEnumerable<CertificateModel>>(certs));
                }
                
                ModelState.AddModelError("Owner", "No certificates found for this owner.");
            }

            return View(model);
        }

		private bool LocalTryUpdateModel(CertificateUploadModel model)
		{
			if (!TryUpdateModel(model)) return false;

			var domain = m_domainRepository.GetByDomainName(model.Owner);
			if (domain != null)
			{
				return true;
			}

			// Check the address host
			try
			{
				domain = m_domainRepository.GetByDomainName(new MailAddress(model.Owner).Host);
				if (domain != null)
				{
					return true;
				}
			}
			catch { }

			model.Owner = "";
			ModelState.AddModelError("Owner", "Domain does not exist");
			return false;
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