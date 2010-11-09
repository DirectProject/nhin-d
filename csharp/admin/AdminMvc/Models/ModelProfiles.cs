using System;

using AutoMapper;

using Health.Direct.Config.Store;

namespace AdminMvc.Models
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