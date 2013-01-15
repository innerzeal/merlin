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
//import org.apache.commons.lang.StringUtils;
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
//public class EntityManagerScheduleTest {
//    
//        IEntityManagerHelper processManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//        Logger logger=Logger.getLogger(this.getClass());
//        
//        @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//        public void scheduleValidProcessViaBundle(Bundle bundle) throws Exception
//        {
//            
//            try {
//                
//                String coordinator=bundle.submitAndScheduleBundle();
//                logger.info("coordinator created="+coordinator);
//                //validate the state of the coordinator
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//            	
//           }
//           catch(Exception e)
//           {
//               e.printStackTrace();
//               throw new TestNGException(e.getMessage());
//           }
//           finally {
//                
//                bundle.deleteBundle();
//                
//            } 
//            
//            
//            
//        }
//        
//        @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//        public void scheduleAlreadyScheduledProcess(Bundle bundle) throws Exception
//        {
//            try {
//                
//                
//                
//                String coordinator=bundle.submitAndScheduleBundle();
//                logger.info("coordinator created="+coordinator);
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//                
//                //now to reschedule bundle :|
//                ServiceResponse response=processManagerHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData());
//                Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
//                logger.info("status after rescheduling a scheduled process is: ");
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"),"scheduled Bundle has been rescheduled!");
//                
//                // add more validations here later
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                throw new TestNGException(e.getMessage());
//                
//            }
//        }
//        
//        @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//        public void scheduleSuspendedProcess(Bundle bundle) throws Exception
//        {
//                
//                
//                String coordinator=bundle.submitAndScheduleBundle();
//                logger.info("coordinator created="+coordinator);
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains(Util.readEntityName(bundle.getProcessData())),"The process is not running even post schedule!");
//                
//                //now to suspend bundle :|
//                ServiceResponse response=processManagerHelper.suspend(URLS.SUSPEND_URL,bundle.getProcessData());
//                Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//                logger.info("suspended status: "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED"));
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"running Bundle has been suspended!");
//                
//                //now to re-schedule the suspended bundle
//                ServiceResponse reScheduleResponse=processManagerHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData());
//                logger.info("rescheduled state: "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())));
//                Assert.assertEquals(Util.parseResponse(reScheduleResponse).getStatusCode(),400);
//                Assert.assertNotNull(Util.parseResponse(reScheduleResponse).getMessage());
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"SUSPENDED").get(0).contains("SUSPENDED"),"The process is getting scheduled post suspend!");
//        }
//            
//          @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//          public void scheduleKilledProcess(Bundle bundle) throws Exception
//          {
//              
//                
//                String coordinator=bundle.submitAndScheduleBundle();
//                logger.info("coordinator created="+coordinator);
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())).get(0).contains("RUNNING"),"The process is not running even post schedule!");
//                
//                //now to suspend bundle :|
//                ServiceResponse response=processManagerHelper.delete(URLS.DELETE_URL,bundle.getProcessData());
//                Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
//                logger.info("suspended status: "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED"));
//                Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"KILLED").get(0).contains("KILLED"),"running Bundle has not been killed!");
//                
//                //now to re-schedule the killed bundle
//                ServiceResponse reScheduleResponse=processManagerHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData());
//                logger.info("rescheduled state: "+Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData())));
//                Assert.assertEquals(Util.parseResponse(reScheduleResponse).getStatusCode(),400);
//                Assert.assertNotNull(Util.parseResponse(reScheduleResponse).getMessage());
//          }
//          
//          @Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
//          public void scheduleNonExistentProcess(Bundle bundle) throws Exception
//          {
//              bundle.generateUniqueBundle();
//              ServiceResponse scheduleResponse=processManagerHelper.schedule(URLS.SCHEDULE_URL,bundle.getProcessData());
//              Assert.assertEquals(Util.parseResponse(scheduleResponse).getStatusCode(),400);
//              Assert.assertNotNull(Util.parseResponse(scheduleResponse).getMessage());
//              
//          } 
//        
//       
//        
//        
//  
////        
////        @Test
////        public void testFetch() throws Exception
////        {
////            //COORDINATORAPP coordinator=Util.getOozieCoordinator("agregator-coord9");
////            //logger.info("got the coordinator with the name: "+coordinator.getName());
////            Util.getOozieCoordinator("agregator-coord10");
////            logger.info("hello");
////            
////        }
//        
//        @DataProvider(name="DP")
//        public Object[][] getTestData(Method m) throws Exception
//        {
//            
//                return Util.readBundles();
//            
//            
//            
//        }
//}
