<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<string>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>

<div class="flash ui-widget">
    <div class="ui-state-highlight">
        <span class="flash-text">Displaying '<%= ((DomainModel)ViewData["Domain"]).Name%>'</span>
        <a href="/<%= Model %>" title="Click to show all <%= Model %>"><span class="ui-icon ui-icon-close"></span></a>
    </div>
</div>
