<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<AddressModel>>" %>
<%@ Import Namespace="AdminMvc.Common"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="AdminMvc.Models"%>

<%= Html.Grid(Model)
    .Attributes(new Dictionary<string, object>{{"class", "grid ui-widget ui-widget-content"}})
    .HeaderRowAttributes(new Dictionary<string, object>{{"class", "ui-widget-header"}})
    .Columns(
        column =>
            {
                column.For(a => a.DomainID).Visible(ViewData["Domain"] == null).Named("Domain ID");
                column.For(a => a.EmailAddress);
                column.For(a => a.DisplayName);
                column.For(a => a.Status).Attributes(@class => "status");
                column.For(d => Html.Span(Formatter.Format(d.CreateDate), new { title = d.CreateDate.ToString() })).Named("Created On");
                column.For(d => Html.Span(Formatter.Format(d.UpdateDate), new { title = d.UpdateDate.ToString() })).Named("Updated On");
                column.For(a => Html.ActionLink("View", "Details", new { id = a.ID }, new { @class = "view-details" }));
                column.For(a => a.IsEnabled
                                    ? Html.ActionLink("Disable", "Disable", new { id = a.ID }, new { @class = "enable-disable-action" })
                                    : Html.ActionLink("Enable", "Enable", new { id = a.ID }, new { @class = "enable-disable-action" }));
                column.For(d => Html.ActionLink("Delete", "Delete", new { id = d.ID }, new { @class = "toolbar-button delete-action" }));
            })%>

<%= Html.Pager((IPagination)Model) %>

<div id="confirm-dialog" style="display: none;"></div>

<script type="text/javascript" language="javascript">
    $(function() {
        $('a.delete-action')
            .button({ icons: { primary: "ui-icon-trash" }, text: false })
            .click(function(event) { confirmDelete(event, $('#confirm-dialog'), $(this), 'Are you sure want to delete this address?', 'Address') });

        $('a.enable-disable-action').click(enableDisableDomain);
        $('a.view-details').click(function(event) {
            showDetailsDialog(event, $(this));
        });
    });
</script>
