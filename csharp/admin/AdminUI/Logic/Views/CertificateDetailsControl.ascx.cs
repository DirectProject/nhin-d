using System;
using System.Collections;
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
using NHINDirect.Config.Client.CertificateService;
using NHINDirect.Config.Store;

namespace AdminUI.Logic.Views
{
    public partial class CertificateDetailsControl : System.Web.UI.UserControl
    {
        public long CertificateId
        {
            get
            {
                long returnValue = -1;
                if (ViewState["CertificateId"] != null)
                {

                    long.TryParse(ViewState["CertificateId"].ToString(), out returnValue);

                }
                return returnValue;
            }
            set { ViewState["CertificateId"] = value; }
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

        private Certificate _model;
        private CertificateStoreClient _certificateStoreClient = new CertificateStoreClient();

        public event EventHandler CertificateCancelled;
        public event EventHandler CertificateSaved;

        protected void Page_Load(object sender, EventArgs e)
        {
            CertificateCancelled += delegate(object s, EventArgs ea) { };
            CertificateSaved += delegate(object s, EventArgs ea) { };
        }

        protected void SaveButton_Click(object sender, EventArgs e)
        {
            //TODO: Great candidate for an extension method
            var status = (EntityStatus)Enum.ToObject(typeof(EntityStatus), int.Parse(StatusDropDownList.SelectedValue));
            _certificateStoreClient.SetCertificateStatus(new long[] { CertificateId }, status);

            CertificateSaved(this, null);
        }

        protected void CancelButton_Click(object sender, EventArgs e)
        {
            CertificateCancelled(this, null);
        }


        protected override void OnPreRender(EventArgs e)
        {
            base.OnPreRender(e);

            if (CertificateId >= 0)
            {
                CertificateGetOptions options = new CertificateGetOptions();
                options.IncludeData = true;
                _model = _certificateStoreClient.GetCertificate(CertificateId, options);

                DataBindControls();
            }
        }

        private void DataBindControls()
        {
            this.OwnerTitleLabel.Text = _model.Owner;

            this.OwnerLabel.Text = _model.Owner;
            this.ThumbprintLabel.Text = _model.Thumbprint;
            this.CreateDateLabel.Text = string.Format("{0:d}", _model.CreateDate);
            this.ValidStartDateLabel.Text = string.Format("{0:d}", _model.ValidStartDate);
            this.ValidEndDateLabel.Text = string.Format("{0:d}", _model.ValidEndDate);
            this.StatusDropDownList.SelectedValue = ((int)_model.Status).ToString();
            this.HasDataCheckBox.Checked = _model.HasData;


        }

     
    }
}