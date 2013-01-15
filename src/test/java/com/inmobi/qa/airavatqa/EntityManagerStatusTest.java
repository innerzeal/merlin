///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
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
//public class EntityManagerStatusTest {
//    
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    Logger logger=Logger.getLogger(this.getClass());
//   
//    
//    @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//    public void getStatusForRunningProcess(Bundle bundle)
//    {
//        try {
//            
//            bundle.generateUniqueBundle();
//            bundle.submitAndScheduleBundle();
//            
//            //lets check the status :)
//            Assert.assertEquals(Util.parseResponse(processHelper.getStatus(URLS.STATUS_URL,bundle.getProcessData())).getMessage(),"RUNNING");
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void getStatusForSuspendedProcess(Bundle bundle)
//    {
//        try {
//            
//            
//            bundle.submitAndScheduleBundle();
//            processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//            //lets check the status :)
//            Assert.assertEquals(Util.parseResponse(processHelper.getStatus(URLS.STATUS_URL,bundle.getProcessData())).getMessage(),"SUSPENDED");
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"));
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void getStatusForSubmittedProcess(Bundle bundle)
//    {
//         try {
//            
//            
//            bundle.submitBundle();
//            //lets check the status :)
//            Assert.assertEquals(Util.parseResponse(processHelper.getStatus(URLS.STATUS_URL,bundle.getProcessData())).getMessage(),"SUBMITTED");
//            
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//    }
//    
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP" )
//    public void getStatusForDeletedProcess(Bundle bundle)
//    {
//        try {
//            
//            
//            bundle.submitAndScheduleBundle();
//            bundle.deleteBundle();
//            //lets check the status :)
//            Assert.assertEquals(Util.parseResponse(processHelper.getStatus(URLS.STATUS_URL,bundle.getProcessData())).getMessage(),Util.readEntityName(bundle.getProcessData())+"(process) is not present");
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"));
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
//    public void getStatusForNonExistentProcess(Bundle bundle)
//    {
//        try {
//            
//            //lets check the status :)
//            Assert.assertEquals(Util.parseResponse(processHelper.getStatus(URLS.STATUS_URL,bundle.getProcessData())).getMessage(),Util.readEntityName(bundle.getProcessData())+" is not present");
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
//    
//    
//    @DataProvider(name="DP")
//    public Object[][] getData() throws Exception
//    {
//        return Util.readBundles();
//        
//    }
//    
//}
