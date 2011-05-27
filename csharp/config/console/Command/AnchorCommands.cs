/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using System.ServiceModel;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;
using System.IO;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage Anchors
    /// </summary>
    public class AnchorCommands : CommandsBase<AnchorStoreClient>
    {        
        CertificateCommands m_certCommands;
        CertificateGetOptions m_standardGetOptions = new CertificateGetOptions() { IncludeData = true };

        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------

        internal AnchorCommands(ConfigConsole console, Func<AnchorStoreClient> client) : base(console, client)
        {
        }

        internal CertificateCommands CertCommands
        {
            get
            {
                if (m_certCommands == null)
                {
                    m_certCommands = GetCommand<CertificateCommands>();
                }
                
                return m_certCommands;
            }
        }
        
        /// <summary>
        /// Import and add an anchor
        /// </summary>
        [Command(Name = "Anchor_Add", Usage = AnchorAddUsage)]
        public void AnchorAdd(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(1, args);                        
            PushCerts(owner, certFileInfo.LoadCerts(),  false, certFileInfo.Status);
        }

        private const string AnchorAddUsage
            = "Import an anchor certificate from a file and push it into the config store."
              + Constants.CRLF + "The anchor is used for both incoming & outgoing trust."
              + Constants.CRLF + "    owner options"
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Import and add an anchor, if one does not already exist
        /// </summary>
        [Command(Name = "Anchor_Ensure", Usage = AnchorAddUsage)]
        public void AnchorEnsure(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(1, args);
            PushCerts(owner, certFileInfo.LoadCerts(), true, certFileInfo.Status);
        }        
        private const string AnchorEnsureUsage
            = "Import an anchor certificate from a file and push it into the config store - if not already there."
              + Constants.CRLF + "The anchor is used for both incoming & outgoing trust."
              + Constants.CRLF + "    owner options"
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Import and add an anchor
        /// </summary>
        [Command(Name = "Anchor_Add_Ex", Usage = AnchorAddExUsage)]
        public void AnchorAddEx(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            bool forIncoming = args.GetRequiredValue<bool>(1);
            bool forOutgoing = args.GetRequiredValue<bool>(2);
            bool checkDupes = args.GetRequiredValue<bool>(3);
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(4, args);
            PushCerts(owner, certFileInfo.LoadCerts(), false, certFileInfo.Status, forIncoming, forOutgoing);
        }

        private const string AnchorAddExUsage
            = "Import an anchor certificate from a file and push it into the config store."
              + Constants.CRLF + "    owner forIncoming forOutgoing checkForDupes options"
              + Constants.CRLF + " \t forIncoming: (true/false) Use anchor to verify trust of INCOMING messages"
              + Constants.CRLF + " \t forOutgoing: (true/false) Use anchor to verify trust of OUTGOING messages"
              + Constants.CRLF + " \t checkDupes: (true/false) Check if anchor is already installed"
              + Constants.CRLF + CertificateFileInfo.Usage;
        
        
        [Command(Name="Anchor_Add_Machine", Usage=AnchorAddMachineUsage)]
        public void AnchorAddMachine(string[] args)
        {
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(0, args);
            using(SystemX509Store store = SystemX509Store.OpenAnchorEdit())
            {
                store.ImportKeyFile(certFileInfo.FilePath, certFileInfo.Password, X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
            }
        }
        private const string AnchorAddMachineUsage
            = "Import an anchor certificate from a file and push it into the system store."
              + Constants.CRLF + "The anchor is used for both incoming & outgoing trust."
              + Constants.CRLF + "   options"
              + Constants.CRLF + CertificateFileInfo.Usage;
        
        /// <summary>
        /// Retrieve an anchor by its ID
        /// </summary>
        [Command(Name = "Anchor_ByID_Get", Usage = AnchorByIDGetUsage)]
        public void AnchorByIDGet(string[] args)
        {
            long anchorID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = new CertificateGetOptions() {
                IncludeData = args.GetOptionalValue(1, false), 
                IncludePrivateKey = args.GetOptionalValue(2, false)
            };

            string outputFile = args.GetOptionalValue(3, string.Empty); 

            Anchor[] anchors = Client.GetAnchors(new[] { anchorID }, options);
            this.Print(anchors);

            if (anchors.Length < 1)
            {
                return; 
            }

            Anchor anchor = anchors[0]; 
            if (!string.IsNullOrEmpty(outputFile) && anchor.HasData)
            {   
                File.WriteAllBytes(outputFile, anchor.Data);
            }
        }

        private const string AnchorByIDGetUsage
            = "Get an anchor by its id."
              + Constants.CRLF + "    anchorID [certData] [privatekey] [outputFile]"
              + Constants.CRLF + "\t anchorID: the anchor ID to get."
              + Constants.CRLF + "\t certData: (True/False) Fetch certificate data"
              + Constants.CRLF + "\t privateKey: (True/False) Include private key"
              + Constants.CRLF + "\t outputFile: (optional) The output filename for the certificate.";         
        
        /// <summary>
        /// Get all anchors for an owner
        /// </summary>
        [Command(Name = "Anchors_Get", Usage = AnchorsGetUsage)]
        public void AnchorsGet(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateGetOptions options = this.CertCommands.GetOptions(args, 1);
     
            Anchor[] anchors = Client.GetAnchorsForOwner(owner, options);
            this.Print(anchors);
        }

        private const string AnchorsGetUsage
            = "Get all anchors for an owner."
              + Constants.CRLF + "  owner [options]"
              + Constants.CRLF + "\t owner: Anchor owner"
              + Constants.CRLF + CertificateCommands.PrintOptionsUsage;
        
        /// <summary>
        /// List ALL anchors
        /// </summary>
        [Command(Name = "Anchors_List", Usage = AnchorsListUsage)]
        public void AnchorsList(string[] args)
        {
            CertificateGetOptions options = this.CertCommands.GetOptions(args, 0);
            foreach(Anchor anchor in Client.EnumerateAnchors(10, options))
            {
                this.Print(anchor);
                CommandUI.PrintSectionBreak();
            }
        }

        private const string AnchorsListUsage
            = "List all anchors"
            + Constants.CRLF + CertificateCommands.PrintOptionsUsage;
        
        /// <summary>
        /// Set the status of all anchors for an owner
        /// </summary>
        [Command(Name = "Anchor_Status_Set", Usage = AnchorStatusSetUsage)]
        public void AnchorStatusSet(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Client.SetAnchorStatusForOwner(owner, status);
        }
        private const string AnchorStatusSetUsage
            = "Set the status for ALL anchors for an owner."
              + Constants.CRLF + "    owner status"
              + Constants.CRLF + "\t owner: Anchor owner"
              + Constants.CRLF + "\t status: " + Constants.EntityStatusString;        
        
        /// <summary>
        /// Remove an anchor
        /// </summary>
        [Command(Name = "Anchor_Remove", Usage = AnchorRemoveUsage)]
        public void AnchorRemove(string[] args)
        {
            long anchorID = args.GetRequiredValue<long>(0);
            Client.RemoveAnchor(anchorID);
        }

        private const string AnchorRemoveUsage
            = "Remove anchors with given ID"
              + Constants.CRLF + "    anchorID";

        /// <summary>
        /// Set the status of all anchors for an owner
        /// </summary>
        [Command(Name = "Anchor_Direction_Set", Usage = AnchorDirectionSetUsage)]
        public void AnchorDirectStatusSet(string[] args)
        {
            long anchorID = args.GetRequiredValue<long>(0);
            bool forIncoming = args.GetRequiredValue<bool>(1);
            bool forOutgoing = args.GetRequiredValue<bool>(2);
            
            Anchor anchor = Client.GetAnchors(new long[] {anchorID}, m_standardGetOptions).FirstOrDefault();
            if (anchor == null)
            {
                WriteLine("Anchor not found");
                return;
            }
            
            Client.RemoveAnchor(anchorID);
            anchor.ForIncoming = forIncoming;
            anchor.ForOutgoing = forOutgoing;
            
            Client.AddAnchor(anchor);
        }
        private const string AnchorDirectionSetUsage
            = "Sets the incoming/outgoing bits on an anchor."
              + Constants.CRLF + "WARNING: Will remove and then re-add (automatically) the anchor with the new settings."
              + Constants.CRLF + "    anchorID forIncoming forOutgoing";
        
        /// <summary>
        /// Mirrors what the production gateway would do
        /// </summary>
        [Command(Name = "Anchor_Resolve", Usage = AnchorResolveUsage)]
        public void AnchorResolve(string[] args)
        {
            MailAddress owner = new MailAddress(args.GetRequiredValue(0));
            CertificateGetOptions options = this.CertCommands.GetOptions(args, 1);
            options.Status = EntityStatus.Enabled;  // We only ever resolve Enabled Anchors

            Anchor[] anchors = Client.GetAnchorsForOwner(owner.Address, options);
            if (anchors.IsNullOrEmpty())
            {
                anchors = Client.GetAnchorsForOwner(owner.Host, options);
            }
            this.Print(anchors);
        }

        private const string AnchorResolveUsage
            = "Resolves anchors for an owner - like the Smtp Gateway would."
              + Constants.CRLF + "    owner [certData] [privateKey]"
              + Constants.CRLF + "\t owner: Anchor owner"
              + Constants.CRLF + "\t certData: (True/False) Fetch certificate data"
              + Constants.CRLF + "\t privateKey: (True/False) Include private key";
              
        /// <summary>
        /// Check if a particular anchor was installed for the given owner
        /// </summary>
        [Command(Name = "Anchor_Search_ByFile", Usage = AnchorSearchFileUsage)]
        public void AnchorSearchByFile(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(1, args);
            MemoryX509Store certs = certFileInfo.LoadCerts();
            
            WriteLine(string.Empty);
            
            this.Search(
                owner,
                x => (certs.FirstOrDefault(y => this.CertCommands.Match(x, y)) != null)
            );
            
        }
        private const string AnchorSearchFileUsage
            = "Search if an owner already has an anchor similar to those in the given file."
              + Constants.CRLF + "    owner options"
              + Constants.CRLF + "\t owner: Anchor owner"
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Check if a particular anchor was installed for the given owner
        /// </summary>
        [Command(Name = "Anchor_Search_ByName", Usage = AnchorSearchName)]
        public void AnchorSearchByName(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string name = args.GetRequiredValue(1);          
            bool exactMatch = args.GetOptionalValue(2, true);
              
            WriteLine(string.Empty);            
            if (exactMatch)
            {
                this.Search(
                    owner,
                    x => this.CertCommands.Match(x, name)
                );
            }
            else
            {
                this.Search(
                    owner,
                    x => this.CertCommands.MatchContains(x, name)
                );
            }
        }
        private const string AnchorSearchName
            = "Search if an owner already has an anchor with the given distinguished name."
              + Constants.CRLF + "    owner name [exactMatch]"
              + Constants.CRLF + "\t owner: Anchor owner"
              + Constants.CRLF + "\t name: distinguished name to look for"
              + Constants.CRLF + "\t exactMatch: (Optional boolean) Do a contains search. Default: true";


        //---------------------------------------
        //
        // Implementation details
        //
        //---------------------------------------               
        internal void PushCerts(string owner, IEnumerable<X509Certificate2> certs, bool checkForDupes)
        {
            this.PushCerts(owner, certs, checkForDupes, null);
        }
        
        internal void PushCerts(string owner, IEnumerable<X509Certificate2> certs, bool checkForDupes, EntityStatus? status)
        {
            this.PushCerts(owner, certs, checkForDupes, status, true, true);
        }
        
        internal void PushCerts(string owner, IEnumerable<X509Certificate2> certs, bool checkForDupes, EntityStatus? status, bool incoming, bool outgoing)
        {
            foreach (X509Certificate2 cert in certs)
            {
                try
                {
                    if (!checkForDupes || !Client.Contains(owner, cert))
                    {
                        Anchor anchor = new Anchor(owner, cert, incoming, outgoing);
                        if (status != null)
                        {
                            anchor.Status = status.Value;
                        }
                        Client.AddAnchor(anchor);
                        WriteLine("Added {0}", cert.Subject);
                    }
                    else
                    {
                        WriteLine("Exists {0}", cert.ExtractEmailNameOrName());
                    }
                }
                catch(FaultException<ConfigStoreFault> ex)
                {
                    if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                    {
                        WriteLine("Exists {0}", cert.Subject);
                    }
                    else
                    {
                        throw;
                    }                    
                }
            }
        }
        
        void Print(Anchor[] anchors)
        {
            if (anchors == null || anchors.Length == 0)
            {
                WriteLine("No certificates found");
                return;
            }
            foreach (Anchor cert in anchors)
            {
                this.Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }

        void Print(Anchor cert)
        {
            CommandUI.Print("Owner", cert.Owner);
            CommandUI.Print("Thumbprint", cert.Thumbprint);
            CommandUI.Print("ID", cert.ID);
            CommandUI.Print("CreateDate", cert.CreateDate);
            CommandUI.Print("ValidStart", cert.ValidStartDate);
            CommandUI.Print("ValidEnd", cert.ValidEndDate);
            CommandUI.Print("ForIncoming", cert.ForIncoming);
            CommandUI.Print("ForOutgoing", cert.ForOutgoing);
            CommandUI.Print("Status", cert.Status);
            
            if (cert.HasData)
            {
                X509Certificate2 x509 = cert.ToX509Certificate();
                this.CertCommands.Print(x509);
            }
        }        
        
        void Search(string owner, Func<X509Certificate2, bool> filter)
        {
            Search(Client.GetAnchorsForOwner(owner, m_standardGetOptions), filter);
        }
        
        void Search(IEnumerable<Anchor> query, Func<X509Certificate2, bool> filter)
        {
            int matchCount = 0;
            var matches = from anchor in query
                where filter(anchor.ToX509Certificate())
                select anchor;

            foreach (Anchor match in matches)
            {
                ++matchCount;
                WriteLine(string.Empty);
                Print(match);
                CommandUI.PrintSectionBreak();
            }

            if (matchCount == 0)
            {
                WriteLine("No matches");
            }
        }        
    }
}