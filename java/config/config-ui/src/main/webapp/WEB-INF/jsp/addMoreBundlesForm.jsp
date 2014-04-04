<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>



<script type="text/javascript">



function selectBundlesToAdd() {
    
    var checkBoxes = $(':checkbox[name=bundlesToAssociate]'); 
            
    if($('#bundlesToAssociate').attr('checked') ) {    
        checkBoxes.attr('checked', true);            
    } else {
        checkBoxes.attr('checked', false);
    }
}



function updateDomainBundles() {

    var bundleHTML = "";

    var selectedBundles = $(':checkbox[name=bundlesToAssociate]:checked').map(function () { 
            var bundle = this.value.split('|');  
            bundleHTML += bundle[1]+"<br/>";
            return bundle[0];
    }).get();


    var directionFlags = $(':checkbox[name=new_incoming]:checked,:checkbox[name=new_outgoing]:checked').map(function() { return $(this).attr('id'); });
    
    var bundleArrayString = "";
    
    

    for(var i=0; i<selectedBundles.length; i++) {

        var direction = '';

        
        

        //$('#incoming_'+selectedBundles[i]).attr('checked',false);

        if( $('#new_incoming_'+selectedBundles[i]).attr('checked') == true && $('#new_outgoing_'+selectedBundles[i]).attr('checked') == true ) {
            direction = 'both';
        } else if ( $('#new_incoming_'+selectedBundles[i]).attr('checked') ) {
            direction = 'in';
        } else if ( $('#new_outgoing_'+selectedBundles[i]).attr('checked') ) {
            direction = 'out';
        } 

        bundleArrayString += selectedBundles[i]+"_"+direction + ":";

    }

    bundleArrayString = bundleArrayString.substr(0,bundleArrayString.length-1);

    if(bundleArrayString == "") {
        $('#noBundlesError').show().fadeOut(5000);
    } else {
        
        

         
         $.post("/config-ui/config/domain/addBundle", { domainId: <c:out value="${domainId}"/>, bundles: bundleArrayString}, function() {
            window.location.reload();

         });       
        
        

    }
    
    
}


</script>

<div style="float:right"><a class="modal_close" href="#tab3" onclick="$('#lean_overlay').fadeOut(200);$('#addMoreBundles').css({'display':'none'});">Close</a></div>
<br clear="both"/>
<h3>Select Trust Bundles</h3>

<c:choose>
<c:when test="${empty trustBundles}">
There are no trust bundles available to select.
</c:when>
<c:otherwise>
    
    <div style="color:red;display:none;font-style:italic;margin:5px 0;" id="noBundlesError">Select some bundles.</div>

    <div>
        <table style="width:100%" class="fancyTable">

            <thead>
                <tr>
                    <th><input type="checkbox" id="bundlesToAssociate" onclick="selectBundlesToAdd();"/></th>
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

                    <td width=10><input name="bundlesToAssociate" type="checkbox" value="${trustBundle.id}|${trustBundle.bundleName}"/></td>
                    <td>${trustBundle.bundleName}</td>                                                     
                    <td><input name="new_incoming" id="new_incoming_${trustBundle.id}" type="checkbox" checked /></td>
                    <td><input name="new_outgoing" id="new_outgoing_${trustBundle.id}" type="checkbox" checked /></td>
                </tr>
            </c:forEach>



        </table>
    </div>
    <div>
        <button name="submitType" id="submitType" type="button" value="assignBundles" onclick="updateDomainBundles();">Assign to Domain</button>
    </div>

</c:otherwise>
</c:choose>


