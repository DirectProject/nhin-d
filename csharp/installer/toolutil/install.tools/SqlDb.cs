/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook     
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Data;
using System.Data.SqlClient;
using System.Runtime.InteropServices;

namespace Health.Direct.Install.Tools
{

    [ComVisible(true), GuidAttribute("18EC01DD-005E-4fb6-80D7-F6D96802C1A7")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface ISqlDb
    {
        bool TestConnection(string connectionString, out string exeption);
    }

    [ComVisible(true), GuidAttribute("5991E691-A272-4e1a-AEFF-0D3E16BA6FB8")]
    [ProgId("Direct.Installer.SqlDbTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class SqlDb : ISqlDb
    {
        public bool TestConnection(string connectionString, out string exception)
        {
            exception = string.Empty;
            try
            {
                using (SqlConnection sqlConnection = new SqlConnection(connectionString))
                {
                    sqlConnection.Open();
                    if (sqlConnection.State == ConnectionState.Open)
                    {
                        return true;
                    }
                    return false;
                }
            }
            catch(Exception e)
            {
                exception = e.Message;
                return false;
            }
        }
    }
}
