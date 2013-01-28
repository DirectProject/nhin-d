/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce te above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    public static class BlueButton
    {
        public enum BundleType
        {
            Provider = 0,
            Patient = 1,
            ProviderTest = 2,
            PatientTest = 3
        }
        
        public const string ProviderBundle = "https://secure.bluebuttontrust.org/p7b.ashx?id=ba942b18-ad48-e211-8bc3-78e3b5114607&name=Provider";
        public const string PatientBundle = "https://secure.bluebuttontrust.org/p7b.ashx?id=d7a59811-ad48-e211-8bc3-78e3b5114607&name=Patient";
        public const string ProviderTestBundle = "https://secure.bluebuttontrust.org/p7b.ashx?id=cb300117-3a4a-e211-8bc3-78e3b5114607&name=ProviderTest";
        public const string PatientTestBundle = "https://secure.bluebuttontrust.org/p7b.ashx?id=4d9daaf9-384a-e211-8bc3-78e3b5114607&name=PatientTest";
        
        public static string UrlForBundleType(BundleType type)
        {
            switch(type)
            {
                default:
                    break;
               
               case BundleType.Patient:
                    return BlueButton.PatientBundle;
                
                case BundleType.PatientTest:
                    return BlueButton.PatientTestBundle;
                
                case BundleType.Provider:
                    return BlueButton.ProviderBundle;
                
                case BundleType.ProviderTest:
                    return BlueButton.ProviderTestBundle;
            }
            
            return null;
        }
    }
        
    /// <summary>
    /// Commands to manage anchor bundle configurations
    /// </summary>
    public class BundleCommands : CommandsBase<BundleStoreClient>
    {
        internal BundleCommands(ConfigConsole console, Func<BundleStoreClient> client) : base(console, client)
        {
        }
        
        Bundle ParseBundleDefinition(string[] args)
        {
            return new Bundle()
            {
                Owner = args.GetRequiredValue<string>(0),
                Url = args.GetRequiredUri(1).AbsoluteUri,
                ForIncoming = args.GetRequiredValue<bool>(2),
                ForOutgoing = args.GetRequiredValue<bool>(3),
                Status = args.GetOptionalEnum<EntityStatus>(4, EntityStatus.New)
            };
        } 
        
        /// <summary>
        /// Add a new bundle
        /// </summary>
        [Command(Name = "Bundle_Add", Usage = BundleAddUsage)]
        public void BundleAdd(string[] args)
        {
            Bundle bundle = this.ParseBundleDefinition(args);
            Bundle existingBundle = Client.GetExistingBundle(bundle.Owner, bundle.Url);
            if (existingBundle != null)
            {
                WriteLine("Bundle exists");
                return;
            }
            
            Client.AddBundle(bundle);
            WriteLine("Added {0}, {1}", bundle.Owner, bundle.Url);
        }

        private const string BundleAddUsage =
            "Add a new bundle definition into the config store - if it doesn't already exist.\n" +
            "owner url forIncoming forOutgoing [status]\n\n" +
            "\t owner: domain or address\n" +
            "\t url: url for bundle\n" +
            "\t forIncoming: (true/false) Use bundle to verify trust of INCOMING messages\n" +
            "\t forOutgoing: (true/false) use bundle to verify trust of OUTGOING messages\n" +
            "\t status: (new/enabled/disabled, default new) status field\n" +
            "\n";

        /// <summary>
        /// set the status field on a bundle
        /// </summary>
        [Command(Name = "Bundle_SetStatus", Usage = BundleSetStatusUsage)]
        public void BundleSetStatus(string[] args)
        {
            long bundleId = args.GetRequiredValue<long>(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            Client.SetBundleStatus(new long[] { bundleId }, status);
            WriteLine("ok");
        }

        private const string BundleSetStatusUsage =
            "Set the status field on an existing bundle.\n\n" +
            "id status\n" +
            "\t id: bundle id\n" +
            "\t status: (new/enabled/disabled) status field\n" +
            "\n";

        /// <summary>
        /// remove a bundle from the config store
        /// </summary>
        [Command(Name = "Bundle_Remove", Usage = BundleRemoveUsage)]
        public void BundleRemove(string[] args)
        {
            long bundleId = args.GetRequiredValue<long>(0);
            Client.RemoveBundles(new long[] { bundleId });
            WriteLine("ok");
        }

        private const string BundleRemoveUsage =
            "Remove an existing bundle from the store.\n" +
            "id\n" +
            "\t id: bundle id\n" +
            "\n";

        /// <summary>
        /// Fetch all bundles for an address or domain
        /// </summary>
        [Command(Name = "Bundles_Get", Usage = BundlesGetUsage)]
        public void BundlesGet(string[] args)
        {
            string owner = args.GetRequiredValue<string>(0);
            Bundle[] bundles = Client.GetBundlesForOwner(owner);
            this.PrintTable(bundles);
        }

        private const string BundlesGetUsage =
            "Return all bunders for owner (address or domain).\n\n" +
            "owner\n" +
            "\t owner: domain or address\n" +
            "\n";

        /// <summary>
        /// List ALL anchors
        /// </summary>
        [Command(Name = "Bundles_List", Usage = BundlesListUsage)]
        public void BundlesList(string[] args)
        {
            this.PrintBundleTableHeader();
            foreach (Bundle bundle in Client.EnumerateBundles(10))
            {
                this.PrintBundleTabular(bundle);
            }
        }

        private const string BundlesListUsage
            = "List all configured bundles";
        
        [Command(Name = "Bundle_Add_BlueButton", Usage = BundleAddBlueButtonUsage)]
        public void BundleAddBlueButton(string[] args)
        {
            BlueButton.BundleType bundleType = args.GetRequiredEnum<BlueButton.BundleType>(1);
            string bundleUrl = BlueButton.UrlForBundleType(bundleType);
            args[1] = bundleUrl;
            
            this.BundleAdd(args);
        }

        private const string BundleAddBlueButtonUsage =
            "Ensure that blue button bundles are registered for the given owner.\n\n" +
            "owner bundleType forIncoming forOutgoing [status]\n" +
            "\t owner: domain or address\n" +
            "\t bundleType: Provider | Patient | ProviderTest | PatientTest \n" +
            "\t forIncoming: (true/false) Use bundle to verify trust of INCOMING messages\n" +
            "\t forOutgoing: (true/false) use bundle to verify trust of OUTGOING messages\n" +
            "\t status: (new/enabled/disabled, default new) status field\n" +
            "\n";

        
        //----------------------------------------------
        //
        // Bundle Download & Verification support
        //
        //----------------------------------------------
        /// <summary>
        /// Download all configured bundles for a given owner into a Folder
        /// </summary>
        [Command(Name = "Bundles_Download", Usage = BundleDownloadUsage)]
        public void BundlesDownload(string[] args)
        {
            string owner = args.GetRequiredValue<string>(0);
            string folderpath = args.GetRequiredValue<string>(1);
            if (!Directory.Exists(folderpath))
            {
                WriteLine("Folder not found");
                return;
            }

            AnchorBundleDownloader bundleDownloader = new AnchorBundleDownloader();
            bundleDownloader.TimeoutMS = 30 * 1000;
            bundleDownloader.MaxRetries = 1;
            bundleDownloader.VerifySSL = false;
            
            Bundle[] bundles = Client.GetBundlesForOwner(owner);
            foreach(Bundle bundle in bundles)
            {
                string fileName = string.Format("Bundle_{0}.p7b", bundle.ID);
                string filePath = Path.Combine(folderpath, fileName);
                try
                {
                    WriteLine("Downloading ID={0}, {1}", bundle.ID, bundle.Url);
                    bundleDownloader.DownloadToFile(bundle.Uri, filePath);
                    WriteLine("ok");
                }
                catch (Exception ex)
                {
                    WriteLine("Error downloading bundle {0} from {1}", bundle.ID, bundle.Url);
                    WriteLine(ex.Message);
                }
            }
        }
        
        private const string BundleDownloadUsage =
            "Download all configured bundles for an owner to a folder.\n\n" +
            "owner [folderPath] [extractCerts]\n" +
            "\t owner: bundle owner\n" +
            "\t folderPath: (optional) Where to save the downloaded bundles\n" +
            "\n";

        [Command(Name = "Bundles_Download_Certs", Usage = BundleDownloadCertsUsage)]
        public void BundlesDownloadCerts(string[] args)
        {
            string owner = args.GetRequiredValue<string>(0);
            string folderpath = args.GetRequiredValue<string>(1);
            if (!Directory.Exists(folderpath))
            {
                WriteLine("Folder not found");
                return;
            }

            AnchorBundleDownloader bundleDownloader = new AnchorBundleDownloader();
            bundleDownloader.TimeoutMS = 30 * 1000;
            bundleDownloader.MaxRetries = 1;
            bundleDownloader.VerifySSL = false;

            Bundle[] bundles = Client.GetBundlesForOwner(owner);
            foreach (Bundle bundle in bundles)
            {
                try
                {
                    WriteLine("Downloading ID={0}, {1}", bundle.ID, bundle.Url);
                    string bundleFolderPath = Path.Combine(folderpath, string.Format("Bundle_{0}", bundle.ID));
                    if (Directory.Exists(bundleFolderPath))
                    {
                        Directory.Delete(bundleFolderPath);
                    }
                    Directory.CreateDirectory(bundleFolderPath);

                    X509Certificate2Collection certs = bundleDownloader.DownloadCertificates(bundle.Uri);
                    foreach (X509Certificate2 cert in certs)
                    {
                        string certName = cert.ExtractEmailNameOrName();
                        WriteLine(certName);
                        string fileName = certName.ToFileName() + ".cer";
                        File.WriteAllBytes(Path.Combine(bundleFolderPath, fileName), cert.RawData);
                    }
                    WriteLine("ok");
                }
                catch (Exception ex)
                {
                    WriteLine("Error downloading bundle {0} from {1}", bundle.ID, bundle.Url);
                    WriteLine(ex.Message);
                }
            }
        }

        private const string BundleDownloadCertsUsage =
            "Download all certs in configured bundles for an owner to a folder.\n\n" +
            "owner [folderPath]\n" +
            "\t owner: bundle owner\n" +
            "\t folderPath: (optional) Where to save the downloaded certs\n" +
            "\n";


        //----------------------------------------------
        //
        // Bundle Display
        //
        //----------------------------------------------
        private void PrintTable(Bundle[] bundles)
        {
            this.PrintBundleTableHeader();
            foreach (Bundle bundle in bundles)
            {
                this.PrintBundleTabular(bundle);
            }
        }
        
        void PrintBundleTableHeader()
        {
            WriteLine("ID\tOwner\tIn\tOut\tStatus\tUrl");
            WriteLine("--\t-----\t--\t---\t------\t---");
        }
        
        void PrintBundleTabular(Bundle bundle)
        {
            WriteLine("{0}\t{1}\t{2}\t{3}\t{4}\t{5}",
                bundle.ID, bundle.Owner, bundle.ForIncoming, bundle.ForOutgoing, bundle.Status, bundle.Url);
        }
        
        void PrintBundle(Bundle bundle)
        {
            WriteLine("{0}", bundle.ID);
            WriteLine(bundle.Owner);
            WriteLine(bundle.Url);
            WriteLine("Incoming={0}", bundle.ForIncoming);
            WriteLine("Outgoing={0}", bundle.ForOutgoing);
            WriteLine("{0}", bundle.Status);
        }
    }
}