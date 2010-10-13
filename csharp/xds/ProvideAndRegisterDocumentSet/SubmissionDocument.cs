using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.XDS
{
    /// <summary>
    /// SubmissionDocument is a helper class for a document that is to be submitted
    /// </summary>
    class SubmissionDocument
    {
        private string m_documentID;

        public string documentID
        {
            get { return m_documentID; }
            set { m_documentID = value; }
        }

        private byte[] m_documentText;

        public byte[] documentText
        {
            get { return m_documentText; }
            set { m_documentText = value; }
        }
    }
}
