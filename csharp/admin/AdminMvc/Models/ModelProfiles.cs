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

using AutoMapper;

using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models
{
    public class ModelProfiles : Profile
    {
        public override string ProfileName
        {
            get
            {
                return "ViewModel";
            }
        }

        protected override void Configure()
        {
            CreateBidirectionalMap<Address, AddressModel>();
            CreateBidirectionalMap<Anchor, AnchorModel>();
            CreateBidirectionalMap<Certificate, CertificateModel>();
            CreateBidirectionalMap<Domain, DomainModel>();
            Mapper.CreateMap<string, EntityStatus>().ConvertUsing(new EntityStatusTypeConverter());
        }

        private void CreateBidirectionalMap<TSource,TDestination>()
        {
            ForSourceType<EntityStatus>()
                .AddFormatExpression(context =>
                ((EntityStatus)context.SourceValue).ToString());

            CreateMap<TSource, TDestination>();
            CreateMap<TDestination, TSource>();
        }
    }

    public class EntityStatusTypeConverter : ITypeConverter<string, EntityStatus>
    {
        public EntityStatus Convert(ResolutionContext context)
        {
            return (EntityStatus) Enum.Parse(typeof(EntityStatus), (string)context.SourceValue, true);
        }
    }
}