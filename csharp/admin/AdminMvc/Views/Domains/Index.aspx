<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Domain>>" %>
<%@ Import Namespace="Health.Direct.Config.Store"%>
<%@ Import Namespace="AdminMvc.Controllers"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="MvcContrib.UI.Pager"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Domains
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Domains</h2>

    <%= Html.ActionLink("Add Domain", "Add", null, new { @class = "action ui-priority-primary"})%>
    <br />
    <br />

    <%= Html.Grid(Model)
        .HeaderRowAttributes(new Dictionary<string, object>{{"class", "ui-widget-header"}})
        .Columns(
            column =>
                {
                    column.For(d => d.Name);
                    column.For(d => d.Status).Attributes(@class => "domain-status");
                    column.For(d => d.CreateDate);
                    column.For(d => d.UpdateDate);
                    column.For(d => Html.ActionLink("View", "Details", new {id = d.ID}, new { @class = "view-details"}));
                    column.For(d => Html.ActionLink("Addresses", "Addresses", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Anchors", "Anchors", new { id = d.ID }));
                    column.For(d => Html.ActionLink("Certificates", "Certificates", new { id = d.ID }));
                    column.For(d => d.Status == EntityStatus.Enabled
                                        ? Html.ActionLink("Disable", "Disable", new {id = d.ID}, new {@class = "enable-disable-domain"})
                                        : Html.ActionLink("Enable", "Enable", new { id = d.ID }, new { @class = "enable-disable-domain" }));
                    column.For(d => Html.ActionLink("Delete", "Delete", new { id = d.ID }, new { @class = "delete-domain" }));
                })%>
    <%= Html.Pager((IPagination)Model) %>

    <div id="domain-dialog" style="display: none;">
        <div class="display-label">Domain Name</div>
        <div id="domain-name" class="display-field"></div>

        <div class="display-label">Status</div>
        <div id="status" class="display-field"></div>
        
        <div class="display-label">Created</div>
        <div id="created" class="display-field"></div>

        <div class="display-label">Updated</div>
        <div id="updated" class="display-field"></div>
    </div>

    <div id="confirm-dialog" style="display: none;">
        Are you sure want to delete this domain?
    </div>

    <script type="text/javascript" language="javascript">
        $(function() {
            $('a.delete-domain')
                .button({ icons: { primary: "ui-icon-trash" }, text: false })
                .click(confirmDelete);

            $('a.enable-disable-domain').click(enableDisableDomain);

            $('a.view-details').click(function(event) {
                event.preventDefault();
                clearDialog($('#domain-dialog'));
                $.get($(this).attr('href'), function(data) { showDialog($('#domain-dialog'), data); });
            });
        });
        function confirmDelete(event) {
            var deleteLink = $(this);
            $('#confirm-dialog').dialog({
                title: 'Confirmation',
                resizable: false,
                modal: true,
                buttons: {
                    "Delete": function() {
                        $.post(deleteLink[0].href, function(data) {
                            if (data == '<%= Boolean.TrueString %>') {
                                deleteLink.closest('tr').hide('fast');
                            } else if (data == 'NotFound') {
                                alert('Domain was not found.');
                            } else {
                                alert('An error occurred - ' + data);
                            }
                        });
                        $(this).dialog("close");
                    },
                    "Cancel": function() { $(this).dialog("close"); }
                }
            });
            return false;
        }
        function enableDisableDomain(event) {
            event.preventDefault();
            var link = $(this);
            $.post(link[0].href, function(data) {
                adjustStatusForRow(link.closest('tr'), data);
            });
        }
        function adjustStatusForRow(row, data) {
            var status = data.Status;
            var oldActionString = status == 'Enabled'? 'Enable' : 'Disable'
            var newActionString = status == 'Enabled'? 'Disable' : 'Enable'
            
            $('td.domain-status', row).text(status);
            
            var actionCell = $('td a.enable-disable-domain', row);
            actionCell.text(newActionString);
            var href = actionCell.attr('href');
            actionCell.attr('href', href.replace(oldActionString, newActionString));
        }
        function endsWith(str, suffix) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        }
        function showDialog(dialog, data) {
            updateDialog(dialog, data);
            dialog.dialog({
                title: 'Domain Details',
                modal: true,
                width: 500
            });
        }
        function clearDialog(dialog) {
            $('div.display-field', dialog).text('');
        }
        function updateDialog(dialog, data) {
            $('#domain-name', dialog).text(data.Name);
            $('#status', dialog).text(data.Status);
            $('#created', dialog).text(data.CreateDate);
            $('#updated', dialog).text(data.UpdateDate);
        }
    </script>

</asp:Content>
