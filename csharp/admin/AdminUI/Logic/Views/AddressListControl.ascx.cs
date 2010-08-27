using System;
using System.Collections.Generic;
using System.ServiceModel;
using System.Web.UI.WebControls;
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
        public long DomainId
        {
            get
            {
                long returnValue = -1;
                if (ViewState["DomainId"] != null)
                {

                    long.TryParse(ViewState["DomainId"].ToString(), out returnValue);

                }
                return returnValue;
            }
            set { ViewState["DomainId"] = value; }
        }



        protected void Page_Load(object sender, EventArgs e)
        {

            Command += delegate(object o, AddressListControlEventArgs ea) { };
        }

        protected override void OnPreRender(EventArgs e)
        {
            base.OnPreRender(e);
            UpdateModel();
            DataBindControls();

        }

        private void DataBindControls()
        {
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

                // TODO: Do not rely on DomainID for filtering. Change it once they service API is updated
                long domainId = -1;
                long.TryParse(Request.QueryString["DomainId"], out domainId);
                DomainId = domainId;
                OwnerTitleContainer.Visible = true;
                OwnerTitleLabel.Text = Owner;

            }

            _model = filterByOwner ? _addressManagerClient.EnumerateDomainAddresses(Owner, MAXRESULTSPERPAGE)
                : _addressManagerClient.EnumerateAddresses(MAXRESULTSPERPAGE);
        }

        protected void AddressesGridView_RowCommand(object sender, GridViewCommandEventArgs e)
        {
            long addressId = WebHelper.GetDataKeyFromGridView(sender, e.CommandArgument, "ID");
            string emailAddress = WebHelper.GetDataKeyAsObjectFromGridView(sender, e.CommandArgument, "EmailAddress").ToString();
            long domainId = WebHelper.GetDataKeyFromGridView(sender, e.CommandArgument, "DomainID");

            switch (e.CommandName)
            {
                case "Remove":
                    try
                    {
                        _addressManagerClient.RemoveAddress(emailAddress);
                    }
                    catch(FaultException<ConfigStoreFault> faultException)
                    {
                        ErrorLiteral.Text = "An error has occurred.";
                    }

                    break;

            }
            //TODO: Provide Owner 
            Command(this, new AddressListControlEventArgs(addressId, emailAddress, domainId, e.CommandName));

        }
        public class AddressListControlEventArgs : EventArgs
        {
            public long AddressId { get; private set; }
            public long DomainId { get; private set; }
            public string CommandName { get; private set; }
            public string EmailAddress { get; private set; }

            public AddressListControlEventArgs(long addressId, string emailAddress, long domainId, string commandName)
            {
                AddressId = addressId;
                EmailAddress = emailAddress;
                CommandName = commandName;
                DomainId = domainId;
            }

        }
    }
}