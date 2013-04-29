/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.interfaces.EntityHelperFactory;
import com.inmobi.qa.ivory.interfaces.IEntityManagerHelper;
import com.inmobi.qa.ivory.response.ServiceResponse;
import com.inmobi.qa.ivory.supportClasses.ENTITY_TYPE;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;

/**
 *
 * @author rishu.mehrotra
 */
public class FeedSchedule {
    
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
      IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
      IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
      
    public void submitCluster(Bundle bundle) throws Exception
    {
        //submit the cluster
        ServiceResponse response=clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
        Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
        Assert.assertNotNull(Util.parseResponse(response).getMessage());
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleValidFeed(Bundle bundle)
    {
        try
        {
            bundle.generateUniqueBundle();
            submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            //submit feed
            ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
            Util.assertSucceeded(response);
            //now schedule the thing
            response=feedHelper.schedule(URLS.SCHEDULE_URL,feed);
            Util.assertSucceeded(response);
            
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING").get(0).contains("RUNNING"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            try{feedHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));}catch(Exception e){}
        }
    }
    
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleAlreadyScheduledFeed(Bundle bundle)
    {
        try {
            
            bundle.generateUniqueBundle();
            submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            
            ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL, feed);
            Util.assertSucceeded(response);
            
            response=feedHelper.schedule(URLS.SCHEDULE_URL, feed);
            Util.assertSucceeded(response);
            
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING").get(0).contains("RUNNING"));
            
            //now try re-scheduling again
            response=feedHelper.schedule(URLS.SCHEDULE_URL, feed);
            Util.assertFailed(response);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
         
            try {
                
                feedHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
            }
            catch(Exception e){}
        }
    }
    
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleSuspendedFeed(Bundle bundle) throws Exception
    {
        try {
            
            bundle.generateUniqueBundle();
            submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            Util.assertSucceeded(feedHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));
            
            //now suspend
            Util.assertSucceeded(feedHelper.suspend(URLS.SUSPEND_URL, feed));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED").get(0).contains("SUSPENDED"));
            
            //now schedule this!
            Util.assertFailed(feedHelper.schedule(URLS.SCHEDULE_URL, feed));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleKilledFeed(Bundle bundle) throws Exception
    {
        try {
            
            bundle.generateUniqueBundle();
            submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            Util.assertSucceeded(feedHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));
            
            //now suspend
            Util.assertSucceeded(feedHelper.delete(URLS.DELETE_URL, feed));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"KILLED").get(0).contains("KILLED"));
            
            //now schedule this!
            Util.assertFailed(feedHelper.schedule(URLS.SCHEDULE_URL, feed));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            feedHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleNonExistentFeed(Bundle bundle) throws Exception
    {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        String feed=Util.getInputFeedFromBundle(bundle);
        Util.assertFailed(feedHelper.schedule(URLS.SCHEDULE_URL,feed));
    }
    
    
      
    
}
