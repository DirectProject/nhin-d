using System;
using System.Web.Security;

namespace Health.Direct.Admin.Console.Common
{
    public class DirectMembershipProvider : MembershipProvider
    {
        public override MembershipUser CreateUser(string username, string password, string email, string passwordQuestion, string passwordAnswer, bool isApproved, object providerUserKey, out MembershipCreateStatus status)
        {
            throw new NotImplementedException();
        }

        public override bool ChangePasswordQuestionAndAnswer(string username, string password, string newPasswordQuestion, string newPasswordAnswer)
        {
            throw new NotImplementedException();
        }

        public override string GetPassword(string username, string answer)
        {
            throw new NotImplementedException();
        }

        public override bool ChangePassword(string username, string oldPassword, string newPassword)
        {
            throw new NotImplementedException();
        }

        public override string ResetPassword(string username, string answer)
        {
            throw new NotImplementedException();
        }

        public override void UpdateUser(MembershipUser user)
        {
            throw new NotImplementedException();
        }

        public override bool ValidateUser(string username, string password)
        {
            return (username == "admin" && password == "admin");
        }

        public override bool UnlockUser(string userName)
        {
            throw new NotImplementedException();
        }

        public override MembershipUser GetUser(object providerUserKey, bool userIsOnline)
        {
            throw new NotImplementedException();
        }

        public override MembershipUser GetUser(string username, bool userIsOnline)
        {
            throw new NotImplementedException();
        }

        public override string GetUserNameByEmail(string email)
        {
            throw new NotImplementedException();
        }

        public override bool DeleteUser(string username, bool deleteAllRelatedData)
        {
            throw new NotImplementedException();
        }

        public override MembershipUserCollection GetAllUsers(int pageIndex, int pageSize, out int totalRecords)
        {
            throw new NotImplementedException();
        }

        public override int GetNumberOfUsersOnline()
        {
            throw new NotImplementedException();
        }

        public override MembershipUserCollection FindUsersByName(string usernameToMatch, int pageIndex, int pageSize, out int totalRecords)
        {
            throw new NotImplementedException();
        }

        public override MembershipUserCollection FindUsersByEmail(string emailToMatch, int pageIndex, int pageSize, out int totalRecords)
        {
            throw new NotImplementedException();
        }

        public override bool EnablePasswordRetrieval { get { return false; } }
        public override bool EnablePasswordReset { get { return false; } }
        public override bool RequiresQuestionAndAnswer { get { return false; } }
        public override string ApplicationName { get; set; }
        public override int MaxInvalidPasswordAttempts { get { return 5; } }
        public override int PasswordAttemptWindow { get { return 10; } }
        public override bool RequiresUniqueEmail { get { return false; } }
        public override MembershipPasswordFormat PasswordFormat { get { return MembershipPasswordFormat.Hashed; } }
        public override int MinRequiredPasswordLength { get { return 6; } }
        public override int MinRequiredNonAlphanumericCharacters { get { return 0; } }
        public override string PasswordStrengthRegularExpression { get { return ".+"; } }
    }
}
