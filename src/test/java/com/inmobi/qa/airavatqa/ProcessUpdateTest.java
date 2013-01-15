//package com.inmobi.qa.airavatqa;
//
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.oozie.client.CoordinatorAction;
//import org.apache.oozie.client.CoordinatorJob;
//import org.apache.oozie.client.WorkflowAction;
//import org.apache.oozie.client.XOozieClient;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import org.apache.oozie.client.Job.Status;
//import com.inmobi.qa.airavatqa.coordinatorCore.CoordinatorHelper;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.DataEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ProcessEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.generated.coordinator.CONFIGURATION;
//import com.inmobi.qa.airavatqa.generated.coordinator.COORDINATORAPP;
//
//public class ProcessUpdateTest {
//
//
//	@BeforeMethod
//	public void testName(Method method)
//	{
//		Util.print("test name: "+method.getName());
//	}
//	ProcessEntityHelperImpl processHelper=new ProcessEntityHelperImpl();
//	final String GET_ENTITY_DEFINITION="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/definition";
//	ServiceResponse response;
//	static XOozieClient oozieClient=new XOozieClient("http://10.14.110.46:11000/oozie");
//	DataEntityHelperImpl feedHelper=new DataEntityHelperImpl();
//
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateOnly01(Bundle b) throws Exception
//	{
//		try
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			
//			b.generateUniqueBundle();
//			b.setProcessConcurrency(2);
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(100,0,1,prefix);
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-6);
//			String endTime = instanceUtil.getTimeWrtSystemTime(2);
//			b.setProcessPeriodicity(FrequencyType.MINUTES,1);
//			b.setOutputFeedPeriodicity("minutes","1");
//			b.setProcessValidity(START_TIME, endTime);
//			b.submitBundle();
//			b.setProcessConcurrency(5);
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(20000);
//			b.submitAndScheduleBundle();
//			Thread.sleep(120000);
//			Assert.assertEquals(instanceUtil.getInstanceCountWithStatus(Util.readEntityName(b.getProcessData()),CoordinatorAction.Status.RUNNING,"PROCESS" ), 5);
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//	}
//	
//	
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateOnly02(Bundle b) throws Exception
//	{
//		try
//		{
//		
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//		
//			b.generateUniqueBundle();
//			b.setProcessConcurrency(2);
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(100,0,1,prefix);
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-6);
//			String endTime = instanceUtil.getTimeWrtSystemTime(2);
//			b.setProcessPeriodicity(FrequencyType.MINUTES,1);
//			b.setOutputFeedPeriodicity("minutes","1");
//			b.setProcessValidity(START_TIME, endTime);
//			b.submitBundle();
//			b.setProcessConcurrency(5);
//			b.setProcessWorkflow("/examples/apps/aggregator1");
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(10000);
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			String wfpath = instanceUtil.getProcessWorkFlowPath(Util.readEntityName(b.getProcessData()),"PROCESS");
//			Assert.assertEquals(wfpath,"/examples/apps/aggregator1");
//			CoordinatorHelper.compareCoordinators(instanceUtil.getLatestCoordinatorApp(Util.readEntityName(b.getProcessData()),"PROCESS") ,CoordinatorHelper.createCoordinator(b));
//			//Assert.assertEquals(instanceUtil.getLatestWorkflowPath(Util.readEntityName(b.getProcessData())), "/examples/apps/aggregator1");
//			//Assert.assertEquals(instanceUtil.getInstanceCountWithStatus(Util.readEntityName(b.getProcessData()),CoordinatorAction.Status.RUNNING ), 5);
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new TestNGException(e.getCause());
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//	}
//
//
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateConcurrencyAndWorkflowSuspended(Bundle b) throws Exception
//	{
//		try
//
//		{
//		
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			final int ORIGINAL_CONCURRENCY = 2;
//			final int FINAL_CONCURRENCY = 10;
//
//			b.generateUniqueBundle();
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-6);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(12);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Thread.sleep(90000);
//			instanceUtil.validateNumberOfInstanceWithStatus(instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS"),CoordinatorAction.Status.RUNNING,2);
//			b.setProcessConcurrency(FINAL_CONCURRENCY);
//			b.setProcessWorkflow("/examples/apps/aggregator1");
//
//			//suspend process
//			processHelper.suspend(URLS.SUSPEND_URL,b.getProcessData());
//			Thread.sleep(20000);
//			Assert.assertEquals(instanceUtil.getDefaultCoordinatorStatus(Util.getProcessName(b.getProcessData()), 0),Status.SUSPENDED);
//
//			processHelper.updateProcess(Util.getProcessName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(150000);
//			Assert.assertEquals(instanceUtil.getCommonInstancesInBundles(instanceUtil.getSequenceBundleID(Util.getProcessName(b.getProcessData()), "PROCESS",0), instanceUtil.getSequenceBundleID(Util.getProcessName(b.getProcessData()), "PROCESS",1)).size(),0);
//
//			//Integrating Instance management
//			//rerun succeeded instance from old bundle 
//			String r= processHelper.processInstanceRerunCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//
//			Thread.sleep(15000);
//			r = processHelper.getProcessInstanceStatusCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//
//			//kill instance via cli
//			r = processHelper.processInstanceKillCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//
//			//rerun killed instance
//			r= processHelper.processInstanceRerunCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//			//Thread.sleep(15000);
//
//			//suspend instance
//			r= processHelper.processInstanceSuspendCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//			//Thread.sleep(15000);
//
//			//resume instance
//			r= processHelper.processInstanceResumeCLI(Util.readEntityName(b.getProcessData()),START_TIME,null);
//			Util.print("CLI output: "+r);
//			//Thread.sleep(15000);
//
//
//			COORDINATORAPP coordObj = instanceUtil.getLatestCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS");
//			String wfpath = instanceUtil.getProcessWorkFlowPath(Util.readEntityName(b.getProcessData()),"PROCESS");
//			Assert.assertEquals(wfpath,"/examples/apps/aggregator1","workflow path was not /examples/apps/aggregator1");
//			Assert.assertEquals(Integer.parseInt(coordObj.getControls().getConcurrency()),FINAL_CONCURRENCY,"concurrency did not icrease to 10");
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_Execution(Bundle b) throws Exception
//	{
//		try
//
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//		
//			final int ORIGINAL_CONCURRENCY = 2;			
//			b.generateUniqueBundle();
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-1440);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(2880);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			//			instanceUtil.validateNumberOfInstanceWithStatus(instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS"),CoordinatorAction.Status.RUNNING,2);
//			b.setProcessExecution(ExecutionType.LIFO);
//			processHelper.updateProcess(Util.getProcessName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(30000);
//
//			COORDINATORAPP coordObj = instanceUtil.getLatestCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS");
//
//			Assert.assertEquals(coordObj.getControls().getExecution(),"LIFO");
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//	
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_addInput(Bundle b) throws Exception
//	{
//		System.setProperty("java.security.krb5.realm", "");
//		System.setProperty("java.security.krb5.kdc", "");
//	
//		b.generateUniqueBundle();
//		String newFeedName = Util.getInputFeedNameFromBundle(b)+"2";
//		String inputFeed = Util.getInputFeedFromBundle(b);
//		try
//
//		{
//			final int ORIGINAL_CONCURRENCY = 2;			
//
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(300,0,1,prefix);
//
//
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-20);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(30);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle(false);
//			Thread.sleep(30000);
//			b.verifyDependencyListing();
//			b.addProcessInput(newFeedName,"inputData2");
//			inputFeed = Util.setFeedName(inputFeed,newFeedName);
//			feedHelper.submitEntity(URLS.SUBMIT_URL,inputFeed);
//			processHelper.updateProcess(Util.getProcessName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(30000);
//			//b.verifyDependencyListing();
//
//		}
//		finally{
//			processHelper.delete(URLS.DELETE_URL,b.getProcessData());
//			feedHelper.delete(URLS.DELETE_URL, inputFeed);
//			b.deleteBundle();
//		}
//	}
//
//	
//	
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateConcurrencyAndWorkflowSucceded(Bundle b) throws Exception
//	{
//		try
//
//		{
//			System.setProperty("java.security.krb5.realm", "");
//		    System.setProperty("java.security.krb5.kdc", "");
//		       
//			final int ORIGINAL_CONCURRENCY = 2;
//			final int FINAL_CONCURRENCY = 10;
//
//			b.generateUniqueBundle();
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(140,0,1,prefix);
//
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-3);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(5);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Util.waitTillCertainPercentageOfProcessIsOver(instanceUtil.getSequenceBundleID(Util.getProcessName(b.getProcessData()), "PROCESS", 0), 50, CoordinatorAction.Status.RUNNING);
//			//instanceUtil.validateNumberOfInstanceWithStatus(instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS"),CoordinatorAction.Status.RUNNING,2);
//			b.setProcessConcurrency(FINAL_CONCURRENCY);
//			b.setProcessWorkflow("/examples/apps/aggregator1");
//
//			for(int i = 0 ; i < 15 ; i++)
//			{
//				org.apache.oozie.client.Job.Status s = instanceUtil.getDefaultCoordinatorStatus(Util.getProcessName(b.getProcessData()), 0);
//				if(s.equals(org.apache.oozie.client.Job.Status.SUCCEEDED))
//					break;
//
//				Thread.sleep(60000);
//
//			}
//
//			ServiceResponse r  = processHelper.updateProcess(Util.getProcessName(b.getProcessData()),b.getProcessData());
//
//			Assert.assertTrue(r.getMessage().contains("is past current time. Entity can't be updated. Use remove and add"));
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateConcurrencyOnly(Bundle b) throws Exception
//	{
//		try
//		{
//		
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			
//			final int ORIGINAL_CONCURRENCY = 2;
//			final int FINAL_CONCURRENCY = 10;
//			b.generateUniqueBundle();
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(500,0,1,prefix);
//
//			b.setProcessValidity(instanceUtil.getTimeWrtSystemTime(-200),instanceUtil.getTimeWrtSystemTime(2880));			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Thread.sleep(120000);
//			instanceUtil.validateNumberOfInstanceWithStatus(instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS"),CoordinatorAction.Status.RUNNING,2);
//			b.setProcessConcurrency(FINAL_CONCURRENCY);
//			String newProcess = b.getProcessData();
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),newProcess);
//			Thread.sleep(60000);
//			for(int koshish = 0 ; koshish < 10 ; koshish ++)
//			{
//				int succeededInstances = instanceUtil.getInstanceCountWithStatus(Util.getProcessName(b.getProcessData()),CoordinatorAction.Status.RUNNING,"PROCESS");
//				if(succeededInstances > ORIGINAL_CONCURRENCY){
//					Thread.sleep(30000);
//					break ;
//				}
//				else 
//					Thread.sleep(30000);
//			}
//
//			Assert.assertEquals(instanceUtil.getInstanceCountWithStatus(Util.getProcessName(b.getProcessData()),CoordinatorAction.Status.RUNNING,"PROCESS"),FINAL_CONCURRENCY);
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_increaseValidity(Bundle b) throws Exception
//	{
//		try
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//		
//			b.generateUniqueBundle();
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(300,0,1,prefix);
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-3);
//			String endTime = instanceUtil.getTimeWrtSystemTime(5);
//			Util.print("original EndTime:"+endTime);
//			b.setProcessValidity(START_TIME,endTime);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(10);
//			b.submitAndScheduleBundle();
//			Thread.sleep(90000);
//			COORDINATORAPP coordObj = instanceUtil.getLatestCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS");
//			int originalNumberOfInstances=instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS").size();
//			endTime = instanceUtil.addMinsToTime(endTime,11);
//			b.setProcessValidity(START_TIME,endTime);
//			Util.print("new EndTime:"+endTime);
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),b.getProcessData());
//			for(int wait = 0 ; wait <45 ; wait++)
//			{
//				if(instanceUtil.getDefaultCoordinatorStatus(Util.readEntityName(b.getProcessData()),0).equals(Status.SUCCEEDED))
//					break;
//				Thread.sleep(60000);
//			}
//
//			processHelper.getProcessInstanceStatus(Util.readEntityName(b.getProcessData()),"?start="+START_TIME);
//			processHelper.getProcessInstanceStatus(Util.readEntityName(b.getProcessData()),"?start="+START_TIME+"&end="+instanceUtil.addMinsToTime(endTime,11));
//			processHelper.getProcessInstanceStatus(Util.readEntityName(b.getProcessData()),"?start="+START_TIME+"&end="+endTime);
//
//			int finalNumberOfInstances=instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS").size();
//			Assert.assertEquals(finalNumberOfInstances-originalNumberOfInstances, 2);
//			Assert.assertEquals(instanceUtil.getInstanceCountWithStatus(Util.getProcessName(b.getProcessData()),CoordinatorAction.Status.SUCCEEDED,"PROCESS"),4,"all instance did not succeed");
//			Assert.assertEquals(instanceUtil.getDefaultCoordinatorStatus(Util.readEntityName(b.getProcessData()),0),Status.SUCCEEDED);
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateDaily(Bundle b) throws Exception
//	{
//		try
//		{
//			b.generateUniqueBundle();
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-11);
//			String endTime = instanceUtil.getTimeWrtSystemTime(30);
//
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(200,0,1,prefix);
//
//			b.setProcessValidity(START_TIME,endTime);
//			b.setProcessPeriodicity(FrequencyType.DAYS,1);
//			b.setOutputFeedPeriodicity("days","1");
//			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(2);
//			
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			b.setProcessValidity(START_TIME,instanceUtil.getTimeWrtSystemTime(10));
//			String newProcess = b.getProcessData();
//			String bundleID = Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(b.getProcessData()),"NONE").get(0));
//
//		
//			Util.print("newProcess: "+newProcess);
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),newProcess);
//			
//
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//	}
//
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_Name(Bundle b) throws Exception
//	{
//		try
//
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//		
//			final int ORIGINAL_CONCURRENCY = 2;
//			final int FINAL_CONCURRENCY = 10;
//
//			b.generateUniqueBundle();
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-1440);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(2880);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			//			instanceUtil.validateNumberOfInstanceWithStatus(instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS"),CoordinatorAction.Status.RUNNING,2);
//			b.setProcessConcurrency(FINAL_CONCURRENCY);
//			b.setProcessWorkflow("/examples/apps/aggregator1");
//			String oldName = Util.getProcessName(b.getProcessData());
//			b.setProcessName("newPorcessName");
//			ServiceResponse r = processHelper.updateProcess(oldName,b.getProcessData());
//			Assert.assertTrue(r.getMessage().contains("name can't be changed"),"name should not have been changed");
//
//			b.setProcessName(oldName);
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_updateRunningFrequency(Bundle b) throws Exception
//	{
//		try
//		{
//			b.generateUniqueBundle();
//		
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-11);
//			String endTime = instanceUtil.getTimeWrtSystemTime(30);
//
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(200,0,1,prefix);
//
//			b.setProcessValidity(START_TIME,endTime);
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(2);
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			b.setProcessPeriodicity(FrequencyType.MINUTES,10);
//			//	b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/changed/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			String newProcess = b.getProcessData();
//			String bundleID = Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(b.getProcessData()),"NONE").get(0));
//
//		
//			Util.print("newProcess: "+newProcess);
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),newProcess);
//			Thread.sleep(300000);
//			
//			String bundleId = instanceUtil.getSequenceBundleID(Util.getProcessName(b.getProcessData()), "PROCESS", 1);
//			
//			CoordinatorJob coordJob = oozieClient.getCoordJobInfo(instanceUtil.getDefaultCoordIDFromBundle(bundleId));
//			
//			Assert.assertEquals(coordJob.getFrequency(),10);
//			
//			//String time1 = instanceUtil.getNominalTimeOfInstance(instanceUtil.getDefaultCoordIDFromBundle(bundleId), 0);
//			//String time2 = instanceUtil.getNominalTimeOfInstance(instanceUtil.getDefaultCoordIDFromBundle(bundleId), 1);
//			//long diff = instanceUtil.diffbw2time(time1,time2);
//			//Assert.assertEquals(diff,10);
//
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//	}
//
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_inputOutputInstance(Bundle b) throws Exception
//	{
//		try
//
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//		
//			final int ORIGINAL_CONCURRENCY = 2;			
//			b.generateUniqueBundle();
//			String START_TIME = instanceUtil.getTimeWrtSystemTime(-20);
//			String END_TIME = instanceUtil.getTimeWrtSystemTime(30);
//			b.setProcessValidity(START_TIME,END_TIME);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(ORIGINAL_CONCURRENCY);
//			b.submitAndScheduleBundle();
//			Thread.sleep(30000);
//			b.setDatasetInstances("now(0,-40)","now(0,-20)");
//			b.setOutputDataInstance("today(0,0)");
//			processHelper.updateProcess(Util.getProcessName(b.getProcessData()),b.getProcessData());
//			Thread.sleep(30000);
//
//			int i = 0 ;
//			while(i != 10 )
//			{
//				CoordinatorAction.Status s = instanceUtil.getInstanceStatus(Util.getProcessName(b.getProcessData()), 0, 0);
//				if(s.equals(CoordinatorAction.Status.RUNNING))
//					break;
//				i++;
//				Thread.sleep(30000);
//			}	
//
//			Thread.sleep(45000);
//			
//			COORDINATORAPP coordApp = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()), "PROCESS", 1);
//			Assert.assertTrue(coordApp.getInputEvents().getDataIn().get(0).getStartInstance().contains("now(0,-40)"));
//			Assert.assertTrue(coordApp.getInputEvents().getDataIn().get(0).getEndInstance().contains("now(0,-20)"));
//
//
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processUpdateTest_decreaseValidity(Bundle b) throws Exception
//	{
//		try
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//			b.generateUniqueBundle();
//
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(200,0,1,prefix);
//
//			final String START_TIME = instanceUtil.getTimeWrtSystemTime(-10);
//			String endTime = instanceUtil.getTimeWrtSystemTime(16);
//			Util.print("StartTime: "+START_TIME);
//			Util.print("original EndTime:"+endTime);
//			b.setProcessValidity(START_TIME,endTime);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(10);
//			b.submitAndScheduleBundle();
//			Thread.sleep(120000);
//			int originalNumberOfInstances=instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS").size();
//			b.setProcessValidity(START_TIME,instanceUtil.addMinsToTime(endTime,-10));
//			Util.print("new EndTime:"+instanceUtil.addMinsToTime(endTime,-10));
//			processHelper.updateProcess(Util.readEntityName(b.getProcessData()),b.getProcessData());
//			for(int wait = 0 ; wait < 50 ; wait++)
//			{
//				if(instanceUtil.getDefaultCoordinatorStatus(Util.readEntityName(b.getProcessData()),0).equals(Status.SUCCEEDED))
//					break;
//				Thread.sleep(30000);
//
//			}
//			int finalNumberOfInstances=instanceUtil.getProcessInstanceList(Util.getProcessName(b.getProcessData()),"PROCESS").size();
//			Assert.assertEquals(originalNumberOfInstances-finalNumberOfInstances, 2);
//			Assert.assertEquals(instanceUtil.getInstanceCountWithStatus(Util.getProcessName(b.getProcessData()),CoordinatorAction.Status.SUCCEEDED,"PROCESS"),4,"all instance did not succeed");
//			Assert.assertEquals(instanceUtil.getDefaultCoordinatorStatus(Util.readEntityName(b.getProcessData()),0),Status.SUCCEEDED);
//
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//	}
//	
//}