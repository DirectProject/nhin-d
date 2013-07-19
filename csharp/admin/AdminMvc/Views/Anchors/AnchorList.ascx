<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<AnchorModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>

<%= Html.Grid(Model)
        .Attributes(@class => "grid ui-widget ui-widget-content")
        .HeaderRowAttributes(new Dictionary<string, object> { { "class", "ui-widget-header" } })
        .Columns(
        column =>
            {
                column.For(c => c.Owner);
                column.For(c => Html.P(c.Thumbprint, new { title = c.Thumbprint, @class = "thumbprint" }))
                    .Named("Thumbprint");
                column.For(a => a.Status).Attributes(@class => "status");
                column.For(a => Html.Span(Formatter.Format(a.CreateDate), new { title = a.CreateDate })).Named("Created On");
                column.For(a => Html.Span(Formatter.Format(a.ValidStartDate), new { title = a.ValidStartDate })).Named("Valid From");
                column.For(a => Html.Span(Formatter.Format(a.ValidEndDate), new { title = a.ValidEndDate })).Named("Valid Until");
                column.For(a => a.Purpose);
                column.For(d => Html.ActionLink("View", "Details", new { d.ID }, new { @class = "view-details" }));
                column.For(d => d.IsEnabled
                                    ? Html.ActionLink("Disable", "Disable", new { d.ID }, new { @class = "enable-disable-action" })
                                    : Html.ActionLink("Enable", "Enable", new { d.ID }, new { @class = "enable-disable-action" }));
                column.For(d => Html.ActionLink("Delete", "Delete", new { d.ID }, new { @class = "toolbar-button delete-action" }));
            })%>

<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this anchor?', 'Anchor') });

        $('a.view-details').click(function(event) {
            showDetailsDialog($('#anchor-dialog'), event, $(this), 'Anchor Details');
        });
    });
</script>
