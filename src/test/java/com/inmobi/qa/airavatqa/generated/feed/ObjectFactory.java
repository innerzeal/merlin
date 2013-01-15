//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.17 at 12:30:58 PM GMT+05:30 
//


package com.inmobi.qa.airavatqa.generated.feed;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apache.ivory.entity.v0.feed package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Feed_QNAME = new QName("uri:ivory:feed:0.1", "feed");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.ivory.entity.v0.feed
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link Partition }
     * 
     */
    public Partition createPartition() {
        return new Partition();
    }

    /**
     * Create an instance of {@link Cluster }
     * 
     */
    public Cluster createCluster() {
        return new Cluster();
    }

    /**
     * Create an instance of {@link LateArrival }
     * 
     */
    public LateArrival createLateArrival() {
        return new LateArrival();
    }

    /**
     * Create an instance of {@link Validity }
     * 
     */
    public Validity createValidity() {
        return new Validity();
    }

    /**
     * Create an instance of {@link ACL }
     * 
     */
    public ACL createACL() {
        return new ACL();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Feed }
     * 
     */
    public Feed createFeed() {
        return new Feed();
    }

    /**
     * Create an instance of {@link Schema }
     * 
     */
    public Schema createSchema() {
        return new Schema();
    }

    /**
     * Create an instance of {@link Retention }
     * 
     */
    public Retention createRetention() {
        return new Retention();
    }

    /**
     * Create an instance of {@link Clusters }
     * 
     */
    public Clusters createClusters() {
        return new Clusters();
    }

    /**
     * Create an instance of {@link Locations }
     * 
     */
    public Locations createLocations() {
        return new Locations();
    }

    /**
     * Create an instance of {@link Partitions }
     * 
     */
    public Partitions createPartitions() {
        return new Partitions();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Feed }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "uri:ivory:feed:0.1", name = "feed")
    public JAXBElement<Feed> createFeed(Feed value) {
        return new JAXBElement<Feed>(_Feed_QNAME, Feed.class, null, value);
    }

}
