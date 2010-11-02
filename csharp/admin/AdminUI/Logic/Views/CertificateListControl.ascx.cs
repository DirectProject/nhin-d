using System;
using System.Collections.Generic;
using System.IO;

using AdminUI.Logic.Views;

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace Health.Direct.AdminUI.Logic.Views
{
    public partial class CertificateListControl : System.Web.UI.UserControl
    {
        private CertificateStoreClient _certStoreClient = new CertificateStoreClient();
        private IEnumerable<Certificate> _model;
        public const int MAXRESULTSPERPAGE = 20;
        public event EventHandler<CertificateListControlEventArgs> Command;

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

        protected override void OnLoad(System.EventArgs e)
        {
            base.OnLoad(e);
            // Subscribe to events
            CertificateUploadControl1.CertificateUploaded += CertificateUploadControl1_CertificateUploaded;
            CertificateUploadControl1.CertificateUploadCancelled += new EventHandler(CertificateUploadControl1_CertificateUploadCancelled);

          
        }

        private void UpdateModel()
        {
            bool filterByOwner = false;
            if (!string.IsNullOrEmpty(Owner ))
            {
                filterByOwner = true;
                OwnerTitleContainer.Visible = true;
                OwnerTitleLabel.Text = Owner;

            }

            if (filterByOwner)
            {
                _model = _certStoreClient.GetCertificatesForOwner(Owner);
                CertificateUploadControl1.Visible = true;
            }
            else
            {
                _model = _certStoreClient.EnumerateCertificates(MAXRESULTSPERPAGE);
            }
        }

        void CertificateUploadControl1_CertificateUploadCancelled(object sender, EventArgs e)
        {
            
        }

        void CertificateUploadControl1_CertificateUploaded(object sender, CertificateUploadControl.CertificateUploadControlEventArgs e)
        {

            Certificate certStore;

            string ext = Path.GetExtension(e.FileName);
            string password = String.Empty;

            switch (ext.ToLower())
            {
                default:
                    password = String.Empty;
                    break;

                case ".pfx":
                    password = e.Password;
                    //password = "passw0rd!";
                    break;
            }

            try
            {
                certStore = new Certificate(Owner, e.CertificateBytes, password);
                _certStoreClient.AddCertificate(certStore);
                UpdateModel();
            }
            catch (System.Security.Cryptography.CryptographicException ex)
            {
                ErrorLiteral.Text = ex.Message;
            }
            catch(System.ServiceModel.FaultException<Health.Direct.Config.Store.ConfigStoreFault> ex )
            {
                ErrorLiteral.Text = ex.Message;
            }
        }

        protected override void OnPreRender(System.EventArgs e)
        {
            base.OnPreRender(e);
            UpdateModel();
            DataBindControls();
        }

        private void DataBindControls()
        {
            this.CertificateGridView.DataSource = _model;
            this.CertificateGridView.DataBind();
        }

        protected void CertificateGridView_RowCommand(object sender, System.Web.UI.WebControls.GridViewCommandEventArgs e)
        {
            long certificateId = WebHelper.GetDataKeyFromGridView(sender, e.CommandArgument, "ID");

            
            switch(e.CommandName){
            
                case "Remove":
                    // TODO: Config deletion
                    _certStoreClient = new CertificateStoreClient();
                    try
                    {
                        _certStoreClient.RemoveCertificates(new long[] { certificateId });
                        UpdateModel();
                        DataBindControls();
                    }
                    catch(System.ServiceModel.FaultException<Health.Direct.Config.Store.ConfigStoreFault> faultException)
                    {
                        //TODO: More testing to figure out what are the possible failures
                        ErrorLiteral.Text = "an error has occurred.";
                    }
                    break;
            }
            
            if (this.Command != null)
            {
                Command(this, new CertificateListControlEventArgs(certificateId, e.CommandName));
            }
        }

        public class CertificateListControlEventArgs : EventArgs
        {
            public long CertificateId { get; private set; }
            public string CommandName { get; private set; }
            public string Owner { get; set; }

            public CertificateListControlEventArgs(long certificateId, string commandName)
            {
                CertificateId = certificateId;
                CommandName = commandName;
            }
            public CertificateListControlEventArgs(int certificateId, string owner, string commandName)
                : this(certificateId, commandName)
            {
                Owner = owner;
            }

        }

        protected void Page_Load(object sender, EventArgs e)
        {

        }
    }
}