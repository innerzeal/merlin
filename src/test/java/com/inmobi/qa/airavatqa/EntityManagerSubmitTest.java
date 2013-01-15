//package com.inmobi.qa.airavatqa;
//
//
//import com.inmobi.qa.airavatqa.core.APIResult;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//
//import java.lang.reflect.Method;
//
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.testng.Assert;
//import org.testng.log4testng.Logger;
//
///**
// *
// * @author samarth.gupta
// */
//
//public class EntityManagerSubmitTest {
//
//
//
//	IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//	IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//        Logger logger=Logger.getLogger(this.getClass());
//
//	//submit valid process from valid folder
//	@Test(groups={"0.1","0.2","submitValidProcess","sanity"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void submitValidProcess(Bundle bundle)
//	{
//
//		try {
//			logger.info("The testcase: submitValidProcess");
//
//			//submit valid bundle 
//			ServiceResponse response =  bundle.submitBundle();
//			Assert.assertEquals(Util.parseResponse(response).getStatus(), APIResult.Status.SUCCEEDED,"Submission was not successful");
//
//		}
//		catch(Exception e)
//		{
//			throw new TestNGException(e.getMessage());
//
//		}
//		finally {
//
//			try {
//				logger.info("deleting entity:");
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
//
//
//	//submit valid process by submit-delete-submit (one test case)
//	@Test(groups={"0.1","0.2","submitValidProcess"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void submitValidProcess_afterDeletion(Bundle bundle)
//	{
//
//
//		try {
//			logger.info("test case: submitValidProcess_afterDeletion");
//
//			//submit unique bundle 
//			ServiceResponse bundleResponse = bundle.submitBundle();
//			Assert.assertEquals(Util.parseResponse(bundleResponse).getStatus(),APIResult.Status.SUCCEEDED,"The submission did not go through :(");
//
//			ServiceResponse response = helperEntity.delete(URLS.DELETE_URL,bundle.getProcessData());
//			Util.assertSucceeded(response,"delete was not successful");
//
//			response = helperEntity.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData());
//			Util.assertSucceeded(response,"submission was not successful");
//
//		}
//		catch(Exception e)
//		{
//			throw new TestNGException(e.getMessage());
//
//		}
//		finally {
//
//			try {
//
//				logger.info("deleting entity:");
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
//	//submit valid process from valid folder
//	@Test(groups={"0.1","0.2","submitValidProcess"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void submitValidProcess_uppeCase(Bundle bundle)
//	{
//
//		try {
//			logger.info("The testcase: submitValidProcess_uppeCase");
//
//			//submit valid bundle 
//			ServiceResponse response =  bundle.submitBundle();
//			Assert.assertEquals(Util.parseResponse(response).getStatus(), APIResult.Status.SUCCEEDED,"Submission was not successful");
//
//			response = helperEntity.submitEntity(URLS.SUBMIT_URL,Util.setProcessName(bundle.getProcessData(),Util.getProcessName(bundle.getProcessData()).toUpperCase()));
//			logger.info("name in upper case: "+ Util.getProcessName(bundle.getProcessData()).toUpperCase());
//			Util.assertSucceeded(response,"submission was not successful");
//			response = helperEntity.delete(URLS.DELETE_URL,Util.setProcessName(bundle.getProcessData(),Util.getProcessName(bundle.getProcessData()).toUpperCase()));
//			Util.assertSucceeded(response,"delete was not successful");
//
//		}
//		catch(Exception e)
//		{
//			throw new TestNGException(e.getMessage());
//
//		}
//		finally {
//
//			try {
//				logger.info("deleting entity:");
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
//	//invalid submit by submitting same process again
//	@Test(groups={"0.1","0.2","invalidProcess"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void submitValidProcess_afterGet(Bundle bundle)
//	{
//		try {
//			logger.info("testcase: submitValidProcess_afterGet");
//
//
//			//submit entity
//			ServiceResponse response=bundle.submitBundle();
//			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED,"first submission did not go through :(");
//
//			//get entity
//			ServiceResponse getEntityResult=helperEntity.getEntityDefinition(URLS.GET_ENTITY_DEFINITION.getValue(), bundle.getProcessData());
//			//Assert.assertEquals(getEntityResult,data,"The entity submitted and the one returned looks mighty different!");
//
//			//submit again entity
//			response=helperEntity.submitEntity(URLS.SUBMIT_URL.getValue(),bundle.getProcessData());
//			Util.assertFailed(response,"submission was not successful");
//
//		}
//		catch(Exception e)
//		{
//			throw new TestNGException(e.getMessage());
//
//		}
//		finally {
//
//			try {
//
//				logger.info("deleting entity:");
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
//	@Test(groups={"0.1","0.2","invalidProcess"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void submitProcessTwice(Bundle bundle)
//	{
//		try {
//			logger.info("testcase: submitProcessTwice");
//
//
//			//submit entity
//			ServiceResponse response= bundle.submitBundle();
//			Util.assertSucceeded(response,"bundle submission was not successful");
//
//			//resubmit sameEntity
//			response=helperEntity.submitEntity(URLS.SUBMIT_URL.getValue(),bundle.getProcessData());
//			Util.assertFailed(response,"submission was success 2nd time also");
//
//			//helperEntity.validateResponse(submittedResult, expectedResult, filename);
//		}
//		catch(Exception e)
//		{
//			throw new TestNGException(e.getMessage());
//
//		}
//		finally {
//
//			try {
//				logger.info("deleting entity:");
//				bundle.deleteBundle();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//}
