<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<AnchorModel>>" %>
<%@ Import Namespace="AdminMvc.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Anchors
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Anchors for <%= ((DomainModel)ViewData["Domain"]).Name %></h2>

    <%= Html.ActionLink("Return to domains", "Index", "Domains") %>
    <br />
    
    <div class="action-bar">
        <%= Html.ActionLink("Add Anchor", "Add", new { domainID = ((DomainModel)ViewData["Domain"]).ID }, new { @class = "action ui-priority-primary" })%>
    </div>

    <%= Html.Partial("AnchorList", Model) %>

</asp:Content>
