package com.inmobi.qa.falcon;
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ProcessEntityHelperImpl;
//import com.inmobi.qa.airavatqa.core.ProcessInstancesResult;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.generated.FrequencyType;
//import com.inmobi.qa.airavatqa.generated.PolicyType;
//import com.inmobi.qa.airavatqa.generated.Retry;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import org.apache.oozie.client.BundleJob;
//import org.apache.oozie.client.CoordinatorAction;
//import org.apache.oozie.client.CoordinatorJob;
//import org.apache.oozie.client.WorkflowJob;
//import org.apache.oozie.client.XOozieClient;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
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
//public class RetryTest {
//    
////    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
////    IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
////    ProcessEntityHelperImpl processHelper=new ProcessEntityHelperImpl();
////	
////    DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
////    Logger logger=Logger.getLogger(this.getClass());
////    //XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////    
////
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testRetryInProcessUpdate(Bundle bundle) throws Exception
////    {
////        try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(8);
////    	  
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        //now wait till the process is over
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        String status=Util.getBundleStatus(bundleId);
////        
////        waitTillCertainPercentageOfProcessHasStarted(bundleId,25);
////        
////        com.inmobi.qa.airavatqa.generated.Process oldProcessObject=bundle.getProcessObject();
////        
////        Retry retry=new Retry();
////        retry=bundle.getProcessObject().getRetry();
////        retry.setAttempts(BigInteger.valueOf(6L));
////        
////        bundle.setRetry(retry);
////        bundle.writeBundleToFiles();
////        
////        logger.info("going to update process at:"+DateTime.now(DateTimeZone.UTC));
////        Assert.assertTrue(processHelper.updateViaCLI(Util.readEntityName(bundle.getProcessData()),bundle.getProcessFilePath()).contains("updated successfully"),"process was not updated successfully");
////        String newBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
////        
////        //Assert.assertEquals(bundleId,newBundleId,"its creating a new bundle!!!");
////            
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        validateRetry(newBundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }
////    
//////    @Test(groups = {"0.1"},dataProvider="DP")
//////    public void testRetryInProcessReduceRetryUpdate(Bundle bundle) throws Exception
//////    {
//////        try {
//////            
//////        System.setProperty("java.security.krb5.realm", "");
//////        System.setProperty("java.security.krb5.kdc", "");
//////    	
//////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
//////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//////    	bundle.getDataSets().add(feed);
//////    	
//////    	//inject data into folders for processing
//////        Util.HDFSCleanup("lateDataTest/testFolders/");
//////        Util.lateDataReplenish(20,0,0);
//////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//////        
//////    	bundle.generateUniqueBundle();
//////    	
//////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//////    	
//////    	for(String data:bundle.getDataSets())
//////    	{
//////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//////    	}
//////    	
//////    	//set process start and end date
//////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(8);
//////    	  
//////    	bundle.setProcessValidity(startDate,endDate);
//////    	
//////    	//submit and schedule process
//////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//////        Util.assertSucceeded(response);
//////        
//////        //now wait till the process is over
//////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//////        String status=Util.getBundleStatus(bundleId);
//////        
//////        waitTillCertainPercentageOfProcessHasStarted(bundleId,100);
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue()-1);
//////        
//////        com.inmobi.qa.airavatqa.generated.Process oldProcessObject=bundle.getProcessObject();
//////        
//////        Retry retry=new Retry();
//////        retry=bundle.getProcessObject().getRetry();
//////        retry.setAttempts(BigInteger.valueOf(1));
//////        
//////        bundle.setRetry(retry);
//////        bundle.writeBundleToFiles();
//////        
//////        logger.info("going to update process at:"+DateTime.now(DateTimeZone.UTC));
//////        Assert.assertTrue(processHelper.updateViaCLI(URLS.HOSTNAME,Util.readEntityName(bundle.getProcessData()),bundle.getProcessFilePath()).contains("updated successfully"),"process was not updated successfully");
//////        String newBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
//////        
//////        //Assert.assertEquals(bundleId,newBundleId,"its creating a new bundle!!!");
//////            
//////        //now to validate all failed instances to check if they were retried or not.
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        validateRetry(newBundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        
//////        }
//////        catch(Exception e)
//////        {
//////            e.printStackTrace();
//////            throw new TestNGException(e.getCause());
//////        }
//////        finally {
//////            bundle.deleteBundle();
//////        }
//////    }
//////    
//////    
//////    @Test(groups = {"0.1"},dataProvider="DP")
//////    public void testRetryInProcessReduceRetryToManageableUpdate(Bundle bundle) throws Exception
//////    {
//////        try {
//////            
//////        System.setProperty("java.security.krb5.realm", "");
//////        System.setProperty("java.security.krb5.kdc", "");
//////    	
//////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
//////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//////    	bundle.getDataSets().add(feed);
//////    	
//////    	//inject data into folders for processing
//////        Util.HDFSCleanup("lateDataTest/testFolders/");
//////        Util.lateDataReplenish(20,0,0);
//////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//////        
//////    	bundle.generateUniqueBundle();
//////    	
//////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//////    	
//////    	for(String data:bundle.getDataSets())
//////    	{
//////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//////    	}
//////    	
//////    	//set process start and end date
//////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(8);
//////    	  
//////    	bundle.setProcessValidity(startDate,endDate);
//////    	
//////    	//submit and schedule process
//////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//////        Util.assertSucceeded(response);
//////        
//////        //now wait till the process is over
//////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//////        String status=Util.getBundleStatus(bundleId);
//////        
//////        waitTillCertainPercentageOfProcessHasStarted(bundleId,100);
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue()-1);
//////        
//////        com.inmobi.qa.airavatqa.generated.Process oldProcessObject=bundle.getProcessObject();
//////        
//////        Retry retry=new Retry();
//////        retry=bundle.getProcessObject().getRetry();
//////        retry.setAttempts(BigInteger.valueOf(2));
//////        
//////        bundle.setRetry(retry);
//////        bundle.writeBundleToFiles();
//////        
//////        logger.info("going to update process at:"+DateTime.now(DateTimeZone.UTC));
//////        Assert.assertTrue(processHelper.updateViaCLI(URLS.HOSTNAME,Util.readEntityName(bundle.getProcessData()),bundle.getProcessFilePath()).contains("updated successfully"),"process was not updated successfully");
//////        String newBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
//////        
//////        //Assert.assertEquals(bundleId,newBundleId,"its creating a new bundle!!!");
//////            
//////        //now to validate all failed instances to check if they were retried or not.
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        validateRetry(newBundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        
//////        }
//////        catch(Exception e)
//////        {
//////            e.printStackTrace();
//////            throw new TestNGException(e.getCause());
//////        }
//////        finally {
//////            bundle.deleteBundle();
//////        }
//////    }
//////    
//////    @Test(groups = {"0.1"},dataProvider="DP")
//////    public void testRetryInProcessReduceRetryToZeroUpdate(Bundle bundle) throws Exception
//////    {
//////        try {
//////            
//////        System.setProperty("java.security.krb5.realm", "");
//////        System.setProperty("java.security.krb5.kdc", "");
//////    	
//////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
//////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
//////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//////    	bundle.getDataSets().add(feed);
//////    	
//////    	//inject data into folders for processing
//////        Util.HDFSCleanup("lateDataTest/testFolders/");
//////        Util.lateDataReplenish(20,0,0);
//////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
//////        
//////    	bundle.generateUniqueBundle();
//////    	
//////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//////    	
//////    	for(String data:bundle.getDataSets())
//////    	{
//////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
//////    	}
//////    	
//////    	//set process start and end date
//////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(8);
//////    	  
//////    	bundle.setProcessValidity(startDate,endDate);
//////    	
//////    	//submit and schedule process
//////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//////        Util.assertSucceeded(response);
//////        
//////        //now wait till the process is over
//////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//////        String status=Util.getBundleStatus(bundleId);
//////        
//////        waitTillCertainPercentageOfProcessHasStarted(bundleId,100);
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue()-1);
//////        
//////        com.inmobi.qa.airavatqa.generated.Process oldProcessObject=bundle.getProcessObject();
//////        
//////        Retry retry=new Retry();
//////        retry=bundle.getProcessObject().getRetry();
//////        retry.setAttempts(BigInteger.valueOf(0));
//////        
//////        bundle.setRetry(retry);
//////        bundle.writeBundleToFiles();
//////        
//////        logger.info("going to update process at:"+DateTime.now(DateTimeZone.UTC));
//////        Assert.assertFalse(processHelper.updateViaCLI(URLS.HOSTNAME,Util.readEntityName(bundle.getProcessData()),bundle.getProcessFilePath()).contains("updated successfully"),"process was updated successfully");
//////        String newBundleId=instanceUtil.getLatestBundleID(Util.readEntityName(bundle.getProcessData()),"process");
//////        
//////        //Assert.assertEquals(bundleId,newBundleId,"its creating a new bundle!!!");
//////            
//////        //now to validate all failed instances to check if they were retried or not.
//////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        validateRetry(newBundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
//////        
//////        }
//////        catch(Exception e)
//////        {
//////            e.printStackTrace();
//////            throw new TestNGException(e.getCause());
//////        }
//////        finally {
//////            bundle.deleteBundle();
//////        }
//////    }
////    
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testRetryInSimpleFailureCase(Bundle bundle) throws Exception
////    {
////        try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(5);
////    	  
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        //now wait till the process is over
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        String status=Util.getBundleStatus(bundleId);
////        
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }
////    
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testUserRetryWhileAutomaticRetriesHappen(Bundle bundle) throws Exception
////    {
////        try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////        DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy-MM-dd/hh:mm");
////        
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(5);
////    	 
////        logger.info("process dates: "+startDate+","+endDate);
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        //now wait till the process is over
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        String status=Util.getBundleStatus(bundleId);
////        
////        Thread.sleep(420000);
////        
////        //now start firing random retries 
////        
////        logger.info("now firing user reruns:");
////        for(int i=0;i<5;i++)
////        {
////            processHelper.getProcessInstanceRerun(Util.readEntityName(bundle.getProcessData()),"?start="+formatter.print(startDate).replace("/","T")+"Z" +"&end="+formatter.print(endDate).replace("/","T")+"Z");
////            Thread.sleep(20000);
////        }           
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }   
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testUserRetryAfterAutomaticRetriesHappen(Bundle bundle) throws Exception
////    {
////        try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////        DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy-MM-dd/hh:mm");
////        
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"8","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(5);
////    	 
////        logger.info("process dates: "+startDate+","+endDate);
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        //now wait till the process is over
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        String status=Util.getBundleStatus(bundleId);
////        
////        
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        logger.info("now firing user reruns:");
////        
////        DateTime[] dateBoundaries=getFailureTimeBoundaries(bundleId);
////        ProcessInstancesResult piResult=processHelper.getProcessInstanceRerun(Util.readEntityName(bundle.getProcessData()),"?start="+formatter.print(dateBoundaries[0]).replace("/","T")+"Z" +"&end="+formatter.print(dateBoundaries[dateBoundaries.length-1]).replace("/","T")+"Z");
////        
////        Assert.assertEquals(piResult.getStatusCode(),0,"rerun failed miserably! you fool!");
////        
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue()+1);
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }       
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testRetryInSuspendedAndResumeCaseWithLateData(Bundle bundle) throws Exception
////    {
////         try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"10","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////        Retry retry =bundle.getProcessObject().getRetry();
////        retry.setAttempts(new BigInteger("10"));
////        
////        bundle.setRetry(retry);
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////        
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(6);
////    	  
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        ArrayList<DateTime> dates=null;
////        
////        do {
////        
////        	dates=Util.getStartTimeForRunningCoordinators(bundleId);
////        	
////        }while(dates==null);
////        
////        
////        
////        logger.info("Start time: "+formatter.print(startDate));
////        logger.info("End time: "+formatter.print(endDate));
////        logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
////        DateTime now=dates.get(0);
////        
////        if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
////        {
////        	now=startDate;
////        }
////        
////        
////        //now wait till the process is over
////        
////        String status=Util.getBundleStatus(bundleId);
////        
////        Thread.sleep(300000);
////        logger.info("now suspending the process altogether....");
////        
////        
////        Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
////        
////        
////        HashMap<String,Integer> initialMap=getFailureRetriesForEachWorkflow(getDefaultOozieCoord(bundleId));
////        logger.info("saved state of workflow retries");
////        
////        for(String key:initialMap.keySet())
////        {
////            logger.info(key+","+initialMap.get(key));
////        }
////        
////        Thread.sleep(300000);
////        
////        
////        HashMap<String,Integer> finalMap=getFailureRetriesForEachWorkflow(getDefaultOozieCoord(bundleId));
////        
////        logger.info("final state of process looks like:");
////        
////        for(String key:finalMap.keySet())
////        {
////            logger.info(key+","+finalMap.get(key));
////        }
////        
////        Assert.assertEquals(initialMap.size(),finalMap.size(),"a new workflow retried while process was suspended!!!!");
////        
////        for(String key:initialMap.keySet())
////        {
////            Assert.assertEquals(initialMap.get(key),finalMap.get(key),"values are different for workflow: "+key);
////        }
////        
////        logger.info("now resuming the process...");
////        Util.assertSucceeded(processHelper.resume(URLS.RESUME_URL,bundle.getProcessData()));
////        
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }
////    
////    
////    @Test(groups = {"0.1"},dataProvider="DP")
////    public void testRetryInLateDataCase(Bundle bundle) throws Exception
////    {
////        try {
////            
////        System.setProperty("java.security.krb5.realm", "");
////        System.setProperty("java.security.krb5.kdc", "");
////    	
////    	String feed=Util.setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/lateDataTest/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
////    	feed=Util.insertLateFeedValue(feed,"10","minutes");
////    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////    	bundle.getDataSets().add(feed);
////    	
////    	//inject data into folders for processing
////        Util.HDFSCleanup("lateDataTest/testFolders/");
////        Util.lateDataReplenish(20,0,0);
////        List<String> initialData=Util.getHadoopLateData(Util.getInputFeedFromBundle(bundle));
////        
////    	bundle.generateUniqueBundle();
////    	
////    	Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
////    	
////        
////    	for(String data:bundle.getDataSets())
////    	{
////    		Util.assertSucceeded(feedHelper.submitEntity(URLS.SUBMIT_URL,data));
////    	}
////    	
////    	//set process start and end date
////    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
////    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(5);
////    	  
////    	bundle.setProcessValidity(startDate,endDate);
////    	
////    	//submit and schedule process
////    	ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
////        Util.assertSucceeded(response);
////        
////        
////        String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////        ArrayList<DateTime> dates=null;
////        
////        do {
////        
////        	dates=Util.getStartTimeForRunningCoordinators(bundleId);
////        	
////        }while(dates==null);
////        
////        
////        
////        logger.info("Start time: "+formatter.print(startDate));
////        logger.info("End time: "+formatter.print(endDate));
////        logger.info("candidate nominal time:"+formatter.print(dates.get(0)));
////        DateTime now=dates.get(0);
////        
////        if(formatter.print(startDate).compareToIgnoreCase(formatter.print(dates.get(0)))>0)
////        {
////        	now=startDate;
////        }
////        
////        
////        //now wait till the process is over
////        
////        String status=Util.getBundleStatus(bundleId);
////        
////        boolean inserted=false;
////        
////        int attempts=0;
////        while(true || attempts>3600)
////        {
////            
////            //keep dancing
////            String insertionFolder=Util.findFolderBetweenGivenTimeStamps(now,now.plusMinutes(5),initialData);
////            
////            if(!inserted && validateFailureRetries(getDefaultOozieCoord(bundleId), bundle.getProcessObject().getRetry().getAttempts().intValue()))
////            {
////                logger.info("inserting data in folder "+insertionFolder+" at "+DateTime.now());
////                Util.injectMoreData(insertionFolder,"src/test/resources/OozieExampleInputData/lateData");
////                inserted=true;
////                break;
////            }
////            
////            status=Util.getBundleStatus(bundleId);
////            Thread.sleep(1000);
////            attempts++;
////        }
////        
////        //now to validate all failed instances to check if they were retried or not.
////        validateRetry(bundleId,bundle.getProcessObject().getRetry().getAttempts().intValue());
////        
////        }
////        catch(Exception e)
////        {
////            e.printStackTrace();
////            throw new TestNGException(e.getCause());
////        }
////        finally {
////            bundle.deleteBundle();
////        }
////    }
////    
////    
////    private void validateRetry(String bundleId,int maxNumberOfRetries) throws Exception
////    {
////       //validate that all failed processes were retried the specified number of times.
////       int attempt=0;
////       boolean result=false;
////       while(true)
////       {
////            result=ensureAllFailedInstancesHaveRetried(bundleId, maxNumberOfRetries);
////            
////            if(result || ++attempt>3600)
////            {
////                break;
////            }
////            else
////            {
////                Thread.sleep(1000);
////            }
////       }
////       Assert.assertTrue(result,"all retries were not attempted correctly!");
////    }
////    
//////    private boolean validateFailureRetries(CoordinatorJob coordinator,int maxNumberOfRetries) throws Exception
//////    {
//////        boolean retriedAll=true;
//////        boolean wentInside=false;
//////        boolean allSucceeded=true;
//////        XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
//////        
//////        for(CoordinatorAction action:coordinator.getActions())
//////        {
//////          
//////          if(null==action.getExternalId())
//////          {
//////              return false;
//////          }
//////          
//////          
//////          
//////          WorkflowJob actionInfo=client.getJobInfo(action.getExternalId());
//////          
//////          actionInfo.getRun();
//////          
//////          if(!(actionInfo.getStatus().equals(WorkflowJob.Status.SUCCEEDED) || actionInfo.getStatus().equals(WorkflowJob.Status.RUNNING)))
//////          {
//////              wentInside=true;
//////              
//////              logger.info("workflow "+actionInfo.getId()+" has action number: "+actionInfo.getRun());
//////              if(actionInfo.getRun()==maxNumberOfRetries)
//////              {
//////                  retriedAll&=true;
//////              }
//////              else
//////              {
//////                  Assert.assertTrue(actionInfo.getRun()<maxNumberOfRetries,"The workflow exceeded the max number of retries specified for it!!!!");
//////                  retriedAll&=false;
//////              }
//////              
//////          }
////////          else if(actionInfo.getStatus().equals(WorkflowJob.Status.SUCCEEDED))
////////          {
////////             allSucceeded&=true; 
////////          }
////////          else
////////          {
////////              allSucceeded&=false;
////////          }
//////                  
//////        }
//////        
//////        
//////        if(wentInside && retriedAll)
//////        {
//////            return retriedAll;
//////        }
//////        else if(wentInside)
//////        {
//////            return true;
//////        }
//////        else
//////        {
//////            return false;
//////        }
//////    }
////    
////    
////    
////    
////    
////
////    
////    
////    private boolean validateFailureRetries(CoordinatorJob coordinator,int maxNumberOfRetries) throws Exception
////    {
////       
////        HashMap<String,Boolean> workflowMap=new HashMap<String, Boolean>();
////        
////        XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////        
////        if(coordinator.getActions().isEmpty() || coordinator.getActions().size()==0)
////        {
////            return false;
////        }
////        
////        for(CoordinatorAction action:coordinator.getActions())
////        {
////          
////          if(null==action.getExternalId())
////          {
////              return false;
////          }
////          
////          
////          
////          WorkflowJob actionInfo=client.getJobInfo(action.getExternalId());
////          
////          
////          if(!(actionInfo.getStatus().equals(WorkflowJob.Status.SUCCEEDED) || actionInfo.getStatus().equals(WorkflowJob.Status.RUNNING)))
////          {
////              
////              logger.info("workflow "+actionInfo.getId()+" has action number: "+actionInfo.getRun());
////              if(actionInfo.getRun()==maxNumberOfRetries)
////              {
////                  workflowMap.put(actionInfo.getId(),true);
////              }
////              else
////              {
////                  Assert.assertTrue(actionInfo.getRun()<maxNumberOfRetries,"The workflow exceeded the max number of retries specified for it!!!!");
////                  workflowMap.put(actionInfo.getId(),false);
////              }
////              
////          }
////          else if(actionInfo.getStatus().equals(WorkflowJob.Status.SUCCEEDED))
////          {
////              workflowMap.put(actionInfo.getId(),true);
////          }
////        }
////        
////        //now to check each of these:
////        
////        //first make sure that the map has all the entries for the coordinator:
////        if(workflowMap.size()!=coordinator.getActions().size())
////        {
////            return false;
////        }
////        else
////        {
////           boolean result=true;
////           
////           for(String key:workflowMap.keySet())
////           {
////               result&=workflowMap.get(key);
////           }
////           
////           return result;
////        }
////    }
////    
////        private CoordinatorJob getDefaultOozieCoord(String bundleId) throws Exception
////        {
////           XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////           BundleJob bundlejob=client.getBundleJobInfo(bundleId); 
////           
////           for(CoordinatorJob coord:bundlejob.getCoordinators())
////           {
////               if(coord.getAppName().contains("DEFAULT"))
////               {
////                    return client.getCoordJobInfo(coord.getId());
////               }
////           }
////           return null;
////        }
////        
////        private CoordinatorJob getLateOozieCoord(String bundleId) throws Exception
////        {
////            XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////            BundleJob bundlejob=client.getBundleJobInfo(bundleId);
////            
////           for(CoordinatorJob coord:bundlejob.getCoordinators())
////           {
////               if(coord.getAppName().contains("LATE"))
////               {
////                    return client.getCoordJobInfo(coord.getId());
////               }
////           }
////           return null;
////        }
////    
////    @DataProvider(name="DP")
////    public Object[][] getData() throws Exception
////    {
////    	return Util.readBundles("src/test/resources/RetryTests");
////    }
////    
////    
////    	private boolean allRelevantWorkflowsAreOver(String bundleId,String insertionFolder) throws Exception
////	{
////		boolean finished=true;
////		
////                XOozieClient oozieClient=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////		BundleJob bundleJob = oozieClient.getBundleJobInfo(bundleId);
////    	
////		DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
////		List<String> actualNominalTimes=new ArrayList<String>();
////		
////		for(CoordinatorJob job:bundleJob.getCoordinators())
////		{
////			if(job.getAppName().contains("DEFAULT"))
////			{
////				
////				CoordinatorJob coordJob=oozieClient.getCoordJobInfo(job.getId());
////                                
////                                
////				for(CoordinatorAction action:coordJob.getActions())
////				{
////                                        CoordinatorAction actionMan=oozieClient.getCoordActionInfo(action.getId());
////                                        
////					if(actionMan.getRunConf().contains(insertionFolder))
////					{
////                                            if((actionMan.getStatus().equals(CoordinatorAction.Status.SUCCEEDED)) || actionMan.getStatus().equals(CoordinatorAction.Status.KILLED) || actionMan.getStatus().equals(CoordinatorAction.Status.FAILED))
////                                            {
////                                                logger.info("related workflow "+actionMan.getId()+" is over....");
////                                                finished&=true; 
////                                            }
////                                            else
////                                            {
////                                                finished&=false;
////                                            }
////					}
////				}
////			}
////		}
////		
////		return finished;
////	}
////        
////        
////        private boolean ensureAllFailedInstancesHaveRetried(String bundleId,int maxNumberOfRetries) throws Exception
////        {
////           boolean retried=false;
////           
////           CoordinatorJob defaultCoordinator=getDefaultOozieCoord(bundleId);
////           CoordinatorJob lateCoordinator=getLateOozieCoord(bundleId);
////           
////           boolean retriedAllDefault=validateFailureRetries(defaultCoordinator, maxNumberOfRetries);
////           boolean retriedAllLate=validateFailureRetries(lateCoordinator, maxNumberOfRetries);
////           
////           if(retriedAllDefault && retriedAllLate)
////           {
////               return true;
////           }
////           return retried;
////        }
////        
////        
////        
////        private void waitTillCertainPercentageOfProcessHasStarted(String bundleId,int percentage) throws Exception
////        {
////            
////            CoordinatorJob defaultCoordinator=getDefaultOozieCoord(bundleId);
////            
////            while(defaultCoordinator.getStatus().equals(CoordinatorJob.Status.PREP))
////            {
////                defaultCoordinator=getDefaultOozieCoord(bundleId);
////            }
////            
////            int totalCount=defaultCoordinator.getActions().size();
////            
////            int percentageConversion=(percentage*totalCount)/100;
////            
////            while(true && percentageConversion>0)
////            {
////                int doneBynow=0;
////                for(CoordinatorAction action:defaultCoordinator.getActions())
////                {
////                   CoordinatorAction actionInfo=getOozieActionInfo(action.getId());
////                   if(actionInfo.getStatus().equals(CoordinatorAction.Status.RUNNING))
////                   {
////                       doneBynow++;
////                       if(doneBynow==percentageConversion)
////                       {
////                          return; 
////                       }
////                   }
////                }
////            }
////        }
////        
////        private void waitTillCertainPercentageOfProcessAreKilled(String bundleId,int percentage) throws Exception
////        {
////            
////            CoordinatorJob defaultCoordinator=getDefaultOozieCoord(bundleId);
////            
////            while(defaultCoordinator.getStatus().equals(CoordinatorJob.Status.PREP))
////            {
////                defaultCoordinator=getDefaultOozieCoord(bundleId);
////            }
////            
////            int totalCount=defaultCoordinator.getActions().size();
////            
////            int percentageConversion=(percentage*totalCount)/100;
////            
////            while(true && percentageConversion>0)
////            {
////                int doneBynow=0;
////                for(CoordinatorAction action:defaultCoordinator.getActions())
////                {
////                   CoordinatorAction actionInfo=getOozieActionInfo(action.getId());
////                   if(actionInfo.getStatus().equals(CoordinatorAction.Status.KILLED) || actionInfo.getStatus().equals(CoordinatorAction.Status.FAILED))
////                   {
////                       doneBynow++;
////                       if(doneBynow==percentageConversion)
////                       {
////                          return; 
////                       }
////                   }
////                }
////            }
////        }
////        
////        
////        private CoordinatorAction getOozieActionInfo(String actionId) throws Exception
////        {
////           XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////           return client.getCoordActionInfo(actionId); 
////        }
////        
////        
////    private HashMap<String,Integer> getFailureRetriesForEachWorkflow(CoordinatorJob coordinator) throws Exception
////    {
////        boolean retriedAll=true;
////        boolean wentInside=false;
////        XOozieClient client=new XOozieClient(Util.readPropertiesFile("oozie_url"));
////        
////        HashMap<String,Integer> workflowRetryMap=new HashMap<String, Integer>();
////        
////        for(CoordinatorAction action:coordinator.getActions())
////        {
////          
////          if(null==action.getExternalId())
////          {
////                continue;
////          }
////          
////          
////          
////          WorkflowJob actionInfo=client.getJobInfo(action.getExternalId());
////          
////          
////          
////          
////             logger.info("adding workflow "+actionInfo.getId()+" to the map"); 
////             workflowRetryMap.put(actionInfo.getId(),actionInfo.getRun()); 
////          
////                  
////        }
////        return workflowRetryMap;
////    }
////    
////    private DateTime[] getFailureTimeBoundaries(String bundleId) throws Exception
////    {
////        List<DateTime> dateList=new ArrayList<DateTime>();
////        
////        CoordinatorJob coordinator=getDefaultOozieCoord(bundleId);
////        
////        for(CoordinatorAction action:coordinator.getActions())
////        {
////            if(action.getExternalId()!=null)
////            {
////                
////                 WorkflowJob jobInfo=client.getJobInfo(action.getExternalId());
////                 if(jobInfo.getRun()>0)
////                 {
////                    dateList.add(new DateTime(jobInfo.getStartTime(), DateTimeZone.UTC));
////                 }
////            }
////        }
////        
////        Collections.sort(dateList);
////        return dateList.toArray(new DateTime[dateList.size()]);
////    }
////        
////    private void checkIfRetriesWereTriggeredCorrectly(String retryType,int delay,String bundleId) throws Exception
////    {
////        
////        //it is presumed that this delay here will be expressed in minutes. Hourly/daily is unfeasible to check :)
////        
////        checkRetryTriggerForCoordinator(retryType, delay,getDefaultOozieCoord(bundleId));
////        checkRetryTriggerForCoordinator(retryType, delay,getLateOozieCoord(bundleId));
////
////    }
////    
////    private void displayInputs(String m,int delay,String policy,int retryAttempts) throws Exception
////    {
////        logger.info("******************");
////        logger.info("This test case is being executed with:");
////        logger.info("test case="+m);
////        logger.info("delay="+delay);
////        logger.info("policy="+policy);
////        logger.info("retries="+retryAttempts);
////        logger.info("******************");
////    }
////    
//////    private void checkIfRetriesWereTriggeredCorrectly(String retryType,int delay,String bundleId) throws Exception
//////    {
//////        
//////        //it is presumed that this delay here will be expressed in minutes. Hourly/daily is unfeasible to check :)
//////        
//////        checkRetryTriggerForCoordinator(retryType, delay,getDefaultOozieCoord(bundleId));
//////        checkRetryTriggerForCoordinator(retryType, delay,getLateOozieCoord(bundleId));
//////
//////    } 
////    
////        private void checkRetryTriggerForCoordinator(String retryType,int delay,CoordinatorJob coordinator) throws Exception
////    {
////        DateTimeFormatter formatter=DateTimeFormat.forPattern("HH:mm:ss");
////        
////        for(CoordinatorAction action: coordinator.getActions())
////        { 
////            
////            CoordinatorAction coordAction=getOozieActionInfo(action.getExternalId());
////            if(!coordAction.getStatus().equals(CoordinatorAction.Status.SUCCEEDED))
////            {
////                //first get data from logs:
////                ArrayList<String> instanceRetryTimes=Util.getInstanceRetryTimes(action.getExternalId());
////                ArrayList<String> instanceFinishTimes=Util.getInstanceFinishTimes(action.getExternalId()); 
////
////                logger.info("checking timelines for retry type "+retryType+" for delay "+delay+" for workflow id: "+action.getExternalId());
////
////                if(retryType.equalsIgnoreCase("backoff"))
////                {
////
////                    //in this case the delay unit will always be a constant time diff
////                    for(int i=0;i<instanceFinishTimes.size()-1;i++)
////                    {
////                      DateTime temp=formatter.parseDateTime(instanceFinishTimes.get(i));  
////                      Assert.assertEquals(formatter.print(temp.plusMinutes(delay)), instanceRetryTimes.get(i));  
////                    }
////                }
////                else
////                {
////                    //check for exponential
////
////                    for(int i=0;i<instanceFinishTimes.size()-1;i++)
////                    {
////                        DateTime temp=formatter.parseDateTime(instanceFinishTimes.get(i)); 
////                        Assert.assertEquals(formatter.print(temp.plusMinutes(delay)), instanceRetryTimes.get(i));
////                        delay*=2;
////                    }
////                }
////            }
////        }
////        
////    }
////        
////    private Retry getRetry(Bundle bundle,int delay,String delayUnits,String retryType,int retryAttempts) throws Exception
////    {
////        Retry retry=new Retry();
////        retry.setAttempts(BigInteger.valueOf(Long.valueOf(retryAttempts)));
////        retry.setDelay(BigInteger.valueOf(delay));
////        retry.setDelayUnit(FrequencyType.fromValue(delayUnits));
////        retry.setPolicy(PolicyType.fromValue(retryType));
////        return retry;
////    }  
////    
////    private Integer getDelay(int delay,int attempts,String attemptType) throws Exception
////    {
////        if(delay==0){delay=1;}
////        if(attempts==0){attempts=1;}
////        
////        
////        if(attemptType.equals("exp-backoff"))
////        {
////            return (Math.abs(delay))*(2^(Math.abs(attempts)));
////        }
////        else
////        {
////            return Math.abs(delay*attempts);
////        }
////        
////    }
//    
//}
