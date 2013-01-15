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
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.generated.Retry;
//import com.inmobi.qa.airavatqa.generated.coordinator.COORDINATORAPP;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import java.lang.reflect.Method;
//import java.math.BigInteger;
//import org.joda.time.DateTime;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class PartitionTest {
//    
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//       
//    @Test(groups={"0.1","0.2","sanity"},dataProvider="DP")
//    public void existingAndNonExistingPartitionsInProcess(Bundle bundle) throws Exception
//    {
//        try {
//       
//        System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");  
//        
//        Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));
//        
//        //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        //now add a partition to this
//        String newFeed=Util.CreateFeedWithNewPartitions(specimenFeed,"newrawbaby");
//        String expectedErrorMessage="Input path does not exist: hdfs://"+Util.readPropertiesFile("hadoop_url") +"/partition/testFolders/2010/01/02/01/newrawbaby";
//        //remove old;add new
//        bundle.getDataSets().remove(specimenFeed);
//        bundle.getDataSets().add(newFeed);
//        //insert partition info into process
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"newrawbaby"));
//        
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        //COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println("errorMessage: "+errorMessage);
//        Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getCause());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void NonExistingPartitionsInFeedOnly(Bundle bundle) throws Exception
//    {
//        try {
//        
//       System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");      
//       Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        //now add a partition to this
//        String newFeed=Util.CreateFeedWithNewPartitions(specimenFeed,"newrawbaby");
//        //String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/examples/input-data/rawLogs/2010/01/02/01/newrawbaby";
//        //remove old;add new
//        bundle.getDataSets().remove(specimenFeed);
//        bundle.getDataSets().add(newFeed);
//        
//        bundle.generateUniqueBundle();
//        
//        Retry retry=bundle.getProcessObject().getRetry();
//        retry.setAttempts(BigInteger.ZERO);
//        bundle.setRetry(retry);
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertNull(errorMessage,"error message should have been null!");
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getCause());
//        }
//        finally {
//            
//            //bundle.deleteBundle();
//        }
//    }
//    
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void NonExistingPartitionsInProcessOnly(Bundle bundle) throws Exception
//    {
//        try {
//       System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");      
//       Util.createHDFSPartitionFolders((Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00)));
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/partition/testFolders/2010/01/02/01/newrawbaby";
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"newrawbaby"));
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    }
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void NonExistingPartitionsWithSpecialCharactersInProcessOnly(Bundle bundle) throws Exception
//    {
//        try {
//       System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");      
//       Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));     
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/partition/testFolders/2010/01/02/01/newr@wb#by";
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"newr@wb#by"));
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    }
//
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void NonExistingPartitionsWithSpecialCharacters(Bundle bundle) throws Exception
//    {
//        try {
//       System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");      
//       Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));     
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        String newFeed=Util.CreateFeedWithNewPartitions(specimenFeed,"newr@wb#by");
//        
//      //remove old;add new
//        bundle.getDataSets().remove(specimenFeed);
//        bundle.getDataSets().add(newFeed);
//        String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/partition/testFolders/2010/01/02/01/newr@wb#by";
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"newr@wb#by"));
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    }  
//    
//
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void FilePartition(Bundle bundle) throws Exception
//    {
//        try {
//            System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", ""); 
//       Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));     
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        String newFeed=Util.CreateFeedWithNewPartitions(specimenFeed,"test*");
//        
//      //remove old;add new
//        bundle.getDataSets().remove(specimenFeed);
//        bundle.getDataSets().add(newFeed);
//        //String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/examples/input-data/rawLogs/2010/01/02/01/newr@wb#by";
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"test*"));
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertNull(errorMessage);
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    } 
//    
//    
//    @Test(groups={"0.1","0.2"},dataProvider="DP")
//    public void RegExPartition(Bundle bundle) throws Exception
//    {
//        try {
//            System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", ""); 
//       Util.createHDFSPartitionFolders(Util.getMinuteDatesOnEitherSide(new DateTime(2010,01,02,00,00),new DateTime(2010,01,04,01,00),00));     
//       //pick a feed
//        String specimenFeed=Util.getInputFeedFromBundle(bundle);
//        String newFeed=Util.CreateFeedWithNewPartitions(specimenFeed,"*test");
//        
//      //remove old;add new
//        bundle.getDataSets().remove(specimenFeed);
//        bundle.getDataSets().add(newFeed);
//        //String expectedErrorMessage="Input path does not exist: hdfs://localhost:8020/examples/input-data/rawLogs/2010/01/02/01/newr@wb#by";
//        bundle.setProcessData(Util.InsertPartitionInProcess(bundle.getProcessData(),Util.readDatasetName(specimenFeed),"*test"));
//        bundle.generateUniqueBundle();
//        
//        //now submit and schedule the crap
//        bundle.submitAndScheduleBundle();
//        
//        //now to get the coord!
//        COORDINATORAPP systemCoord=instanceUtil.getLatestCoordinatorApp(Util.readEntityName(bundle.getProcessData()),"process");
//        System.out.println(systemCoord.getName());
//        
//        //generate our own
//        COORDINATORAPP expectedCoord=CoordinatorHelper.createCoordinator(bundle);
//        
//        System.out.println(Util.printCoordinator(systemCoord));
//        System.out.println(Util.printCoordinator(expectedCoord));
//
//        
//        CoordinatorHelper.compareCoordinators(systemCoord, expectedCoord);
//        
//        String errorMessage=Util.getJobErrors(Util.getCoordID(Util.getOozieJobStatus(Util.getProcessName(bundle.getProcessData()),"NONE").get(0)));
//        System.out.println(errorMessage);
//        Assert.assertNull(errorMessage);
//        //Assert.assertTrue(errorMessage.contains(expectedErrorMessage));
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            bundle.deleteBundle();
//        }
//    } 
//    
//    
//    @DataProvider(name="DP")
//	public static Object[][] getTestData(Method m) throws Exception
//	{
//
//		return Util.readBundles("src/test/resources/PartitionBundles");
//	}
// 
//}
