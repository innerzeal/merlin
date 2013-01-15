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
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class EntityManagerSubmitAndScheduleTest {
//    
//    IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    
//    @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//    public void snsNewProcess(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            for(String dataset:bundle.getDataSets())
//            {
//               Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
//            }
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//            
//            
//            
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//            
//            Thread.sleep(5000);
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsExistingProcess(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            for(String dataset:bundle.getDataSets())
//            {
//               Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
//            }
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//            
//            //try to submitand schedule the same process again
//            response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//            
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsProcessWithoutDataSet(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.FAILED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsProcessWithoutDataSetAndCluster(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            
//            
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.FAILED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsRunningProcess(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            for(String dataset:bundle.getDataSets())
//            {
//               Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
//            }
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//            
//            response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.FAILED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsSuspendedProcess(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            for(String dataset:bundle.getDataSets())
//            {
//               Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
//            }
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//            
//            Assert.assertEquals(Util.parseResponse(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData())).getStatusCode(),200);
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPEND"));
//            
//            response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.FAILED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPEND"));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void snsDeletedProcess(Bundle bundle)
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//            for(String dataset:bundle.getDataSets())
//            {
//               Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
//            }
//
//            ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//            
//            Assert.assertEquals(Util.parseResponse(processHelper.delete(URLS.DELETE_URL,bundle.getProcessData())).getStatusCode(),200);
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"));
//            
//            response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//            Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
//            Assert.assertNotNull(Util.parseResponse(response).getMessage());
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        
//    }
//    
//    
//    @DataProvider(name="DP")
//    public Object[][] getBundleData() throws Exception
//    {
//        return Util.readBundles();
//    }
//}
