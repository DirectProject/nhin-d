using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Metadata;
using Xunit;


namespace NHINDirect.Tests.metadata
{
    public class AuthorFacts
    {


        [Fact]
        public void SimpleAuthor()
        {
            Author a = new Author();
            a.Person = new Person { First = "Tom", Last = "Jones" };
            Assert.Equal("Tom Jones", a.Person.ToString());
            Assert.Equal("^Jones^Tom^^^^", a.Person.ToXCN());
        }

        [Fact]
        public void AuthorWithMI()
        {
            Author a = new Author();
            a.Person = new Person { First="Tom", Last="Jones", MI="A" };
            Assert.Equal("Tom A Jones", a.Person.ToString());
            Assert.Equal("^Jones^Tom^A^^^", a.Person.ToXCN());
        }

        [Fact]
        public void MD()
        {
            Author a = new Author();
            a.Person = new Person { First = "Tom", Last = "Jones", Degree = "M.D." };
            Assert.Equal("Tom Jones, M.D.", a.Person.ToString());
            Assert.Equal("^Jones^Tom^^^^M.D.", a.Person.ToXCN());
        }

        [Fact]
        public void FullMonty()
        {
            Author a = new Author();
            a.Person = new Person { Prefix = "Sir", First = "Tom", MI = "A", Last = "Jones", Suffix = "Jr.", Degree = "M.D., PhD, JD, PharmD" };
            Assert.Equal("Sir Tom A Jones, Jr., M.D., PhD, JD, PharmD", a.Person.ToString());
            Assert.Equal("^Jones^Tom^A^Jr.^Sir^M.D., PhD, JD, PharmD", a.Person.ToXCN());
        }

        [Fact]
        public void Celebrity()
        {
            Author a = new Author();
            a.Person = new Person { Prefix = "Lady", First = "Gaga", Degree = "M.A." };
            Assert.Equal("Lady Gaga, M.A.", a.Person.ToString());
            Assert.Equal("^^Gaga^^^Lady^M.A.", a.Person.ToXCN());
        }

        [Fact]
        public void OnlyLastName()
        {
            Author a = new Author();
            a.Person = new Person { Last = "Bond", Degree = "Licensed to Kill" };
            Assert.Equal("Bond, Licensed to Kill", a.Person.ToString());
            Assert.Equal("^Bond^^^^^Licensed to Kill", a.Person.ToXCN());
        }

        [Fact]
        public void Institutions()
        {
            Author a = new Author();
            a.Institutions.Add(new Institution("ONC-U"));
            Assert.Contains("ONC-U", a.Institutions.Select( i => i.ToXON()));
        }
    }
}
