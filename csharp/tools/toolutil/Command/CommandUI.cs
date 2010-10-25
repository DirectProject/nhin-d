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

namespace Health.Direct.Config.Tools.Command
{
    /// <summary>
    /// Common things most Console apps do on the command line
    /// </summary>
    public static class CommandUI
    {
        /// <summary>
        /// Print a prompt sign, then get line
        /// </summary>
        /// <returns></returns>
        public static string GetInput()
        {
            Console.Write('>');
            return Console.ReadLine();            
        }
        
        public static void PrintDivider()
        {
            Console.WriteLine("=====================================");
        }
        
        public static void PrintThickDivider()
        {
            Console.WriteLine("*************************************");
        }

        public static void PrintSectionBreak()
        {
            Console.WriteLine("------");
        }

        public static void PrintHeading(string format, params object[] parameters)
        {
            PrintHeading(string.Format(format, parameters));
        }
        
        public static void PrintHeading(string heading)
        {
            PrintDivider();   
            Console.WriteLine('|');
            Console.Write("|\t");
            Console.WriteLine(heading); 
            Console.WriteLine("|");
            PrintDivider();
        }
        
        public static void PrintHilite(string format, params object[] parameters)
        {
            PrintHilite(string.Format(format, parameters));
        }
        
        public static void PrintHilite(string message)
        {
            Console.Write("# ");
            Console.WriteLine(message);
        }

        public static void PrintBold(string format, params object[] parameters)
        {
            PrintBold(string.Format(format, parameters));
        }

        public static void PrintBold(string message)
        {
            Console.WriteLine("**{0}**", message);
        }

        public static void PrintUpperCase(string format, params object[] parameters)
        {
            PrintUpperCase(string.Format(format, parameters));
        }
        
        public static void PrintUpperCase(string message)
        {
            Console.WriteLine(message.ToUpper());
        }
        
        public static void Print(Exception ex)
        {
            PrintHilite("ERROR");
            Console.WriteLine(ex.Message);
        }
        
        public static void Print(string name, object  value)
        {
            Console.WriteLine("{0} = {1}", name, value);
        }
    }
}