$(function() {
    $(':submit').button().click(function() { $(this).button("disable"); $('form').submit(); });
    $('a.action').button();
    $('div.ui-tabs #tab-' + controllerName).addClass('ui-tabs-selected ui-state-active');
    $('a.enable-disable-action').click(changeAndUpdateStatus);
    $('table.grid tbody tr').hover(
                function() { $(this).addClass('ui-state-highlight'); },
                function() { $(this).removeClass('ui-state-highlight'); }
                )
                .dblclick(function(event) {
                    event.preventDefault();
                    clearSelection();
                    $('a.view-details', this).click();
                });
    $('#close-flash').click(function() { $('div.flash').hide(); });

    $(".input-validation-error").addClass("ui-state-error");
    $(".field-validation-error").addClass("ui-state-error-text");
    $("span.ui-state-error-text:contains('*')").html('<span class="ui-icon ui-icon-alert"></span>');
});
function clearSelection() {
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    } else if (window.getSelection) {
        var sel = window.getSelection();
        sel.removeAllRanges();
    }
}
function confirmDelete(event, dialog, link, message, objectName) {
    event.preventDefault();
    dialog.html('<p>' + message + '</p>');
    dialog.dialog({
        title: 'Confirmation',
        resizable: false,
        width: 500,
        modal: true,
        buttons: {
            "Delete": function() {
                $.post(link[0].href, function(data) {
                    if (data == 'True') {
                        link.closest('tr').hide('fast');
                    } else if (data == 'NotFound') {
                        alert(objectName + ' was not found.');
                    } else {
                        alert('An error occurred - ' + data);
                    }
                });
                $(this).dialog("close");
            },
            "Cancel": function() { $(this).dialog("close"); }
        }
    });
}
function changeAndUpdateStatus(event) {
    event.preventDefault();
    var link = $(this);
    link.text(link.text().replace(/le$/, 'ling...')).attr('disabled', true).addClass('ui-state-disabled');
    $.post(link[0].href, function(data) {
        adjustStatusForRow(link, data);
    });
}
function adjustStatusForRow(link, data) {
    link.attr('disabled', false).removeClass('ui-state-disabled');
    var row = link.closest('tr');
    
    var status = data.Status;
    var oldActionString = status == 'Enabled' ? 'Enable' : 'Disable'
    var newActionString = status == 'Enabled' ? 'Disable' : 'Enable'
    $('td.status', row).text(status);

    if (data.UpdateDate) {
        var updatedOn = data.UpdateDate.parseJSONDate();
        $('td span.update-date', row).text(updatedOn.format('MM/dd/yyyy')).attr('title', updatedOn.format('MM/dd/yyyy HH:mm:ss'));
    }

    var actionCell = $('td a.enable-disable-action', row);
    actionCell.text(newActionString);
    var href = actionCell.attr('href');
    actionCell.attr('href', href.replace(oldActionString, newActionString));
}
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
function showDetailsDialog(dialog, event, link, title) {
    event.preventDefault();
    clearDialog();
    $.get(link.attr('href'), function(data) { updateAndShowDialog(dialog, data, title); });
}
function updateAndShowDialog(dialog, data, title) {
    updateDialog(dialog, data);
    dialog.dialog({
        title: title,
        modal: true,
        width: 550
    });
}
function clearDialog(dialog) {
    $('div.display-field', dialog).text('');
}
