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
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Agent
{
    public class AgentConsole
    {
        Commands m_commands;
        
        public AgentConsole()
        {
            m_commands = new Commands("AgentConsole");
            m_commands.Register(new AgentCommands());
            m_commands.Register(new DnsCommands());
            m_commands.Register(new SmtpAgentCommands());
            m_commands.Register(new CertificateCommands());
            m_commands.Register(new MailCommands());
        }
        
        public void Run(string[] args)
        {
            if (args.IsNullOrEmpty())
            {
                m_commands.RunInteractive();
            }
            else
            {
                m_commands.Run(args);
            }
        }
        
        static void Main(string[] args)
        {
            AgentConsole console = new AgentConsole();
            console.Run(args);
        }
    }

    internal struct IOFiles
    {
        internal IOFiles(string[] args)
        {
            this.InputFile = args.GetRequiredValue(0);
            this.OutputFile = args.GetOptionalValue(1, null);
        }
        
        internal string InputFile;
        internal string OutputFile;
        
        internal bool HasOutputFile
        {
            get
            {
                return !string.IsNullOrEmpty(this.OutputFile);
            }
        }
        
        internal string Read()
        {
            return File.ReadAllText(this.InputFile);
        }
        
        internal void Write(string text)
        {
            if (this.HasOutputFile)
            {
                File.WriteAllText(this.OutputFile, text);
            }
        }
        
        internal void Write(MimeEntity mime)
        {
            if (this.HasOutputFile)
            {
                MimeSerializer.Default.Serialize(mime, this.OutputFile);
            }
        }
    }
}
