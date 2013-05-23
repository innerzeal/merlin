/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.falcon;


import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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
public class FeedSuspendTest {
    
	PrismHelper prismHelper=new PrismHelper("prism.properties");
	//ColoHelper ivoryqa1 = new ColoHelper("gs1001.config.properties");
	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");    
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
	
    IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
    
    public void submitCluster(Bundle bundle) throws Exception
    {
        //submit the cluster
        ServiceResponse response=prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, bundle.getClusters().get(0));
        Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
        Assert.assertNotNull(Util.parseResponse(response).getMessage());
    }
    
  
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendScheduledFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,feed);
        Util.assertSucceeded(response);
        
        response=prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
        
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED",ivoryqa1).get(0).contains("SUSPENDED"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
        	prismHelper.getFeedHelper().delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendAlreadySuspendedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed);
        Util.assertSucceeded(response);
        
        response=prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED", ivoryqa1).get(0).contains("SUSPENDED"));
        
        response=prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
        
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED", ivoryqa1).get(0).contains("SUSPENDED"));
        
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
        	prismHelper.getFeedHelper().delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendDeletedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed);
        Util.assertSucceeded(response);
        
        response=prismHelper.getFeedHelper().delete(URLS.DELETE_URL, feed);
        Util.assertSucceeded(response);
        
        response=prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
        Util.assertFailed(response);
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
        	prismHelper.getFeedHelper().delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendNonExistentFeed(Bundle bundle) throws Exception
    {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=prismHelper.getFeedHelper().suspend(URLS.SCHEDULE_URL, feed);
        
        Util.assertFailed(response);
        
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendSubmittedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
        Util.assertSucceeded(response);
        
        response=prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
        Util.assertFailed(response);
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
        	prismHelper.getFeedHelper().delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
        @DataProvider(name="DP")
	public Object[][] getTestData(Method m) throws Exception
	{

		return Util.readELBundles();
	}
    
    
}
