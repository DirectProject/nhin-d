using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace NHINDirect.Config.Service
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
                this.TestStatus.Text = ex.Message;
            }
        }
    }
}
