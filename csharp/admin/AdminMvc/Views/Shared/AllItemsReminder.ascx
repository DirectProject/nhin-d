<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<string>" %>

<div class="ui-widget info-flash" style="margin-bottom: 1em; float: left;">
    <div class="ui-state-highlight" style="padding: .3em .7em;">
        <span style="float: left; padding-right: .5em;">Displaying <strong>all</strong> <%= Model %>. <em>Select a domain from the <%= Html.ActionLink("domain tab", "Index", "Domains") %> to add <%= Model %>.</em></span>
        <a id="close-flash" title="Click to hide this text"><span class="ui-icon ui-icon-close"></span></a>
    </div>
</div>

<br class="clear" />
