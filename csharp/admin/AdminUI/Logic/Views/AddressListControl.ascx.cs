using System;
using System.Collections;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using NHINDirect.Config.Client.DomainManager;
using NHINDirect.Config.Store;

namespace AdminUI.Logic.Views
{
    public partial class AddressListControl : System.Web.UI.UserControl
    {
        private NHINDirect.Config.Client.DomainManager.AddressManagerClient _addressManagerClient =
            new AddressManagerClient();

        private IEnumerable<Address> _model;
        public const int MAXRESULTSPERPAGE = 20;
        public event EventHandler<AddressListControlEventArgs> Command;
        public string Owner
        {
            get
            {
                if (ViewState["Owner"] != null)
                {
                    return ViewState["Owner"] as string;
                }
                else
                {
                    return String.Empty;
                }
            }
            set { ViewState["Owner"] = value; }
        }

        protected void Page_Load(object sender, EventArgs e)
        {
            UpdateModel();
            Command += delegate(object o, AddressListControlEventArgs ea) { };
        }

        protected override void OnPreRender(EventArgs e)
        {
            base.OnPreRender(e);
            //DataBindControls();
            AddressesGridView.DataSource = _model;
            AddressesGridView.DataBind();
        }
        private void UpdateModel()
        {
            bool filterByOwner = false;
            if (!string.IsNullOrEmpty(Request.QueryString["Owner"]))
            {
                filterByOwner = true;
                Owner = Request.QueryString["Owner"];
                OwnerTitleContainer.Visible = true;
                OwnerTitleLabel.Text = Owner;
            }

            _model = filterByOwner ? _addressManagerClient.EnumerateDomainAddresses(2 /*DomainID*/, MAXRESULTSPERPAGE) : _addressManagerClient.EnumerateAddresses(MAXRESULTSPERPAGE);
        }

        protected void AddressesGridView_RowCommand(object sender, GridViewCommandEventArgs e)
        {
            long addressId = WebHelper.GetDataKeyFromGridView(sender, e.CommandArgument, "ID");
            int domainId = WebHelper.GetDataKeyFromGridView(sender, e.CommandArgument, "DomainID");
           
            Command(this, new AddressListControlEventArgs(addressId, e.CommandName));

        }
        public class AddressListControlEventArgs : EventArgs
        {
            public long AddressId { get; private set; }
            public string CommandName { get; private set; }
            public string Owner { get; set; }

            public AddressListControlEventArgs(long addressId, string commandName)
            {
                AddressId = addressId;
                CommandName = commandName;
            }
            public AddressListControlEventArgs(int certificateId, string owner, string commandName)
                : this(certificateId, commandName)
            {
                Owner = owner;
            }

        }
    }
}