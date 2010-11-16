<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<AddressModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Addresses
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <%= Html.Partial(ViewData["Domain"] == null ? "AllItemsReminder" : "FilterReminder", "addresses")%>
    <div class="action-bar clear">
        <%= Html.ActionLink("Add Address", "Add", new { domainID = ((DomainModel)ViewData["Domain"] ?? new DomainModel()).ID }, new { @class = "action ui-priority-primary" })%>
    </div>

    <%= Html.Partial("AddressList", Model, ViewData) %>
    <%= Html.Partial("AddressDetailsDialog") %>

</asp:Content>
