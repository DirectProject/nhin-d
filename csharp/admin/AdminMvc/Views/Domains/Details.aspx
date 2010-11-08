<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Domain>" %>
<%@ Import Namespace="Health.Direct.Config.Store"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Details
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Details</h2>

    <%= Html.ActionLink("Return to domains", "Index") %>
    <br />
    
    <div class="display-label">Domain Name</div>
    <div class="display-field"><%= Model.Name %></div>

    <div class="display-label">Status</div>
    <div class="display-field"><%= Model.Status %></div>
    
    <div class="display-label">Created</div>
    <div class="display-field"><%= Model.CreateDate %></div>

    <div class="display-label">Updated</div>
    <div class="display-field"><%= Model.UpdateDate %></div>

    <br class="clear" />
    
    <% if (Model.Status == EntityStatus.Enabled) { %> <%= Html.ActionLink("Disable", "Disable", new { id = Model.ID }, new {@class = "action"})%><% } %>
    &nbsp;
    <% if (Model.Status != EntityStatus.Enabled) { %> <%= Html.ActionLink("Enable", "Enable", new { id = Model.ID }, new { @class = "action" })%><% } %>
    &nbsp;
    <%= Html.ActionLink("Delete", "Delete", new { id = Model.ID }, new { @class = "action" })%>

</asp:Content>
