<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Anchor>>" %>
<%@ Import Namespace="Health.Direct.Config.Store"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Anchors
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Anchors for <%= ((Domain)ViewData["Domain"]).Name %></h2>

    <%= Html.ActionLink("Return to domains", "Index", "Domains") %>
    <br />
    
    <%= Html.ActionLink("Add Anchor", "Add", new { domainID = ((Domain)ViewData["Domain"]).ID })%>
    <br />
    <br />

    <%= Html.Grid(Model)
        .Columns(
            column =>
                {
                    column.For(d => d.Owner);
                    column.For(d => d.Thumbprint);
                    column.For(d => d.CreateDate);
                    column.For(d => d.ValidStartDate);
                    column.For(d => d.ValidEndDate);
                    column.For(d => d.ForIncoming);
                    column.For(d => d.ForOutgoing);
                    column.For(d => d.HasData);
                    column.For(d => d.Status);
                })%>
    <%= Html.Pager((IPagination)Model) %>

</asp:Content>
