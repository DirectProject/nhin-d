<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>

<div id="domain-dialog" style="display: none;">
    <div class="display-label">Name</div>
    <div id="domain-name" class="display-field"></div>

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
        clearDialog($('#domain-dialog'));
        $.getJSON(link.attr('href'), function(data) { showDialog($('#domain-dialog'), data); });
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
        $('#created', dialog).text(data.CreateDate.parseJSONDate().format(dateTimeFormatString));
        $('#updated', dialog).text(data.UpdateDate.parseJSONDate().format(dateTimeFormatString));
    }
</script>
