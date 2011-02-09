namespace Health.Direct.Admin.Console.Models.Repositories
{
    public interface IAuthRepository
    {
        bool IsEnabled(string username);
        bool ValidateUser(string username, string password);
    }
}