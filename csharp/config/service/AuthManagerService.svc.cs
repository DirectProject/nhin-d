using System;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class AuthManagerService : ConfigServiceBase, IAuthManager
    {
        public Administrator GetUser(string username)
        {
            try
            {
                return Store.Administrators.Get(username);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetUser", ex);
            }
        }

        public bool ValidateUser(string username, PasswordHash passwordHash)
        {
            try
            {
                var user = Store.Administrators.Get(username);
                return user != null && user.Status == EntityStatus.Enabled && user.PasswordHash == passwordHash;
            }
            catch (Exception ex)
            {
                throw CreateFault("Authenticate", ex);
            }
        }
    }
}
