<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<DomainModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>

<%= Html.Grid(Model)
    .Attributes(@class => "grid ui-widget ui-widget-content")
    .HeaderRowAttributes(new Dictionary<string, object> { { "class", "ui-widget-header" } })
    .Columns(
        column =>
        {
            column.For(d => d.ID).Attributes(style => "display: none;").HeaderAttributes(style => "display: none;");
            column.For(d => d.Name);
            column.For(d => d.Status).Attributes(@class => "status");
            column.For(d => Html.Span(Formatter.Format(d.CreateDate), new { title = d.CreateDate })).Named("Created On");
            column.For(d => Html.Span(Formatter.Format(d.UpdateDate), new { @class = "update-date", title = d.UpdateDate })).Named("Updated On");
            
            column.For(d => Html.ActionLink("View", "Details", new {id = d.ID}, new { @class = "view-details"}));
            column.For(d => Html.ActionLink("Addresses", "Addresses", new { id = d.ID }));
            column.For(d => Html.ActionLink("Anchors", "Anchors", new { id = d.ID }));
            column.For(d => Html.ActionLink("Certificates", "Certificates", new { id = d.ID }));
            column.For(d => d.IsEnabled
                                ? Html.ActionLink("Disable", "Disable", new {id = d.ID}, new {@class = "enable-disable-action"})
                                : Html.ActionLink("Enable", "Enable", new { id = d.ID }, new { @class = "enable-disable-action" }));
            column.For(d => Html.ActionLink("Delete", "Delete", new { id = d.ID }, new { @class = "toolbar-button delete-action" }));
        })%>
            
<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this domain?', 'Domain') });

        $('a.view-details').click(function(event) {
            showDetailsDialog($('#domain-dialog'), event, $(this), 'Domain Details');
        });
    });
</script>
