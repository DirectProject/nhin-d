package org.nhindirect.config.store;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "setting")
/**
 * The JPA settings class.  This tables holds various configuration settings such as how the configuration service should behave or settings
 * for a gateway.  This structure is made up of simple name value pairs. 
 */
public class Setting 
{
    private String name;
    private String value;
    private long id;
    private Calendar createTime;
    private Calendar updateTime;
    private EntityStatus status = EntityStatus.NEW;
    
    /**
     * Get the name of the setting.
     * 
     * @return the name of the setting.
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Set the name of the setting.
     * 
     * @param name
     *            The name of setting.
     */
    public void setName(String name) {
        this.name = name;
    }    
    
    /**
     * Get the value of the setting.
     * 
     * @return the value of the setting.
     */
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    /**
     * Set the name of the setting.
     * 
     * @param name
     *            The value of setting.
     */
    public void setValue(String value) {
        this.value = value;
    }      
    
    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Get the value of status.
     * 
     * @return the value of status.
     */
    @Enumerated
    public EntityStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status.
     * 
     * @param status
     *            The value of status.
     */
    public void setStatus(EntityStatus status) {
        this.status = status;
    }
    
    /**
     * Get the value of createTime.
     * 
     * @return the value of createTime.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreateTime() {
        return createTime;
    }

    /**
     * Set the value of createTime.
     * 
     * @param timestamp
     *            The value of createTime.
     */
    public void setCreateTime(Calendar timestamp) {
        createTime = timestamp;
    }   
    
    /**
     * Get the value of updateTime.
     * 
     * @return the value of updateTime.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getUpdateTime() {
        return updateTime;
    }

    /**
     * Set the value of updateTime.
     * 
     * @param timestamp
     *            The value of updateTime.
     */
    public void setUpdateTime(Calendar timestamp) {
        updateTime = timestamp;
    }     
}
