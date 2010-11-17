using System;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;
using System.Security.Cryptography;
using System.Text;

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
            PasswordHash = HashPassword(password);
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
            PasswordHash = HashPassword(password);
        }

        public bool CheckPassword(string password)
        {
            if (string.IsNullOrEmpty(Username))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidUsername);
            }

            if (string.IsNullOrEmpty(password))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidPassword);
            }

            return PasswordHash.Equals(HashPassword(password), StringComparison.Ordinal);
        }

        private string HashPassword(string password)
        {
            var source = Username.ToLower() + "|" + CreateDate.ToString("yyyyMMdd'T'HHmmss") + "|" + password;
            return Convert.ToBase64String(SHA1.Create().ComputeHash(Encoding.UTF8.GetBytes(source)));
        }

        [Column(Name = "AdministratorID", IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID { get; set; }

        [Column(Name = "Username", CanBeNull = false, IsPrimaryKey = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Username { get; set; }

        [Column(Name = "PasswordHash", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public string PasswordHash { get; set; }

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