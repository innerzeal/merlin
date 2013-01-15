//package com.inmobi.qa.airavatqa;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.inmobi.qa.airavatqa.core.APIResult;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import org.testng.log4testng.Logger;
//
//
///**
// * 
// * @author samarth.gupta
// *
// */
//
//public class EntityManagerResumeTest {
//	
//	
//        Logger logger=Logger.getLogger(this.getClass());
//        
//        @BeforeMethod
//	public void testName(Method method)
//	{
//		Util.print("test name: "+method.getName());
//	}
//	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//
//
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void resumeValidSuspended(Bundle bundle) {
//
//		try{
//			bundle.submitAndScheduleBundle();
//			processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData());
//			
//			ArrayList<String> oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//			logger.info("oozie AfterSchedule size: "+oozieStatus.size());
//			for(int i = 0 ; i < oozieStatus.size();i++)
//				logger.info("job status from oozie: "+oozieStatus.get(i));
//			//now lets try to suspend the damn thing
//			ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//
//
//			Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.SUCCEEDED,"Suspension did not go through :(");
//
//
//			oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"SUSPENDED");
//			logger.info("oozie AfterSuspension: "+oozieStatus.size());
//			Assert.assertTrue(oozieStatus.get(0).contains("SUSPENDED"));
//
//
//			ServiceResponse resumeResponse=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//			
//			Assert.assertEquals(Util.parseResponse(resumeResponse).getStatus(),APIResult.Status.SUCCEEDED,"Suspension did not go through :(");
//			Assert.assertEquals(resumeResponse.getCode(),200,"Suspension did not go through :(");
//
//
//			//oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//                        
//                        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//			//logger.info("oozie AfterSuspension: "+oozieStatus.size());
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//
//		finally
//		{
//			try{processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());}catch(Exception e){}
//		}
//	}
//
//	
//	@Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void nonExistentResume(Bundle bundle)
//	{
//		try{
//			ServiceResponse getResumeResult=processHelper.resume(URLS.RESUME_URL, Util.generateUniqueProcessEntity(bundle.getProcessData()));
//			Assert.assertEquals(Util.parseResponse(getResumeResult).getStatus(),APIResult.Status.FAILED,"resume does not seem to be successful...");
//			Assert.assertEquals(getResumeResult.getCode(),400,"resume does not seem to be successful...");
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//
//		finally
//		{
//			try {
//				processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//	
//	@Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass = Bundle.class )
//	public void resumeAfterDelete(Bundle bundle)
//	{
//		try{
//			
//			
//			EntityManagerKillTest.scheduleAndDelete_Helper(bundle);
//			
//			ServiceResponse resumeResponse=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//			Assert.assertEquals(Util.parseResponse(resumeResponse).getStatus(),APIResult.Status.FAILED,"resume does not seem to be successful...");
//			Assert.assertEquals(resumeResponse.getCode(),400,"resume does not seem to be successful...");
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//
//		finally
//		{
//			try {
//				processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass=Bundle.class)
//        public void resumeRunningProcess(Bundle bundle)
//        {
//            try {
//                
//                  bundle.generateUniqueBundle();
//                  bundle.submitAndScheduleBundle();
//                  
//                  //now call resume on this
//                  ServiceResponse response=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//                  Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//                  Assert.assertNotNull(Util.parseResponse(response).getMessage());
//                  
//                  Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//                
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//            
//            finally
//		{
//			try {
//				//processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//        }
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass=Bundle.class)
//        public void resumeSubmittedProcess(Bundle bundle)
//        {
//            try {
//                
//                  bundle.generateUniqueBundle();
//                  bundle.submitBundle();
//                  
//                  //now call resume on this
//                  ServiceResponse response=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//                  Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//                  Assert.assertNotNull(Util.parseResponse(response).getMessage());
//                
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//            
//            finally
//		{
//			try {
//				//processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//        }     
//	
//	
//	
//}
