//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.25 at 03:25:52 PM GMT+05:30 
//


package com.inmobi.qa.airavatqa.generated.dataset;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{uri:ivory:dataset:0.1}Discovery"/>
 *         &lt;element ref="{uri:ivory:dataset:0.1}Source"/>
 *         &lt;element ref="{uri:ivory:dataset:0.1}Target" maxOccurs="unbounded"/>
 *         &lt;element ref="{uri:ivory:dataset:0.1}Load" minOccurs="0"/>
 *         &lt;element ref="{uri:ivory:dataset:0.1}Transform" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "discovery",
    "source",
    "target",
    "load",
    "transform"
})
@XmlRootElement(name = "Import")
public class Import {

    @XmlElement(name = "Discovery", required = true)
    protected Discovery discovery;
    @XmlElement(name = "Source", required = true)
    protected Source source;
    @XmlElement(name = "Target", required = true)
    protected List<Target> target;
    @XmlElement(name = "Load")
    protected Load load;
    @XmlElement(name = "Transform")
    protected Transform transform;

    /**
     * Gets the value of the discovery property.
     * 
     * @return
     *     possible object is
     *     {@link Discovery }
     *     
     */
    public Discovery getDiscovery() {
        return discovery;
    }

    /**
     * Sets the value of the discovery property.
     * 
     * @param value
     *     allowed object is
     *     {@link Discovery }
     *     
     */
    public void setDiscovery(Discovery value) {
        this.discovery = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Source }
     *     
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Source }
     *     
     */
    public void setSource(Source value) {
        this.source = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the target property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Target }
     * 
     * 
     */
    public List<Target> getTarget() {
        if (target == null) {
            target = new ArrayList<Target>();
        }
        return this.target;
    }

    /**
     * Gets the value of the load property.
     * 
     * @return
     *     possible object is
     *     {@link Load }
     *     
     */
    public Load getLoad() {
        return load;
    }

    /**
     * Sets the value of the load property.
     * 
     * @param value
     *     allowed object is
     *     {@link Load }
     *     
     */
    public void setLoad(Load value) {
        this.load = value;
    }

    /**
     * Gets the value of the transform property.
     * 
     * @return
     *     possible object is
     *     {@link Transform }
     *     
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Sets the value of the transform property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transform }
     *     
     */
    public void setTransform(Transform value) {
        this.transform = value;
    }

}