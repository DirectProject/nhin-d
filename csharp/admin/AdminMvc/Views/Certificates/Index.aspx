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
    <div class="ui-widget" style="margin-bottom: 1em; float: left;">
        <div class="ui-state-highlight" style="padding: .3em .7em; font-size: .8em; width: auto;">
            <span style="float: left; padding-right: .5em;">Displaying '<%= ((DomainModel)ViewData["Domain"]).Name%>'</span>
            <a href="/Certificates" title="Click to show all certificates"><span class="ui-icon ui-icon-close"></span></a>
        </div>
    </div>
    <div class="action-bar clear">
        <%= Html.ActionLink("Add Certificate", "Add", new { domainID = ((DomainModel)ViewData["Domain"]).ID }, new { @class = "action ui-priority-primary" })%>
    </div>
    <% } %>
    
    <%= Html.Partial("CertificateList", Model, ViewData) %>
    <%= Html.Partial("CertificateDetailsDialog") %>

</asp:Content>
