<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.ResolveModel>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Resolve Certificate
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Resolve Certificate</h2>
    
    <%= Html.ValidationSummary("Please correct the errors and try again.", new { @class = "ui-state-error", style = "padding: 0.5em" })%>

    <% using (Html.BeginForm()) { %>
    
        <fieldset class="ui-widget-content">
        
            <span class="display-label"><%= Html.LabelFor(m => m.Owner)%></span>
            <span class="display-field"><%= Html.EditorFor(m => m.Owner)%></span>
            <br class="clear" />

            <span class="display-label"></span>
            <span class="display-field"><%= Html.CheckBoxFor(m => m.ShowData)%> Include data</span>
            <br class="clear" />

            <div class="action-buttons">
                <input type="submit" value="Resolve" />
                <%= Html.ActionLink("Cancel", "Index", new { domainID = 0 })%>
            </div>

        </fieldset>
        
    <% } %>

</asp:Content>
