//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.17 at 12:30:58 PM GMT+05:30 
//


package com.inmobi.qa.airavatqa.generated.feed;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.inmobi.qa.airavatqa.core.SchemaHelper;

public class Adapter1
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (SchemaHelper.parseDateUTC(value));
    }

    public String marshal(Date value) {
        return (SchemaHelper.formatDateUTC(value));
    }

}