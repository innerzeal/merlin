//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.05 at 11:17:46 AM GMT+05:30 
//


package com.inmobi.qa.airavatqa.generated.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.ivory.entity.v0.Frequency;


/**
 * <p>Java class for retry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="retry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="policy" use="required" type="{uri:ivory:process:0.1}policy-type" />
 *       &lt;attribute name="delay" use="required" type="{uri:ivory:process:0.1}frequency-type" />
 *       &lt;attribute name="attempts" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *             &lt;minInclusive value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "retry")
public class Retry {

    @XmlAttribute(required = true)
    protected PolicyType policy;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Frequency delay;
    @XmlAttribute(required = true)
    protected int attempts;

    /**
     * Gets the value of the policy property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyType }
     *     
     */
    public PolicyType getPolicy() {
        return policy;
    }

    /**
     * Sets the value of the policy property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyType }
     *     
     */
    public void setPolicy(PolicyType value) {
        this.policy = value;
    }

    /**
     * Gets the value of the delay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Frequency getDelay() {
        return delay;
    }

    /**
     * Sets the value of the delay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelay(Frequency value) {
        this.delay = value;
    }

    /**
     * Gets the value of the attempts property.
     * 
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Sets the value of the attempts property.
     * 
     */
    public void setAttempts(int value) {
        this.attempts = value;
    }

}
