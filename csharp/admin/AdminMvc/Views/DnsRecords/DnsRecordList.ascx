<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<DnsRecordModel>>" %>
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
                column.For(d => d.ID).Visible(false);
                column.For(d => d.DomainName);
                column.For(d => d.TypeString).Named("Type");
                column.For(d => d.Notes);
                column.For(d => Html.Span(Formatter.Format(d.CreateDate), new { title = d.CreateDate })).Named("Created On");
                column.For(d => Html.Span(Formatter.Format(d.UpdateDate), new { @class = "update-date", title = d.UpdateDate })).Named("Updated On");
                column.For(d => Html.ActionLink("View", d.TypeString + "Details", new {id = d.ID}, new { @class = "view-details"}));
                column.For(d => Html.ActionLink("Edit", "Edit" + d.TypeString, new { id = d.ID }));
                column.For(d => Html.ActionLink("Delete", "Delete", new { id = d.ID }, new { @class = "toolbar-button delete-action" }));
            })%>
            
<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this domain?', 'Domain') });

        $('a.view-details').click(function (event) {
            var recordType = event.target.parentNode.parentNode.cells[1].innerHTML;
            showDetailsDialog($('#dnsrecord-dialog'), event, $(this), recordType + ' Record Details');
        });
    });
</script>
