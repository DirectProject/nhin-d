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
        #region "Public Properties with ViewState as backing store"
     
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
        #endregion
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
                 CertificateListControl1.Owner = EmailAddress;
                _model = _addressManagerClient.GetAddress(EmailAddress);
                DataBindControls();

            }
        }

        private void DataBindControls()
        {
            this.OwnerTitleLabel.Text = Owner;
            this.EmailAddressLabel.Text = _model.EmailAddress;
            this.DisplayNameTextBox.Text = _model.DisplayName;
            this.CreateDateLabel.Text = string.Format("{0:d}", _model.CreateDate);
            this.UpdateDateLabel.Text = string.Format("{0:d}", _model.UpdateDate);
            this.StatusDropDownList.SelectedValue = ((int)_model.Status).ToString();
            this.TypeTextBox.Text = _model.Type;

        }

      

        protected void SaveButton_Click(object sender, EventArgs e)
        {
            string newDisplayName = this.DisplayNameTextBox.Text;
            string newType = TypeTextBox.Text;
            var newStatus = (EntityStatus)Enum.ToObject(typeof(EntityStatus), int.Parse(StatusDropDownList.SelectedValue));

            Address oldAddress;
            if (!string.IsNullOrEmpty(EmailAddress))
            {
                oldAddress = _addressManagerClient.GetAddress(EmailAddress);
                oldAddress.DisplayName = newDisplayName;
                oldAddress.Type = newType;
                oldAddress.Status = newStatus;

                _addressManagerClient.UpdateAddress(oldAddress);
                AddressSaved(this, null);
            }
        }

        protected void CancelButton_Click(object sender, EventArgs e)
        {
            AddressCancelled(this, null);
        }
    }
}