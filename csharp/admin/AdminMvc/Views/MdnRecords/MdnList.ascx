<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl<IEnumerable<Health.Direct.Admin.Console.Models.MdnModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Common" %>
<%@ Import Namespace="MvcContrib.Pagination" %>
<%@ Import Namespace="MvcContrib.UI.Grid" %>
<%@ Import Namespace="MvcContrib.UI.Pager" %>

<%= Html.Grid(Model)
    .Attributes(@class => "grid ui-widget ui-widget-content")
    .HeaderRowAttributes(new Dictionary<string, object> { { "class", "ui-widget-header" } })
    .Columns(
        column =>
        {
            column.For(d => d.Id).Attributes(style => "display: none;").HeaderAttributes(style => "display: none;");
            column.For(d => d.MdnIdentifier);
            column.For(d => d.MessageId);
            column.For(d => d.SubjectValue).Named("Subject");
            column.For(d => d.Sender);
            column.For(d => d.Recipient);
            column.For(d => d.Status).Attributes(@class => "status");
            column.For(d => d.NotifyDispatched);
            column.For(d => d.Timedout);
            column.For(d => d.MdnProcessedDate).Named("Processed On");
            column.For(d => d.CreateDate).Named("Created On");
            column.For(d => d.UpdateDate).Named("Updated On");
        })%>
            
<%= Html.Pager((IPagination)Model) %>