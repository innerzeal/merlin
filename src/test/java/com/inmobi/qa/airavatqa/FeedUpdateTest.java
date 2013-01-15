//package com.inmobi.qa.airavatqa;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.oozie.client.WorkflowAction;
//import org.apache.oozie.client.XOozieClient;
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.DataEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.Frequency.TimeUnit;
//import com.inmobi.qa.airavatqa.core.ProcessEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.generated.coordinator.CONFIGURATION;
//import com.inmobi.qa.airavatqa.generated.coordinator.COORDINATORAPP;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//
///**
// * 
// * @author samarth.gupta
// *
// */
//public class FeedUpdateTest {
//	@BeforeMethod(alwaysRun=true)
//	public void testName(Method method)
//	{
//		Util.print("test name: "+method.getName());
//	}
//	DataEntityHelperImpl feedHelper=new DataEntityHelperImpl();
//	IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//	ProcessEntityHelperImpl processHelper = new ProcessEntityHelperImpl();
//
//
//	final String GET_ENTITY_DEFINITION=Util.readPropertiesFile("ivory_hostname")+"/api/entities/definition";
//	ServiceResponse response;
//	static XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//
//	public void submitCluster(Bundle bundle) throws Exception
//	{
//		//submit the cluster
//		ServiceResponse response=clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//		Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//		Assert.assertNotNull(Util.parseResponse(response).getMessage());
//	}
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_nonScheduledFeedUpdate(Bundle b) throws Exception
//	{
//		try
//		{
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			
//			b.generateUniqueBundle();
//			submitCluster(b);
//			String feed=Util.getInputFeedFromBundle(b);
//			ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
//			Util.assertSucceeded(response);
//			final String startInstance="2010-04-01T00:00Z" ;
//			final String endInstance="2099-04-01T00:00Z";
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE");
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setInputFeedRetentionLimit(120,TimeUnit.months);
//			Util.print("updatedFeed: "+Util.getInputFeedFromBundle(b));
//			feedHelper.updateFeed(Util.getInputFeedNameFromBundle(b),Util.getInputFeedFromBundle(b));
//			Thread.sleep(5000);
//			feedHelper.schedule(URLS.SCHEDULE_URL, Util.getInputFeedFromBundle(b));
//			COORDINATORAPP coordObj = instanceUtil.getLatestCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED");
//
//			Util.print(coordObj.getStart()+ coordObj.getEnd());
//			//Assert.assertEquals(coordObj.getStart(), startInstance);
//			Assert.assertEquals(coordObj.getEnd(), endInstance);
//			boolean flag1 = false;
//			boolean flag2 = false;
//
//			for(CONFIGURATION.Property prop : coordObj.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("feedDataPath") && prop.getValue().equals("/samarth/input-data/rawLogs/newPath/?{YEAR}/?{MONTH}/?{DAY}/?{HOUR}/?{MINUTE"))
//				{
//					flag1 = true;
//					break;
//				}
//			}
//			for(CONFIGURATION.Property prop : coordObj.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("limit") && prop.getValue().equals("months(120)"))
//				{
//					flag2 = true;break;
//
//				}
//			}
//			Assert.assertTrue(flag1);
//			Assert.assertTrue(flag2);
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//
//
//
//	//@Test add cluster 
//	//@Test add feed 
//
//
//
///*
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_multiplePorcessAndFeedUpdate(Bundle b) throws Exception
//	{
//		String process01 ="";
//		String process02="";
//		String outputFeed01="";
//		String outputFeed02="";
//		try
//		{	
//			
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			
//			
//			b.generateUniqueBundle();
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			String processStart = instanceUtil.getTimeWrtSystemTime(-6);
//			String processEnd = instanceUtil.getTimeWrtSystemTime(36);
//			Util.print("processStart: "+processStart+" processEnd: "+processEnd);
//
//			b.setProcessValidity(processStart,processEnd);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setProcessConcurrency(2);
//
//			submitCluster(b);
//			String commonInputFeed = Util.getInputFeedFromBundle(b);
//
//			String outputFeed = Util.getOutputFeedFromBundle(b);
//			outputFeed01 = Util.setFeedName(outputFeed,Util.getOutputFeedNameFromBundle(b)+"01");
//			outputFeed02 = Util.setFeedName(outputFeed,Util.getOutputFeedNameFromBundle(b)+"02");
//
//			outputFeed01 = Util.setFeedPathValue(outputFeed01, "/samarth/testFolder/output-data01/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			outputFeed02 = Util.setFeedPathValue(outputFeed02, "/samarth/testFolder/output-data02/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String process = b.getProcessData();
//			process01 = Util.setProcessName(process,Util.getProcessName(process)+"01");
//			process02 = Util.setProcessName(process, Util.getProcessName(process)+"02");
//
//			process01 = Util.setProcessOutputFeed(process01,Util.getFeedName(outputFeed01));
//			process02 = Util.setProcessOutputFeed(process02,Util.getFeedName(outputFeed02));
//
//
//			feedHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, commonInputFeed);
//			feedHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, outputFeed01);
//			feedHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, outputFeed02);
//			processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, process01);
//			processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, process02);
//
//			Thread.sleep(30000);
//
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(70,0,1,prefix);
//			//Thread.sleep(90000);
//
//
//			commonInputFeed = Util.setFeedPathValue(commonInputFeed, "/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			feedHelper.updateFeed(Util.getFeedName(commonInputFeed), commonInputFeed);
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(70,0,1,prefix);
//			Thread.sleep(30000);
//
//			outputFeed01 = Util.setFeedPathValue(outputFeed01, "/samarth/testFolder/output-data03/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			feedHelper.updateFeed(Util.getFeedName(outputFeed01), outputFeed01);
//			Thread.sleep(30000);
//
//			outputFeed02 = Util.setFeedPathValue(outputFeed02,"/samarth/testFolder/output04/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			feedHelper.updateFeed(Util.getFeedName(outputFeed02), outputFeed02);
//			Thread.sleep(30000);
//
//		}
//		finally{
//			processHelper.delete(URLS.DELETE_URL, process01);
//			processHelper.delete(URLS.DELETE_URL, process02);
//			feedHelper.delete(URLS.DELETE_URL, outputFeed01);
//			feedHelper.delete(URLS.DELETE_URL, outputFeed02);
//			b.deleteBundle();
//		}
//	}
//
//
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_updateSuspendedFeed(Bundle b) throws Exception
//	{
//		String process01 ="";
//		String process02="";
//		String outputFeed01="";
//		String outputFeed02="";
//		try
//		{	
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//			
//			
//			b.generateUniqueBundle();
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			String processStart = instanceUtil.getTimeWrtSystemTime(-6);
//			String processEnd = instanceUtil.getTimeWrtSystemTime(36);
//			Util.print("processStart: "+processStart+" processEnd: "+processEnd);
//			b.setProcessValidity(processStart,processEnd);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setProcessConcurrency(2);
//
//
//			b.submitAndScheduleBundle(false);
//
//			Thread.sleep(20000);
//
//			feedHelper.schedule(URLS.SCHEDULE_URL, Util.getInputFeedFromBundle(b));
//			Thread.sleep(30000);
//			feedHelper.suspend(URLS.SUSPEND_URL,Util.getInputFeedFromBundle(b));
//			Thread.sleep(15000);
//
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(120,0,1,prefix);
//
//			b.setInputFeedRetentionLimit("months(240)");
//			Util.print("updatedFeed: "+Util.getInputFeedFromBundle(b));
//
//			feedHelper.updateFeed(Util.getInputFeedNameFromBundle(b),Util.getInputFeedFromBundle(b));
//			Thread.sleep(60000);
//
//			COORDINATORAPP coord00 = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()), "PROCESS", 0);
//			COORDINATORAPP coord01 = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()), "PROCESS", 1);
//			COORDINATORAPP feed00 = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()), "PROCESS", 0);
//
//			for(CONFIGURATION.Property prop : feed00.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//
//			for(CONFIGURATION.Property prop : coord00.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//
//		}
//		finally{
//
//			b.deleteBundle();
//		}
//	}
//
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_scheduleProcessOnUpdateFeed(Bundle b) throws Exception
//	{
//		try
//		{	
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//			final String startInstance="2010-04-01T00:00Z" ;
//			String endInstance="2013-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//
//			b.generateUniqueBundle();
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			submitCluster(b);
//			String feed=Util.getInputFeedFromBundle(b);
//
//			feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
//			Thread.sleep(5000);
//
//			feedHelper.schedule(URLS.SCHEDULE_URL,feed);
//			Thread.sleep(20000);
//
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(120,0,1,prefix);
//
//			endInstance="2099-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setDatasetInstances("now(0,-20)", "now(0,0)");
//			
//			String processStart=instanceUtil.getTimeWrtSystemTime(-30);
//			String processEnd=instanceUtil.getTimeWrtSystemTime(1440);
//			Util.print("processStart: "+processStart+" processEnd: "+processEnd);
//			
//			b.setProcessValidity(instanceUtil.getTimeWrtSystemTime(-30),instanceUtil.getTimeWrtSystemTime(1440));			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(2);
//			b.setInputFeedRetentionLimit("months(240)");
//
//			Util.print("updatedFeed: "+Util.getInputFeedFromBundle(b));
//
//			feedHelper.updateFeed(Util.getInputFeedNameFromBundle(b),Util.getInputFeedFromBundle(b));
//			Thread.sleep(60000);
//
//			b.submitAndScheduleBundle(false);
//			Thread.sleep(120000);
//
//			COORDINATORAPP feedCord01 = instanceUtil.getLatestCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED");
//			COORDINATORAPP feedCord00 = instanceUtil.getCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED",0);
//			COORDINATORAPP processCord01 = instanceUtil.getLatestCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS");
//
//			Assert.assertEquals(feedCord00.getEnd(), "2013-04-01T00:00Z","end instance of feed did not match");
//			Assert.assertEquals(feedCord01.getEnd(), endInstance,"end instance of feed did not match");
//			Assert.assertEquals(processCord01.getInputEvents().getDataIn().get(0).getEndInstance(),"${elext:now(0,0)}");
//
//			Util.print(instanceUtil.getLatestCoordinator(Util.getProcessName(b.getProcessData()),"PROCESS"));
//
//			for(CONFIGURATION.Property prop : feedCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//			Util.print("-----------------------------------------------------------------------------------------");
//
//			for(CONFIGURATION.Property prop : processCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//		}
//		finally{
//			b.deleteBundle();
//		}
//	}
//
//
//
//	
//
//
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_retentionAndValidity(Bundle b) throws Exception
//	{
//		try
//		{	
//
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//			b.generateUniqueBundle();
//
//			String startInstance="2010-04-01T00:00Z" ;
//			String endInstance="2013-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setInputFeedRetentionLimit("months(120)");
//			b.setDatasetInstances("now(0,-20)", "now(0,0)");
//			
//			String startValidity = instanceUtil.getTimeWrtSystemTime(-30);
//			String endValidity = instanceUtil.getTimeWrtSystemTime(1440);
//			Util.print("startValidity: "+startValidity+" endValidity: "+endValidity);
//			
//			
//			b.setProcessValidity(startValidity,endValidity);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(2);
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//
//			submitCluster(b);
//			String feed=Util.getInputFeedFromBundle(b);
//
//			feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
//			Thread.sleep(5000);
//
//			feedHelper.schedule(URLS.SCHEDULE_URL,feed);
//			Thread.sleep(30000);
//
//			b.submitAndScheduleBundle(false);
//
//			String prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(120,0,1,prefix);
//
//			endInstance="2099-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setInputFeedRetentionLimit("months(240)");
//			Util.print("updatedFeed: "+Util.getInputFeedFromBundle(b));
//
//			feedHelper.updateFeed(Util.getInputFeedNameFromBundle(b),Util.getInputFeedFromBundle(b));
//			Thread.sleep(120000);
//
//
//			COORDINATORAPP feedCord01 = instanceUtil.getCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED",1);
//			COORDINATORAPP feedCord00 = instanceUtil.getCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED",0);
//			COORDINATORAPP processCord01 = instanceUtil.getLatestCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS");
//
//			boolean flag01 = false;
//			boolean flag02 = false;
//
//			for(CONFIGURATION.Property prop : feedCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("limit") && prop.getValue().equals("months(240)"))
//				{
//					flag01 = true;break;
//
//				}
//			}
//
//			for(CONFIGURATION.Property prop : feedCord00.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("limit") && prop.getValue().equals("months(120)"))
//				{
//					flag02 = true;break;
//
//				}
//			}
//
//			Assert.assertTrue(flag01,"flag01 failed");
//			Assert.assertTrue(flag02,"flag02 failed");
//
//			for(CONFIGURATION.Property prop : feedCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//			Util.print("-----------------------------------------------------------------------------------------");
//
//			for(CONFIGURATION.Property prop : processCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//		}
//		finally{
//			b.deleteBundle();
//		}
//
//
//	}
//
//	
//	
//	@Test(groups={"0.1","0.2","sanity"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedUpdate_runningProcessInstance(Bundle b) throws Exception
//	{
//		String prefix = new String();
//		try
//		{	
//
//			System.setProperty("java.security.krb5.realm", "");
//			System.setProperty("java.security.krb5.kdc", "");
//
//
//			b.generateUniqueBundle();
//
//			String startInstance="2010-04-01T00:00Z" ;
//			String endInstance="2013-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setInputFeedRetentionLimit("months(120)");
//
//			submitCluster(b);
//			String feed=Util.getInputFeedFromBundle(b);
//
//			feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
//			Thread.sleep(5000);
//
//			feedHelper.schedule(URLS.SCHEDULE_URL,feed);
//			Thread.sleep(40000);
//
//			prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(60,0,1,prefix);
//
//			
//			
//			//basic process setup
//			b.setDatasetInstances("now(0,-20)", "now(0,0)");
//			String processStart = instanceUtil.getTimeWrtSystemTime(-2);
//			String processEnd = instanceUtil.getTimeWrtSystemTime(30);
//			
//			Util.print("processStart: "+processStart+" processEnd: "+processEnd);
//			
//			b.setProcessValidity(processStart,processEnd);			
//			b.setProcessPeriodicity(FrequencyType.MINUTES,5);
//			b.setOutputFeedPeriodicity("minutes","5");
//			b.setOutputFeedLocationData("/samarth/testFolder/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			b.setProcessConcurrency(2);
//
//			b.submitAndScheduleBundle(false);
//
//
//			endInstance="2099-04-01T00:00Z";
//			b.setInputFeedValidity(startInstance,endInstance);
//			b.setInputFeedRetentionLimit("months(240)");
//			b.setInputFeedDataPath("/samarth/input-data/rawLogs/newPath/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//			Util.print("updatedFeed: "+Util.getInputFeedFromBundle(b));
//			feedHelper.updateFeed(Util.getInputFeedNameFromBundle(b),Util.getInputFeedFromBundle(b));
//			Thread.sleep(15000);
//			
//			
//			prefix = b.getFeedDataPathPrefix();
//			Util.HDFSCleanup(prefix.substring(1));
//			Util.lateDataReplenish(60,0,1,prefix);
//			Thread.sleep(150000);
//
//			COORDINATORAPP feedCord01 = instanceUtil.getCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED",1);
//			COORDINATORAPP feedCord00 = instanceUtil.getCoordinatorApp(Util.getInputFeedNameFromBundle(b),"FEED",0);
//			COORDINATORAPP processCord01 = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS",1);
//			COORDINATORAPP processCord00 = instanceUtil.getCoordinatorApp(Util.getProcessName(b.getProcessData()),"PROCESS",0);
//			Assert.assertEquals(instanceUtil.getProcessInstanceNominalTime(Util.getProcessName(b.getProcessData()),0,0,"PORCESS"),processStart);
//			Assert.assertEquals(instanceUtil.getProcessInstanceNominalTime(Util.getProcessName(b.getProcessData()),1,0,"PROCESS"),instanceUtil.addMinsToTime(processStart,5));
//			String folderPath00 = instanceUtil.getInputFoldersForInstance(Util.getProcessName(b.getProcessData()), 0, 0).get(0);
//			String folderPath01 = instanceUtil.getInputFoldersForInstance(Util.getProcessName(b.getProcessData()), 1, 0).get(0);
//
//			Assert.assertTrue(folderPath00.contains("/samarth/input-data/rawLogs/oozieExample"));
//			Assert.assertTrue(folderPath01.contains("/samarth/input-data/rawLogs/newPath"));
//
//
//			boolean flag01 = false;
//			boolean flag02 = false;
//
//			for(CONFIGURATION.Property prop : feedCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("limit") && prop.getValue().equals("months(240)"))
//				{
//					flag01 = true;break;
//
//				}
//			}
//
//			for(CONFIGURATION.Property prop : feedCord00.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//				if(prop.getName().equals("limit") && prop.getValue().equals("months(120)"))
//				{
//					flag02 = true;break;
//
//				}
//			}
//
//			Assert.assertTrue(flag01,"flag01 failed");
//			Assert.assertTrue(flag02,"flag02 failed");
//
//			for(CONFIGURATION.Property prop : feedCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//			Util.print("-----------------------------------------------------------------------------------------");
//
//			for(CONFIGURATION.Property prop : processCord01.getAction().getWorkflow().getConfiguration().getProperty())
//			{
//				Util.print("prop: "+prop.getName() +" "+ prop.getValue());
//
//			}
//		}
//		finally{
//			Util.HDFSCleanup(prefix.substring(1));
//			b.deleteBundle();
//		}
//	}
//
//*/
//
//}
