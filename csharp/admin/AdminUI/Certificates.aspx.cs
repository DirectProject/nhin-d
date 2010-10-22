using System;

using Health.Direct.AdminUI.Logic.Views;

namespace Health.Direct.AdminUI
{
    public partial class Certificates : System.Web.UI.Page
    {
        private Health.Direct.Config.Client.CertificateService.CertificateStoreClient _certStoreClient;

        protected void Page_Load(object sender, EventArgs e)
        {
            CertificateListControl1.Command += new EventHandler<CertificateListControl.CertificateListControlEventArgs>(CertificateListControl1_Command);
            CertificateDetailsControl1.CertificateCancelled += new EventHandler(CertificateDetailsControl1_CertificateCancelled);
            CertificateDetailsControl1.CertificateSaved += new EventHandler(CertificateDetailsControl1_CertificateSaved);
            // Provide context for user control
            CertificateListControl1.Owner = Request.QueryString["Owner"];
        }

        void CertificateListControl1_Command(object sender, CertificateListControl.CertificateListControlEventArgs e)
        {

            switch (e.CommandName)
            {
                case "Details":
                    CertificateDetailsControl1.CertificateId = e.CertificateId;
                    CertificateDetailsControl1.Owner = e.Owner;


                    CertificatesMultiView.SetActiveView(DetailsView);
                    break;

               
            }
        }

        void CertificateDetailsControl1_CertificateSaved(object sender, EventArgs e)
        {
            CertificatesMultiView.SetActiveView(MasterView);
        }

        void CertificateDetailsControl1_CertificateCancelled(object sender, EventArgs e)
        {
            CertificatesMultiView.SetActiveView(MasterView);
        }
    }
}