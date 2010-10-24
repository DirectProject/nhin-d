using System;
using System.Web;

namespace Health.Direct.Config.Service
{
    public partial class TestService : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            Response.Cache.SetCacheability(HttpCacheability.NoCache);
            try
            {
                Service.Current.Store.Domains.Get("somerandomdomain.xyz");
                this.TestStatus.Text = "Database is accessible!";
            }
            catch(Exception ex)
            {
                this.TestStatus.Text = ex.Message + Environment.NewLine + ex.InnerException.Message;
            }
        }
    }
}
