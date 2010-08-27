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
using NHINDirect.Config.Client.CertificateService;

namespace AdminUI
{
    public partial class Certificates : System.Web.UI.Page
    {
        private NHINDirect.Config.Client.CertificateService.CertificateStoreClient _certStoreClient;

        protected void Page_Load(object sender, EventArgs e)
        {
            CertificateListControl1.Command += new EventHandler<AdminUI.Logic.Views.CertificateListControl.CertificateListControlEventArgs>(CertificateListControl1_Command);
            CertificateDetailsControl1.CertificateCancelled += new EventHandler(CertificateDetailsControl1_CertificateCancelled);
            CertificateDetailsControl1.CertificateSaved += new EventHandler(CertificateDetailsControl1_CertificateSaved);
           // Provide context for user control
            CertificateListControl1.Owner = Request.QueryString["Owner"];
        }

        void CertificateListControl1_Command(object sender, AdminUI.Logic.Views.CertificateListControl.CertificateListControlEventArgs e)
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
