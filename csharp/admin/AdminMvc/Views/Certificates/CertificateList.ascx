<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<CertificateModel>>" %>
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
                column.For(c => Html.P(c.Thumbprint, new {title = c.Thumbprint, @class = "thumbprint"}))
                    .Named("Thumbprint");
                column.For(c => c.Status).Attributes(@class => "status");
                column.For(c => Html.Span(Formatter.Format(c.CreateDate), new {title = c.CreateDate}))
                    .Named("Created On");
                column.For(c => Html.Span(Formatter.Format(c.ValidStartDate), new {title = c.ValidStartDate}))
                    .Named("Valid From");
                column.For(c => Html.Span(Formatter.Format(c.ValidEndDate), new {title = c.ValidEndDate}))
                    .Named("Valid Until");
                column.For(c => Html.ActionLink("View", "Details", new {c.ID}, new {@class = "view-details"}));
                column.For(c => c.IsEnabled
                                    ? Html.ActionLink("Disable", "Disable", new {c.ID},
                                                      new {@class = "enable-disable-action"})
                                    : Html.ActionLink("Enable", "Enable", new {c.ID},
                                                      new {@class = "enable-disable-action"}));

                column.For(
                    c => Html.ActionLink("Delete", "Delete", new {c.ID}, new {@class = "toolbar-button delete-action"}));
            })%>

<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this certificate?', 'Certificate') });

        $('a.view-details').click(function(event) {
            showDetailsDialog($('#certificate-dialog'), event, $(this), 'Certificate Details');
        });
    });
</script>
