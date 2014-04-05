<%@ Control Language="C#" Inherits="System.Web.Mvc.ViewUserControl" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>

<div id="dnsrecord-dialog" style="display: none;">
    <div id="dnsrecord-container"/>
</div>

<script type="text/javascript" language="javascript">
    function updateDialog(dialog, data) {
        $('#dnsrecord-container', dialog).html(data);
    }
</script>
