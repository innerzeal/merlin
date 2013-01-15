///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.core.APIResult;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.custommonkey.xmlunit.XMLAssert;
//import org.custommonkey.xmlunit.XMLUnit;
//import org.testng.log4testng.Logger;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class EntityManagerGetTest {
//
//	Logger logger=Logger.getLogger(this.getClass());
//	final String GET_ENTITY_DEFINITION=Util.readPropertiesFile("ivory_hostname")+"/api/entities/definition";
//	final String DELETE_URL=Util.readPropertiesFile("ivory_hostname")+"/api/entities/delete";
//
//	IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//
//	@Test(groups={"0.1","0.2","submitValidData","sanity"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void getSubmittedProcessFile(Bundle bundle)
//	{
//
//		try {
//			logger.info("testcase: getSubmittedProcessFile");
//
//			ServiceResponse response = bundle.submitBundle();
//			Util.assertSucceeded(response,"problem submitting bndle");
//
//
//			//lets get the entity
//			response =helperEntity.getEntityDefinition(GET_ENTITY_DEFINITION, bundle.getProcessData());
//			
//			logger.info("response xml is: "+response.getMessage());
//			logger.info("bundle xml is: "+bundle.getProcessData());
//			XMLUnit.setIgnoreWhitespace(true);
//			XMLAssert.assertXMLEqual("comparing response xml to bundle porcess xml failed",response.getMessage(),bundle.getProcessData());
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//		finally {
//
//			try {
//
//				logger.info("deleting bundle");
//				bundle.deleteBundle();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Test(groups={"0.1","0.2","getDeletedProcessFile"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void getDeletedProcessFile(Bundle bundle)
//	{
//
//		
//
//		try {
//			logger.info("testcase: getDeletedProcessFile");
//
//			//lets submit an entity
//			ServiceResponse response = bundle.submitBundle();
//			Util.assertSucceeded(response);
//
//			//lets get the entity
//			response =helperEntity.getEntityDefinition(GET_ENTITY_DEFINITION, bundle.getProcessData());
//			XMLUnit.setIgnoreWhitespace(true);
//			XMLAssert.assertXMLEqual("comparing response xml to bundle porcess xml failed",response.getMessage(),bundle.getProcessData());
//
//
//			//lets delete the entity
//			response =helperEntity.delete(DELETE_URL, bundle.getProcessData());
//			Util.assertSucceeded(response,"porcess not deleted");
//
//			//lets try getting the file again
//			response =helperEntity.getEntityDefinition(GET_ENTITY_DEFINITION, bundle.getProcessData());
//			Util.assertFailed(response,"get should have failed");
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//		finally {
//
//			try {
//
//				logger.info("deleting bundle");
//				bundle.deleteBundle();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//    @Test(groups={"0.1","0.2","getInvalidProcessFile"},dataProvider="DP", dataProviderClass = Bundle.class )
//    public void getInvalidProcessFile(Bundle bundle)
//    {
//
//        IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//
//        try {
//            logger.info("testcase: getInvalidProcessFile");
//
//            
//            bundle.generateUniqueBundle();
//            ServiceResponse response =helperEntity.getEntityDefinition(GET_ENTITY_DEFINITION, bundle.getProcessData());
//			Util.assertFailed(response,"get should have failed");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//
//    }
//
//}
