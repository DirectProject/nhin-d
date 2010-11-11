<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<CertificateModel>>" %>
<%@ Import Namespace="AdminMvc.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Certificates
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <% if (ViewData["Domain"] != null) { %>
        <%= Html.Partial("FilterReminder", "certificates") %>
        <div class="action-bar clear">
            <%= Html.ActionLink("Add Certificate", "Add", new { owner = ((DomainModel)ViewData["Domain"]).Name }, new { @class = "action ui-priority-primary" })%>
        </div>
    <% } else { %>
        <%= Html.Partial("AllItemsReminder", "certificates") %>
    <% } %>
    
    <%= Html.Partial("CertificateList", Model, ViewData) %>
    <%= Html.Partial("CertificateDetailsDialog") %>

</asp:Content>
