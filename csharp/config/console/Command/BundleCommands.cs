/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce te above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage anchor bundle configurations
    /// </summary>
    public class BundleCommands : CommandsBase<BundleStoreClient>
    {
        internal BundleCommands(ConfigConsole console, Func<BundleStoreClient> client) : base(console, client)
        {
        }

        /// <summary>
        /// Add a new bundle
        /// </summary>
        [Command(Name = "Bundle_Add", Usage = BundleAddUsage)]
        public void BundleAdd(string[] args)
        {
            Bundle bundle = new Bundle()
            {
                Owner = args.GetRequiredValue<string>(0),
                Url = args.GetRequiredValue<string>(1),
                ForIncoming = args.GetRequiredValue<bool>(2),
                ForOutgoing = args.GetRequiredValue<bool>(3),
                Status = args.GetOptionalEnum<EntityStatus>(4, EntityStatus.New),
            };

            Client.AddBundle(bundle);
            WriteLine("ok");
        }

        private const string BundleAddUsage =
            "Add a new bundle definition too the config store.\n" +
            "\t owner url forIncoming forOutgoing [status]\n" +
            "\t\t owner: domain or address\n" +
            "\t\t url: url for bundle\n" +
            "\t\t forIncoming: (true/false) Use bundle to verify trust of INCOMING messages\n" +
            "\t\t forOutgoing: (true/false) use bundle to verify trust of OUTGOING messages\n" +
            "\t\t status: (new/enabled/disabled, default new) status field\n" +
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
            "Set the status field on an existing bundle.\n" +
            "\t id status\n" +
            "\t\t id: bundle id\n" +
            "\t\t status: (new/enabled/disabled) status field\n" +
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
            "\t id\n" +
            "\t\t id: bundle id\n" +
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
            "Return all bunders for owner (address or domain).\n" +
            "\t owner\n" +
            "\t\t owner: domain or address\n" +
            "\n";

        private void PrintTable(Bundle[] bundles)
        {
            WriteLine("ID\tOwner\tIn\tOut\tStatus\tUrl");
            WriteLine("--\t-----\t--\t---\t------\t---");

            foreach (Bundle bundle in bundles)
            {
                WriteLine("{0}\t{1}\t{2}\t{3}\t{4}\t{5}",
                    bundle.ID, bundle.Owner, bundle.ForIncoming, bundle.ForOutgoing, bundle.Status, bundle.Url);
            }
        }
    }
}