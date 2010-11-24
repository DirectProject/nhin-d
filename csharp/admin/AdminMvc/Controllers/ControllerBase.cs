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

using Health.Direct.Admin.Console.Models.Repositories;

using AutoMapper;

using Health.Direct.Config.Store;

using MvcContrib.Pagination;

namespace Health.Direct.Admin.Console.Controllers
{
    public abstract class ControllerBase<T, TModel, TRepository> : ControllerErrorBase
        where T : class
        where TRepository : IRepository<T>
    {
        protected const int DefaultPageSize = 10;

        private readonly TRepository m_repository;

        protected ControllerBase(TRepository repository)
        {
            m_repository = repository;
        }

        protected TRepository Repository
        {
            get { return m_repository; }
        }

        protected abstract void SetStatus(T item, EntityStatus status);

        protected ActionResult IndexBase(int? page)
        {
            ViewData["DateTimeFormat"] = "M/d/yyyy h:mm:ss tt";

            new DateTime().ToString("");

            var paginatedItems = (from item in Repository.Query()
                        select Mapper.Map<T, TModel>(item))
                        .AsPagination(page ?? 1, DefaultPageSize);

            return View(paginatedItems);
        }

        [Authorize]
        [HttpPost]
        public string Delete(long id)
        {
            try
            {
                var item = Repository.Get(id);
                if (item == null) return "NotFound";

                Repository.Delete(item);

                return Boolean.TrueString;
            }
            catch (Exception ex)
            {
                return ex.GetBaseException().Message;
            }
        }

        [Authorize]
        public ActionResult Disable(long id)
        {
            return EnableDisable(id, EntityStatus.Disabled);
        }

        [Authorize]
        public ActionResult Enable(long id)
        {
            return EnableDisable(id, EntityStatus.Enabled);
        }

        protected virtual ActionResult EnableDisable(long id, EntityStatus status)
        {
            var item = Repository.Get(id);
            if (item == null) return View("NotFound");

            SetStatus(item, status);
            Repository.Update(item);

            return Json(Mapper.Map<T, TModel>(item), "text/json");
        }

        protected byte[] GetFileFromRequest(string keyName)
        {
            var file = Request.Files.Get(keyName);
            var bytes = new byte[file.ContentLength];
            file.InputStream.Read(bytes, 0, file.ContentLength);
            return bytes;
        }
    }
}