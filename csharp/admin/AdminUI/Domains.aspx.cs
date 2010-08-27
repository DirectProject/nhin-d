
using System;

namespace AdminUI
{
    public partial class DomainsPage : WebFormsMvp.Web.MvpPage
    {

        protected override void OnLoad(System.EventArgs e)
        {
            base.OnLoad(e);
            DomainListControl1.Command += DomainListControl1_Command;
            DomainDetailsControl1.DomainCancelled += DomainDetailsControl1_DomainCancelled;
            DomainDetailsControl1.DomainSaved += DomainDetailsControl1_DomainCancelled;
        }

        void DomainDetailsControl1_DomainCancelled(object sender, System.EventArgs e)
        {
            DomainsMultiView.SetActiveView(MasterView);
        }

        void DomainListControl1_Command(object sender, Logic.Views.DomainListControl.DomainListEventArgs e)
        {
            switch (e.CommandName)
            {
                case "Details":
                    // Tell DomainDetails what domain to display
                    DomainDetailsControl1.DomainId = e.DomainId;
                    DomainDetailsControl1.DomainName = e.DomainName;

                    DomainsMultiView.SetActiveView(DetailsView);
                    break;

                case "New":
                    DomainsMultiView.SetActiveView(DetailsView);
                    break;

                case "Certificates":
                    Response.Redirect(string.Format("~/Certificates.aspx?Owner={0}&DomainId={1}", e.DomainName,e.DomainId));
                    break;
               
                case "Addresses":
                    Response.Redirect(string.Format("~/Addresses.aspx?Owner={0}&DomainId={1}", e.DomainName, e.DomainId));
                    break;

                case "Anchors":
                    Response.Redirect(string.Format("~/Anchors.aspx?Owner={0}&DomainId={1}", e.DomainName, e.DomainId));
                    break;

                default:
                    break;
            }
        }
    }
}
