<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.AddressModel>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add Address
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add Address</h2>

    <%= Html.ValidationSummary("Please correct the errors and try again.", new { @class = "ui-state-error", style = "padding: 0.5em" })%>

    <% using (Html.BeginForm()) { %> 
    
        <fieldset class="ui-widget-content">
            <span class="display-label"><%= Html.LabelFor(m => m.EmailAddress) %></span>
            <span class="display-field"><%= Html.EditorFor(m => m.EmailAddress, new {@class="ui-widget-content"}) %></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.EmailAddress, "*", new { @class = "ui-state-error-text" })%></span>
            <br class="clear" />

            <span class="display-label"><%= Html.LabelFor(m => m.DisplayName) %></span>
            <span class="display-field"><%= Html.EditorFor(m => m.DisplayName, new { @class = "ui-widget-content" })%></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.DisplayName, "*", new { @class = "ui-state-error-text" })%></span>
            <br class="clear" />

            <span class="display-label"><%= Html.LabelFor(m => m.Type) %></span>
            <span class="display-field"><%= Html.EditorFor(m => m.Type, new { @class = "ui-widget-content" })%></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.Type, "*", new { @class = "ui-state-error-text" })%></span>
            <br class="clear" />
            
            <div class="action-buttons">
                <%= Html.HiddenFor(m => m.DomainID) %>
                <input type="submit" value="Save" />
                <%= Html.ActionLink("Cancel", "Index", new {domainID = Model.DomainID}) %>
            </div>
        </fieldset>
    
    <% } %>

</asp:Content>
