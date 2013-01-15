//package com.inmobi.qa.airavatqa;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//
//import org.testng.Assert;
//import org.testng.TestNGException;
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
//
/////home/rishu/oozie-3.0.2/bin/oozie jobs -oozie http://10.14.110.46:11000/oozie -jobtype coordinator -localtime -filter status=RUNNING;name=aggregator-coord12
////testng test for kill Api 
//public class EntityManagerKillTest {
//        
//        Logger logger=Logger.getLogger(this.getClass());
//        static Logger staticLogger=Logger.getLogger(EntityManagerKillTest.class);
//        
//	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="DP", dataProviderClass = Bundle.class)
//	public void nonExistentDelete(Bundle bundle) throws Exception {
//
//		try{
//			//first lets just get the store and archive data for process before submit 
//			ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//
//			//delete an non Existence Process
//			ServiceResponse getDeleteResult=processHelper.delete(URLS.DELETE_URL, Util.generateUniqueProcessEntity(bundle.getProcessData()));
//			
//			Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.FAILED,"delete does not seem to be successful...");
//
//			//files after invalid submit
//			ArrayList<String> processStoreAfterBundle=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreAfterBundle=Util.getArchiveStoreInfo();
//
//			processStoreAfterBundle.removeAll(processStoreInitialState);
//			Assert.assertEquals(processStoreAfterBundle.size(),0,"size of PROCESS store should not have changed");
//			archiveStoreAfterBundle.removeAll(archiveStoreInitialState);
//			Assert.assertEquals(archiveStoreAfterBundle.size(),0,"size of ARCHIVE store should have inclreased only by 0");
//
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//	}
//
//	@Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass = Bundle.class)
//	public void scheduleAndDelete(Bundle bundle) throws Exception {
//
//		try{
//
//			scheduleAndDelete_Helper(bundle);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//		finally {
//
//			logger.info("deleting entity:");
//			bundle.deleteBundle();
//
//		} 
//	}
//	
//	@Test(groups={"0.1","0.2"},dataProvider="DP", dataProviderClass = Bundle.class)
//	public void deleteAlreadyDeleted(Bundle bundle) throws Exception {
//
//		try{
//
//			scheduleAndDelete_Helper(bundle);
//			ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//
//			//delete an non Existence Process
//			ServiceResponse getDeleteResult=processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.FAILED,"delete does not seem to be successful...");
//
//			//files after invalid submit
//			ArrayList<String> processStoreAfterBundle=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreAfterBundle=Util.getArchiveStoreInfo();
//
//			processStoreAfterBundle.removeAll(processStoreInitialState);
//			Assert.assertEquals(processStoreAfterBundle.size(),0,"size of PROCESS store should not have changed");
//			archiveStoreAfterBundle.removeAll(archiveStoreInitialState);
//			Assert.assertEquals(archiveStoreAfterBundle.size(),0,"size of ARCHIVE store should have inclreased only by 0");
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//		finally{
//			logger.info("deleting entity:");
//			bundle.deleteBundle();
//		}
//	}
//	
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="DP", dataProviderClass = Bundle.class)
//	public void suspendAndDelete(Bundle bundle) throws Exception {
//
//		try{
//
//			ArrayList<String> oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//			logger.info("oozieInitail status size: "+oozieStatus.size());
//			for(int i = 0 ; i < oozieStatus.size();i++)
//				logger.info("job status from oozie: "+oozieStatus.get(i));
//			
//			bundle.submitAndScheduleBundle();
//
//			
//			oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//			logger.info("oozie AfterSchedule size: "+oozieStatus.size());
//			for(int i = 0 ; i < oozieStatus.size();i++)
//				logger.info("job status from oozie: "+oozieStatus.get(i));
//			//now lets try to suspend the damn thing
//			ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//
//			Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.SUCCEEDED,"Suspension did not go through :(");
//		
//			oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"SUSPENDED");
//			logger.info("oozie AfterSuspension: "+oozieStatus.size());
//			for(int i = 0 ; i < oozieStatus.size();i++)
//				logger.info("job status from oozie: "+oozieStatus.get(i));
//			
//			
//			ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//			
//			//delete an non Existence Process
//			ServiceResponse getDeleteResult=processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//			Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.SUCCEEDED,"delete does not seem to be successful...");
//			
//			oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"SUSPENDED");
//			logger.info("oozieInitail status size: "+oozieStatus.size());
//			for(int i = 0 ; i < oozieStatus.size();i++)
//				logger.info("job status from oozie: "+oozieStatus.get(i));
//		
//			ArrayList<String> processStoreAfterBundle=Util.getProcessStoreInfo();
//			ArrayList<String> archiveStoreAfterBundle=Util.getArchiveStoreInfo();
//			
//			processStoreInitialState.removeAll(processStoreAfterBundle);
//			Assert.assertEquals(processStoreInitialState.size(),1,"size of PROCESS store should have inclreased only by 1");
//			archiveStoreAfterBundle.removeAll(archiveStoreInitialState);
//			Assert.assertEquals(archiveStoreAfterBundle.size(),1,"size of ARCHIVE store should have inclreased only by 1");
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getMessage());
//		}
//		finally{
//			logger.info("deleting entity:");
//			bundle.deleteBundle();
//		}
//	} 
//
//	public static void scheduleAndDelete_Helper(Bundle bundle) throws Exception
//	{
//		
//		IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//		
//		ArrayList<String> oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//		staticLogger.info("oozieInitail status size: "+oozieStatus.size());
//		for(int i = 0 ; i < oozieStatus.size();i++)
//			staticLogger.info("job status from oozie: "+oozieStatus.get(i));
//
//		//first lets just get the store and archive data for process before submit 
//		ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//		ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//
//		bundle.submitAndScheduleBundle();
//		oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//		staticLogger.info("oozie status size: "+oozieStatus.size());
//		for(int i = 0 ; i < oozieStatus.size();i++)
//			staticLogger.info("job status from oozie: "+oozieStatus.get(i));
//
//		//files after submit
//		ArrayList<String> processStoreAfterBundle=Util.getProcessStoreInfo();
//		ArrayList<String> archiveStoreAfterBundle=Util.getArchiveStoreInfo();
//
//		ArrayList<String> processStoreAfterBundleCopy= new ArrayList<String>(processStoreAfterBundle);
//		ArrayList<String> archiveStoreAfterBundleCopy= new ArrayList<String>(archiveStoreAfterBundle);
//
//		processStoreAfterBundle.removeAll(processStoreInitialState);
//		Assert.assertEquals(processStoreAfterBundle.size(),1,"size of PROCESS store should have inclreased only by 1");
//		archiveStoreAfterBundle.removeAll(archiveStoreInitialState);
//		Assert.assertEquals(archiveStoreAfterBundle.size(),0,"size of ARCHIVE store should have inclreased only by 0");
//
//		processHelper.delete(URLS.DELETE_URL, bundle.getProcessData());
//
//		oozieStatus= Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()));
//		staticLogger.info("oozie status size: "+oozieStatus.size());
//		for(int i = 0 ; i < oozieStatus.size();i++)
//			staticLogger.info("job status from oozie: "+oozieStatus.get(i));
//
//		//files after delete
//		ArrayList<String> processStoreAfterDelete=Util.getProcessStoreInfo();
//		ArrayList<String> archiveStoreAfterDelete=Util.getArchiveStoreInfo();
//
//		processStoreAfterBundleCopy.removeAll(processStoreAfterDelete);
//		Assert.assertEquals(processStoreAfterBundleCopy.size(),1,"size of PROCESS store should have inclreased only by 1");
//		archiveStoreAfterDelete.removeAll(archiveStoreAfterBundleCopy);
//		Assert.assertEquals(archiveStoreAfterDelete.size(),1,"size of ARCHIVE store should have inclreased only by 1");
//
//	}
//
//}
