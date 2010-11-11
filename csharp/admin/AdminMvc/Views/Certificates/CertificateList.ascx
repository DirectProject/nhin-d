<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<CertificateModel>>" %>
<%@ Import Namespace="AdminMvc.Common"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="AdminMvc.Models"%>

<%= Html.Grid(Model)
        .Attributes(new Dictionary<string, object> { { "class", "grid ui-widget ui-widget-content" } })
        .HeaderRowAttributes(new Dictionary<string, object> { { "class", "ui-widget-header" } })
        .Columns(
        column =>
            {
                column.For(a => a.Owner).Visible(false);
                column.For(a => a.Thumbprint);
                column.For(a => a.Status).Attributes(@class => "status");
                column.For(a => Html.Span(Formatter.Format(a.CreateDate), new { title = a.CreateDate.ToString() })).Named("Created On");
                column.For(a => Html.Span(Formatter.Format(a.ValidStartDate), new { title = a.ValidStartDate.ToString() })).Named("Valid From");
                column.For(a => Html.Span(Formatter.Format(a.ValidEndDate), new { title = a.ValidEndDate.ToString() })).Named("Valid Until");
                column.For(d => Html.ActionLink("View", "Details", new { d.Owner, d.Thumbprint }, new { @class = "view-details" }));
                column.For(d => d.IsEnabled
                                    ? Html.ActionLink("Disable", "Disable", new { d.Owner, d.Thumbprint }, new { @class = "enable-disable-action" })
                                    : Html.ActionLink("Enable", "Enable", new { d.Owner, d.Thumbprint }, new { @class = "enable-disable-action" }));
                column.For(d => Html.ActionLink("Delete", "Delete", new { d.Owner, d.Thumbprint }, new { @class = "toolbar-button delete-action" }));
            })%>

<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this certificate?', 'Certificate') });

        $('a.enable-disable-action').click(enableDisableDomain);
        $('a.view-details').click(function(event) {
            showDetailsDialog(event, $(this));
        });
    });
</script>
