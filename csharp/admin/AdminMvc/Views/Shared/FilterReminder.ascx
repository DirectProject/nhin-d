<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<string>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>

<div class="ui-widget" style="margin-bottom: 1em; float: left;">
    <div class="ui-state-highlight" style="padding: .3em .7em;">
        <span style="float: left; padding-right: .5em;">Displaying '<%= ((DomainModel)ViewData["Domain"]).Name%>'</span>
        <a href="/<%= Model %>" title="Click to show all <%= Model %>"><span class="ui-icon ui-icon-close"></span></a>
    </div>
</div>
