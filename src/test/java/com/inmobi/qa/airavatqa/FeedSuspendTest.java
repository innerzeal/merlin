/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author rishu.mehrotra
 */
public class FeedSuspendTest {
    
	
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
        ServiceResponse response=clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
        Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
        Assert.assertNotNull(Util.parseResponse(response).getMessage());
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendScheduledFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,feed);
        Util.assertSucceeded(response);
        
        response=dataHelper.suspend(URLS.SUSPEND_URL, feed);
        
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED").get(0).contains("SUSPENDED"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendAlreadySuspendedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed);
        Util.assertSucceeded(response);
        
        response=dataHelper.suspend(URLS.SUSPEND_URL, feed);
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED").get(0).contains("SUSPENDED"));
        
        response=dataHelper.suspend(URLS.SUSPEND_URL, feed);
        
        Util.assertSucceeded(response);
        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED").get(0).contains("SUSPENDED"));
        
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendDeletedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=dataHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed);
        Util.assertSucceeded(response);
        
        response=dataHelper.delete(URLS.DELETE_URL, feed);
        Util.assertSucceeded(response);
        
        response=dataHelper.suspend(URLS.SUSPEND_URL, feed);
        Util.assertFailed(response);
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendNonExistentFeed(Bundle bundle) throws Exception
    {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=dataHelper.suspend(URLS.SCHEDULE_URL, feed);
        
        Util.assertFailed(response);
        
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void suspendSubmittedFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        submitCluster(bundle);
        
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=dataHelper.submitEntity(URLS.SUBMIT_URL, feed);
        Util.assertSucceeded(response);
        
        response=dataHelper.suspend(URLS.SUSPEND_URL, feed);
        Util.assertFailed(response);
        }
                catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            dataHelper.delete(URLS.DELETE_URL,Util.getInputFeedFromBundle(bundle));
        }
    }
    
        @DataProvider(name="DP")
	public Object[][] getTestData(Method m) throws Exception
	{

		return Util.readBundles();
	}
    
    
}
