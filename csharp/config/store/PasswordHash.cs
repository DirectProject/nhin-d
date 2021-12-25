using System;
using System.Security.Cryptography;
using System.Text;
using Health.Direct.Config.Store.Entity;

namespace Health.Direct.Config.Store
{
    
    public class PasswordHash : IEquatable<PasswordHash>
    {
        private readonly string _hashedPassword;

        public PasswordHash(string hashedPassword)
        {
            if (hashedPassword == null)
            {
                throw new ArgumentNullException(nameof(hashedPassword));
            }

            _hashedPassword = hashedPassword;
        }

        public PasswordHash(Administrator user, string password)
        {
            if (user == null)
            {
                throw new ArgumentNullException(nameof(user));
            }

            _hashedPassword = HashPassword(user, password);
        }

        private static string HashPassword(Administrator user, string password)
        {
            var source = user.Username.ToLower() + "|" + user.CreateDate.ToString("yyyyMMdd'T'HHmmss") + "|" + password;
            return Convert.ToBase64String(SHA1.Create().ComputeHash(Encoding.UTF8.GetBytes(source)));
        }

        internal string? PasswordHashDB { get; set; }

       
        public string HashedPassword => _hashedPassword;

        public bool Equals(PasswordHash other)
        {
            if (other == null) throw new ArgumentNullException(nameof(other));

            return (this.HashedPassword ?? "").Equals(other.HashedPassword ?? "", StringComparison.Ordinal);
        }

        public override bool Equals(object obj)
        {
            return this.Equals(obj as PasswordHash);
        }

        public override int GetHashCode()
        {
            return (_hashedPassword != null ? _hashedPassword.GetHashCode() : 0);
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