<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Admin.Console.Models.SoaRecordModel>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add SOA Record
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add SOA Record</h2>

    <% using (Html.BeginForm("AddSoa", "DnsRecords", FormMethod.Post)) {%>
        
        <%= Html.ValidationSummary(true) %>

        <fieldset>
            
            <span class="display-label"><%= Html.LabelFor(model => model.DomainName) %></span>
            <span class="display-field">
                <%= Html.TextBoxWithMaxLengthFor(model => model.DomainName) %>
                <%= Html.ValidationMessageFor(model => model.DomainName) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.PrimarySourceDomain) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.PrimarySourceDomain) %>
                <%= Html.ValidationMessageFor(model => model.PrimarySourceDomain) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.ResponsibleName) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.ResponsibleName) %>
                <%= Html.ValidationMessageFor(model => model.ResponsibleName) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.SerialNumber) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.SerialNumber) %>
                <%= Html.ValidationMessageFor(model => model.SerialNumber) %>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Refresh) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Refresh) %>
                <%= Html.ValidationMessageFor(model => model.Refresh)%>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Retry) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Retry)%>
                <%= Html.ValidationMessageFor(model => model.Retry)%>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Expire) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Expire)%>
                <%= Html.ValidationMessageFor(model => model.Expire)%>
            </span>
            <br class="clear" />
            
            <span class="display-label"><%= Html.LabelFor(model => model.Minimum) %></span>
            <span class="display-field">
                <%= Html.TextBoxFor(model => model.Minimum)%>
                <%= Html.ValidationMessageFor(model => model.Minimum)%>
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

