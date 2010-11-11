<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<AdminMvc.Models.DomainModel>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add Domain
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add Domain</h2>

    <%= Html.ValidationSummary("Please correct the errors and try again.", new {@class = "ui-state-error", style="padding: 0.5em"}) %>

    <% using (Html.BeginForm()) { %> 
    
        <fieldset class="ui-widget-content">
            <span class="display-label"><%= Html.LabelFor(m => m.Name) %></span>
            <span class="display-field"><%= Html.TextBoxFor(m => m.Name, new { @class = "ui-widget-content" })%></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.Name, "*", new {@class = "ui-state-error-text"}) %></span>
            <br class="clear" />
            
            <p>
                <input type="submit" value="Save" />
                <%= Html.ActionLink("Cancel", "Index") %>
            </p>
        </fieldset>
    
    <% } %>

</asp:Content>
