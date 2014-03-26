using System;

using Health.Direct.Config.Client.AuthManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models.Repositories
{
    public class AuthRepository : IAuthRepository
    {
        private readonly IAuthManager m_client;

        public AuthRepository(IAuthManager client)
        {
            m_client = client;
        }

        protected IAuthManager Client { get { return m_client; } }

        public bool IsEnabled(string username)
        {
            if (username == null)
            {
                throw new ArgumentNullException("username");
            }

            var user = m_client.GetUser(username);
            return user != null && user.Status == EntityStatus.Enabled;
        }

        public bool ValidateUser(string username, string password)
        {
            if (username == null)
            {
                throw new ArgumentNullException("username");
            }

            var user = m_client.GetUser(username);
            return user != null && m_client.ValidateUser(user.Username, new PasswordHash(user, password));
        }
    }
}