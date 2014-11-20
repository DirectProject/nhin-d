using System;
using System.Runtime.Serialization;
using System.Security.Cryptography;
using System.Text;

namespace Health.Direct.Config.Store
{
    [DataContract]
    public class PasswordHash : IEquatable<PasswordHash>
    {
        private string m_hashedPassword;

        public PasswordHash(string hashedPassword)
        {
            if (hashedPassword == null)
            {
                throw new ArgumentNullException("hashedPassword");
            }

            m_hashedPassword = hashedPassword;
        }

        public PasswordHash(Administrator user, string password)
        {
            if (user == null)
            {
                throw new ArgumentNullException("user");
            }

            m_hashedPassword = HashPassword(user, password);
        }

        private static string HashPassword(Administrator user, string password)
        {
            var source = user.Username.ToLower() + "|" + user.CreateDate.ToString("yyyyMMdd'T'HHmmss") + "|" + password;
            return Convert.ToBase64String(SHA1.Create().ComputeHash(Encoding.UTF8.GetBytes(source)));
        }

        [DataMember(IsRequired = true)]
        public string HashedPassword
        {
            get
            {
                return m_hashedPassword;
            }
            private set
            {
                m_hashedPassword = value;
            }
        }

        public bool Equals(PasswordHash other)
        {
            if (ReferenceEquals(other, null)) return false;

            return (this.HashedPassword ?? "").Equals(other.HashedPassword ?? "", StringComparison.Ordinal);
        }

        public override bool Equals(object obj)
        {
            return this.Equals(obj as PasswordHash);
        }

        public override int GetHashCode()
        {
            return (m_hashedPassword != null ? m_hashedPassword.GetHashCode() : 0);
        }

        public static bool operator ==(PasswordHash a, PasswordHash b)
        {
            if (ReferenceEquals(a, null))
            {
                return ReferenceEquals(b, null);
            }

            return a.Equals(b);
        }

        public static bool operator !=(PasswordHash a, PasswordHash b)
        {
            return !(a == b);
        }

        public override string ToString()
        {
            return HashedPassword;
        }
    }
}