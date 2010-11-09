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

    <h2>Addresses for <%= ((DomainModel)ViewData["Domain"]).Name %></h2>

    <%= Html.ActionLink("Return to domains", "Index", "Domains") %>
    <br />
    
    <div class="action-bar">
        <%= Html.ActionLink("Add Address", "Add", new { domainID = ((DomainModel)ViewData["Domain"]).ID }, new { @class = "action ui-priority-primary" })%>
    </div>

    <%= Html.Partial("AddressList", Model) %>
    <%= Html.Partial("AddressDetailsDialog") %>

</asp:Content>
