using System.Data.Entity;
using System.Data.Entity.Core.Objects;
using System.Data.Entity.Infrastructure;

namespace Health.Direct.DatabaseAuditor
{
    public class AuditContext : DbContext
    {
        public DbSet<AuditEvent> Events { get; set; }

        public AuditContext()
        {
        }

        public AuditContext(string connectionString, int commandTimeout):base(connectionString)
        {
            ((IObjectContextAdapter)this).ObjectContext.CommandTimeout = commandTimeout;
        }

        public AuditContext CreateContext(AuditorSettings settings)
        {
            if (settings != null)
            {
                return new AuditContext(settings.ConnectionString, settings.GetQueryTimeoutSeconds());
            }
            return new AuditContext();
        }

        public ObjectContext ObjectContext
        {
            get
            {
                return ((IObjectContextAdapter)this).ObjectContext;
            }
        }
    }
}