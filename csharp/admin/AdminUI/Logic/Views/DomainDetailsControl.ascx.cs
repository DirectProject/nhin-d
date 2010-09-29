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
using NHINDirect.Config.Client.DomainManager;
using NHINDirect.Config.Store;

namespace AdminUI.Logic.Views
{
    public partial class DomainDetailsControl : System.Web.UI.UserControl
    {
        public int DomainId
        {
            get
            {
                int returnValue = -1;
                if (ViewState["DomainId"] != null)
                {

                    int.TryParse(ViewState["DomainId"].ToString(), out returnValue);

                }
                return returnValue;
            }
            set { ViewState["DomainId"] = value; }
        }
        public string DomainName
        {
            get
            {
                if (ViewState["DomainName"] != null)
                {
                    return ViewState["DomainName"] as string;
                }
                else
                {
                    return String.Empty;
                }
            }
            set { ViewState["DomainName"] = value; }
        }

        public event EventHandler DomainCancelled;
        public event EventHandler DomainSaved;

        private DomainManagerClient _domainManagerClient = new DomainManagerClient();
        private Domain _model;

        protected void Page_Load(object sender, EventArgs e)
        {
            DomainCancelled += delegate(object s, EventArgs ea) { };
            DomainSaved += delegate(object s, EventArgs ea) { };
        }
        protected override void OnPreRender(EventArgs e)
        {
            base.OnPreRender(e);

            if (!string.IsNullOrEmpty(DomainName))
            {
                // Load Domain
                _model = _domainManagerClient.GetDomain(DomainName);
                DataBindControls();
            }

        }

        private void DataBindControls()
        {
            this.DomainNameLabel.Text = _model.Name;
            this.CreateDateLabel.Text = WebHelper.FormatDateTime(_model.CreateDate);
            this.UpdateDateLabel.Text = WebHelper.FormatDateTime(_model.UpdateDate);
            this.StatusDropDownList.SelectedValue = ((int)_model.Status).ToString();
        }


        protected void Cancel_Click(object sender, EventArgs e)
        {
            DomainCancelled(sender, e);
        }

        protected void Save_Click(object sender, EventArgs e)
        {
            SaveDomain();
        }

        private void SaveDomain()
        {
            Domain d;
            try
            {
                d = _domainManagerClient.GetDomain(DomainName);
            }

            catch (System.ServiceModel.FaultException<ConfigStoreFault> ex)
            {
                // The domain was not found
                d = null;
            }
          
            if (d != null)
            {
                //TODO: Great candidate for an extension method
                d.Status = (EntityStatus)Enum.ToObject(typeof(EntityStatus), int.Parse(StatusDropDownList.SelectedValue));
            }
            _domainManagerClient.UpdateDomain(d);
            
            DomainSaved(this, null);
        }
    }
}