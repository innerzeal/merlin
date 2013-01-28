/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ColoHelper;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.PrismHelper;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author rishu.mehrotra
 */
public class FeedResumeTest {
	
	PrismHelper prismHelper=new PrismHelper("prism.properties");
	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
    
      IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
      IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
     
    public void submitCluster(Bundle bundle) throws Exception
    {
        ServiceResponse response=prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, bundle.getClusters().get(0));

        Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
        Assert.assertNotNull(Util.parseResponse(response).getMessage());
    }
    
 
   @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void resumeSuspendedFeed(Bundle bundle) throws Exception
    {
    	    
        try {
           bundle.generateUniqueBundle();
        
           bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
           submitCluster(bundle);
           String feed=Util.getInputFeedFromBundle(bundle);
           
           Util.assertSucceeded(prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));
           Util.assertSucceeded(prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed));

           
          Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"SUSPENDED").get(0).contains("SUSPENDED"));
           
                                  
           
           Util.assertSucceeded(prismHelper.getFeedHelper().resume(URLS.RESUME_URL, feed));

           ServiceResponse response=prismHelper.getFeedHelper().getStatus(URLS.STATUS_URL, feed);

           String colo=prismHelper.getFeedHelper().getColo();
           Assert.assertTrue(response.getMessage().contains(colo+"/RUNNING"));

           Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING").get(0).contains("RUNNING"));
           
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
    public void resumeNonExistentFeed(Bundle bundle) throws Exception
    {
        try {
           bundle.generateUniqueBundle();
           submitCluster(bundle);
           String feed=Util.getInputFeedFromBundle(bundle);

           
           Util.assertFailed(prismHelper.getFeedHelper().resume(URLS.RESUME_URL, feed));

 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
  
    }
  

    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void resumeDeletedFeed(Bundle bundle) throws Exception
    {
        try {
           bundle.generateUniqueBundle();
           submitCluster(bundle);
           String feed=Util.getInputFeedFromBundle(bundle);

           Util.assertSucceeded(prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));

           Util.assertSucceeded(prismHelper.getFeedHelper().delete(URLS.DELETE_URL, feed));

           Util.assertFailed(prismHelper.getFeedHelper().resume(URLS.RESUME_URL, feed));

        
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
    public void resumeScheduledFeed(Bundle bundle) throws Exception
    {
        try {
           bundle.generateUniqueBundle();
           bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
           submitCluster(bundle);
           String feed=Util.getInputFeedFromBundle(bundle);

           Util.assertSucceeded(prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed));

           Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING").get(0).contains("RUNNING"));

           Util.assertSucceeded(prismHelper.getFeedHelper().resume(URLS.RESUME_URL, feed));
           
           
           ServiceResponse response=prismHelper.getFeedHelper().getStatus(URLS.STATUS_URL, feed);
           String colo=prismHelper.getFeedHelper().getColo();
           Assert.assertTrue(response.getMessage().contains(colo+"/RUNNING"));
           Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(feed),"RUNNING").get(0).contains("RUNNING"));
 
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
    public Object[][] getData(Method m) throws Exception
    {
        return Util.readELBundles();
    }   
   
}
