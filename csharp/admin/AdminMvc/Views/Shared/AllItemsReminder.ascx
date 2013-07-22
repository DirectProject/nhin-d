<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<string>" %>

<div class="flash ui-widget">
    <div class="ui-state-highlight">
        <span class="flash-text">Displaying <strong>all</strong> <%= Model %>. <em>Select a domain from the <%= Html.ActionLink("domains tab", "Index", "Domains") %> to <strong>filter</strong> <%= Model %>.</em></span>
        <a id="close-flash" title="Click to hide this text"><span class="ui-icon ui-icon-close"></span></a>
    </div>
</div>
