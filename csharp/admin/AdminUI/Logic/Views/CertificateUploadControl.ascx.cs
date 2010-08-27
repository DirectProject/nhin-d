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
using System.IO;

namespace AdminUI.Logic.Views
{
    public partial class CertificateUploadControl : System.Web.UI.UserControl
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            CertificateUploaded += delegate(object s, CertificateUploadControlEventArgs ea) { };
            CertificateUploadCancelled += delegate(object o, EventArgs ea) { };
        }

        public event EventHandler<CertificateUploadControlEventArgs> CertificateUploaded;
        public event EventHandler CertificateUploadCancelled;


        protected void UploadButton_Click(object sender, EventArgs e)
        {
            if (CertificateUpload.HasFile)
            {
                string ext = Path.GetExtension(CertificateUpload.FileName);

                switch (ext.ToLower())
                {
                    default:
                        CertificateUploaded(this, new CertificateUploadControlEventArgs(CertificateUpload.FileBytes, CertificateUpload.FileName, string.Empty));
                        break;

                    case ".pfx":
                        // Save the file in the session object 
                        Session["CertificateUploadControl.CertificateFile"] = CertificateUpload.FileBytes;
                        Session["CertificateUploadControl.CertificateFileName"] = CertificateUpload.FileName;
                        CertificateUploadMultiView.SetActiveView(CertificatePasswordView);
                        break;
                }
            }
        }



        protected void Cancel_Click(object sender, EventArgs e)
        {
            CertificateUploadMultiView.SetActiveView(CertificateUploadView);
            CertificatePasswordTextBox.Text = String.Empty;
            CertificateUploadCancelled(this, null);
        }

        public class CertificateUploadControlEventArgs : EventArgs
        {
            public byte[] CertificateBytes { get; private set; }
            public string FileName { get; private set; }
            public string Password { get; private set; }

            public CertificateUploadControlEventArgs(byte[] certificateBytes, string fileName, string password)
            {
                CertificateBytes = certificateBytes;
                FileName = fileName;
                Password = password;
            }
        }

        protected void Process_Click(object sender, EventArgs e)
        {
            // Get the Certificate File out of session and submit it
            byte[] data = Session["CertificateUploadControl.CertificateFile"] as byte[];
            string fileName = Session["CertificateUploadControl.CertificateFileName"].ToString();

            Session["CertificateUploadControl.CertificateFile"] = null;
            Session["CertificateUploadControl.CertificateFileName"] = null;
            if (data.Length > 0)
            {
                CertificatePasswordTextBox.Text = String.Empty;
                CertificateUploadMultiView.SetActiveView(CertificateUploadView);
                CertificateUploaded(this, new CertificateUploadControlEventArgs(data,fileName, CertificatePasswordTextBox.Text));
            }
        }
    }
}