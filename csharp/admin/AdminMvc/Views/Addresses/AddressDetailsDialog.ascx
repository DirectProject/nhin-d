<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>

<div id="address-dialog" style="display: none;">
    <div class="display-label">Domain ID</div>
    <div id="domain-id" class="display-field"></div>

    <div class="display-label">Email Address</div>
    <div id="email-address" class="display-field"></div>

    <div class="display-label">Display Name</div>
    <div id="display-name" class="display-field"></div>

    <div class="display-label">Type</div>
    <div id="type" class="display-field"></div>

    <div class="display-label">Status</div>
    <div id="status" class="display-field"></div>

    <div class="display-label">Created</div>
    <div id="created" class="display-field"></div>

    <div class="display-label">Updated</div>
    <div id="updated" class="display-field"></div>
</div>

<script type="text/javascript" language="javascript">
    var dateTimeFormatString = '<%= ViewData["DateTimeFormat"] %>';
    function showDetailsDialog(event, link) {
        event.preventDefault();
        clearDialog($('#address-dialog'));
        $.get(link.attr('href'), function(data) { showDialog($('#address-dialog'), data); });
    }
    function showDialog(dialog, data) {
        updateDialog(dialog, data);
        dialog.dialog({
            title: 'Address Details',
            modal: true,
            width: 500
        });
    }
    function clearDialog(dialog) {
        $('div.display-field', dialog).text('');
    }
    function updateDialog(dialog, data) {
        $('#domain-id', dialog).text(data.DomainID);
        $('#email-address', dialog).text(data.EmailAddress);
        $('#display-name', dialog).text(data.DisplayName);
        $('#type', dialog).text(data.Type);
        $('#status', dialog).text(data.Status);
        $('#created', dialog).text(data.CreateDate.parseJSONDate().format(dateTimeFormatString));
        $('#updated', dialog).text(data.UpdateDate.parseJSONDate().format(dateTimeFormatString));
    }
</script>
