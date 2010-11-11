/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Configuration;
using System.Linq;

using Health.Direct.Common.Container;

using Xunit;

namespace Health.Direct.Common.Tests.Container
{
    public class SimpleContainerSectionFacts
    {
        [Fact]
        public SimpleContainerSection LoadSection()
        {
            SimpleContainerSection containerSection = (SimpleContainerSection)ConfigurationManager.GetSection("container");
            Assert.NotNull(containerSection);
            return containerSection;
        }

        [Fact]
        public SimpleComponentElement SectionHasComponent()
        {
            var section = LoadSection();

            var component = section.Components.Cast<SimpleComponentElement>().FirstOrDefault();
            Assert.NotNull(component);
            Assert.Equal("Health.Direct.Common.Tests.Container.IFoo, Health.Direct.Common.Tests", component.Service);
            Assert.Equal("Health.Direct.Common.Tests.Container.Foo, Health.Direct.Common.Tests", component.Type);
            return component;
        }

        [Fact]
        public void CreateInstance()
        {
            var component = SectionHasComponent();
            
            Assert.Equal(typeof(IFoo), component.ServiceType);

            object instance  = component.CreateInstance();
            Assert.IsType(typeof(Foo), instance);
        }

        [Fact]
        public void RegisterContainerWithSection()
        {
            var foo = new SimpleDependencyResolver().RegisterFromConfig().Resolve<IFoo>();
            Assert.NotNull(foo);
            Assert.IsType<Foo>(foo);
        }

        [Fact]
        public void LifetimeSingleton()
        {
            var foo = IoC.Resolve<IFoo>();
            Assert.NotNull(foo);

            var foo2 = IoC.Resolve<IFoo>();
            Assert.Same(foo, foo2);
        }

        [Fact]
        public void LifetimeTransient()
        {
            var container = new SimpleDependencyResolver().RegisterFromConfig();
            var bar = container.Resolve<IBar>();
            Assert.NotNull(bar);

            var bar2 = container.Resolve<IBar>();
            Assert.NotSame(bar, bar2);
        }
    }

    public interface IFoo
    {
    }

    public class Foo : IFoo
    {
    }

    public interface IBar
    {
    }

    public class Bar : IBar
    {
    }
}