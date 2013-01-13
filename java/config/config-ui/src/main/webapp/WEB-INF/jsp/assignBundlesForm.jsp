<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<script type="text/javascript">
function selectBundles() {

    var bundleHTML = "";

    var bundleArray = $(':checkbox[name=bundlesSelected]:checked').map(function () { 
            var bundle = this.value.split('|');  
            bundleHTML += bundle[1]+"<br/>";
            return bundle[0];
    }).get();
    
    $('#selectedBundles').val(bundleArray);

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


</script>

<div style="float:right"><a class="modal_close" href="#">Close</a></div>
<br clear="both"/>
<h3>Assign Trust Bundles</h3>

<div>
    <table style="width:100%" class="fancyTable">
    
        <thead>
            <tr>
                <th><input type="checkbox" id="selectAllCheckbox" onclick="selectAll();"/></th>
                <th>Bundle Name</th>
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

                <td width=10><input name="bundlesSelected" type="checkbox" value="${trustBundle.id}|${trustBundle.bundleName}"/></td>
                <td>${trustBundle.bundleName}<br/>

                </td>                                                     
            </tr>
        </c:forEach>
                    

        
    </table>
</div>
<div>
    <button name="submitType" id="submitType" type="button" value="assignBundles" onclick="selectBundles();">Assign to Domain</button>
</div>


