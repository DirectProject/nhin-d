<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<Health.Direct.Admin.Console.Models.MxRecordModel>" %>

<div id="dnsrecord-details">

    <span class="display-label"><%= Html.LabelFor(model => model.DomainName) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.DomainName) %>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Exchange) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Exchange)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.TTL) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.TTL)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Preference) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Preference)%>
    </span>
    <br class="clear" />
            
    <span class="display-label"><%= Html.LabelFor(model => model.Notes) %></span>
    <span class="display-field">
        <%= Html.DisplayTextFor(model => model.Notes)%>
    </span>
    <br class="clear" />
            
</div>