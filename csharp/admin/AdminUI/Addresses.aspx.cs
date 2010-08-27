using System;
using System.Collections;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;
using System.Xml.Linq;

namespace AdminUI
{
    public partial class Addresses : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            AddressListControl1.Command += new EventHandler<AdminUI.Logic.Views.AddressListControl.AddressListControlEventArgs>(AddressListControl1_Command);
        }

        void AddressListControl1_Command(object sender, AdminUI.Logic.Views.AddressListControl.AddressListControlEventArgs e)
        {
            switch (e.CommandName)
            {
                case "Details":
                    AddressDetailsControl1.EmailAddress = e.EmailAddress;
                    AddressDetailsControl1.AddressId = e.AddressId;   
                    AddressesMultiView.SetActiveView(DetailsView);
                    break;
            }
        }
    }
}
