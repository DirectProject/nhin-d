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
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.SettingsManager;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    public class PropertyCommands : CommandsBase<PropertyManagerClient>
    {
        internal PropertyCommands(ConfigConsole console, Func<PropertyManagerClient> client)
            : base(console, client)
        {
        }
        
        /// <summary>
        /// Add a new property
        /// </summary>
        [Command(Name = "Property_Add", Usage = PropertyAddUsage)]
        public void Add(string[] args)
        {
            Property property = ParseProperty(args);
            this.Client.AddProperty(property);
        }
        const string PropertyAddUsage = "property_add " + PropertyUsage;
        
        /// <summary>
        /// If doesn't exist, creates the property, else updates its value
        /// </summary>
        [Command(Name = "Property_Set", Usage = PropertyUpdateUsage)]
        public void Update(string[] args)
        {
            Property property = ParseProperty(args);
            this.Client.SetProperty(property);
        }
        const string PropertyUpdateUsage = "property_update " + PropertyUsage;
        
        /// <summary>
        /// Gets a property
        /// </summary>
        [Command(Name = "Property_Get", Usage = PropertyGetUsage)]
        public void Get(string[] args)
        {
            string name = args.GetRequiredValue(0);
            Property[] properties = this.Client.GetProperties(new string[] {name});
            Print(properties);
        }
        const string PropertyGetUsage = "property_get name ";
        
        /// <summary>
        /// Gets all properties matching the given prefix
        /// </summary>
        [Command(Name = "Property_GetPrefix", Usage = PropertyGetPrefixUsage)]
        public void GetPrefix(string[] args)
        {
            string namePrefix = args.GetOptionalValue(0, string.Empty);
            Property[] properties = this.Client.GetPropertiesByPrefix(namePrefix);
            Print(properties);
        }
        const string PropertyGetPrefixUsage = "property_getprefix name ";
        
        /// <summary>
        /// Remove a property
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Property_Remove", Usage = PropertyRemoveUsage)]
        public void Remove(string[] args)
        {
            this.Client.RemoveProperty(args.GetRequiredValue(0));
        }
        const string PropertyRemoveUsage = "property_remove name";
                                    
        const string PropertyUsage = " name value";
        Property ParseProperty(string[] args)
        {
            return new Property(args.GetRequiredValue(0), args.GetRequiredValue(1));
        }
        
        void Print(Property[] properties)
        {
            if (properties.IsNullOrEmpty())
            {
                WriteLine("No matches");
                return;
            }
                        
            foreach(Property property in properties)
            {
                WriteLine("{0} = {1}", property.Name, property.Value);
            }
        }        
    }
}
