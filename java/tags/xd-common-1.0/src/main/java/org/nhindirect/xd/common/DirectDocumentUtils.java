package org.nhindirect.xd.common;

import java.util.ArrayList;
import java.util.List;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

import org.nhindirect.xd.common.type.SlotType1Enum;
import org.nhindirect.xd.transform.pojo.SimplePerson;

public class DirectDocumentUtils
{
    public static SlotType1 makeSlot(SlotType1Enum slotTypeEnum, SimplePerson person)
    {
        SlotType1 slot = new SlotType1();
        ValueListType values = new ValueListType();
        List<String> vals = values.getValue();

        slot.setName(slotTypeEnum.getName());
        slot.setValueList(values);

        // <rim:Value>PID-3|pid1^^^domain</rim:Value>
        StringBuffer pid3 = new StringBuffer("PID-3|");
        pid3.append(person.getLocalId() != null ? person.getLocalId() : "");
        pid3.append("^^^&");
        pid3.append(person.getLocalOrg() != null ? person.getLocalOrg() : "");
        pid3.append("&ISO");
        
        vals.add(pid3.toString());

        // <rim:Value>PID-5|Doe^John^Middle^^</rim:Value>
        StringBuffer pid5 = new StringBuffer("PID-5|");
        pid5.append(person.getLastName() != null ? person.getLastName() : "");
        pid5.append("^");
        pid5.append(person.getFirstName() != null ? person.getFirstName() : "");
        pid5.append("^");
        pid5.append(person.getMiddleName() != null ? person.getMiddleName() : "");
        pid5.append("^^");
        
        vals.add(pid5.toString());

        // <rim:Value>PID-7|19560527</rim:Value>
        StringBuffer pid7 = new StringBuffer("PID-7|");
        pid7.append(person.getBirthDateTime() != null ? person.getBirthDateTime() : "");

        vals.add(pid7.toString());
        
        // <rim:Value>PID-8|M</rim:Value>
        StringBuffer pid8 = new StringBuffer("PID-8|");
        pid8.append(person.getGenderCode() != null ? person.getGenderCode() : "");

        vals.add(pid8.toString());
        
        // <rim:Value>PID-11|100 Main St^^Metropolis^Il^44130^USA</rim:Value>
        StringBuffer pid11 = new StringBuffer("PID-11|");
        pid11.append(person.getStreetAddress1() != null ? person.getStreetAddress1() : "");
        pid11.append("^^");
        pid11.append(person.getCity() != null ? person.getCity() : "");
        pid11.append("^");
        pid11.append(person.getState() != null ? person.getState() : "");
        pid11.append("^");
        pid11.append(person.getZipCode() != null ? person.getZipCode() : "");
        pid11.append("^");
        pid11.append(person.getCountry() != null ? person.getCountry() : "");
        
        vals.add(pid11.toString());

        return slot;
    }
    
    public static SlotType1 makeSlot(SlotType1Enum slotTypeEnum, String value)
    {
        SlotType1 slot = new SlotType1();
        ValueListType valueListType = new ValueListType();
        List<String> vals = valueListType.getValue();

        slot.setName(slotTypeEnum.getName());
        slot.setValueList(valueListType);
        vals.add(value);

        return slot;
    }

    public static SlotType1 makeSlot(SlotType1Enum slotTypeEnum, List<String> values)
    {
        SlotType1 slot = new SlotType1();
        ValueListType valueListType = new ValueListType();
        List<String> vals = valueListType.getValue();

        slot.setName(slotTypeEnum.getName());
        slot.setValueList(valueListType);
        vals.addAll(values);

        return slot;
    }
    
    public static void addSlot(List<SlotType1> slots, SlotType1 slot)
    {
        if (slots == null)
            slots = new ArrayList<SlotType1>();

        if (slotNotEmpty(slot))
            slots.add(slot);
    }
    
    public static InternationalStringType makeInternationalStringType(String value)
    {
        InternationalStringType name = new InternationalStringType();
        List<LocalizedStringType> names = name.getLocalizedString();
        LocalizedStringType lname = new LocalizedStringType();
        lname.setValue(value);
        names.add(lname);

        return name;
    }

    public static boolean slotNotEmpty(SlotType1 slot)
    {
        if (slot != null && slot.getValueList() != null && slot.getValueList().getValue() != null
                && !slot.getValueList().getValue().isEmpty() && slot.getValueList().getValue().get(0) != null)
            return true;

        return false;
    }
}
