/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Command
{
    internal class ConfigConsole
    {
        internal static ConfigConsole Current = new ConfigConsole();
        
        Commands m_commands;
        
        internal ConfigConsole()
        {
            m_commands = new Commands("ConfigConsole");
            m_commands.Error += PrintError;
            
            m_commands.Register(new DomainCommands());
            m_commands.Register(new AddressCommands());
            m_commands.Register(new CertificateCommands());
            m_commands.Register(new AnchorCommands());
        }
        
        internal void Run(string[] args)
        {
            if (args != null && args.Length > 0)
            {
                m_commands.Run(args);
            }
            else
            {
                m_commands.RunInteractive();
            }
        }
        
        static void Main(string[] args)
        {
            ConfigConsole.Current.Run(args);
        }
        
        void PrintError(Exception ex)
        {
            FaultException<ConfigStoreFault> fault = ex as FaultException<ConfigStoreFault>;
            if (fault != null)
            {
                Console.WriteLine(fault.ToString());
            }
            else
            {
                Console.WriteLine(ex.Message);
            }
        }        
    }
}
