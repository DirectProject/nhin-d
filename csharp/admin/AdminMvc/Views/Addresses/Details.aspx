<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Config.Store.Address>" %>
<%@ Import Namespace="Health.Direct.Config.Store"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Details
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Details</h2>

    <%= Html.ActionLink("Back to addresses", "Show", new {domainID = Model.DomainID}) %>
    <br />
    <br />

    <div class="display-label">Domain ID</div>
    <div class="display-field"><%= Model.DomainID %></div>

    <div class="display-label">Email Address</div>
    <div class="display-field"><%= Model.EmailAddress %></div>

    <div class="display-label">Display Name</div>
    <div class="display-field"><%= Model.DisplayName %></div>

    <div class="display-label">Type</div>
    <div class="display-field"><%= Model.Type %></div>

    <div class="display-label">Status</div>
    <div class="display-field"><%= Model.Status %></div>

    <div class="display-label">Created</div>
    <div class="display-field"><%= Model.CreateDate %></div>

    <div class="display-label">Updated</div>
    <div class="display-field"><%= Model.UpdateDate %></div>

    <br class="clear" />
    
    <% if (Model.Status == EntityStatus.Enabled) { %> <%= Html.ActionLink("[Disable]", "Disable", new { id = Model.ID })%><% } %>
    &nbsp;
    <% if (Model.Status != EntityStatus.Enabled) { %> <%= Html.ActionLink("[Enable]", "Enable", new { id=Model.ID}) %><% } %>
    &nbsp;
    <%= Html.ActionLink("[Delete]", "Delete", new { id=Model.ID}) %>

</asp:Content>
