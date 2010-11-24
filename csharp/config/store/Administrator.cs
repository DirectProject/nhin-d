using System;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;

namespace Health.Direct.Config.Store
{
    [Table(Name = "Administrators")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class Administrator
    {
        public Administrator()
        {
            this.CreateDate = DateTimeHelper.Now;
            this.UpdateDate = this.CreateDate;
            this.Status = EntityStatus.New;
        }

        public Administrator(string username, string password)
            : this()
        {
            Username = username;
            PasswordHash = new PasswordHash(this, password);
        }

        public Administrator(Administrator that)
        {
            this.CreateDate = that.CreateDate;
            this.ID = that.ID;
            this.PasswordHash = that.PasswordHash;
            this.Status = that.Status;
            this.UpdateDate = that.UpdateDate;
            this.Username = that.Username;
        }

        public void UpdateFrom(Administrator that)
        {
            this.PasswordHash = that.PasswordHash;
            this.Status = that.Status;
            this.UpdateDate = DateTimeHelper.Now;
        }

        public void SetPassword(string password)
        {
            PasswordHash = new PasswordHash(this, password);
        }

        public bool CheckPassword(string password)
        {
            if (string.IsNullOrEmpty(password))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidPassword);
            }

            return PasswordHash == new PasswordHash(this, password);
        }

        [Column(Name = "AdministratorID", IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID { get; set; }

        [Column(Name = "Username", CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Username { get; set; }

        [Column(Name = "PasswordHash", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        internal string PasswordHashDB { get; set; }

        [DataMember(IsRequired = true)]
        public PasswordHash PasswordHash
        {
            get
            {
                return new PasswordHash(PasswordHashDB);
            }
            set
            {
                PasswordHashDB = value != null ? value.HashedPassword : null;
            }
        }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate { get; set; }

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate { get; set; }

        [Column(Name = "Status", DbType = "tinyint", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public EntityStatus Status { get; set; }
    }
}