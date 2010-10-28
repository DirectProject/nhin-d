using System;
using System.Collections.Generic;
using System.Web.UI;

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.AdminUI.Logic.Views
{
    public partial class DomainListControl : UserControl
    {
        private DomainManagerClient _domainManagerClient = new DomainManagerClient();
        private IEnumerable<Domain> _model;
        
        
        public const int MAXRESULTSPERPAGE = 20;
        /// <summary>
        /// Subscribe to this event 
        /// </summary>
        public event EventHandler<DomainListEventArgs> Command;

        protected override void OnLoad(System.EventArgs e)
        {
            base.OnLoad(e);
            _model = _domainManagerClient.EnumerateDomains(MAXRESULTSPERPAGE);

        }

        protected override void OnPreRender(System.EventArgs e)
        {
            base.OnPreRender(e);
            DataBindControls();
        }

        private void DataBindControls()
        {
            this.DomainGridView.DataSource = _model;
            this.DomainGridView.DataBind();
        }

        protected void DomainGridView_RowCommand(object sender, System.Web.UI.WebControls.GridViewCommandEventArgs e)
        {
            int domaindId = WebHelper.GetDataKeyFromGridView( sender, e.CommandArgument, "Id");
            string domainName = WebHelper.GetDataKeyAsObjectFromGridView(sender, e.CommandArgument, "Name").ToString();

            
            if (this.Command != null)
            {
                Command(this, new DomainListEventArgs(domaindId, domainName, e.CommandName));
            }

        }
        public class DomainListEventArgs : EventArgs
        {
            public int DomainId { get; private set; }
            public string CommandName { get; private set; }
            public string DomainName { get; set; }

            public DomainListEventArgs(int domainId, string commandName)
            {
                DomainId = domainId;
                CommandName = commandName;
            }
            public DomainListEventArgs(int domainId, string domainName, string commandName)
                : this(domainId, commandName)
            {
                DomainName = domainName;
            }

        }


    }
}