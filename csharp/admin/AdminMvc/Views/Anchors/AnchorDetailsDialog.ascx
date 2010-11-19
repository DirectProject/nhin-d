<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>

<div id="anchor-dialog" style="display: none;">
    <span class="display-label">Owner</span>
    <span id="owner" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Thumbprint</span>
    <span id="thumbprint" class="display-field"></span>
    <br class="clear" />
    
    <span class="display-label">Status</span>
    <span id="status" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Created On</span>
    <span id="created" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Valid From</span>
    <span id="valid-start-date" class="display-field"></span>
    <br class="clear" />
    
    <span class="display-label">Valid Until</span>
    <span id="valid-end-date" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">For Incoming</span>
    <span id="for-incoming" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">For Outgoing</span>
    <span id="for-outgoing" class="display-field"></span>
    <br class="clear" />
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
