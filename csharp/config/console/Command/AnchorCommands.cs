/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using System.ServiceModel;

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

using NHINDirect.Certificates;
using NHINDirect.Tools.Command;
using NHINDirect.Extensions;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage Anchors
    /// </summary>
    public class AnchorCommands : CommandsBase
    {
        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------

        /// <summary>
        /// Import and add an anchor
        /// </summary>
        public void Command_Anchor_Add(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string filePath = args.GetRequiredValue(1);
            string password = args.GetOptionalValue(2, string.Empty);
            
            PushCerts(owner, CertificateCommands.LoadCerts(filePath, password), false);
        }
        public void Usage_Anchor_Add()
        {
            System.Console.WriteLine("Import an anchor certificate from a file and push it into the store.");
            System.Console.WriteLine("The anchor is used for both incoming & outgoing trust.");
            System.Console.WriteLine("    owner filepath [password]");
            System.Console.WriteLine("\t owner: Anchor owner");
            System.Console.WriteLine("\t filePath: path fo the certificate file. Can be .DER, .CER or .PFX");
            System.Console.WriteLine("\t password: (optional) file password");
        }
        
        /// <summary>
        /// Retrieve an anchor by its ID
        /// </summary>
        public void Command_Anchor_ByID_Get(string[] args)
        {
            long anchorID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = CertificateCommands.GetOptions(args, 1);

            Anchor[] anchors = ConfigConsole.Current.AnchorClient.GetAnchors(new long[] { anchorID }, options);
            this.Print(anchors);
        }
        public void Usage_Anchor_ByID_Get()
        {
            System.Console.WriteLine("Get an anchor by its id.");
            System.Console.WriteLine("    anchorID [options]");
            CertificateCommands.PrintOptionsUsage();
        }
        
        /// <summary>
        /// Get all anchors for an owner
        /// </summary>
        public void Command_Anchors_Get(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateGetOptions options = CertificateCommands.GetOptions(args,1);
     
            Anchor[] anchors = ConfigConsole.Current.AnchorClient.GetAnchorsForOwner(owner, options);
            this.Print(anchors);
        }
        public void Usage_Anchors_Get()
        {
            System.Console.WriteLine("Get all anchors for an owner.");
            System.Console.WriteLine("  owner [options]");
            System.Console.WriteLine("\t owner: Anchor owner");
            CertificateCommands.PrintOptionsUsage();
        }
        
        /// <summary>
        /// List ALL anchors
        /// </summary>
        public void Command_Anchors_List(string[] args)
        {
            CertificateGetOptions options = CertificateCommands.GetOptions(args, 0);
            //
            // TODO: Give the ability to "more" through this list
            //
            foreach(Anchor anchor in ConfigConsole.Current.AnchorClient.EnumerateAnchors(10, options))
            {
                this.Print(anchor);
                CommandUI.PrintSectionBreak();
            }
        }
        public void Usage_Anchors_List()
        {
            System.Console.WriteLine("List all anchors");
            CertificateCommands.PrintOptionsUsage();
        }
        
        /// <summary>
        /// Set the status of all anchors for an owner
        /// </summary>
        public void Command_Anchor_Status_Set(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            ConfigConsole.Current.AnchorClient.SetAnchorStatusForOwner(owner, status);
        }
        public void Usage_Anchor_Status_Set()
        {
            System.Console.WriteLine("Set the status for ALL anchors for an owner.");
            System.Console.WriteLine("    owner");
            System.Console.WriteLine("\t owner: Anchor owner");
            System.Console.WriteLine("\t status: {0}", EntityStatusString);
        }
        
        /// <summary>
        /// Remove an anchor
        /// </summary>
        public void Command_Anchor_Remove(string[] args)
        {
            long anchorID = args.GetRequiredValue<long>(0);
            ConfigConsole.Current.AnchorClient.RemoveAnchor(anchorID);
        }
        
        public void Usage_Anchor_Remove()
        {
            System.Console.WriteLine("Remove anchors with given ID");
            System.Console.WriteLine("    anchorID");
        }
        
        /// <summary>
        /// Mirrors what the production gateway would do
        /// </summary>
        public void Command_Anchor_Resolve(string[] args)
        {
            MailAddress owner = new MailAddress(args.GetRequiredValue(0));
            CertificateGetOptions options = CertificateCommands.GetOptions(args, 1);

            Anchor[] anchors = ConfigConsole.Current.AnchorClient.GetAnchorsForOwner(owner.Address, options);
            if (ArrayExtensions.IsNullOrEmpty(anchors))
            {
                anchors = ConfigConsole.Current.AnchorClient.GetAnchorsForOwner(owner.Host, options);
            }
            this.Print(anchors);
        }
        public void Usage_Anchor_Resolve()
        {
            System.Console.WriteLine("Resolves anchors for an owner - like the Smtp Gateway would.");
            System.Console.WriteLine("    owner [options]");
            System.Console.WriteLine("\t owner: Anchor owner");
            CertificateCommands.PrintOptionsUsage();
        }

        //---------------------------------------
        //
        // Implementation details
        //
        //---------------------------------------               
        internal static void PushCerts(string owner, IEnumerable<X509Certificate2> certs, bool checkForDupes)
        {
            AnchorStoreClient client = ConfigConsole.Current.AnchorClient;
            foreach (X509Certificate2 cert in certs)
            {
                try
                {
                    if (!checkForDupes || !client.Contains(owner, cert))
                    {
                        client.AddAnchor(new Anchor(owner, cert, true, true));
                        System.Console.WriteLine("Added {0}", cert.Subject);
                    }
                    else
                    {
                        System.Console.WriteLine("Exists {0}", cert.ExtractEmailNameOrName());
                    }
                }
                catch(FaultException<ConfigStoreFault> ex)
                {
                    if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                    {
                        System.Console.WriteLine("Exists {0}", cert.Subject);
                    }
                    else
                    {
                        throw;
                    }                    
                }
            }
        }

        internal static void PushCerts(string owner, IEnumerable<X509Certificate2> certs, bool checkForDupes, EntityStatus status)
        {
            PushCerts(owner, certs, checkForDupes);
            ConfigConsole.Current.AnchorClient.SetAnchorStatusForOwner(owner, EntityStatus.Enabled);
        }
        
        void Print(Anchor[] anchors)
        {
            if (anchors == null || anchors.Length == 0)
            {
                System.Console.WriteLine("No certificates found");
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
            
            if (cert.HasData)
            {
                X509Certificate2 x509 = cert.ToX509Certificate();
                CertificateCommands.Print(x509);
            }
        }
    }
}