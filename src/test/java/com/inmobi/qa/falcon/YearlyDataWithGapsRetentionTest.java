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
//public class YearlyDataWithGapsRetentionTest {
//    
//    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//    IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//    IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//    
//    DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test24MonthRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(24)");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(35,2));
//        
//        Util.CommonDataRetentionWorkflow(bundle,24,"months");
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            throw new TestNGException(e.getMessage());
//        }
//        finally {
//            
//            try{finalCheck(bundle);}catch(Exception e){}
//        }
//  
//    }
//    
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test15MonthRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(15)");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(35,3));
//        
//        Util.CommonDataRetentionWorkflow(bundle,15,"months");
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
//    public void test12MonthRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(12)");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(3,3));
//        
//        Util.CommonDataRetentionWorkflow(bundle,12,"months");
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
//    public void test11MonthRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(11)");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(35,3));
//        
//        Util.CommonDataRetentionWorkflow(bundle,11,"months");
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
//    
//    @Test(groups = {"0.1","0.2"},dataProvider="DP",priority=-1)
//    public void test1MonthRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
//        //put the desired interval in the feed
//        feed=Util.insertRetentionValueInFeed(feed,"months(1)");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(35,3));
//        
//        Util.CommonDataRetentionWorkflow(bundle,1,"months");
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
//    public void test0MonthRetentionOnFullPathAndMissingDailyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        //Util.replenishData(Util.getYearlyDatesOnEitherSide(5,1));
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
//    public void test10DayRetentionOnFullPathAndMissingDailyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(1,0));
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
//    public void test1DayRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test0DayRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
////        //create data in cluster
////        Util.replenishData(Util.getYearlyDatesOnEitherSide(3,1));
////        
////        Util.CommonDataRetentionWorkflow(bundle,0,"days");
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
//    public void test10080MinuteRetentionOnFullPathAndMissingDailyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,2));
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
//    public void test10080MinuteRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(3,1));
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
//    public void test2160MinutesRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(3,1));
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
//    public void test1440MinuteRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(3,1));
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
//    public void test60MinutesRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        //Util.replenishData(Util.getYearlyDatesOnEitherSide(3,1));
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
//    public void test0MinuteRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        //Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test0HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
////        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
////        
////        Util.CommonDataRetentionWorkflow(bundle,0,"hours");
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
//    public void test168HourlyRetentionOnFullPathAndMissingDailyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test168HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test48HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test24HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test36HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    public void test6HourlyRetentionOnFullPathAndYearlyData(Bundle bundle) throws Exception
//    {
//        try {
//            //System.setProperty("java.security.krb5.realm", "");
//            //System.setProperty("java.security.krb5.kdc", "");
//        //make sure the feed has full path
//        String feed=setFeedPathValue(Util.getInputFeedFromBundle(bundle),"/retention/testFolders/${YEAR}");
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
//        Util.replenishData(Util.getYearlyDatesOnEitherSide(4,1));
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
//    
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
//    
//    
//}
