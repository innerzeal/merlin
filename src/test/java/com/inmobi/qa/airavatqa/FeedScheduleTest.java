/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
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
public class FeedScheduleTest {
	

	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
    
      IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
      IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
      PrismHelper prismHelper=new PrismHelper("prism.properties");
  	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");
    
  	
  	public void submitCluster(Bundle bundle) throws Exception
    {
       
        bundle.submitClusters(prismHelper);
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleValidFeed(Bundle bundle) throws Exception
    {
        try
        {
        	
        	bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
            submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            //submit feed

           bundle. submitFeeds(prismHelper);
            //now schedule the thing
            ServiceResponse response = bundle.getFeedHelper().schedule(URLS.SCHEDULE_URL,feed);
            Util.assertSucceeded(response);
            
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));
        }
       
        finally {
            
        	bundle.deleteBundle(prismHelper);
        }
    }
    
   
     
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleKilledFeed(Bundle bundle) throws Exception
    {
        try {
            
        	bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            Util.assertSucceeded(prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));
            
            //now suspend
            Util.assertSucceeded(prismHelper.getFeedHelper().delete(URLS.DELETE_URL, feed));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"KILLED",ivoryqa1).get(0).contains("KILLED"));
            
            //now schedule this!
            Util.assertFailed(prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed));
            
        }
       
        finally {
            
        	bundle.deleteBundle(prismHelper);
        }
    } 
    
      
    
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleSuspendedFeed(Bundle bundle) throws Exception
    {
        try {
            
        	bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            Util.assertSucceeded(prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));
            
            //now suspend
            Util.assertSucceeded(prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED",ivoryqa1).get(0).contains("SUSPENDED"));
            
            //now schedule this!
            Util.assertSucceeded((prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed)));
            
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED",ivoryqa1).get(0).contains("SUSPENDED"));

        }
      
        finally {
        	bundle.deleteBundle(prismHelper);

        }
    }
  	
  	
    @Test(groups={"0.1","0.2"},dataProvider="DP",dataProviderClass=FeedSubmitTest.class)
    public void scheduleAlreadyScheduledFeed(Bundle bundle) throws Exception
    {
        try {
            
        	bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			submitCluster(bundle);
            String feed=Util.getInputFeedFromBundle(bundle);
            
            ServiceResponse response=prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
            Util.assertSucceeded(response);
            
            response=prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
            Util.assertSucceeded(response);
            
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));
            
            //now try re-scheduling again
            response=prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
            Util.assertSucceeded(response);
            
        }
       
        finally {
        	bundle.deleteBundle(prismHelper);
        }               
    }
  	
}


