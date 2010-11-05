<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Health.Direct.Config.Store.Domain>>" %>
<%@ Import Namespace="AdminMvc.Controllers"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Domains
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Domains</h2>

    <%= Html.ActionLink("Add Domain", "Add")%>
    <br />
    <br />

    <%= Html.Grid(Model)
        .Columns(
            column =>
                {
                    column.For(d => d.Name);
                    column.For(d => d.Status);
                    column.For(d => d.CreateDate);
                    column.For(d => d.UpdateDate);
                    column.For(d => Html.ActionLink("Details", "Details", new {id = d.ID}));
                    column.For(d => Html.ActionLink("Addresses", "Addresses", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Anchors", "Anchors", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Certificates", "Certificates", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Delete", "Delete", new {id = d.ID}));
                })%>
    <%= Html.Pager((IPagination)Model) %>

</asp:Content>
