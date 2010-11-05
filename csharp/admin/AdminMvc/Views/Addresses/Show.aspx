<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Address>>" %>
<%@ Import Namespace="Health.Direct.Config.Store"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Addresses
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Addresses for <%= ((Domain)ViewData["Domain"]).Name %></h2>

    <%= Html.ActionLink("Return to domains", "Index", "Domains") %>
    <br />
    
    <%= Html.ActionLink("Add Address", "Add", new { domainID = ((Domain)ViewData["Domain"]).ID })%>
    <br />
    <br />

    <%= Html.Grid(Model)
        .Columns(
            column =>
                {
                    column.For(d => d.EmailAddress);
                    column.For(d => d.DisplayName);
                    column.For(d => d.Status);
                    column.For(d => d.CreateDate);
                    column.For(d => d.UpdateDate);
                    column.For(d => Html.ActionLink("Details", "Details", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Delete", "Delete", new { id = d.ID }));
                })%>
    <%= Html.Pager((IPagination)Model) %>

</asp:Content>
