<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>

<div id="domain-dialog" style="display: none;">
    <span class="display-label">Name</span>
    <span id="domain-name" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Status</span>
    <span id="status" class="display-field"></span>
    <br class="clear" />
    
    <span class="display-label">Created</span>
    <span id="created" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Updated</span>
    <span id="updated" class="display-field"></span>
    <br class="clear" />
</div>

<script type="text/javascript" language="javascript">
    var dateTimeFormatString = '<%= ViewData["DateTimeFormat"] %>';
    function updateDialog(dialog, data) {
        $('#domain-name', dialog).text(data.Name);
        $('#status', dialog).text(data.Status);
        $('#created', dialog).text(data.CreateDate.parseJSONDate().format(dateTimeFormatString));
        $('#updated', dialog).text(data.UpdateDate.parseJSONDate().format(dateTimeFormatString));
    }
</script>
