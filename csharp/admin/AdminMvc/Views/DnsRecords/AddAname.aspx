<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.AddressRecordModel>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add ANAME Record
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add ANAME Record</h2>

    <% using (Html.BeginForm("AddAname", "DnsRecords", FormMethod.Post)) {%>
        
        <%= Html.ValidationSummary(true) %>

        <fieldset>
            
            <span class="display-label"><%= Html.LabelFor(model => model.DomainName) %></span>
            <span class="display-field">
                <%= Html.TextBoxWithMaxLengthFor(model => model.DomainName) %>
                <%= Html.ValidationMessageFor(model => model.DomainName) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.IPAddress) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.IPAddress) %>
                <%= Html.ValidationMessageFor(model => model.IPAddress) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.TTL) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.TTL) %>
                <%= Html.ValidationMessageFor(model => model.TTL) %>
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

