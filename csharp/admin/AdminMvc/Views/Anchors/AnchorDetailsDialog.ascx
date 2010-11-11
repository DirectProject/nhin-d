<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>
<%@ Import Namespace="AdminMvc.Models"%>

<div id="anchor-dialog" style="display: none;">
    <div class="display-label">Owner</div>
    <div id="owner" class="display-field"></div>

    <div class="display-label">Thumbprint</div>
    <div id="thumbprint" class="display-field"></div>
    
    <div class="display-label">Status</div>
    <div id="status" class="display-field"></div>

    <div class="display-label">Created On</div>
    <div id="created" class="display-field"></div>

    <div class="display-label">Valid From</div>
    <div id="valid-start-date" class="display-field"></div>
    
    <div class="display-label">Valid Until</div>
    <div id="valid-end-date" class="display-field"></div>

    <div class="display-label">For Incoming</div>
    <div id="for-incoming" class="display-field"></div>

    <div class="display-label">For Outgoing</div>
    <div id="for-outgoing" class="display-field"></div>
</div>

<script type="text/javascript" language="javascript">
    var dateTimeFormatString = '<%= ViewData["DateTimeFormat"] %>';
    function updateDialog(dialog, data) {
        $('#owner', dialog).text(data.Owner);
        $('#thumbprint', dialog).text(data.Thumbprint);
        $('#status', dialog).text(data.Status);
        $('#created', dialog).text(data.CreateDate.parseJSONDate().format(dateTimeFormatString));
        $('#valid-start-date', dialog).text(data.ValidStartDate.parseJSONDate().format(dateTimeFormatString));
        $('#valid-end-date', dialog).text(data.ValidEndDate.parseJSONDate().format(dateTimeFormatString));
        $('#for-incoming', dialog).text(data.ForIncoming);
        $('#for-outgoing', dialog).text(data.ForOutgoing);
    }
</script>
