
package org.nhindirect.config.service.jaxws;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Setting;

@XmlRootElement(name = "getAllSettingsResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllSettingsResponse", namespace = "http://nhind.org/config")
public class GetAllSettingsResponse {

    @XmlElement(name = "return", namespace = "")
    private Collection<Setting> _return;

    /**
     * 
     * @return
     *     returns Collection<Setting>
     */
    public Collection<Setting> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(Collection<Setting> _return) {
        this._return = _return;
    }

}
