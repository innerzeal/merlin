///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.coordinatorCore.CoordinatorHelper;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.generated.coordinator.COORDINATORAPP;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import com.inmobi.qa.airavatqa.mq.Consumer;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class JMSIntegrationTest {
//    
//    
//    
//    @Test(dataProvider="DP")
//    public void processWithSingleOutput(Bundle bundle) throws Exception
//    {
//      try { 
//       
//       
//         System.out.println("executing processWithSingleOutput...."); 
//          
//          //start taking a look at the JMS queue
//       
//       populateData(bundle);   
//          
//       Assert.assertTrue(bundle.submitAndScheduleBundle().contains("RUNNING"));
//       
//       Consumer consumer=new Consumer("IVORY."+Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData())); 
//       consumer.start(); 
//       
//       //Thread.sleep(120000);
//       
//       String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
//       String status=Util.getBundleStatus(bundleId);
//       while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
//       {
//          status=Util.getBundleStatus(bundleId); 
//       }
//       
//       
//       consumer.stop();
//       
//       commonVerificationSteps(bundle, consumer);
//       
//       
//      }
//      catch(Exception e)
//      {
//          e.printStackTrace();
//          //throw new TestNGException(e.getMessage());
//      }
//       
//      finally { 
//       bundle.deleteBundle();
//      }
//      
//    }
// 
//    
//    
////    @Test(dataProvider="DP")
////    public void processWithSingleOutputTwoSuccessiveRuns(Bundle bundle) throws Exception
////    {
////      try {
////          
////       System.out.println("executing processWithSingleOutputTwoSuccessiveRuns....");    
////       
////       //start taking a look at the JMS queue
////       
////       populateData(bundle);
////       
////       Assert.assertTrue(bundle.submitAndScheduleBundle().contains("RUNNING"));
////       
////       Consumer consumer=new Consumer(Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData())); 
////       consumer.start(); 
////       
////       //Thread.sleep(120000);
////       String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////       String status=Util.getBundleStatus(bundleId);
////       while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
////       {
////          status=Util.getBundleStatus(bundleId); 
////       }
////       
////       consumer.stop();
////       
////       //commonVerificationSteps(bundle, consumer);
////       
////       //save initial output
////       List<String> consumerData=consumer.getTextMessageList();
////       
////       //now to re-run the process
////       
////       IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
////       Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL,bundle.getProcessData()));
////       Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
////       consumer=new Consumer(Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData()));
////       Util.assertSucceeded(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData()));
////       
////       consumer.start();
////       
////       bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////       status=Util.getBundleStatus(bundleId);
////       while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
////       {
////          status=Util.getBundleStatus(bundleId); 
////       }
////       
////       consumer.stop();
////       
////       System.out.println("now consumer has "+consumer.getTextMessageList().size()+" lines of data!");
////       Assert.assertEquals(consumer.getTextMessageList().size(),consumerData.size()*2);
////       commonVerificationSteps(bundle, consumer);
////      }
////      catch(Exception e)
////      {
////          e.printStackTrace();
////          throw new TestNGException(e.getMessage());
////      }
////       
////      finally { 
////       bundle.deleteBundle();
////      }
////      
////    }
////    
////    @Test(dataProvider="DP")
////    public void processSuspended(Bundle bundle) throws Exception
////    {
////      try { 
////          
////       System.out.println("executing processSuspended....");   
////       
////       //start taking a look at the JMS queue
////       
////       populateData(bundle);
////       
////       
////       Assert.assertTrue(bundle.submitAndScheduleBundle().contains("RUNNING"));
////       
////       Consumer consumer=new Consumer(Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData())); 
////       consumer.start(); 
////       
////       while(!(consumer.getTextMessageList().size()>0))
////       {
////           Thread.sleep(1000);
////       }
////       
////       int initialSize=consumer.getTextMessageList().size();
////       List<String> initialState=consumer.getTextMessageList();
////       
////       IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
////       Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
////       
////       Thread.sleep(10000);
////       
////       consumer.stop();
////       
////       List<String> finalState=consumer.getTextMessageList();
////       
////       Assert.assertTrue(Arrays.deepEquals(finalState.toArray(new String[finalState.size()]),initialState.toArray(new String[initialState.size()])),"It seems there were more messages incoming even when the process was suspended!");
////       
////       
////      }
////      catch(Exception e)
////      {
////          e.printStackTrace();
////          throw new TestNGException(e.getMessage());
////      }
////       
////      finally { 
////       bundle.deleteBundle();
////      }
////      
////    }   
////    
////    @Test(dataProvider="DP")
////    public void processDeleted(Bundle bundle) throws Exception
////    {
////      try { 
////          
////       System.out.println("executing processDeleted.....");   
////       
////       //start taking a look at the JMS queue
////       populateData(bundle);
////       
////       Assert.assertTrue(bundle.submitAndScheduleBundle().contains("RUNNING"));
////       
////       Consumer consumer=new Consumer(Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData())); 
////       consumer.start(); 
////       
////       while(!(consumer.getTextMessageList().size()>0))
////       {
////           Thread.sleep(1000);
////       }
////       
////       int initialSize=consumer.getTextMessageList().size();
////       List<String> initialState=consumer.getTextMessageList();
////       
////       IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
////       Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL,bundle.getProcessData()));
////       
////       Thread.sleep(10000);
////       
////       consumer.stop();
////       
////       List<String> finalState=consumer.getTextMessageList();
////       
////       Assert.assertTrue(Arrays.deepEquals(finalState.toArray(new String[finalState.size()]),initialState.toArray(new String[initialState.size()])),"It seems there were more messages incoming even when the process was suspended!");
////       
////       
////      }
////      catch(Exception e)
////      {
////          e.printStackTrace();
////          throw new TestNGException(e.getMessage());
////      }
////    }
////    
////    
////    @Test(dataProvider="DP")
////    public void processResumed(Bundle bundle) throws Exception
////    {
////      try { 
////       
////       System.out.println("executing processResumed....");   
////          
////       //start taking a look at the JMS queue
////       
////       populateData(bundle);
////       Assert.assertTrue(bundle.submitAndScheduleBundle().contains("RUNNING"));
////       
////       Consumer consumer=new Consumer(Util.readEntityName(bundle.getProcessData()),Util.readQueueLocationFromCluster(bundle.getClusterData())); 
////       consumer.start(); 
////       
////       while(!(consumer.getTextMessageList().size()>0))
////       {
////           Thread.sleep(1000);
////       }
////       
////       int initialSize=consumer.getTextMessageList().size();
////       List<String> initialState=consumer.getTextMessageList();
////       
////       System.out.println("Going to suspend process now:");
////       IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
////       Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
////       Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"Process suspension did not work!!!!");
////       System.out.println("process suspended!");
////       
////       Thread.sleep(30000);
////       
////       Util.assertSucceeded(processHelper.resume(URLS.RESUME_URL,bundle.getProcessData()));
////       
////       String bundleId=Util.getCoordID(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"NONE").get(0));
////       String status=Util.getBundleStatus(bundleId);
////       while(!(status.contains("SUCCEEDED") || status.contains("DONEWITHERROR") || status.contains("FAILED") || status.contains("KILLED") ))
////       {
////          status=Util.getBundleStatus(bundleId); 
////       }
////       
////       consumer.stop();
////       
////       List<String> finalState=consumer.getTextMessageList();
////       
////       //Assert.assertFalse(Arrays.deepEquals(finalState.toArray(new String[finalState.size()]),initialState.toArray(new String[initialState.size()])),"It seems there were more messages incoming even when the process was suspended!");
////       commonVerificationSteps(bundle, consumer);
////       
////      }
////      catch(Exception e)
////      {
////          e.printStackTrace();
////          throw new TestNGException(e.getMessage());
////      }
////       
////      finally { 
////       bundle.deleteBundle();
////      }
////      
////    }   
//    
//    
//    private void commonVerificationSteps(Bundle bundle,Consumer consumer) throws Exception
//    {
////       for(String data:consumer.getTextMessageList())
////       {
////           System.out.println(data);
////       }
//        
//            System.out.println("deleted data which has been received from messaging queue:");
//            for(HashMap<String,String> data:consumer.getMessageData())
//            {
//                 System.out.println("*************************************");
//                for(String key:data.keySet())
//                {
//                     System.out.println(key+"="+data.get(key));
//                }
//                 System.out.println("*************************************");
//            }
//       
//       
//       ArrayList<String> jobIds=Util.getCoordinatorJobs(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//       
//       //validate process output
//       Assert.assertTrue(!(consumer.getMessageData()!=null && consumer.getMessageData().isEmpty()),"The consumer looks empty when it should not be");
//       
//       HashMap<String,String> outputFeedMap=Util.getOutputFeedMapFromBundle(bundle);
//       
//       for(HashMap<String,String> data:consumer.getMessageData())
//       {
//           Assert.assertTrue(data.size()==15,"Not all fields have been emitting correctly.");
//           
//           Assert.assertEquals(Util.readEntityName(bundle.getProcessData()),data.get("entityName"),"Process name is not present in data line "+data);
//           
//           
//           //check if the output feed is present or not!
//           
//           
//           boolean feedFound=false;
//           for(String outputFeed:outputFeedMap.keySet())
//           {
//             if(outputFeed.equalsIgnoreCase(data.get("entityName")))
//             {
//                 feedFound=true;
//                 //Assert.assertTrue(data.split(",")[2].contains(outputFeedMap.get(outputFeed)),"expected feed path was not present in the process!");
//                 Assert.assertTrue(data.get("feedInstancePaths").contains(outputFeedMap.get(outputFeed).split("\\$")[0]),"expected feed path was not present in the process!");
//                 break;
//             }
//           }
//           
//           Assert.assertTrue(feedFound,"The feed "+data.get("entityName")+" was not found in the output! Something is wrong! Please check");
//       }
//       
//       //now to compare if job ids are present or not
//       for(String jobId:jobIds)
//       {
//          boolean found=false;
//          for(HashMap<String,String> data:consumer.getMessageData())
//          {
//             if(data!=null)
//             {
//                 if(jobId.equalsIgnoreCase(data.get("workflowId")))
//                 {
//                     found=true;
//                     break;
//                 }
//             }
//          }
//          Assert.assertTrue(found,"job Id : "+jobId+" was not found in output!!!");
//       }
//       
//       //now to validate if the coordinator is being created correctly
//       CoordinatorHelper cHelper=new CoordinatorHelper();
//       COORDINATORAPP expectedCoord=cHelper.createCoordinator(bundle);
//       COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//       
//       
//       System.out.println("System coordinator:"+Util.printCoordinator(systemCoord));
//       System.out.println("Expected coordinator:"+Util.printCoordinator(expectedCoord));
//       
//       //CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//       
//    }
//    
//    
//    
//    
//    private void populateData(Bundle bundle) throws Exception
//    {
//        System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");
//        
//        Util.HDFSCleanup("/examples/input-data/rawLogs/");
//        Util.lateDataReplenish("/examples/input-data/rawLogs/",30,0,0);
//        
//        //set process start and end date
//    	DateTime startDate=new DateTime(DateTimeZone.UTC).plusMinutes(2);
//    	DateTime endDate=new DateTime(DateTimeZone.UTC).plusMinutes(5);
//    	  
//    	bundle.setProcessValidity(startDate,endDate);
//        
//        String feed=Util.getInputFeedFromBundle(bundle);
//        feed=Util.insertLateFeedValue(feed,"5","minutes");
//    	bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//    	bundle.getDataSets().add(feed);
//    }
//    
//    
//    @DataProvider(name="DP")
//    public Object [][] getBundles(Method m) throws Exception
//    {
//        return Util.readBundles("src/test/resources/JMSBundle/valid/bundle1");
//    }
//    
//}
