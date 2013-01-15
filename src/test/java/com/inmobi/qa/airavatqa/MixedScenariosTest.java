///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import java.lang.reflect.Method;
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
//public class MixedScenariosTest {
//    
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//    Logger logger=Logger.getLogger(this.getClass());
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void suspendProcessWithMissingDataset(Bundle bundle)
//    {
//        try
//        {
//            bundle.submitAndScheduleBundle();
//            bundle.listBundle();
//            
//            //confirm bundle is running
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//
//            //lets delete one dataset
//            ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,bundle.getDataSets().get(0));
//
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//
//            //now lets try suspending the process
//            ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//
//            Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//            logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void suspendProcessWithMissingCluster(Bundle bundle)
//    {
//        try{
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        verifyDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2","sanity"},dataProvider="DP" )
//    public void resumeProcessWithMissingDataset(Bundle bundle) 
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,bundle.getDataSets().get(0));
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        //resume process
//        ServiceResponse resumeResponse=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//        Assert.assertEquals(Util.parseResponse(resumeResponse).getStatusCode(),200);
//       
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"The process is not suspended !");
//        
//        verifyDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void resumeProcessWithMissingCluster(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        //resume process
//        ServiceResponse resumeResponse=processHelper.resume(URLS.RESUME_URL,bundle.getProcessData());
//        Assert.assertEquals(Util.parseResponse(resumeResponse).getStatusCode(),200);
//       
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"The process is not suspended !");
//        
//        verifyDependencyListing(bundle);
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//        
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteRunningProcessWithMissingDataset(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,bundle.getDataSets().get(0));
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        verifyDependencyListing(bundle);
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//        
//        logger.info(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0));
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        
//        verifyFeedDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteRunningProcessWithMissingCluster(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        verifyDependencyListing(bundle);
//        
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        
//        verifyFeedDependencyListing(bundle);
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSuspendedProcessWithMissingCluster(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //suspend the process
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//        logger.info(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0));
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        verifyDependencyListing(bundle);
//        
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        
//        verifyFeedDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }   
// 
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSuspendedProcessWithMissingDataset(Bundle bundle)
//    {
//      try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //suspend the process
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//         Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        //lets delete one dataset
//        ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,bundle.getDataSets().get(0));
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        
//        verifyDependencyListing(bundle);
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        verifyFeedDependencyListing(bundle);
//        
//      }
//       catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSuspendedProcessWithMissingAllDataset(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        //suspend the process
//        
//        //now lets try suspending the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(suspendResponse).getMessage());
//         Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended !");
//        
//        //lets delete  datasets
//         
//        for(String data:bundle.getDataSets())
//        {
//            ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,data);
//        
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        }
//        
//        
//        verifyDependencyListing(bundle);
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("suspend response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        
//        verifyFeedDependencyListing(bundle);
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteRunningProcessWithMissingAllDataset(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//       
//        for(String data:bundle.getDataSets())
//        {
//            ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,data);
//        
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        }
//        
//        verifyDependencyListing(bundle);
//        
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("killed response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        
//        verifyFeedDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteRunningProcessWithMissingDataAndCluster(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//       
//        for(String data:bundle.getDataSets())
//        {
//            ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,data);
//        
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        }
//        
//        ServiceResponse clusterDelete=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        Assert.assertEquals(Util.parseResponse(clusterDelete).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(clusterDelete).getMessage());
//        
//        verifyDependencyListing(bundle);
//
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("killed response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        verifyFeedDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSuspendedProcessWithMissingDataAndCluster(Bundle bundle)
//    {
//        try {
//        bundle.submitAndScheduleBundle();
//        bundle.listBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//        
//        
//        //suspend the process
//        ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//        Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200);
//        
//        //verify suspended
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is not suspended even post schedule!");
//       
//        for(String data:bundle.getDataSets())
//        {
//            ServiceResponse deleteResponse=dataHelper.delete(URLS.DELETE_URL,data);
//        
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),400);
//            Assert.assertNotNull(Util.parseResponse(deleteResponse).getMessage());
//        }
//        
//        ServiceResponse clusterDelete=clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData());
//        Assert.assertEquals(Util.parseResponse(clusterDelete).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(clusterDelete).getMessage());
//        
//        verifyDependencyListing(bundle);
//        
//        //now lets try killing the process
//        ServiceResponse killResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(killResponse).getStatusCode(),200);
//        logger.info("killed response= "+Util.parseResponse(killResponse).getMessage());
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"The process is not killed !");
//        verifyFeedDependencyListing(bundle);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void scheduleNewProcessWithDeletedDataset(Bundle bundle)
//    {
//        try {
//        bundle.generateUniqueBundle();
//       
//        
//        //submit cluster
//        Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//        
//        //submit all datasets
//        for(String data:bundle.getDataSets())
//        {
//          Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,data)).getStatusCode(),200);
//          
//        }
//        
//        //now try submitting and scheduling our dear process
//        Assert.assertEquals(Util.parseResponse(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData())).getStatusCode(),200);
//        
//        bundle.listBundle();
//        
//        //now delete 1 of datasets >:)
//        ServiceResponse dataDelete=dataHelper.delete(URLS.DELETE_URL,bundle.getDataSets().get(0));
//        Assert.assertEquals(Util.parseResponse(dataDelete).getStatusCode(),400);
//        Assert.assertNotNull(Util.parseResponse(dataDelete).getMessage());
//        
//        //now schedule
//        Assert.assertEquals(Util.parseResponse(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData())).getStatusCode(),200);
//        verifyDependencyListing(bundle);
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void scheduleNewProcessWithDeletedCluster(Bundle bundle)
//    {
//        try {
//        bundle.generateUniqueBundle();
//        //bundle.listBundle();
//        
//        //submit cluster
//        Assert.assertEquals(Util.parseResponse(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData())).getStatusCode(),200);
//        
//        //submit all datasets
//        for(String data:bundle.getDataSets())
//        {
//          Assert.assertEquals(Util.parseResponse(dataHelper.submitEntity(URLS.SUBMIT_URL,data)).getStatusCode(),200);  
//          Assert.assertTrue(dataHelper.getDependencies(Util.readDatasetName(data)).contains("(cluster) "+Util.readClusterName(bundle.getClusterData())),"Feed does not have the designated cluster as dependency!");
//        }
//        
//        //now delete cluster >:)
//        Assert.assertEquals(Util.parseResponse(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData())).getStatusCode(),400);
//        
//        //now try submitting and scheduling our dear process
//        Assert.assertEquals(Util.parseResponse(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData())).getStatusCode(),200);
//        
//        bundle.listBundle();
//        
//        //now schedule
//        Assert.assertEquals(Util.parseResponse(processHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData())).getStatusCode(),200);
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void submitDataSetWithoutCluster(Bundle bundle) throws Exception
//    {
//        try {
//            bundle.generateUniqueBundle();
//            Util.assertFailed(dataHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void submitFeedReferingToDeletedCluster(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            Util.assertSucceeded(clusterHelper.delete(URLS.DELETE_URL,Util.readClusterName(bundle.getClusterData())));
//            Util.assertFailed(dataHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void suspendScheduledFeedWithRunningProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void suspendScheduledFeedWithSuspendedProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was not suspended incorrectly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void suspendScheduledFeedWithResumedProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            verifyDependencyListing(bundle);
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was not suspended correctly!");
//            
//            Util.assertSucceeded(processHelper.resume(URLS.RESUME_URL, bundle.getProcessData()));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was not resumed correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void suspendScheduledFeedWithKilledProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was impacted incorrectly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void suspendScheduledFeedPostProcessSubmission(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                        
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            verifyDependencyListing(bundle);
//                        
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteScheduledFeedWhileProcessIsRunning(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was affected incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteScheduledFeedWhileProcessIsSuspended(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteScheduledFeedWithResumedProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was not suspended correctly!");
//            
//            Util.assertSucceeded(processHelper.resume(URLS.RESUME_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was not resumed correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteScheduledFeedPostProcessSubmission(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteScheduledFeedWithKilledProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//            Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"KILLED").get(0).contains("KILLED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was impacted incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void resumeSuspendedFeedWithRunningProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not suspended correctly!");
//            
//            Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void resumeSuspendedFeedWhileProcessIsSuspended(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was affected incorrectly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL, feedToBeScheduled));
//                        
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void resumeSuspendedFeedWithResumedProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was not suspended correctly!");
//            
//            Util.assertSucceeded(processHelper.resume(URLS.RESUME_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was not resumed correctly!");
//            
//            Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was impacted incorrectly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    } 
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void resumeSuspendedFeedWithKilledProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//            Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//            Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was impacted incorrectly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void resumeSuspendedFeedPostProcessSubmission(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//                        
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//            
//            verifyFeedDependencyListing(bundle);
//            
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                        
//                        
//            Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            verifyDependencyListing(bundle);
//            
//                                    
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSuspendedFeedWhileProcessIsRunning(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                                                
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was affected incorrectly!");
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            verifyDependencyListing(bundle);
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was affected incorrectly!");
//            
//            verifyDependencyListing(bundle);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSuspendedFeedWhileProcessIsSuspended(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//            
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSuspendedFeedWithKilledProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//              
//            Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed correctly!");
//            
//            Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"KILLED").get(0).contains("KILLED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was impacted incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSuspendedFeedPostProcessSubmission(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//                                    
//            
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//                        
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"SUSPENDED").get(0).contains("SUSPENDED"),"Feed was not scheduled correctly!");
//                                    
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSubmittedFeedWhileProcessIsRunning(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            //Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                                                
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"process was affected incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSubmittedFeedWhileProcessIsSuspended(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            //Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was affected incorrectly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"process was affected incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSubmittedFeedWithKilledProcess(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            Util.assertSucceeded(processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData()));
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            //Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.delete(URLS.DELETE_URL, bundle.getProcessData()));
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed correctly!");
//            
//            Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"KILLED").get(0).contains("KILLED"),"Feed was not suspended correctly!");
//            
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was impacted incorrectly!");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP")
//    public void deleteSubmittedFeedPostProcessSubmission(Bundle bundle) throws Exception
//    {
//       try {
//            bundle.generateUniqueBundle();
//            Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//            
//            for(String data:bundle.getDataSets())
//            {
//                Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL, data));
//            }
//            
//            
//            String feedToBeScheduled=Util.getInputFeedFromBundle(bundle);
//            
//            //Util.assertSucceeded(dataHelper.schedule(URLS.SCHEDULE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//            
//            Util.assertSucceeded(processHelper.submitEntity(URLS.SUBMIT_URL,bundle.getProcessData()));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                        
//            Util.assertFailed(dataHelper.delete(URLS.DELETE_URL, feedToBeScheduled));
//            
//            //Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feedToBeScheduled),"RUNNING").get(0).contains("RUNNING"),"Feed was not scheduled correctly!");
//                                    
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            bundle.deleteBundle();
//        } 
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteScheduledFeedWithMissingCluster(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        //lets delete the cluster
//        Util.assertFailed(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSuspendedFeedWithMissingCluster(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"SUSPENDED").get(0).contains("SUSPENDED"),"The feed is not running even post schedule!");
//        
//        //lets delete the cluster
//        Util.assertFailed(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"SUSPENDED").get(0).contains("SUSPENDED"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteSubmittedFeedWithMissingCluster(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//
//        //lets delete the cluster
//        Util.assertFailed(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteResumedFeedWithMissingCluster(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"SUSPENDED").get(0).contains("SUSPENDED"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        
//        //lets delete the cluster
//        Util.assertFailed(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteClusterAfterDeletingResumedFeed(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"SUSPENDED").get(0).contains("SUSPENDED"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.resume(URLS.RESUME_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running even post schedule!");
//        
//        
//        //lets delete the cluster
//        Util.assertSucceeded(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteClusterAfterDeletingSuspendedFeed(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.suspend(URLS.SUSPEND_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"SUSPENDED").get(0).contains("SUSPENDED"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running even post schedule!");
//        
//        
//        //lets delete the cluster
//        Util.assertSucceeded(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteClusterAfterDeletingScheduledFeed(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        
//        //confirm bundle is running
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"RUNNING").get(0).contains("RUNNING"),"The feed is not running even post schedule!");
//        
//        Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle)));
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running even post schedule!");
//        
//        
//        //lets delete the cluster
//        Util.assertSucceeded(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(Util.getInputFeedFromBundle(bundle)),"KILLED").get(0).contains("KILLED"),"The feed is not running !");
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP" )
//    public void deleteClusterAfterDeletingSubmittedFeed(Bundle bundle)
//    {
//        try {
//        
//        bundle.generateUniqueBundle();
//        Util.assertSucceeded(clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData()));
//        Util.assertSucceeded(dataHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        //bundle.submitAndScheduleBundle();
//        Util.assertSucceeded(dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle)));
//        //lets delete the cluster
//        Util.assertSucceeded(clusterHelper.delete(URLS.DELETE_URL,bundle.getClusterData()));
//        
//        
//        }
//         catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{bundle.deleteBundle();}catch(Exception e){};
//        }
//        
//    }
//    
//    @DataProvider(name="DP")
//	public static Object[][] getTestData(Method m) throws Exception
//	{
//
//		return Util.readBundles();
//	}
//    
//    private void verifyDependencyListing(Bundle bundle) throws Exception
//    {
//            //display dependencies of process:
//            String dependencies=processHelper.getDependencies(Util.readEntityName(bundle.getProcessData()));
//            
//            //verify presence
//            Assert.assertTrue(dependencies.contains("(cluster) "+Util.readClusterName(bundle.getClusterData())));
//            for(String feed:bundle.getDataSets())
//            {
//                Assert.assertTrue(dependencies.contains("(feed) "+Util.readDatasetName(feed)));
//                Assert.assertTrue(dataHelper.getDependencies(Util.readDatasetName(feed)).contains("(cluster) "+Util.readClusterName(bundle.getClusterData())));
//                Assert.assertFalse(dataHelper.getDependencies(Util.readDatasetName(feed)).contains("(process)" +Util.readEntityName(bundle.getProcessData())));
//            }
//            
//    }
//    
//    private void verifyFeedDependencyListing(Bundle bundle) throws Exception
//    {
//        for(String feed:bundle.getDataSets())
//            {
//                
//                Assert.assertTrue(dataHelper.getDependencies(Util.readDatasetName(feed)).contains("(cluster) "+Util.readClusterName(bundle.getClusterData())));
//                Assert.assertFalse(dataHelper.getDependencies(Util.readDatasetName(feed)).contains("(process)" +Util.readEntityName(bundle.getProcessData())));
//            }
//    }
//    
//    
//}
