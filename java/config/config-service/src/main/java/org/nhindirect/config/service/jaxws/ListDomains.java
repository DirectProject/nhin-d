
package org.nhindirect.config.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ListDomains", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListDomains", namespace = "http://nhind.org/config", propOrder = {
    "names",
    "maxCount"
})
public class ListDomains {

    @XmlElement(name = "names", namespace = "")
    private String names;
    @XmlElement(name = "maxCount", namespace = "")
    private int maxCount;

    /**
     * 
     * @return
     *     returns String
     */
    public String getNames() {
        return this.names;
    }

    /**
     * 
     * @param names
     *     the value for the names property
     */
    public void setNames(String names) {
        this.names = names;
    }

    /**
     * 
     * @return
     *     returns int
     */
    public int getMaxCount() {
        return this.maxCount;
    }

    /**
     * 
     * @param maxCount
     *     the value for the maxCount property
     */
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

}
