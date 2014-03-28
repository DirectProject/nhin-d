/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.SettingsManager;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    public class BlobCommands : CommandsBase<BlobManagerClient>
    {
        internal BlobCommands(ConfigConsole console, Func<BlobManagerClient> client)
            : base(console, client)
        {
        }
                
        [Command(Name="Blob_Add")]        
        public void Add(string[] args)
        {
            NamedBlob blob = CreateBlob(args);
            this.Client.AddBlob(blob);
        }
        
        [Command(Name="Blob_Update")]
        public void Update(string[] args)
        {
            NamedBlob blob = CreateBlob(args);
            this.Client.UpdateBlob(blob);
        }
        
        [Command(Name="Blob_Get")]
        public void Get(string[] args)
        {
            string name = args.GetRequiredValue(0);
            string outputFilePath = args.GetOptionalValue(1, null);
            
            NamedBlob blob = this.Client.GetBlob(name);
            if (blob == null)
            {
                WriteLine("No matches");
                return;
            }
            
            if (outputFilePath != null)
            {
                File.WriteAllBytes(outputFilePath, blob.Data);
            }
            else
            {
                WriteLine("{0} bytes", blob.Data.Length);
            }
        }
        
        [Command(Name="Blob_Remove")]
        public void Remove(string[] args)
        {
            string name = args.GetRequiredValue(0);
            this.Client.RemoveBlob(name);        
        }
        
        NamedBlob CreateBlob(string[] args)
        {
            string name = args.GetRequiredValue(0);
            string filePath = args.GetRequiredValue(1);

            return new NamedBlob(name, File.ReadAllBytes(filePath));
        }
    }
}
