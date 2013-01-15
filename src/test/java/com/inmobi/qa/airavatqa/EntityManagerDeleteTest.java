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
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collections;
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
//public class EntityManagerDeleteTest {
//    
////    final String VALIDATE_URL="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/validate";
////    final String SUBMIT_URL="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/submit";
////    final String GET_ENTITY_DEFINITION="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/definition";
////    final String DELETE_URL="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/delete";
//    final String FAIL_DELETE="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><status>FAILED</status><message>Entity: sample does not exists</message></result>";
//    
//    Logger logger=Logger.getLogger(this.getClass());
//    IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    
//    @Test(groups={"01","0.2","submitValidData","sanity"},dataProvider="validDP" )
//    public void deleteProcessFile(Bundle bundle)
//    {
//        IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//        
//        try {
//            logger.info("testCase: deleteProcessFile");
//            //logger.info("The testcase is being run for "+filename);
//
//            
//            //first lets just get the store and archive data for process
//            ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//            
//            logger.info("initial state:");
//            
//            logger.info(processStoreInitialState.toString());
//            logger.info(archiveStoreInitialState.toString());
//            
//            bundle.submitBundle();
////            ServiceResponse submittedResult=helperEntity.submitEntity(SUBMIT_URL,bundle.submitBundle());
////            Assert.assertEquals(Util.parseResponse(submittedResult).getStatus(),APIResult.Status.SUCCEEDED,"The submission did not go through :(");
////            Assert.assertEquals(Util.parseResponse(submittedResult).getStatusCode(),200);
//            
//            //lets get the entity
//            ServiceResponse getEntityResult=helperEntity.getEntityDefinition(URLS.GET_ENTITY_DEFINITION.getValue(), bundle.getProcessData());
//            //Assert.assertEquals(Util.parseResponse(getEntityResult).getMessage(),data,"The entity submitted and the one returned looks mighty different!");
//            
//            ServiceResponse getDeleteResult=helperEntity.delete(URLS.DELETE_URL, bundle.getProcessData());
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.SUCCEEDED,"delete does not seem to be successful...");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatusCode(),200,"delete does not seem to be successful...");
//            //logger.info("delete result= "+getDeleteResult);
//            //helperEntity.validateResponse(getDeleteResult, expectedResult, filename);
//            
//            
//            //get the aftermath
//            ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
//            
//            //now to check that the process store has not changes from the initial one.
//            Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
//            Assert.assertTrue(processStoreFinalState.equals(processStoreInitialState),"The process store values are not the same!");
//            
//            
//            //now to check that there is only one file that has been added to process archive and that has the same name as our deleted file
//            archiveStoreFinalState.removeAll(archiveStoreInitialState);
//            Assert.assertEquals(archiveStoreFinalState.size(),1,"more than 1 different files were found!");
//            Assert.assertTrue(archiveStoreFinalState.get(0).contains(Util.readEntityName(bundle.getProcessData())),"seems like some other file was being archived upon deletion!");
//        }
//        catch(Exception e)
//        {
//            throw new TestNGException(e.getMessage());
//            
//        }
//       
//    }
//    
//    @Test(groups={"01","0.2","submitValidData"},dataProvider="validDP" )
//    public void deleteRedundantOperationProcess(Bundle bundle)
//    {
//        IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//        
//        try {
//        	logger.info("testCase: deleteRedundantOperationProcess");
//           // logger.info("The testcase is being run for "+filename);
//
//            bundle.submitBundle();
//            
////            ServiceResponse submittedResult=helperEntity.submitEntity(SUBMIT_URL,data);
////            Assert.assertEquals(Util.parseResponse(submittedResult).getStatus(),APIResult.Status.SUCCEEDED,"The submission did not go through :(");
//            
//            //lets get the entity
//            ServiceResponse getEntityResult=helperEntity.getEntityDefinition(URLS.GET_ENTITY_DEFINITION.getValue(), bundle.getProcessData());
//            //Assert.assertEquals(getEntityResult,data,"The entity submitted and the one returned looks mighty different!");
//            
//            ServiceResponse getDeleteResult=helperEntity.delete(URLS.DELETE_URL,bundle.getProcessData());
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.SUCCEEDED,"delete does not seem to be successful...");
//            logger.info("delete result= "+getDeleteResult);
//            //helperEntity.validateResponse(getDeleteResult, expectedResult, filename);
//            
//            //lets save the state of our repositories here
//            ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//            
//            //redundant call
//            getDeleteResult=helperEntity.delete(URLS.DELETE_URL,bundle.getProcessData());
//            Assert.assertNotSame(getDeleteResult,bundle.getProcessData(),"The deleted entity is being returned :O !");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.FAILED,"Oops! the returned status does not match here!");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatusCode(),400,"Oops! the returned statuscode does not match here!");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getMessage(),Util.readEntityName(bundle.getProcessData()) +" (process) not found","Oops! the returned statuscode does not match here!");
//            
//            //now to make sure that nothing has changed post deletion.
//            //get the aftermath
//            ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
//            
//            //now to check that the process store has not changes from the initial one.
//            Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
//            Assert.assertTrue(processStoreFinalState.equals(processStoreInitialState),"The process store values are not the same!");
//            
//            Collections.sort(archiveStoreFinalState);Collections.sort(archiveStoreFinalState);
//            Assert.assertTrue(archiveStoreFinalState.equals(archiveStoreFinalState),"The archive store values are not the same!");
//            
//        }
//        catch(Exception e)
//        {
//            throw new TestNGException(e.getMessage());
//            
//        }
//       
//    }
//    
//    @Test(groups={"01","0.2","submitValidData"},dataProvider="validDP" )
//    public void deleteNonExistantProcess(Bundle bundle)
//    {
//        IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//        
//        try {
//        	logger.info("testCase: deleteNonExistantProcess");
//            //logger.info("The testcase is being run for "+filename);
//            
//            ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
//            
//            
//            ServiceResponse getDeleteResult=helperEntity.delete(URLS.DELETE_URL,bundle.getProcessData());
//            Assert.assertNotSame(getDeleteResult,bundle.getProcessData(),"The deleted entity is being returned :O !");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getMessage(),Util.readEntityName(bundle.getProcessData())+" (process) not found","Oops! the returned data does not match here!");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatus(),APIResult.Status.FAILED,"Oops! the operation status does not match here!");
//            Assert.assertEquals(Util.parseResponse(getDeleteResult).getStatusCode(),400,"Oops! the operation status code does not match here!");
//            
//            
//            ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
//            ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
//            
//            Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
//            Assert.assertTrue(processStoreFinalState.equals(processStoreInitialState),"The process store values are not the same!");
//            
//            Collections.sort(archiveStoreFinalState);Collections.sort(archiveStoreFinalState);
//            Assert.assertTrue(archiveStoreFinalState.equals(archiveStoreFinalState),"The archive store values are not the same!");
//            
//
//        }
//        catch(Exception e)
//        {
//            throw new TestNGException(e.getMessage());
//            
//        }
//       
//    }
//    
//    @Test(groups={"01","0.2","submitValidData","sanity"},dataProvider="validDP" )
//    public void deleteSuspendedProcess(Bundle bundle) throws Exception
//    {
//        bundle.generateUniqueBundle();
//        bundle.submitAndScheduleBundle();
//        
//        ServiceResponse suspendResponse=helperEntity.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"));
//        
//        ServiceResponse deleteResponse=helperEntity.delete(URLS.DELETE_URL,bundle.getProcessData());
//        
//        Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),200);
//        
//        Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"));
//    }
//    
////    @DataProvider(name="validDP")
////    public Object[][] getData(Method m) throws Exception
////    {
////        
////        String testDataFolderPath = Util.getTestDataFolderPath(m.getName());
////
////        return Util.getDataFromFolder(testDataFolderPath,m.getName());
////
////    }
//    
//    @DataProvider(name="validDP")
//    public Object[][] getData(Method m) throws Exception
//    {
//        return Util.readBundles();
//    }
//    
//    
//    
//}
