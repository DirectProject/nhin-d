<%@ Page Language="C#" MasterPageFile="~/Views/Shared/UnAuthSite.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.LogOnModel>" %>

<asp:Content ID="loginTitle" ContentPlaceHolderID="TitleContent" runat="server">
    Log On
</asp:Content>

<asp:Content ID="loginContent" ContentPlaceHolderID="MainContent" runat="server">
    <h2>log in to the console</h2>

    <% using (Html.BeginForm()) { %>
        <%= Html.ValidationSummary(true, "Login was unsuccessful. Please correct the errors and try again.", new{@class="ui-state-error-text"}) %>
        <div>
            <fieldset>

                <div class="display-label">
                    <%= Html.LabelFor(m => m.UserName) %>
                </div>
                <div class="display-field">
                    <%= Html.TextBoxFor(m => m.UserName) %>
                    <%= Html.ValidationMessageFor(m => m.UserName, "*", new { @class = "ui-state-error-text" })%>
                    <br />
                    <%= Html.ValidationMessageFor(m => m.Password, "Please enter your user name.", new { @class = "ui-state-error-text", style = "font-weight: bold; font-size: 0.9em;" })%>
                </div>
                <br class="clear" />
                
                <div class="display-label">
                    <%= Html.LabelFor(m => m.Password) %>
                </div>
                <div class="display-field">
                    <%= Html.PasswordFor(m => m.Password) %>
                    <%= Html.ValidationMessageFor(m => m.Password, "*", new {@class = "ui-state-error-text"}) %>
                    <br />
                    <%= Html.ValidationMessageFor(m => m.Password, "Please enter your password.", new { @class = "ui-state-error-text", style = "font-weight: bold; font-size: 0.9em;" })%>
                </div>
                <br class="clear" />
                
                <div class="action-buttons">
                    <input type="submit" value="Log In" />
                </p>
            </fieldset>
        </div>
    <% } %>
</asp:Content>
