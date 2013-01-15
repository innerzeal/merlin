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
//public class EntityManagerSuspendTest {
//    
//    IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    Logger logger=Logger.getLogger(this.getClass());
//    
//        @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//        public void suspendValidSubmittedProcess(Bundle bundle) throws Exception
//        {
//            try {
//                
//                bundle.submitAndScheduleBundle();
//                //lets just presume the bundle has been scheduled correctly.
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"bundle has not been scheduled properly!");
//                
//                //lets just get the store snapshots
//                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//                ArrayList<String> dataStoreInitialState=Util.getDataSetStoreInfo();
//                
//                ArrayList<String> processArchiveState=Util.getArchiveStoreInfo();
//                ArrayList<String> dataStoreArchiveState=Util.getDataSetArchiveInfo();
//                
//                //now lets try to suspend the damn thing
//                ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//                
//                //ensure suspension is awesome! Add a check via system call to oozie later...There are some problems with how to call up oozie darling here
//                Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.SUCCEEDED,"Suspension did not go through :(");
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"running Bundle has not been suspended!");
//                
//                //just make sure that the process and the datasets have not been deleted from stores during suspension!
//                ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
//                
//                Assert.assertEquals(processStoreInitialState.size(),processStoreFinalState.size(),"Something has changed in process store on suspend!!!");
//                
//                ArrayList<String> dataStoreFinalState=Util.getDataSetStoreInfo();
//                
//                Assert.assertEquals(dataStoreInitialState.size(),dataStoreFinalState.size(),"Something has changed in data store on suspend!!!");
//                
//                ArrayList<String> dataArchiveFinalState=Util.getDataSetArchiveInfo();
//                
//                Assert.assertEquals(dataStoreArchiveState.size(),dataArchiveFinalState.size(),"Something has changed in dataset archive on suspend!");
//                
//                ArrayList<String> processArchiveFinalState=Util.getArchiveStoreInfo();
//                
//                Assert.assertEquals(processArchiveState.size(),processArchiveFinalState.size(),"Something has changed in process archive on suspend!");
//              
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                throw new TestNGException(e.getMessage());
//            }
//            finally {
//                
//                    bundle.deleteBundle();
//            }
//        }
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP" )
//        public void suspendValidSuspendedProcess(Bundle bundle) throws Exception
//        {
//            try {
//                
//                String coordinator=bundle.submitAndScheduleBundle();
//                logger.info("created coordinator "+coordinator);
//                //lets just presume the bundle has been scheduled correctly.
//                logger.info("job state= "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING"));
//                
//                //lets just get the store snapshots
//                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
//                ArrayList<String> dataStoreInitialState=Util.getDataSetStoreInfo();
//                
//                ArrayList<String> processArchiveState=Util.getArchiveStoreInfo();
//                ArrayList<String> dataStoreArchiveState=Util.getDataSetArchiveInfo();
//                
//                //now lets try to suspend the damn thing
//                ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//                
//                //ensure suspension is awesome! Add a check via system call to oozie later...There are some problems with how to call up oozie darling here
//                Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.SUCCEEDED,"Suspension did not go through :(");
//                logger.info("result of suspension call= "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED"));
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"running Bundle has not been suspended!");
//                
//                //just make sure that the process and the datasets have not been deleted from stores during suspension!
//                ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
//                
//                Assert.assertEquals(processStoreInitialState.size(),processStoreFinalState.size(),"Something has changed in process store on suspend!!!");
//                
//                ArrayList<String> dataStoreFinalState=Util.getDataSetStoreInfo();
//                
//                Assert.assertEquals(dataStoreInitialState.size(),dataStoreFinalState.size(),"Something has changed in data store on suspend!!!");
//                
//                ArrayList<String> dataArchiveFinalState=Util.getDataSetArchiveInfo();
//                
//                Assert.assertEquals(dataStoreArchiveState.size(),dataArchiveFinalState.size(),"Something has changed in dataset archive on suspend!");
//                
//                ArrayList<String> processArchiveFinalState=Util.getArchiveStoreInfo();
//                
//                Assert.assertEquals(processArchiveState.size(),processArchiveFinalState.size(),"Something has changed in process archive on suspend!");
//                
//                //suspend the damn thing again >:)
//                suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//                
//                Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.SUCCEEDED,"A suspended process got suspended again!!!! :O ");
//                Assert.assertNotNull(Util.parseResponse(suspendResponse).getMessage());
//                Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),200,"A suspended process got suspended again!!!! :O ");
//                logger.info("Suspending a suspended process again: "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED"));
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"running Bundle has its state changed!!");
//                
//                //check the data store again
//                ArrayList<String> processStoreFinalState2=Util.getProcessStoreInfo();
//                Assert.assertEquals(processStoreFinalState2.size(),processStoreFinalState.size(),"Something has changed in process store on invalid suspend!!!");
//                ArrayList<String> dataStoreFinalState2=Util.getDataSetStoreInfo();
//                Assert.assertEquals(dataStoreFinalState2.size(),dataStoreFinalState.size(),"Something has changed in data store on invalid suspend!!!");
//                ArrayList<String> dataArchiveFinalState2=Util.getDataSetArchiveInfo();
//                
//                Assert.assertEquals(dataArchiveFinalState2.size(),dataArchiveFinalState.size(),"Something has changed in dataset archive on invalid suspend!");
//                ArrayList<String> processArchiveFinalState2=Util.getArchiveStoreInfo();
//                Assert.assertEquals(processArchiveFinalState2.size(),processArchiveFinalState.size(),"Something has changed in process archive on invalid suspend!");
// 
//              
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                throw new TestNGException(e.getMessage());
//            }
//            finally {
//                
//                    bundle.deleteBundle();
//            }
//        }
//    
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP" )
//        public void suspendedDeletedProcess(Bundle bundle) throws Exception
//        {
//            bundle.submitAndScheduleBundle();
//            
//            //delete the bundle
//            ServiceResponse deleteResponse=processHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//            
//            Assert.assertEquals(Util.parseResponse(deleteResponse).getStatusCode(),200);
//            Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"process was not killed :(");
//            
//            //now try suspending this
//            ServiceResponse suspendResponse=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//            Assert.assertEquals(Util.parseResponse(suspendResponse).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(suspendResponse).getStatus(),APIResult.Status.FAILED);
//        }
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP" )
//        public void suspendedNonExistentProcess(Bundle bundle) throws Exception
//        {
//            bundle.generateUniqueBundle();
//            
//            //now try suspending this
//            ServiceResponse scheduleResponse=processHelper.suspend(URLS.SCHEDULE_URL,bundle.getProcessData());
//            Assert.assertEquals(Util.parseResponse(scheduleResponse).getStatusCode(),400);
//            Assert.assertEquals(Util.parseResponse(scheduleResponse).getStatus(),APIResult.Status.FAILED);
//        }
//        
//        @Test(groups={"0.1","0.2"},dataProvider="DP")
//        public void suspendSubmittedProcess(Bundle bundle) throws Exception
//        {
//            bundle.generateUniqueBundle();
//            ServiceResponse response=bundle.submitBundle();
//            Util.assertSucceeded(response);
//            response=processHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//            
//            Util.assertFailed(response);
//        }
//    
//        @DataProvider(name="DP")
//	public Object[][] getTestData(Method m) throws Exception
//	{
//
//		return Util.readBundles();
//	}
//    
//    
//    
//}
