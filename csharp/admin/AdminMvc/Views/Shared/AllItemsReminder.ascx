<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<string>" %>

<div class="ui-widget info-flash" style="margin-bottom: 1em; float: left;">
    <div class="ui-state-highlight" style="padding: .3em .7em;">
        <span style="float: left; padding-right: .5em;">Displaying <strong>all</strong> <%= Model %>. <em>Select a domain from the <%= Html.ActionLink("domains tab", "Index", "Domains") %> to <strong>filter</strong> <%= Model %>.</em></span>
        <a id="close-flash" title="Click to hide this text"><span class="ui-icon ui-icon-close"></span></a>
    </div>
</div>

<br class="clear" />
