using System;
using NHINDirect.Config.Client.DomainManager;
using NHINDirect.Config.Store;

namespace AdminUI.Logic.Views
{
    public partial class NewDomainControl : System.Web.UI.UserControl
    {
        // TODO: Validate domain name
        public event EventHandler DomainSaved;

        private DomainManagerClient _domainManagerClient = new DomainManagerClient();
        private Domain _model;
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        protected void Add_Click(object sender, EventArgs e)
        {
           
            SaveDomain();
        }
       
        private void SaveDomain()
        {
            Domain d = new Domain();
            bool domainFound = true;
            try
            {
                d = _domainManagerClient.GetDomain(this.DomainNameTextBox.Text);
            }

            catch (System.ServiceModel.FaultException<ConfigStoreFault> ex)
            {
                // The domain was not found
                domainFound = false;
            }

            if ( !domainFound)
            {
                //TODO: Great candidate for an extension method
                d.Name = this.DomainNameTextBox.Text;
                d.Status = (EntityStatus)Enum.ToObject(typeof(EntityStatus), int.Parse(StatusDropDownList.SelectedValue));
                _domainManagerClient.UpdateDomain(d);
                DomainSaved(this, null);
            }
            else
            {
                ErrorLiteral.Text = "This domain already exists.";
            }
           

           
        }
    }
}