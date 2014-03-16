<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<AnchorUploadModel>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add Anchor
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add Anchor</h2>

    <%= Html.ValidationSummary("Please correct the errors and try again.", new { @class = "ui-state-error", style = "padding: 0.5em" })%>

    <% using (Html.BeginForm("Add", "Anchors", null, FormMethod.Post, new { enctype = "multipart/form-data" })) { %>
    
        <fieldset class="ui-widget-content">
        
            <%= Html.HiddenFor(m => m.DomainID) %>
        
            <span class="display-label">Owner</span>
            
            <% if (Model.DomainID == 0) { %>
                <span class="display-field"><%= Html.EditorFor(m => m.Owner)%></span>
                <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.Owner, "*", new { @class = "ui-state-error-text" })%></span>
            <% } else { %>
                <span class="display-field"><%= Html.DisplayFor(m => m.Owner)%></span>
            <% } %>
            <br class="clear" />

            <span class="display-label"><%= Html.LabelFor(m => m.Purpose) %></span>
            <span class="display-field"><%= Html.DropDownListFor(m => m.Purpose, AnchorUploadModel.PurposeTypeList) %></span>
            <br class="clear" />
        
            <span class="display-label">Certificate Path</span>
            <span class="display-field"><%= Html.File("certificateFile", new { @class = "ui-widget-content" })%></span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(m => m.Password) %></span>
            <span class="display-field"><%= Html.PasswordFor(m => m.Password, new { @class = "ui-widget-content" })%></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.Password, "*", new { @class = "ui-state-error-text" })%></span>
            <br class="clear" />

            <span class="display-label"><%= Html.LabelFor(m => m.PasswordConfirm) %></span>
            <span class="display-field"><%= Html.PasswordFor(m => m.PasswordConfirm, new { @class = "ui-widget-content date-text" })%></span>
            <span class="editor-validator"><%= Html.ValidationMessageFor(m => m.PasswordConfirm, "*", new { @class = "ui-state-error-text" })%></span>
            <br class="clear" />

            <div class="action-buttons">
                <%= Html.HiddenFor(m => m.Owner) %>
                <input type="submit" value="Save" />
                <%= Html.ActionLink("Cancel", "Index", new { owner = Model.Owner })%>
            </div>
        </fieldset>
    
    <% } %>

    <script type="text/javascript" language="javascript" src="<%= Url.Content("~/Scripts/OwnerAutocomplete.js") %>"></script>

</asp:Content>
