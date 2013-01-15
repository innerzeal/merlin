///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.DataEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.generated.FrequencyType;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import com.inmobi.qa.airavatqa.mq.Consumer;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.apache.hadoop.mapred.JobHistory.JobInfo;
//import org.apache.oozie.client.BundleJob;
//import org.apache.oozie.client.CoordinatorAction;
//import org.apache.oozie.client.CoordinatorJob;
//import org.apache.oozie.client.OozieClient;
//import org.apache.oozie.client.WorkflowJob;
//import org.apache.oozie.client.WorkflowJob.Status;
//import org.apache.oozie.client.XOozieClient;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.joda.time.Minutes;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.testng.log4testng.Logger;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class LateDataTest {
//    
//	
//	
//    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//    IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    Logger logger=Logger.getLogger(this.getClass());
//    DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void testLateDataWithCutOffInFuture(Bundle bundle) throws Exception
//    {
//        String delay="15";
//        
//    	try {
//    	System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//    	
//    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//    	feed=Util.insertLateFeedValue(feed,delay,"minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    	
//    	//inject data into folders for processing
//        Util.HDFSCleanup("lateDataTest/testFolders/");
//        Util.lateDataReplenish(20,0,0);
//        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        
//    	bundle.generateUniqueBundle();
//    	
//    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//    	
//    	for(String data:bundle.getDataSets())
//    	{
//    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//    	}
//    	
//    	//set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(7);
//    	  
//    	bundle.setProcessValidity(startDate,endDate);
//    	
//    	//submit and schedule process
//    	Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//    	
//    	logger.info(bundle.getProcessData());
// 
//        Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//        
//        //attach the consumer
//        Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
//        consumer.start();
//        
//        //get the bundle ID
//        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//        String status=Util.getBundleStatus(bundleId);
//        
//        //Thread.sleep(20000);
//        
//        //DateTime now=Util.getStartTimeForDefaultCoordinator(bundleId);
//        ArrayList<DateTime> dates=null;
//        
//        do {
//        
//        	dates=Util.getStartTimeForRunningCoordinators(bundleId);
//        	
//        }while(dates==null);
//        
//        
//        
//        logger.info("Start time: "+formatter.print(startDate));
//        logger.info("End time: "+formatter.print(endDate));
//        logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
//        DateTime now=dates.get(0);
//        
//        if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
//        {
//        	now=startDate;
//        }
//        
//        logger.info("Now:"+formatter.print(now));
//        
//        Assert.assertFalse(!(formatter.print(now.plusMinutes(1)).compareTo(formatter.print(endDate))<0),"Uh oh! The timing is not at all correct!");
//        
//       
//        int diff=getTimeDiff(formatter.print(now),formatter.print(endDate));;
//        
//        String insertionFolder="";
//        DateTime insertionTime=null;
//        
//        boolean toInsert=true;
//        
//        while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
//        {
//            
//        	//insert data at the correct point based on test case
//        	insertionFolder=Util.findFolderBetweenGivenTimeStamps(now.plusMinutes(1),now.plusMinutes(5),initialData);
//        	
//        	if((toInsert) && insertionFolder!=null && new DateTime(DateTimeZone.UTC).isBefore(now.plusMinutes(Integer.parseInt(delay))) && allRelevantWorkflowsAreOver(bundleId, insertionFolder))
//        	{
//        		
//        		if(Util.isOver(bundleId,now.plusMinutes(1)))
//        		{
//        			logger.info("going to insert data at: "+insertionFolder);
//                                insertionTime=new DateTime(DateTimeZone.UTC);
//        			logger.info("insertion time is :"+insertionTime);
//        			Util.injectMoreData(insertionFolder,"src/test/resources/OozieExampleInputData/lateData");
//        			initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        			toInsert=false;
//        		}
//        	}
//        	
//            status=Util.getBundleStatus(bundleId);
//        }
//        
//        consumer.stop();
//        
//        logger.info("dumping all queue data:");
//        
//        for(String data:consumer.getTextMessageList())
//        {
//        	logger.info(data);
//        }
//        
//        
//        //verify if the correct number of late data coordinators were created with correct nominal times
//        
//        
//        verifyLateDataCoordinators(bundleId,now,delay,"minutes",diff);
//        //isLateDataWorkflowPresentInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(1),insertionFolder);
//        verifyLateWorkflowPresenceInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(1), insertionFolder,insertionTime);
//    	}
//    	catch(Exception e)
//    	{
//    		e.printStackTrace();
//    		throw new TestNGException(e.getMessage());
//    	}
//    	finally {
//    		
//    		bundle.deleteBundle();
//    	}
//  
//    }
//    
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void testLateDataWithDataAtCutOff(Bundle bundle) throws Exception
//    {
//    	try {
//    	
//    	System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//    	
//    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//    	feed=Util.insertLateFeedValue(feed,"10","minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    	
//    	//inject data into folders for processing
//        Util.HDFSCleanup("lateDataTest/testFolders/");
//        Util.lateDataReplenish(20,0,0);
//        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        
//    	bundle.generateUniqueBundle();
//    	
//    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//    	
//    	for(String data:bundle.getDataSets())
//    	{
//    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//    	}
//    	
//    	//set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(7);
//    	
//    	bundle.setProcessValidity(startDate,endDate);
//    	
//    	//submit and schedule process
//    	Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//    	
//    	logger.info(bundle.getProcessData());
// 
//        Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//        
//        //attach the consumer
//        Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
//        consumer.start();
//        
//        //get the bundle ID
//        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//        String status=Util.getBundleStatus(bundleId);
//        
//        //Thread.sleep(20000);
//        
//        //DateTime now=Util.getStartTimeForDefaultCoordinator(bundleId);
//        ArrayList<DateTime> dates=null;
//        
//        do {
//        
//        	dates=Util.getStartTimeForRunningCoordinators(bundleId);
//        	
//        }while(dates==null);
//        
//        
//        
//        logger.info("Start time: "+formatter.print(startDate));
//        logger.info("End time: "+formatter.print(endDate));
//        
//        logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
//        DateTime now=dates.get(0);
//        
//        if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
//        {
//        	now=startDate;
//        }
//        
//        logger.info("Now:"+formatter.print(now));
//        
//        Assert.assertFalse(formatter.print(now).equalsIgnoreCase(formatter.print(endDate)),"Uh oh! The timing is not at all correct!");
//        
//        int diff=getTimeDiff(formatter.print(now),formatter.print(endDate));
//        
//        String insertionFolder="";
//        DateTime insertionTime=null;
//        boolean toInsert=true;
//        
//        while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
//        {
//            
//        	//insert data at the correct point based on test case
//        	insertionFolder=Util.findFolderBetweenGivenTimeStamps(now,now.plusMinutes(10),initialData);
//        	
//        	if((toInsert) && insertionFolder!=null && new DateTime(DateTimeZone.UTC).isBefore(now.plusMinutes(10)))
//        	{
//        		
//        		if(Util.isOver(bundleId,now))
//        		{
//        			logger.info("going to insert data at: "+insertionFolder);
//                                insertionTime=new DateTime(DateTimeZone.UTC);
//        			logger.info("insertion time is :"+insertionTime);
//        			Util.injectMoreData(insertionFolder,"src/test/resources/OozieExampleInputData/lateData");
//        			initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        			toInsert=false;
//        		}
//        	}
//        	
//            status=Util.getBundleStatus(bundleId);
//        }
//        
//        consumer.stop();
//        
//        logger.info("dumping all queue data:");
//        
//        for(String data:consumer.getTextMessageList())
//        {
//        	logger.info(data);
//        }
//        
//        
//        //verify if the correct number of late data coordinators were created with correct nominal times
//        
//        
//        verifyLateDataCoordinators(bundleId,now,"10","minutes",diff);
//        //isLateDataWorkflowPresentInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(10));
//    	verifyLateWorkflowPresenceInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(10), insertionFolder,insertionTime);
//    	}
//    	catch(Exception e)
//    	{
//    		e.printStackTrace();
//    		throw new TestNGException(e.getMessage());
//    	}
//    	finally {
//    		
//    		bundle.deleteBundle();
//    	}
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void testLateDataWithDataInsertedAtCutOff(Bundle bundle) throws Exception
//    {
//    	try {
//    	
//    	System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//    	
//    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//    	feed=Util.insertLateFeedValue(feed,"10","minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    	
//    	//inject data into folders for processing
//        Util.HDFSCleanup("lateDataTest/testFolders/");
//        Util.lateDataReplenish(20,0,0);
//        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        
//    	bundle.generateUniqueBundle();
//    	
//    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//    	
//    	for(String data:bundle.getDataSets())
//    	{
//    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//    	}
//    	
//    	//set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(7);
//    	
//    	bundle.setProcessValidity(startDate,endDate);
//    	
//    	//submit and schedule process
//    	Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//    	
//    	logger.info(bundle.getProcessData());
// 
//        Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//        
//        //attach the consumer
//        Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
//        consumer.start();
//        
//        //get the bundle ID
//        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//        String status=Util.getBundleStatus(bundleId);
//        
//        //Thread.sleep(20000);
//        
//        //DateTime now=Util.getStartTimeForDefaultCoordinator(bundleId);
//        ArrayList<DateTime> dates=null;
//        
//        do {
//        
//        	dates=Util.getStartTimeForRunningCoordinators(bundleId);
//        	
//        }while(dates==null);
//        
//        
//        
//        logger.info("Start time: "+formatter.print(startDate));
//        logger.info("End time: "+formatter.print(endDate));
//        logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
//        DateTime now=dates.get(0);
//        
//        if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
//        {
//        	now=startDate;
//        }
//        
//        logger.info("Now:"+formatter.print(now));
//        
//        Assert.assertFalse(formatter.print(now).equalsIgnoreCase(formatter.print(endDate)),"Uh oh! The timing is not at all correct!");
//        
//        int diff=getTimeDiff(formatter.print(now),formatter.print(endDate));
//        
//        String insertionFolder="";
//        DateTime insertionTime=null;
//        boolean toInsert=true;
//        
//        while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
//        {
//            
//        	//insert data at the correct point based on test case
//        	insertionFolder=Util.findFolderBetweenGivenTimeStamps(now,now.plusMinutes(5),initialData);
//        	
//        	if((toInsert) && insertionFolder!=null && new DateTime(DateTimeZone.UTC).isEqual(now.plusMinutes(10)) && allRelevantWorkflowsAreOver(bundleId, insertionFolder))
//        	{
//        		
//        		if(Util.isOver(bundleId,now))
//        		{
//        			logger.info("going to insert data at: "+insertionFolder);
//                                insertionTime=new DateTime(DateTimeZone.UTC);
//        			logger.info("insertion time is :"+insertionTime);
//        			Util.injectMoreData(insertionFolder,"src/test/resources/OozieExampleInputData/lateData");
//        			initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        			toInsert=false;
//        		}
//        	}
//        	
//            status=Util.getBundleStatus(bundleId);
//        }
//        
//        consumer.stop();
//        
//        logger.info("dumping all queue data:");
//        
//        for(String data:consumer.getTextMessageList())
//        {
//        	logger.info(data);
//        }
//        
//        
//        //verify if the correct number of late data coordinators were created with correct nominal times
//        
//        
//        verifyLateDataCoordinators(bundleId,now,"10","minutes",diff);
//        //isLateDataWorkflowPresentInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(10));
//    	verifyLateWorkflowPresenceInQueue(consumer.getTextMessageList(), bundleId, now.plusMinutes(10), insertionFolder,insertionTime);
//    	}
//    	catch(Exception e)
//    	{
//    		e.printStackTrace();
//    		throw new TestNGException(e.getMessage());
//    	}
//    	finally {
//    		
//    		bundle.deleteBundle();
//    	}
//  
//    }   
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void testWithNoDataCutoffSpecified(Bundle bundle) throws Exception
//    {
//       try {
//           
//        System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//    	
//    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//    	feed=Util.insertLateFeedValue(feed,"10","minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    	
//    	//inject data into folders for processing
//        Util.HDFSCleanup("lateDataTest/testFolders/");
//        Util.lateDataReplenish(20,0,0);
//        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        
//    	bundle.generateUniqueBundle();
//    	
//    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//    	
//    	for(String data:bundle.getDataSets())
//    	{
//    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//    	}
//    	
//    	//set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(7);
//    	
//    	bundle.setProcessValidity(startDate,endDate);
//        bundle.setProcessLatePolicy(null);
//    	
//    	//submit and schedule process
//    	Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//    	
//    	logger.info(bundle.getProcessData());
// 
//        Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//        
//        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//        
//        Thread.sleep(10000);
//        
//        validateNoLateDataCoordinators(bundleId);
//        
//       } 
//       catch(Exception e)
//       {
//           e.printStackTrace();
//           throw new TestNGException(e.getCause());
//       }
//       finally {
//           
//           bundle.deleteBundle();
//       }
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void testWithNoProcessContextInPast(Bundle bundle) throws Exception
//    {
//       try {
//           
//        System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//    	
//    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//    	feed=Util.insertLateFeedValue(feed,"10","minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    	
//    	//inject data into folders for processing
//        Util.HDFSCleanup("lateDataTest/testFolders/");
//        Util.lateDataReplenish(20,0,0);
//        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//        
//    	bundle.generateUniqueBundle();
//    	
//    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//    	
//    	for(String data:bundle.getDataSets())
//    	{
//    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//    	}
//    	
//    	//set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).minusDays(5);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).minusDays(3);
//    	
//    	bundle.setProcessValidity(startDate,endDate);
//        
//    	
//    	//submit and schedule process
//    	Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//    	
//    	logger.info(bundle.getProcessData());
// 
//        Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//        
//        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//        
//        Thread.sleep(10000);
//        
//        validateNoLateDataCoordinators(bundleId);
//        
//       } 
//       catch(Exception e)
//       {
//           e.printStackTrace();
//           throw new TestNGException(e.getCause());
//       }
//       finally {
//           
//           bundle.deleteBundle();
//       }
//    }    
//    
//
//    
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
//    public void outdatedLateDataHandlingWithFeedUpdateForOldInputLocation(Bundle bundle) throws Exception
//    {
//        DataEntityHelperImpl feedHelper=new DataEntityHelperImpl();
//        try {
//            
//            
//            System.setProperty("java.security.krb5.realm", "");
//            System.setProperty("java.security.krb5.kdc", "");
//            
//            String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//            feed=Util.insertLateFeedValue(feed,"10","minutes");
//            bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//            bundle.getDataSets().add(feed);
//            bundle.setProcessPeriodicity(FrequencyType.MINUTES,1);
//            bundle.setOutputFeedPeriodicity("minutes","1");
//            bundle.setProcessConcurrency(1);
//            Util.HDFSCleanup("lateDataTest/testFolders/");
//            Util.lateDataReplenish(20,0,0);
//            
//            Util.HDFSCleanup("/lateDataTest/testFolders2/");
//            Util.lateDataReplenish("/lateDataTest/testFolders2/", 20, 0, 0);
//            
//            List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//            
//            bundle.generateUniqueBundle();
//
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//
//            for(String data:bundle.getDataSets())
//            {
//                    Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//            }
//            
//            
//
//            //set process start and end date
//            DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//            DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(8);
//
//            bundle.setProcessValidity(startDate,endDate);
//
//            //submit and schedule process
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//
//            logger.info(bundle.getProcessData());
//
//            Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
//
//            //attach the consumer
//            Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
//            consumer.start();
//
//            //get the bundle ID
//            String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//            String status=Util.getBundleStatus(bundleId);
//
//            //Thread.sleep(20000);
//
//            //DateTime now=Util.getStartTimeForDefaultCoordinator(bundleId);
//            ArrayList<DateTime> dates=null;
//
//            do {
//
//                    dates=Util.getStartTimeForRunningCoordinators(bundleId);
//
//            }while(dates==null);
//
//
//
//            logger.info("Start time: "+formatter.print(startDate));
//            logger.info("End time: "+formatter.print(endDate));
//            logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
//            DateTime now=dates.get(0);
//
//            if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
//            {
//                    now=startDate;
//            }
//
//            logger.info("Now:"+formatter.print(now));
//
//            Assert.assertFalse(!(formatter.print(now.plusMinutes(1)).compareTo(formatter.print(endDate))<0),"Uh oh! The timing is not at all correct!");
//
//
//            //int diff=getTimeDiff(formatter.print(now),formatter.print(endDate));
//            
//            logger.info("waiting till 20% of process is over....");
//            waitTillCertainPercentageOfProcessIsOver(bundleId,20);
//            logger.info("done...will now update the process @"+DateTime.now(DateTimeZone.UTC)+"....");
//            
//            int originalNumberOfWorkflowsInCoordinator=getNumberOfWorkflowInstances(bundleId);
//            
//            //now to update the input feed itself.
//            feed=Util.getInputFeedFromBundle(bundle);
//            feed=Util.setFeedPathValue(feed,"/lateDataTest/testFolders2/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//            bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//            bundle.getDataSets().add(feed);
//            bundle.writeBundleToFiles();
//            
//            //schedule before update
//            //Util.assertSucceeded(feedHelper.schedule(URLS.SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//            //Assert.assertTrue(feedHelper.updateViaCLI(URLS.HOSTNAME,Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),bundle.getFeedFilePath(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)))).contains("updated successfully"),"feed update failed!!! Please check!");
//            Util.assertSucceeded(feedHelper.updateFeed(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),feed));
//            DateTime updatedNow=DateTime.now(DateTimeZone.UTC);
//            
//            //now lets assume the feed has created a new bundle and get its id
//            String updatedBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
//            
//            //now lets insert data for one of the ended late data coordinators!
//            String insertionFolderForOldData=Util.findFolderBetweenGivenTimeStamps(updatedNow,endDate,initialData);
//            
//            while(!allRelevantWorkflowsAreOver(bundleId, insertionFolderForOldData))
//            {
//                //keep dancing
//            }
//            
//            logger.info("injecting data into old feed location in folder "+insertionFolderForOldData+" at timestamp "+DateTime.now(DateTimeZone.UTC));
//            DateTime insertionTime=DateTime.now(DateTimeZone.UTC);
//            Util.injectMoreData(insertionFolderForOldData,"src/test/resources/OozieExampleInputData/lateData");
//            
//            waitTillAllBundlesAreOver(bundleId,updatedBundleId);
//            
//            consumer.stop();
//            
//            //first lets make sure none of the coordinators workflows are missing in default coordinator
//            validateNumberOfWorkflowInstances(originalNumberOfWorkflowsInCoordinator, bundleId, updatedBundleId);
//            
//            logger.info("consumer dump looks like:");
//            for(String data:consumer.getTextMessageList())
//            {
//                logger.info(data);
//            }
//                    
//            
//            //now start analyzing the damage
//            logger.info("validating the default bundle....");
//            verifyLateDataCoordinators(bundleId, now,"10","minutes",getTimeDiff(formatter.print(now),formatter.print(updatedNow)));
//            verifyLateWorkflowPresenceInQueue(consumer.getTextMessageList(), bundleId, now, insertionFolderForOldData,insertionTime);
//            
//            logger.info("Done.Now validating the updated bundle....");
//            
//            verifyLateWorkflowPresenceInQueue(initialData, updatedBundleId, updatedNow, insertionFolderForOldData, insertionTime);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally{
//            bundle.deleteBundle();
//        }
//        
//    }
//    
//    
//    
////    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-20)
////    public void outdatedLateDataHandlingWithFeedUpdateForNewInputLocation(Bundle bundle) throws Exception
////    {
////        DataEntityHelperImpl feedHelper=new DataEntityHelperImpl();
////        try {
////            
////            
////            System.setProperty("java.security.krb5.realm", "");
////            System.setProperty("java.security.krb5.kdc", "");
////            
////            String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////            feed=Util.insertLateFeedValue(feed,"10","minutes");
////            bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////            bundle.getDataSets().add(feed);
////            
////            Util.HDFSCleanup("lateDataTest/testFolders/");
////            Util.lateDataReplenish(20,0,0);
////            
////            Util.HDFSCleanup("/lateDataTest/testFolders2/");
////            Util.lateDataReplenish("/lateDataTest/testFolders2/", 20, 0, 0);
////            
////            List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////            
////            bundle.generateUniqueBundle();
////
////            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////
////            for(String data:bundle.getDataSets())
////            {
////                    Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////            }
////            
////            
////
////            //set process start and end date
////            DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////            DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(7);
////
////            bundle.setProcessValidity(startDate,endDate);
////
////            //submit and schedule process
////            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
////
////            logger.info(bundle.getProcessData());
////
////            Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
////
////            //attach the consumer
////            Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
////            consumer.start();
////
////            //get the bundle ID
////            String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////            String status=Util.getBundleStatus(bundleId);
////
////            //Thread.sleep(20000);
////
////            //DateTime now=Util.getStartTimeForDefaultCoordinator(bundleId);
////            ArrayList<DateTime> dates=null;
////
////            do {
////
////                    dates=Util.getStartTimeForRunningCoordinators(bundleId);
////
////            }while(dates==null);
////
////
////
////            logger.info("Start time: "+formatter.print(startDate));
////            logger.info("End time: "+formatter.print(endDate));
////            logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
////            DateTime now=dates.get(0);
////
////            if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
////            {
////                    now=startDate;
////            }
////
////            logger.info("Now:"+formatter.print(now));
////
////            Assert.assertFalse(!(formatter.print(now.plusMinutes(1)).compareTo(formatter.print(endDate))<0),"Uh oh! The timing is not at all correct!");
////
////
////            //int diff=getTimeDiff(formatter.print(now),formatter.print(endDate));
////            
////            logger.info("waiting till 20% of process is over....");
////            waitTillCertainPercentageOfProcessIsOver(bundleId,20);
////            logger.info("done...will now update the process @"+DateTime.now(DateTimeZone.UTC)+"....");
////            
////            int originalNumberOfWorkflowsInCoordinator=getNumberOfWorkflowInstances(bundleId);
////            
////            //now to update the input feed itself.
////            feed=Util.getInputFeedFromBundle(bundle);
////            feed=Util.setFeedPathValue(feed,"/lateDataTest/testFolders2/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////            bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////            bundle.getDataSets().add(feed);
////            bundle.writeBundleToFiles();
////            
////            //schedule input feed so that it can update process on being updated
////            //Util.assertSucceeded(feedHelper.schedule(URLS.SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
////            //Assert.assertTrue(feedHelper.updateViaCLI(URLS.HOSTNAME,Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),bundle.getFeedFilePath(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)))).contains("updated successfully"),"feed update failed!!! Please check!");
////            Util.assertSucceeded(feedHelper.updateFeed(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),feed));
////            DateTime updatedNow=DateTime.now(DateTimeZone.UTC);
////            
////            //now lets assume the feed has created a new bundle and get its id
////            String updatedBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
////            
////            //now lets insert data for one of the ended late data coordinators!
////            String insertionFolderForOldData=Util.findFolderBetweenGivenTimeStamps(updatedNow,endDate,initialData);
////            
////            while(!allRelevantWorkflowsAreOver(bundleId, insertionFolderForOldData))
////            {
////                //keep dancing
////            }
////            
////            logger.info("injecting data into old feed location in folder "+insertionFolderForOldData+" at timestamp "+DateTime.now(DateTimeZone.UTC));
////            DateTime insertionTime=DateTime.now(DateTimeZone.UTC);
////            Util.injectMoreData(insertionFolderForOldData,"src/test/resources/OozieExampleInputData/lateData","/lateDataTest/testFolders2");
////            
////            
////            waitTillAllBundlesAreOver(bundleId,updatedBundleId);
////            
////            consumer.stop();
////            
////            //first lets make sure none of the coordinators workflows are missing in default coordinator
////            validateNumberOfWorkflowInstances(originalNumberOfWorkflowsInCoordinator, bundleId, updatedBundleId);
////            
////            logger.info("consumer dump looks like:");
////            for(String data:consumer.getTextMessageList())
////            {
////                logger.info(data);
////            }
////                    
////            
////            //now start analyzing the damage
////            logger.info("validating the default bundle....");
////            verifyLateDataCoordinators(bundleId, now,"10","minutes",getTimeDiff(formatter.print(now),formatter.print(updatedNow)));
////            verifyLateWorkflowPresenceInQueue(consumer.getTextMessageList(), bundleId, now, insertionFolderForOldData,insertionTime);
////            
////            logger.info("Done.Now validating the updated bundle....");
////            
////            verifyLateWorkflowPresenceInQueue(initialData, updatedBundleId, updatedNow, insertionFolderForOldData, insertionTime);
////            
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getMessage());
////        }
////        finally{
////            bundle.deleteBundle();
////        }
////        
////    }
//    
//    private void isLateDataWorkflowPresentInQueue(List<String> queueData,String bundleId,DateTime nominalTime,String insertionFolder) throws Exception
//    {
//    	List<String> workflowIdList=getLateWorkflowIdByNominalTime(bundleId,nominalTime,insertionFolder);
//    	
//    	Assert.assertNotNull(workflowIdList,"there was no workflow found with the desired nominal time!!!");
//    	
//    	for(String id:workflowIdList)
//    	{
//	    	boolean found=false;
//	    	for(String data:queueData)
//	    	{
//	    		if(data.split("\\$")[3].equalsIgnoreCase(id))
//	    			{
//	    				found=true;
//	    				break;
//	    			}
//	    	}
//	    	
//	    	Assert.assertTrue(found,"late data workflow "+id+" was not present in the queue!");
//    	}
//    	
//    	
//    }
//    
//    private void verifyLateWorkflowPresenceInQueue(List<String> queueData,String bundleId,DateTime nominalTime,String insertionFolder,DateTime insertionTime) throws Exception
//    {
//    	//List<String> workflowIdList=getLateWorkflowIdByNominalTime(bundleId,nominalTime,insertionFolder);
//    	HashMap<String,String> workflowMap=getLateWorkflowMap(bundleId, nominalTime, insertionFolder);
//    	
//    	Assert.assertNotNull(workflowMap,"there was no workflow found with the desired nominal time!!!");
//    	
//    	for(String id:workflowMap.keySet())
//    	{
//	    	boolean found=false;
//	    	for(String data:queueData)
//	    	{
//	    		if(data.split("\\$")[3].equalsIgnoreCase(id))
//	    			{
//	    				found=true;
//	    				break;
//	    			}
//	    	}
//	    	
//	    	if(!workflowMap.get(id).contains(insertionFolder))
//	    	{
//	    		Assert.assertFalse(found,"The ineligible late data workflow "+id+" seems to be appearing in the queue messages!");
//	    		logger.info("Verified that the ineligible late workflow "+id+" was not found in the queue list");
//	    		//isThisWorkflowIsCorrectlyFormed(bundleId,nominalTime,insertionFolder);
//	    	}
//	    	else
//	    	{
//	    		//first check if this workflow should have occured in the queue. It should not appear if its start time is before insertion folder time :P
//
//                    if(isEligible(bundleId,id,insertionFolder,insertionTime))
//	    		{
//	    			Assert.assertTrue(found,"eligible late data workflow "+id+" was not present in the queue!");
//	    			logger.info("Verified that the eligible late workflow "+id+" was found in the queue list");
//	    			//isThisWorkflowIsCorrectlyFormed(bundleId,nominalTime,insertionFolder);
//	    		}
//	    	}
//    	}
//    	
//    	
//    }
//    
//    
//    private boolean isEligible(String bundleId,String workflowId,String insertionFolder,DateTime insertionTime) throws Exception
//    {
//    	XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//    	
//    	WorkflowJob job=oozieClient.getJobInfo(workflowId);
//    	
//    	
//    	DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//    	
//    	if(formatter.print(new DateTime(job.getActions().get(0).getStartTime(),DateTimeZone.UTC)).compareTo(formatter.print(insertionTime))>0 && (job.getStatus().equals(Status.SUCCEEDED)))
//    		{
//                    
//    			return true;
//                    
//    		}
//    	else
//    	{
//    		return false;
//    	}
//    	
//    }
//    
//    
//    private List<String> getLateWorkflowIdByNominalTime(String bundleId,DateTime nominalTime,String insertionFolder) throws Exception
//    {
//    	XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    	
//		List<String> idList=new ArrayList<String>();
//		
//		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//		
//		for(CoordinatorJob job:bundleJob.getCoordinators())
//		{
//			if(job.getAppName().contains("LATE"))
//			{
//				CoordinatorJob jobInfo=oozieClient.getCoordJobInfo(job.getId());
//				
//				//validate that the late data coordinators present have a 
//				for(CoordinatorAction action:jobInfo.getActions())
//				{
//					//logger.info(action.getRunConf());
//					//actualNominalTimes.add(formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)));
//					if(formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)).equalsIgnoreCase(formatter.print(nominalTime)) || formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)).compareTo(formatter.print(nominalTime))>0)
//					{
//						if(action.getRunConf().contains(insertionFolder))
//						{
//							logger.info("found eligible late workflow: "+action.getExternalId());
//							idList.add(action.getExternalId());
//						}
//					}
//				}
//			}
//		}
//    	
//    	return idList;
//    }
//    
//    private HashMap<String,String> getLateWorkflowMap(String bundleId,DateTime nominalTime,String insertionFolder) throws Exception
//    {
//    	XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    	
//		HashMap<String,String> idMap=new HashMap<String,String>();
//		
//		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//		
//		for(CoordinatorJob job:bundleJob.getCoordinators())
//		{
//			if(job.getAppName().contains("LATE"))
//			{
//				CoordinatorJob jobInfo=oozieClient.getCoordJobInfo(job.getId());
//				
//				//validate that the late data coordinators present have a 
//				for(CoordinatorAction action:jobInfo.getActions())
//				{
//					//logger.info(action.getRunConf());
//					//actualNominalTimes.add(formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)));
//					if(formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)).equalsIgnoreCase(formatter.print(nominalTime)) || formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)).compareTo(formatter.print(nominalTime))>0)
//					{
//						//if(action.getRunConf().contains(insertionFolder))
//						{
//							logger.info("found eligible late workflow: "+action.getExternalId());
//							idMap.put(action.getExternalId(),action.getRunConf());
//						}
//					}
//				}
//			}
//		}
//    	
//    	return idMap;
//    }
//    
//    private void verifyLateDataCoordinators(String bundleId, DateTime nowValue,
//			String cutOffTime, String cutOffUnit,int expectedNumberOfLateCoordinators) throws Exception {
//		
//    	XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    	
//		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//		List<String> actualNominalTimes=new ArrayList<String>();
//		
//		for(CoordinatorJob job:bundleJob.getCoordinators())
//		{
//			if(job.getAppName().contains("LATE"))
//			{
//				CoordinatorJob jobInfo=oozieClient.getCoordJobInfo(job.getId());
//				
//				
//				//validate that the correct number of late data coordinators are present
//				Assert.assertEquals(jobInfo.getActions().size(),expectedNumberOfLateCoordinators,"Expected number of late data coordinators does not match!");
//				
//				//validate that the late data coordinators present have a 
//				for(CoordinatorAction action:jobInfo.getActions())
//				{
//					actualNominalTimes.add(formatter.print(new DateTime(action.getNominalTime(),DateTimeZone.UTC)));
//				}
//			}
//		}
//		
//		//ensure that the correct nominal times are there
//		for(int i=0;i<expectedNumberOfLateCoordinators;i++)
//		{
//			Assert.assertTrue(actualNominalTimes.contains(formatter.print(nowValue.plusMinutes(Integer.parseInt(cutOffTime)).plusMinutes(i))),"The nominal time value:"+formatter.print(nowValue.plusMinutes(Integer.parseInt(cutOffTime)))+" was not found in the list!!!");
//		}
//		
//	}
//
//    private boolean isDefaultWorkflowComplete(String bundleId) throws Exception
//    {
//		
//    	XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    	
//		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//		List<String> actualNominalTimes=new ArrayList<String>();
//		
//		for(CoordinatorJob job:bundleJob.getCoordinators())
//		{
//			if(job.getAppName().contains("DEFAULT"))
//			{
//				CoordinatorJob jobInfo=oozieClient.getCoordJobInfo(job.getId());
//				if(jobInfo.getStatus().equals(CoordinatorJob.Status.SUCCEEDED)|| jobInfo.getStatus().equals(CoordinatorJob.Status.FAILED) || jobInfo.getStatus().equals(CoordinatorJob.Status.KILLED))
//				{
//					return true;
//				}
//			}
//		}
//		
//		return false;
//    }
//
//    private int getTimeDiff(String timestamp1,String timestamp2) throws Exception
//    {
//    	DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//    	//DateTime start=new DateTime(timestamp1,DateTimeZone.UTC);
//    	//DateTime end=new DateTime(timestamp2,DateTimeZone.UTC);
//    	
//    	return Minutes.minutesBetween(formatter.parseDateTime(timestamp1),formatter.parseDateTime(timestamp2)).getMinutes();
//    }
//
//	@DataProvider(name="DP")
//    public Object[][] getData() throws Exception
//    {
//    	return Util.readBundles("src/test/resources/LateDataBundles");
//    }
//	
//	private boolean allRelevantWorkflowsAreOver(String bundleId,String insertionFolder) throws Exception
//	{
//		boolean finished=true;
//		
//                XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    	
//		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//		List<String> actualNominalTimes=new ArrayList<String>();
//		
//		for(CoordinatorJob job:bundleJob.getCoordinators())
//		{
//			if(job.getAppName().contains("DEFAULT"))
//			{
//				
//				CoordinatorJob coordJob=oozieClient.getCoordJobInfo(job.getId());
//                                
//                                
//				for(CoordinatorAction action:coordJob.getActions())
//				{
//                                        CoordinatorAction actionMan=oozieClient.getCoordActionInfo(action.getId());
//                                        
//					if(actionMan.getRunConf().contains(insertionFolder))
//					{
//                                            if((actionMan.getStatus().equals(CoordinatorAction.Status.SUCCEEDED)) || actionMan.getStatus().equals(CoordinatorAction.Status.KILLED) || actionMan.getStatus().equals(CoordinatorAction.Status.FAILED))
//                                            {
//						finished&=true; 
//                                            }
//                                            else
//                                            {
//                                                finished&=false;
//                                            }
//					}
//				}
//			}
//		}
//		
//		return finished;
//	}
//        
//        private void validateNoLateDataCoordinators(String bundleId) throws Exception
//        {
//                XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//    		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
//    		
//                //make sure only 1 coordinator exists
//                Assert.assertEquals(bundleJob.getCoordinators().size(),1,"Bundle has more or less coordinators than 1. Please check!");
//                //make sure the one coordinator present is not late data coordinator
//                Assert.assertTrue(bundleJob.getCoordinators().get(0).getAppName().contains("DEFAULT"),"The coordinator created is not a default coordinator!!!");
//                Assert.assertFalse(bundleJob.getCoordinators().get(0).getAppName().contains("LATE"),"The coordinator created is a late coordinator!!!");
//        }
//    
//    
//        private void waitTillCertainPercentageOfProcessIsOver(String bundleId,int percentage) throws Exception
//        {
//            
//            CoordinatorJob defaultCoordinator=getDefaultOozieCoord(bundleId);
//            
//            while(defaultCoordinator.getStatus().equals(CoordinatorJob.Status.PREP))
//            {
//                defaultCoordinator=getDefaultOozieCoord(bundleId);
//            }
//            
//            int totalCount=defaultCoordinator.getActions().size();
//            
//            int percentageConversion=(percentage*totalCount)/100;
//            
//            while(true && percentageConversion>0)
//            {
//                int doneBynow=0;
//                for(CoordinatorAction action:defaultCoordinator.getActions())
//                {
//                   CoordinatorAction actionInfo=getOozieActionInfo(action.getId());
//                   if(actionInfo.getStatus().equals(CoordinatorAction.Status.SUCCEEDED) || actionInfo.getStatus().equals(CoordinatorAction.Status.FAILED) || actionInfo.getStatus().equals(CoordinatorAction.Status.KILLED) || actionInfo.getStatus().equals(CoordinatorAction.Status.TIMEDOUT))
//                   {
//                       doneBynow++;
//                       if(doneBynow==percentageConversion)
//                       {
//                          return; 
//                       }
//                   }
//                }
//            }
//        }
//        
//        private CoordinatorAction getOozieActionInfo(String actionId) throws Exception
//        {
//           XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//           return client.getCoordActionInfo(actionId); 
//        }
//        
//        private CoordinatorJob getDefaultOozieCoord(String bundleId) throws Exception
//        {
//           XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//           BundleJob bundlejob=client.getBundleJobInfo(bundleId); 
//           
//           for(CoordinatorJob coord:bundlejob.getCoordinators())
//           {
//               if(coord.getAppName().contains("DEFAULT"))
//               {
//                    return client.getCoordJobInfo(coord.getId());
//               }
//           }
//           return null;
//        }
//        
//        private CoordinatorJob getLateOozieCoord(String bundleId) throws Exception
//        {
//            XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//            BundleJob bundlejob=client.getBundleJobInfo(bundleId);
//            
//           for(CoordinatorJob coord:bundlejob.getCoordinators())
//           {
//               if(coord.getAppName().contains("LATE"))
//               {
//                    return client.getCoordJobInfo(coord.getId());
//               }
//           }
//           return null;
//        }
//        
//        private void waitTillAllBundlesAreOver(String ... bundleIds) throws Exception
//        {
//            XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//            
//            while(true)
//            {
//                boolean finished=true;
//                
//                for(String id:bundleIds)
//                {
//                    CoordinatorJob defaultCoord=getDefaultOozieCoord(id);
//                    CoordinatorJob lateCoord=getLateOozieCoord(id);
//
//                    if(isCoordOver(defaultCoord) && isCoordOver(lateCoord))
//                    {
//                        finished&=true;
//                    }
//                    else
//                    {
//                        finished&=false;
//                    }
//                }
//                
//                if(finished) return;
//            }
//        }
//        
//        
//        private boolean isCoordOver(CoordinatorJob coord) throws Exception
//        {
//            if(coord.getStatus().equals(CoordinatorJob.Status.SUCCEEDED) || coord.getStatus().equals(CoordinatorJob.Status.DONEWITHERROR) || coord.getStatus().equals(CoordinatorJob.Status.FAILED) || coord.getStatus().equals(CoordinatorJob.Status.KILLED))
//            {
//                return true;
//            }
//            return false;
//        }
//        
//        private int getNumberOfWorkflowInstances(String bundleId) throws Exception
//        {
//            return getDefaultOozieCoord(bundleId).getActions().size();
//        }
//        
//        private int getNumberOfLateWorkflowInstances(String bundleId) throws Exception
//        {
//            return getLateOozieCoord(bundleId).getActions().size();
//        }
//        
//        private void validateNumberOfWorkflowInstances(int originalCount,String oldBundleId,String updatedBundleId) throws Exception
//        {
//            //first make sure sum of all parts is same
//            Assert.assertEquals(getNumberOfWorkflowInstances(oldBundleId)+getNumberOfWorkflowInstances(updatedBundleId),originalCount,"The total number of workflow instances dont match post update! Please check.");
//            
//            //now check for late coordinators
//            Assert.assertEquals(getNumberOfLateWorkflowInstances(oldBundleId)+getNumberOfLateWorkflowInstances(updatedBundleId),originalCount,"The total number of late data coordinators are not seemingly same as they should have been! Please check.");
//            
//            //now check if the number of coordinators and their late coordinators is same or not
//            Assert.assertEquals(getNumberOfWorkflowInstances(oldBundleId),getNumberOfLateWorkflowInstances(oldBundleId),"There is a mismatch between number of coordinators and their late counterparts in old bundle. please check.");
//            
//            // now check the same for updated bundle
//            Assert.assertEquals(getNumberOfWorkflowInstances(updatedBundleId),getNumberOfLateWorkflowInstances(updatedBundleId),"There is a mismatch between number of coordinators and their late counterparts in updated bundle. please check.");
//        }
//}
