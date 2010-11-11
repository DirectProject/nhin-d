<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<AddressModel>>" %>
<%@ Import Namespace="AdminMvc.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Addresses
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <% if (ViewData["Domain"] != null) { %>
        <%= Html.Partial("FilterReminder", "addresses") %>
        <div class="action-bar clear">
            <%= Html.ActionLink("Add Address", "Add", new { domainID = ((DomainModel)ViewData["Domain"]).ID }, new { @class = "action ui-priority-primary" })%>
        </div>
    <% } else { %>
        <%= Html.Partial("AllItemsReminder", "addresses") %>
    <% } %>

    <%= Html.Partial("AddressList", Model, ViewData) %>
    <%= Html.Partial("AddressDetailsDialog") %>

</asp:Content>
