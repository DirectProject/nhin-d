/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Contains XML to Linq custom Axis methods for IHE XD* ebXML
    /// </summary>
    public static class AxisMethods
    {

        /// <summary>
        /// Returns child elements ignoring namespace
        /// </summary>
        public static IEnumerable<XElement> ElementsAnyNs(this XElement source, string name)
        {
            return source.Elements().Where(e => e.Name.LocalName == name);
        }

        /// <summary>
        /// Returns the first named child element, ignoring namespace
        /// </summary>
        public static XElement ElementAnyNs(this XElement source, string name)
        {
            IEnumerable<XElement> elts = source.ElementsAnyNs(name);
            if (elts.Count() == 0) return null;
            return elts.First();
        }

        /// <summary>
        /// Returns descendent elements ignoring namespace
        /// </summary>
        public static IEnumerable<XElement> DescendantsAnyNs(this XElement source, string name)
        {
            return source.Descendants().Where(e => e.Name.LocalName == name);
        }

        /// <summary>
        /// Returns the value of a named attribute, or <c>null</c> if no such attribute exists.
        /// </summary>
        public static string AttributeValue(this XElement source, string name)
        {
            XAttribute attr = source.Attribute(name);
            if (attr == null) return null;
            return attr.Value;
        }

        /// <summary>
        /// Returns the value of a named attribute, transformed by a mapping function to a specialized return type.
        /// </summary>
        public static T AttributeValue<T>(this XElement source, string name, Func<string, T> map)
        {
            string val = source.AttributeValue(name);
            if (val == null) return default(T);
            return map(val);
        }


        /// <summary>
        /// Returns classfication elements of the specified <paramref name="scheme"/>
        /// </summary>
        public static IEnumerable<XElement> Classifications(this XElement source, string scheme)
        {
            return from el in source.DescendantsAnyNs(XDMetadataStandard.Elts.Classification)
                   where (string)el.Attribute(XDMetadataStandard.Attrs.ClassificationScheme) == scheme
                   select el;
        }

        /// <summary>
        /// Returns the first classification element of the specified scheme.
        /// </summary>
        public static XElement Classification(this XElement source, string scheme)
        {
            var classifications = source.Classifications(scheme);
            if (classifications == null || classifications.Count() == 0)
            {
                return null;
            }
            return source.Classifications(scheme).First();
        }

        /// <summary>
        /// Returns all slots that are a child of the supplied element
        /// </summary>
        public static IEnumerable<XElement> Slots(this XElement source)
        {
            return source.ElementsAnyNs(XDMetadataStandard.Elts.Slot);
        }

        /// <summary>
        /// Returns slots that are a child of the supplied with the appropriate name
        /// </summary>
        public static IEnumerable<XElement> Slots(this XElement source, string slotName)
        {
            return source.Slots().Where(s => s.Attribute(XDMetadataStandard.Attrs.SlotName).Value == slotName);
        }

        /// <summary>
        /// Returns the first slot with the appropriate name
        /// </summary>
        public static XElement Slot(this XElement source, string slotName)
        {
            IEnumerable<XElement> slots = source.Slots(slotName);
            if (slots == null || slots.Count() == 0) return null;
            return slots.First();
        }

        /// <summary>
        /// Returns the slot value for a single valued slot, or the first value for a multivalued slot.
        /// </summary>
        public static string SlotValue(this XElement source, string slotName)
        {
            IEnumerable<string> values = source.SlotValues(slotName);
            if (values == null || values.Count() == 0) return null;
            return values.First();
        }

        /// <summary>
        /// Returns the slot values for a multivalued slot.
        /// </summary>
        public static IEnumerable<string> SlotValues(this XElement source, string slotName)
        {
            XElement slot = source.Slot(slotName);
            if (slot == null) return null;
            return from el in source.Slot(slotName).DescendantsAnyNs("Value")
                   select el.Value;
        }

        /// <summary>
        /// Returns the slot value for a single valued slot (or the first value in a multivalued slot) using a map function
        /// to convert to type T
        /// </summary>
        /// <returns>The mapped value of the slot, or <c>null</c> if the slot does not exist or has no values.</returns>
        public static T SlotValue<T>(this XElement source, string slotName, Func<string, T> map)
        {
            string val = source.SlotValue(slotName);
            if (val == null) return default(T);
            return map(val);
        }


        /// <summary>
        /// Returns the slot values for a multivalued slot using a map function to convert to type T
        /// </summary>
        /// <returns>The mapped values of the slot, or null if the slot does not exist or has no values</returns>
        public static IEnumerable<T> SlotValues<T>(this XElement source, string slotName, Func<string, T> map)
        {
            IEnumerable<string> vals = source.SlotValues(slotName);
            if (vals == null) return null;
            return vals.Select(map);
        }

        

        /// <summary>
        /// Returns ExternalIdentifier elements encoding patient identifiers.
        /// </summary>
        public static IEnumerable<XElement> ExternalIdentifiers(this XElement source, string scheme)
        {
            return from el in source.DescendantsAnyNs("ExternalIdentifier")
                   where (string)el.Attribute("identificationScheme") == scheme
                   select el;
        }

        /// <summary>
        /// Returns the value for a specified external identifier or null if no identifier exists
        /// </summary>
        public static string ExternalIdentifierValue(this XElement source, string scheme)
        {
            IEnumerable<XElement> elts = source.ExternalIdentifiers(scheme);
            if (elts == null || elts.Count() == 0) return null;

            return elts.First().Attribute("value").Value;
        }

        /// <summary>
        /// Returns the mapped value for a specified external identifier using the supplied mapping function. Returns <c>null</c> if no identifier exists.
        /// </summary>
        public static T ExternalIdentifierValue<T>(this XElement source, string scheme, Func<string, T> map)
        {
            string val = source.ExternalIdentifierValue(scheme);
            if (val == null) return default(T);
            return map(val);
        }



        /// <summary>
        /// Returns <see cref="XElement"/> instances for each document entry node for this element
        /// </summary>
        public static IEnumerable<XElement> DocumentEntries(this XElement source)
        {
            return source.DescendantsAnyNs(XDMetadataStandard.Elts.DocumentEntry).Where(e => e.Attribute(XDMetadataStandard.Attrs.ObjectType).Value == XDMetadataStandard.UUIDs.DocumentEntry);
        }

        /// <summary>
        /// Returns the <see cref="XElement"/> corresponding to the Submission Set
        /// </summary>
        public static XElement SubmissionSet(this XElement source)
        {
            IEnumerable<XElement> classifications = from el in source.DescendantsAnyNs(XDMetadataStandard.Elts.Classification)
                                                    where (string)el.Attribute(XDMetadataStandard.Attrs.ClassificationNode) == XDMetadataStandard.UUIDs.SubmissionSetClassification
                                                    select el;

            //TODO More specific exception
            if (classifications.Count() != 1) throw new ArgumentException();
            string id = classifications.First().AttributeValue(XDMetadataStandard.Attrs.ClassifiedObject);

            IEnumerable<XElement> submissionsets = from el in source.DescendantsAnyNs(XDMetadataStandard.Elts.SubmissionSet)
                                                   where (string)el.Attribute("id") == id
                                                   select el;
            //TODO More specific exception
            if (submissionsets.Count() != 1) throw new ArgumentException();

            return submissionsets.First();
        }
        /// <summary>
        /// Returns the child Name element
        /// </summary>
        public static XElement Name(this XElement source)
        {
            IEnumerable<XElement> names = source.ElementsAnyNs(XDMetadataStandard.Elts.Name);
            if (names.Count() == 0) return null;
            return names.First();
        }

        /// <summary>
        /// Returns the value of the child Name element
        /// </summary>
        public static string NameValue(this XElement source)
        {
            XElement name = source.Name();
            if (name == null) return null;
            XElement localizedString = name.ElementAnyNs(XDMetadataStandard.Elts.LocalizedString);
            if (localizedString == null) return null;
            XAttribute valueAttr = localizedString.Attribute("value");
            if (valueAttr == null) return null;
            return valueAttr.Value;
        }

        /// <summary>
        /// Returns the child Description element
        /// </summary>
        public static XElement Description(this XElement source)
        {
            IEnumerable<XElement> names = source.ElementsAnyNs(XDMetadataStandard.Elts.Description);
            if (names.Count() == 0) return null;
            return names.First();
        }

        /// <summary>
        /// Returns the value of the child Name element
        /// </summary>
        public static string DescriptionValue(this XElement source)
        {
            XElement name = source.Description();
            if (name == null) return null;
            XElement localizedString = name.ElementAnyNs(XDMetadataStandard.Elts.LocalizedString);
            if (localizedString == null) return null;
            XAttribute valueAttr = localizedString.Attribute("value");
            if (valueAttr == null) return null;
            return valueAttr.Value;
        }
    
    }
}