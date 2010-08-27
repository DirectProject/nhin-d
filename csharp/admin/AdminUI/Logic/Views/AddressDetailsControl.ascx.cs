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
    public partial class AddressDetailsControl : System.Web.UI.UserControl
    {
        public event EventHandler AddressCancelled;
        public event EventHandler AddressSaved;
        private Address _model;
        private AddressManagerClient _addressManagerClient = new AddressManagerClient();

        protected void Page_Load(object sender, EventArgs e)
        {
            AddressCancelled += delegate(object s, EventArgs ea) { };
            AddressSaved += delegate(object s, EventArgs ea) { };
        }
        protected override void OnPreRender(EventArgs e)
        {
            base.OnPreRender(e);
            if (!string.IsNullOrEmpty(EmailAddress))
            {
                _model = _addressManagerClient.GetAddress(EmailAddress);
                DataBindControls();
            }
        }

        private void DataBindControls()
        {
            this.OwnerTitleLabel.Text = "[Domain Name]";
            this.EmailAddressTextBox.Text = _model.EmailAddress;

            this.CreateDateLabel.Text = string.Format("{0:d}", _model.CreateDate);
            this.UpdateDateLabel.Text = string.Format("{0:d}", _model.UpdateDate);
            this.StatusDropDownList.SelectedValue = ((int)_model.Status).ToString();
            this.TypeTextBox.Text = _model.Type;

        }

        #region "Public Properties with ViewState as backing store"
        public long AddressId
        {
            get
            {
                long returnValue = -1;
                if (ViewState["AddressId"] != null)
                {

                    long.TryParse(ViewState["AddressId"].ToString(), out returnValue);

                }
                return returnValue;
            }
            set { ViewState["AddressId"] = value; }
        }
        public string EmailAddress
        {
            get
            {
                if (ViewState["EmailAddress"] != null)
                {
                    return ViewState["EmailAddress"] as string;
                }
                else
                {
                    return String.Empty;
                }
            }
            set { ViewState["EmailAddress"] = value; }
        }
        #endregion

        protected void SaveButton_Click(object sender, EventArgs e)
        {
            //TODO: Great candidate for an extension method
            string newDisplayName = this.DisplayNameTextBox.Text;
            string newEmailAddress = this.EmailAddressTextBox.Text;
            string newType = TypeTextBox.Text;
            var newStatus = (EntityStatus)Enum.ToObject(typeof(EntityStatus), int.Parse(StatusDropDownList.SelectedValue));

            Address oldAddress;
            if (!string.IsNullOrEmpty(EmailAddress))
            {
                oldAddress = _addressManagerClient.GetAddress(EmailAddress);
                oldAddress.EmailAddress = newEmailAddress;
                oldAddress.DisplayName = newDisplayName;
                oldAddress.Type = newType;
                oldAddress.Status = newStatus;

                _addressManagerClient.UpdateAddress(oldAddress);
                AddressSaved(this, null);
            }
            else
            {
                oldAddress = new Address(1, newEmailAddress, newDisplayName) {Type = newType, Status = newStatus};
                _addressManagerClient.AddAddress(oldAddress);
            }
        }
    }
}