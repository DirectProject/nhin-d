using System;

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.AdminUI.Logic.Views
{
    public partial class NewDomainControl : System.Web.UI.UserControl
    {
        // TODO: Validate domain name
        public event EventHandler DomainSaved;

        private DomainManagerClient m_domainManagerClient = new DomainManagerClient();
        private Domain m_model;

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            DomainSaved += delegate(object o, EventArgs eventArgs) { };
        }
        
        protected void Add_Click(object sender, EventArgs e)
        {

            SaveDomain();
        }

        private void SaveDomain()
        {
            Domain d = null;
            d = m_domainManagerClient.GetDomain(this.DomainNameTextBox.Text);

            if (d == null)
            {
                d = new Domain(this.DomainNameTextBox.Text);
                m_domainManagerClient.UpdateDomain(d);
                DomainSaved(this, null);
            }

            else if (d.Name.ToLower() == DomainNameTextBox.Text.ToLower())
            {
                ErrorLiteral.Text = "This domain already exists.";
            }



        }
    }
}