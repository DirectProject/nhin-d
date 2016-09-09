<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<script type="text/javascript">

function selectBundles() {

    var bundleHTML = "";

    var selectedBundles = $(':checkbox[name=bundlesSelected]:checked').map(function () { 
            var bundle = this.value.split('|');  
            bundleHTML += bundle[1]+"<br/>";
            return bundle[0];
    }).get();


    var directionFlags = $(':checkbox[name=incoming]:checked,:checkbox[name=outgoing]:checked').map(function() { return $(this).attr('id'); });
    
    var bundleArrayString = "";
    
    for(var i=0; i<selectedBundles.length; i++) {

        var direction = '';

        if( $('#incoming_'+selectedBundles[i]).attr('checked') && $('#outgoing_'+selectedBundles[i]).attr('checked') ) {
            direction = 'both';
        } else if ( $('#incoming_'+selectedBundles[i]).attr('checked') ) {
            direction = 'in';
        } else if ( $('#outgoing_'+selectedBundles[i]).attr('checked') ) {
            direction = 'out';
        } 

        bundleArrayString += selectedBundles[i]+"||||"+direction + ",";

    }

    // Set form input to the json array
    $('#selectedBundles').val(bundleArrayString);

    // Update display on page of which bundles are selected
    $('#bundlesList').html(bundleHTML);

    // Fade out overlay
    $('#lean_overlay').fadeOut(200);

    // Hide overlay
    $('#assignBundles').css({'display':'none'})
}

function selectAll() {
    var checkBoxes = $(':checkbox[name=bundlesSelected]'); 
        
    if($('#selectAllCheckbox').attr('checked')) {    
        checkBoxes.attr('checked', 'checked');            
    } else {
        checkBoxes.attr('checked', '');
    }
}



function updateDomainBundles() {

    

    
}


</script>

<div style="float:right"><a class="modal_close" href="#">Close</a></div>
<br clear="both"/>
<h3>Select Trust Bundles</h3>

<div>
    <table style="width:100%" class="fancyTable">
    
        <thead>
            <tr>
                <th><input type="checkbox" id="selectAllCheckbox" onclick="selectAll();"/></th>
                <th>Bundle Name</th>
                <th width=20>In</th>
                <th width=20>Out</th>
            </tr>
        </thead>
    
        <c:forEach var="trustBundle" items="${trustBundles}" varStatus="rowCounter">
            <c:choose>
                <c:when test="${rowCounter.count % 2 == 0}">
                    <tr class="evenRow">
                </c:when>
                <c:otherwise>
                    <tr class="oddRow">
                </c:otherwise>
            </c:choose>
                <c:set var="unencodedBundle" value="${trustBundle.bundleName}"/>
			    <c:set var="encodedBundle" value="${fn:replace(unencodedBundle,' ', 'wHiTeSpAcE')}"/>
                <td width=10><input name="bundlesSelected" type="checkbox" value="${encodedBundle}|${trustBundle.bundleName}"/></td>
                <td>${trustBundle.bundleName}</td>                                                     
                <td><input name="incoming" id="incoming_${encodedBundle}" type="checkbox" checked=""/></td>
                <td><input name="outgoing" id="outgoing_${encodedBundle}" type="checkbox" checked=""/></td>
            </tr>
        </c:forEach>
                    

        
    </table>
</div>
<div>
    <button name="submitType" id="submitType" type="button" value="assignBundles" onclick="selectBundles();">Assign to Domain</button>
</div>


