/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.
 
 Title:   ProvideAndRegisterDocumentSetRequest.cs
 Purpose: Class representing and building the IHE PnR transaction
 Authors:
    Justin Stauffer     justin@epic.com
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Text;
using System.Xml;
using System.Xml.XPath;
using System.Xml.Serialization;
using System.IO;
using System.Collections;
using System.Collections.Generic;

using Health.Direct.Common.Metadata;
using Health.Direct.Xd.Common.ebXml;
using Health.Direct.Xd.Common;


namespace Health.Direct.Xdr
{
	/// <summary>
	/// This class represents an IHE Cross Enterprise Document Sharing (XDS) Document Submission.
	/// </summary>
	[System.ServiceModel.MessageContract]
	[System.Runtime.Serialization.DataContract]
	[XmlRoot("ProvideAndRegisterDocumentSetRequest", Namespace = GlobalValues.IHEXDSbNamespace)]
	public class ProvideAndRegisterDocumentSetRequest
	{
        private const string XD_PANDR_DEFAULT_DOCUMENTID = "Document";
        private const string XD_PANDR_DEFAULT_SUBMISSIONSETID = "theSubmissionSet";

		private SubmitObjectsRequest _submitObjectsRequest;
		private List<Document> _documents;

		/// <summary>
        /// Creates a new ProvideAndRegisterDocumentSetRequest instance for a <see cref="DocumentPackage"/>
		/// </summary>
		/// <param name="package">The list of content entities to submit</param>
		/// <param name="submissionSetId">A specific Submission Set ID</param>
		public ProvideAndRegisterDocumentSetRequest(DocumentPackage package, string submissionSetId)
		{
            // Build the meta-data for the transaction.
            BuildMetaData(submissionSetId, package);
		}

        public ProvideAndRegisterDocumentSetRequest(DocumentPackage package)
        {
            //Default Submission Set Id
            string submissionSetId = XD_PANDR_DEFAULT_SUBMISSIONSETID;

            // Build the meta-data for the transaction.
            BuildMetaData(submissionSetId, package);
        }

		public ProvideAndRegisterDocumentSetRequest()
		{
			// for XML serialization
		}


		#region Properties

		[System.ServiceModel.MessageBodyMember(Name="SubmitObjectsRequest", Namespace=GlobalValues.ebXmlLCMNamespace, Order=0)]
		[XmlElement("SubmitObjectsRequest", Namespace=GlobalValues.ebXmlLCMNamespace)]
		public SubmitObjectsRequest SubmitObjectsRequest
		{
			get { return _submitObjectsRequest; }
			set { _submitObjectsRequest = value; }
		}

		[System.ServiceModel.MessageBodyMember(Name = "Document", Namespace = GlobalValues.IHEXDSbNamespace, Order=1)]
		[XmlElement("Document", Namespace=GlobalValues.IHEXDSbNamespace)]
		public List<Document> Documents
		{
		    get { return _documents; }
		    set { _documents = value; }
		}



		#endregion Properties


		#region Methods

		/// <summary>
		/// Builds the serializable Document Set Submission Request.
		/// </summary>
		/// <returns>Nothing</returns>
		public void BuildMetaData(string submissionSetId, DocumentPackage package)
		{
            // Add the set of document to the list of documents in this set.
            _documents = new List<Document>(package.Documents.Count);
            foreach (DocumentMetadata doc in package.Documents)
            {

                // Add each document to the list of documents in this set.
                _documents.Add(new Document(XD_PANDR_DEFAULT_DOCUMENTID + (package.Documents.IndexOf(doc) + 1), doc.DocumentBytes));
            }

			_submitObjectsRequest = new SubmitObjectsRequest();
			//Add ExtrinsicObject for each document
            foreach (DocumentMetadata doc in package.Documents)
            {
                _submitObjectsRequest.RegistryObjectList.ExtrinsicObjects.Add(StaticHelpers.CreateDocumentEntry(doc, XD_PANDR_DEFAULT_DOCUMENTID + (package.Documents.IndexOf(doc) + 1)));
            }


			//Add SubmissionSet Registry Package
			StaticHelpers.AddSubmissionSet(_submitObjectsRequest, package, submissionSetId);
			_submitObjectsRequest.RegistryObjectList.Classifications.Add(
				new ClassificationType(null, XD_PANDR_DEFAULT_SUBMISSIONSETID,
				GlobalValues.XDSSubmissionSetUUID, null, "class01", null, null));

            SlotType[] slots = null;
            string[] slotVals = null;
			// Associate Documens and Submission Set
            foreach (Document doc in _documents)
            {
                slots = new SlotType[1];
                slotVals = new string[1];
                slotVals[0] = "Original";
                slots[0] = new SlotType(SlotNameType.SubmissionSetStatus, slotVals);

                _submitObjectsRequest.RegistryObjectList.Associations.Add(new AssociationType(AssociationKind.HasMember,
                XD_PANDR_DEFAULT_SUBMISSIONSETID, doc.Id , "assoc" + doc.Id , slots));

            }
			/*

			try
			{
				XmlSerializer ser = new XmlSerializer(typeof(SubmitObjectsRequest));
				MemoryStream ms = new MemoryStream();
				ser.Serialize(ms, _submitObjectsRequest);
				ms.Position = 0;
				StreamReader requestReader = new StreamReader(ms);
				string requestString = requestReader.ReadToEnd();
				ms.Close();

				// This removes <?xml version="1.0"?> from the top of the requestString
				if (requestString.StartsWith("<?")) requestString = requestString.Substring(requestString.IndexOf("?>") + 2).Trim();

				this.XmlMessage.LoadXml(requestString);
			}
			catch (Exception ex)
			{
				Console.WriteLine("Exception caught while serializing XML: " + ex.Message);
			}
             */
		}

       

        ///// <summary>
        ///// creates a folder to the Submission Object Request
        /////   Classifications
        /////     (R) codeList
        /////   ExternalIdentifiers
        /////     (R) patientId
        /////     (R) uniqueId
        ///// 
        ///// NOTE: Folders are not yet supported.
        ///// </summary>
        ///// <param name="sor">SubmitObjectsRequest corresponding to the new document submission</param>
        //protected void AddFolder(SubmitObjectsRequest sor)
        //{
        //    sor.AddObjectRef("urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5");
        //    sor.AddObjectRef("urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a");
        //    sor.AddObjectRef("urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a");

        //    string[] values;
        //    SlotType[] slots;
        //    //codeList
        //    ClassificationType[] classifications = new ClassificationType[1];
        //    slots = new SlotType[1];
        //    values = new string[1];
        //    values[0] = "Connect-a-thon codeList";
        //    slots[0] = new SlotType(SlotNameType.codingScheme, values);
        //    classifications[0] = CreateClassification("urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5", "SubmissionSet", null, "Multidisciplinary", "Multidisciplinary", slots);

        //    string[] eiScheme = new string[2];
        //    string[] eiValue = new string[2];
        //    string[] eiName = new string[2];
        //    eiScheme[0] = "urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a";
        //    eiScheme[1] = "urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a";
        //    eiValue[0] = _patID;
        //    eiValue[1] = "1.3.6.1.4.1.21367.2005.3.11.15" + "^" + GetPatID(_patID);
        //    eiName[0] = "XDSFolder.patientId";
        //    eiName[1] = "XDSFolder.uniqueId";

        //    sor.AddRegistryPackage("Folder", "FOLDER", eiScheme, eiValue, eiName, null, null);
        //}



		#endregion Methods


	}
}
