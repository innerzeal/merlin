package com.inmobi.qa.airavatqa.core;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.inmobi.qa.airavatqa.generated.cluster.Cluster;
import com.inmobi.qa.airavatqa.generated.feed.ActionType;
import com.inmobi.qa.airavatqa.generated.feed.Retention;
import com.inmobi.qa.airavatqa.generated.feed.Validity;
import org.apache.ivory.entity.v0.Frequency;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


/**
 * 
 * @author samarth.gupta
 *
 */


public class xmlUtil {

	public static Validity createValidity(String start, String end) throws Exception {
		Validity v = new Validity();
		v.setStart(instanceUtil.oozieDateToDate(start).toDate());
		v.setEnd(instanceUtil.oozieDateToDate(end).toDate());
		//v.setTimezone(timeZone);
		return v;
	}

	
	@Deprecated
	public static Validity createValidity(String start, String end, TimeZone timeZone) throws Exception {
		Validity v = new Validity();
		v.setStart(instanceUtil.oozieDateToDate(start).toDate());
		v.setEnd(instanceUtil.oozieDateToDate(end).toDate());
		//v.setTimezone(timeZone);
		return v;
	}
	
	

	
	
	public static Retention createRtention(String limit,ActionType action) {
		Retention r = new Retention();
		r.setLimit(new Frequency(limit));
		r.setAction(action);
		return r;
	}
	
	public static String marshalUnmarshalCLuster(String originalXml) throws JAXBException
	{
		JAXBContext jc=JAXBContext.newInstance(Cluster.class); 
		Unmarshaller u=jc.createUnmarshaller();
		Cluster c = (Cluster)u.unmarshal((new StringReader(originalXml)));
		
		
		   
		java.io.StringWriter sw = new StringWriter();
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(c,sw);
		
		c = (Cluster)u.unmarshal((new StringReader(sw.toString())));
		              
                
		com.thoughtworks.xstream.XStream xstream = new com.thoughtworks.xstream.XStream(new com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider(
	               new com.thoughtworks.xstream.converters.reflection.FieldDictionary(new com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter())),
	               new com.thoughtworks.xstream.io.xml.DomDriver("utf-8"));
	       
                
                
                String thisStr = xstream.toXML(c);
	       
		
		return thisStr;
                
                
	    

	}

	public static com.inmobi.qa.airavatqa.generated.process.Validity createProcessValidity(
			String startTime, String endTime) throws Exception{
		
		com.inmobi.qa.airavatqa.generated.process.Validity v = new com.inmobi.qa.airavatqa.generated.process.Validity();
		v.setEnd(instanceUtil.oozieDateToDate(endTime).toDate());
		v.setStart(instanceUtil.oozieDateToDate(startTime).toDate());
		return v;
		
	}
}
