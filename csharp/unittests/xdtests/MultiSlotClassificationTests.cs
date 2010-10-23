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
using System.Linq;

using Xunit;

namespace Health.Direct.Xd.Tests
{
    public class MultiSlotClassificationTests
    {
        [Fact]
        public void EmptyMultiSlotClassificationHasCorrectScheme()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc", new Slot[] { });
            Assert.Equal("abc", c.Attribute("classificationScheme").Value);
        }

        [Fact]
        public void EmptyMultiSlotClassificationHasCorrectNodeRepresentation()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc", new Slot[] { });
            Assert.Equal("123", c.Attribute("nodeRepresentation").Value);
        }

        [Fact]
        public void EmptyMultiSlotClassificationHasCorrectClassifiedObject()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc", new Slot[] { });
            Assert.Equal("doc", c.Attribute("classifiedObject").Value);
        }

        [Fact]
        public void EmptyMultiSlotClassificationHasNoContent()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc", new Slot[] { });
            Assert.Empty(c.Elements());
        }

        [Fact]
        public void SingleSlottedMultiSlotClassificationHasSingleSlot()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc", new Slot[] { new Slot("name", "value") });
            Assert.Equal("value", c.SlotValue("name"));
        }

        [Fact]
        public void MultiSlottedMultiSlotClassificationHasMultipleSlots()
        {
            MultiSlotClassification c = new MultiSlotClassification("abc", "123", "doc",
                                                                    new Slot[] { new Slot("a", "1"), new Slot("b", "2") });
            Assert.Equal(2, c.Slots().Count());
            Assert.Equal("1", c.SlotValue("a"));
            Assert.Equal("2", c.SlotValue("b"));
        }

    }
}