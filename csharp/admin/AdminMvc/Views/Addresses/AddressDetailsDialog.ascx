<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>

<div id="address-dialog" style="display: none;">
    <span class="display-label">Domain ID</span>
    <span id="domain-id" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Email Address</span>
    <span id="email-address" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Display Name</span>
    <span id="display-name" class="display-field"></span>
    <br class="clear" />

    <span class="display-label">Type</span>
    <span id="type" class="display-field"></span>
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
        $('#domain-id', dialog).text(data.DomainID);
        $('#email-address', dialog).text(data.EmailAddress);
        $('#display-name', dialog).text(data.DisplayName);
        $('#type', dialog).text(data.Type);
        $('#status', dialog).text(data.Status);
        $('#created', dialog).text(data.CreateDate.parseJSONDate().format(dateTimeFormatString));
        $('#updated', dialog).text(data.UpdateDate.parseJSONDate().format(dateTimeFormatString));
    }
</script>
