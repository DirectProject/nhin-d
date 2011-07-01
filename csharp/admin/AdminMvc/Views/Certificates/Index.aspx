<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<CertificateModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Certificates
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <%= Html.Partial(ViewData["Domain"] == null? "AllItemsReminder" : "FilterReminder", "certificates")%>
    <div class="action-bar clear">
        <%= Html.ActionLink("Add Certificate", "Add", new { domainID=((DomainModel)ViewData["Domain"] ?? new DomainModel()).ID }, new { @class = "action ui-priority-primary" })%>
        <%= Html.ActionLink("Resolve", "Resolve", new { domainID=((DomainModel)ViewData["Domain"] ?? new DomainModel()).ID }, new { @class = "action ui-priority-secondary" })%>
    </div>
    
    <%= Html.Partial("CertificateList", Model, ViewData) %>
    <%= Html.Partial("CertificateDetailsDialog") %>

</asp:Content>
