package com.inmobi.qa.airavatqa.prism;

import java.io.StringBufferInputStream;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ColoHelper;
import com.inmobi.qa.airavatqa.core.PrismHelper;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.core.xmlUtil;

public class PrismGetTestCase {

	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
	public PrismGetTestCase() throws Exception {
		
	}
	PrismHelper prismHelper=new PrismHelper("prism.properties");

	ColoHelper UA1coloHelper=new ColoHelper("mk-qa.config.properties");

	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");

	
	
	@Test
	public void submitAll_getAllViaPrismAndColo() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());
			
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusterData());
		
			
			
			r = prismHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
		
			String submittedCluster = b.getClusterData();
			String returnedCluster = r.getMessage();
			Util.print("from api   : "+returnedCluster);
			Util.print("submitted  : "+submittedCluster);
					
			submittedCluster = xmlUtil.marshalUnmarshalCLuster(b.getClusterData());
			returnedCluster = xmlUtil.marshalUnmarshalCLuster(r.getMessage());
			
			Util.print("from api   : "+returnedCluster);
			Util.print("submitted  : "+submittedCluster);

			Diff diff = new Diff(returnedCluster, submittedCluster);
			boolean isSimilar = diff.similar();
			Util.print("test print");
			
			
		/*	
			XMLUnit.setIgnoreWhitespace(true);
			XMLUnit.setIgnoreAttributeOrder(true);
			XMLUnit.setIgnoreComments(true);
			XMLUnit.setNormalize(true);
			
			String s = xmlUtil.marshalUnmarshalCLuster(b.getClusterData());
			String a = xmlUtil.marshalUnmarshalCLuster(r.getMessage());
			
			Util.print("from api   : "+a);
			Util.print("submitted  : "+s);*/

			

			//Diff d  = 	XMLUnit.compareXML(a,s);
			
			
			//XMLAssert.assertXMLEqual("comparing response xml to bundle porcess xml failed",a,s);
			

		//	r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
		//	r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
		//	r = prismHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
		//	r = UA1coloHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
		//	r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
		//	r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
		//	r = UA1coloHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
			
		}

		finally{
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}
	
	
	@Test
	public void submitAll_nonSubmitPrismColo() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());
			
		
			
			
			ServiceResponse r = prismHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
		

			r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
			r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
			r = prismHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
			r = UA1coloHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
			r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
			r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
			r = UA1coloHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
			
		}

		finally{
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}
	
	
	
	@Test
	public void submitAll_getPARTIAL() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());
			
			Util.shutDownService(UA1coloHelper.getClusterHelper());

			ServiceResponse r = prismHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
		

			r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
			r = prismHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
			r = prismHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
			r = UA1coloHelper.getClusterHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION,b.getClusterData());
			r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(0));
			r = UA1coloHelper.getFeedHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getDataSets().get(1));
			r = UA1coloHelper.getProcessHelper().getEntityDefinition(URLS.GET_ENTITY_DEFINITION, b.getProcessData());
			
			
		}

		finally{
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}
	
	
	
	public void compareXMLs(String source,String dest) throws Exception
	   {
	       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	       dbf.setNamespaceAware(true);
	       dbf.setCoalescing(true);
	       dbf.setIgnoringElementContentWhitespace(true);
	       dbf.setIgnoringComments(true);
	       DocumentBuilder db = dbf.newDocumentBuilder();

	       Document doc1 = db.parse(new StringBufferInputStream(source));
	       doc1.normalizeDocument();

	       Document doc2 = db.parse(new StringBufferInputStream(dest));
	       doc2.normalizeDocument();

	       Assert.assertTrue(doc1.isEqualNode(doc2));
	   }
	
}
