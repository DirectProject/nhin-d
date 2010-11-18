using Health.Direct.Config.Client.AuthManager;

namespace Health.Direct.Config.Client
{
    public static class AuthManagerExtensions
    {
        public static bool Authenticate(this IAuthManager client, string username, string password)
        {

            return client.Authenticate()
        }
    }
}