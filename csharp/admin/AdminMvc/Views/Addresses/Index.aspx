<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Health.Direct.Config.Store.Address>>" %>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Addresses
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Addresses</h2>

    <%= Html.Grid(Model)
        .Columns(
            column =>
                {
                    column.For(d => d.Status);
                    column.For(d => Html.ActionLink(d.EmailAddress, "Details", new {id = d.ID})).Named("Email Address");
                })%>
    <%= Html.Pager((IPagination)Model) %>

</asp:Content>
