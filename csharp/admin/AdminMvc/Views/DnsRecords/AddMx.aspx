<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.MxRecordModel>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add MX Record
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add MX Record</h2>

    <% using (Html.BeginForm("AddMx", "DnsRecords", FormMethod.Post)) {%>
        
        <%= Html.ValidationSummary(true) %>

        <fieldset>
            
            <span class="display-label"><%= Html.LabelFor(model => model.DomainName) %></span>
            <span class="display-field">
                <%= Html.TextBoxWithMaxLengthFor(model => model.DomainName) %>
                <%= Html.ValidationMessageFor(model => model.DomainName) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Exchange) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Exchange) %>
                <%= Html.ValidationMessageFor(model => model.Exchange) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.TTL) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.TTL) %>
                <%= Html.ValidationMessageFor(model => model.TTL) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Preference) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Preference) %>
                <%= Html.ValidationMessageFor(model => model.Preference) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Notes) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Notes) %>
                <%= Html.ValidationMessageFor(model => model.Notes) %>
            </span>
            <br class="clear" />
            
            <div class="action-buttons">
                <input type="submit" value="Create" />
                <%= Html.ActionLink("Cancel", "Index") %>
            </div>
        </fieldset>

    <% } %>

</asp:Content>

