<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<Health.Direct.Admin.Console.Models.SoaRecordModel>" %>

<div id="dnsrecord-details">
    <span class="display-label"><%= Html.LabelFor(model => model.DomainName) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.DomainName) %>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.PrimarySourceDomain) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.PrimarySourceDomain)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.ResponsibleName) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.ResponsibleName)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.SerialNumber) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.SerialNumber)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Refresh) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Refresh)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Retry) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Retry)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Expire) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Expire)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Minimum) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Minimum)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Notes) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Notes)%>
    </span>
    <br class="clear" />
            
</div>