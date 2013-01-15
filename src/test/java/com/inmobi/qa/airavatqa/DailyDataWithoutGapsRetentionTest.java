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
//import com.inmobi.qa.airavatqa.generated.feed.Feed;
//import com.inmobi.qa.airavatqa.generated.feed.Location;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import javax.xml.bind.JAXBContext;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.testng.Assert;
//import org.testng.TestNGException;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class DailyDataWithoutGapsRetentionTest {
//    
//    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//    IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    
//    DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//    
//    
//    
//// commenting this out since this takes abnormally long time!    
//    
////    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
////    public void test1MonthRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
////    {
////        //make sure the feed has full path
////        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
////        //put the desired interval in the feed
////        feed=Util.insertRetentionValueInFeed(feed,"months(1)");
////        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
////        bundle.getDataSets().add(feed);
////        bundle.generateUniqueBundle();
////        
////        //submit cluster
////        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
////        //submit feed
////        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
////        
////        //create data in cluster
////        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(35,0),0));
////        
////        //get Data created in the cluster
////        List<String> initialData=Util.getHadoopData();
////        
////        DateTime currentTime=new DateTime(DateTimeZone.UTC);
////        
////        //schedule feed
////        feedHelper.schedule(URLS.SCHEDULE_URL,Util.getInputFeedFromBundle(bundle));
////        
////        Thread.sleep(60000);
////        
////        //now look for cluster data
////        
////        ArrayList<String> finalData=Util.getHadoopData();
////        
////        //now see if retention value was matched to as expected
////        ArrayList<String> expectedOutput=Util.filterDataOnRetention(Util.getInputFeedFromBundle(bundle),1,"months", currentTime,initialData);
////        
////        System.out.println("initial data in system was:");
////        for(String line:initialData)
////        {
////            System.out.println(line);
////        }
////        
////        System.out.println("system output is:");
////        for(String line:finalData)
////        {
////            System.out.println(line);
////        }
////        
////        System.out.println("actual output is:");
////        for(String line:expectedOutput)
////        {
////            System.out.println(line);
////        }
////        
////        Assert.assertEquals(finalData.size(), expectedOutput.size(),"sizes of outputs are different! please check");
////        
////        Assert.assertTrue(Arrays.deepEquals(finalData.toArray(new String[finalData.size()]),expectedOutput.toArray(new String[expectedOutput.size()])));
////  
////    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test0MonthRetentionOnFullPathAndMissingDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(0)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        Util.assertFailed(feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        
//        //create data in cluster
//        //Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(2,0),0));
//        
//        //Util.CommonDataRetentionWorkflow(bundle,0,"months");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test10DayRetentionOnFullPathAndMissingDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"days(13)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,13,"days");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test1DayRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"days(1)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(2,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,1,"days");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test0DayRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"days(0)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        Util.assertFailed(feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        
//        //create data in cluster
//        //Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        //Util.CommonDataRetentionWorkflow(bundle,0,"days");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test10080MinuteRetentionOnFullPathAndMissingDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//            
//         System.setProperty("java.security.krb5.realm", "");
//        System.setProperty("java.security.krb5.kdc", "");    
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(10080)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(2,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,10080,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            //finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test10080MinuteRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(10080)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(10,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,10080,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test2160MinutesRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(2160)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(3,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,2160,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test1440MinuteRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(1440)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(3,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,1440,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test60MinutesRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(60)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        Util.assertFailed(feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        
//        //create data in cluster
//        //Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        //Util.CommonDataRetentionWorkflow(bundle,60,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test0MinuteRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"minutes(0)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        Util.assertFailed(feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        
//        //create data in cluster
//        //Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        //Util.CommonDataRetentionWorkflow(bundle,0,"minutes");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test0HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(0)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        Util.assertFailed(feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle)));
//        
//        //create data in cluster
//        //Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        //Util.CommonDataRetentionWorkflow(bundle,0,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test168HourlyRetentionOnFullPathAndMissingDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(168)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(2,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,168,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test168HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(168)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(10,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,168,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test48HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(48)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(3,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,48,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test24HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(24)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(3,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,24,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test36HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(36)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(3,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,36,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test6HourlyRetentionOnFullPathAndDailyDataWithoutGaps(Bundle bundle) throws Exception
//    {
//        try {
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}/${MONTH}/${DAY}/${HOUR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"hours(6)");
//        bundle.getDataSets().remove(Util.getInputFeedFromBundle(bundle));
//        bundle.getDataSets().add(feed);
//        bundle.generateUniqueBundle();
//        
//        //submit cluster
//        clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//        //submit feed
//        feedHelper.submitEntity(URLS.SUBMIT_URL,Util.getInputFeedFromBundle(bundle));
//        
//        //create data in cluster
//        Util.replenishData(Util.convertDatesToFolders(Util.getDailyDatesOnEitherSide(1,0),0));
//        
//        Util.CommonDataRetentionWorkflow(bundle,6,"hours");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            finalCheck(bundle);
//        }
//  
//    }
//    
//    @DataProvider(name="DP")
//    public Object[][] getBundles(Method m) throws Exception
//    {
//        return Util.readBundles("src/test/resources/RetentionBundles/valid/bundle1");
//    }
//    
//    private String setFeedPathValue(String feed,String pathValue) throws Exception
//    {
//        JAXBContext feedContext=JAXBContext.newInstance(Feed.class);
//        Feed feedObject=(Feed)feedContext.createUnmarshaller().unmarshal(new StringReader(feed));
//        
//        //set the value
//        for(Location location: feedObject.getLocations().getLocation())
//        {
//            if(location.getType().equalsIgnoreCase("data"))
//            {
//                location.setPath(pathValue);
//            }
//        }
//        
//        StringWriter feedWriter =new StringWriter();
//        feedContext.createMarshaller().marshal(feedObject,feedWriter);
//        return feedWriter.toString();
//    }
//    
//    private void finalCheck(Bundle bundle) throws Exception
//    {
//        feedHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
//        Util.verifyFeedDeletion(Util.getInputFeedFromBundle(bundle));
//    }
//}
