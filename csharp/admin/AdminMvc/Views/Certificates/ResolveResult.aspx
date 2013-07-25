<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Health.Direct.Admin.Console.Models.CertificateModel>>" %>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Common"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Resolve Result
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Resolve Result</h2>

    <%= Html.Grid(Model)
        .Attributes(@class => "grid ui-widget ui-widget-content")
        .HeaderRowAttributes(new Dictionary<string, object> { { "class", "ui-widget-header" } })
        .Columns(
        column =>
            {
                column.For(c => c.Owner);
                column.For(c => Html.P(c.Thumbprint, new {@class = "thumbprint", title = c.Thumbprint}))
                    .Named("Thumbprint");
                column.For(c => c.Status).Attributes(@class => "status");
                column.For(c => Html.Span(Formatter.Format(c.CreateDate), new { title = c.CreateDate }))
                    .Named("Created On");
                column.For(c => Html.Span(Formatter.Format(c.ValidStartDate), new { title = c.ValidStartDate }))
                    .Named("Valid From");
                column.For(c => Html.Span(Formatter.Format(c.ValidEndDate), new { title = c.ValidEndDate }))
                    .Named("Valid Until");
                column.For(c => Html.ActionLink("View", "Details", new { c.ID }, new { @class = "view-details" }));
            })%>

    <%= Html.Partial("CertificateDetailsDialog") %>

    <script type="text/javascript" language="javascript">
        $(function() {
            $('a.view-details').click(function(event) {
                showDetailsDialog($('#certificate-dialog'), event, $(this), 'Certificate Details');
            });
        });
    </script>

</asp:Content>

