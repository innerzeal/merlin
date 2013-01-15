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
public class FeedSubmitTest {
    
	
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
    IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
    IEntityManagerHelper feedHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
    
    public void submitCluster(Bundle bundle) throws Exception
    {
        //submit the cluster
        ServiceResponse response=clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
        Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
        Assert.assertNotNull(Util.parseResponse(response).getMessage());
    }
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void submitValidFeed(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        
        submitCluster(bundle);
        //now submit an input dataset
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
        Util.assertSucceeded(response);
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
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void submitValidFeedPostDeletion(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        
        submitCluster(bundle);
        //now submit an input dataset
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
        Util.assertSucceeded(response);
        
        response=feedHelper.delete(URLS.DELETE_URL, feed);
        Util.assertSucceeded(response);
        
        response=feedHelper.submitEntity(URLS.SUBMIT_URL, feed);
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
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void submitValidFeedPostGet(Bundle bundle) throws Exception
    {
        try {
              bundle.generateUniqueBundle();
        
        submitCluster(bundle);
        //now submit an input dataset
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
        Util.assertSucceeded(response);
        
        response=feedHelper.getEntityDefinition(URLS.GET_ENTITY_DEFINITION.getValue(), feed);
        Util.assertSucceeded(response);
        
        response=feedHelper.submitEntity(URLS.SUBMIT_URL, feed); 
        Util.assertFailed(response);
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
    
    @Test(groups={"0.1","0.2"},dataProvider="DP")
    public void submitValidFeedTwice(Bundle bundle) throws Exception
    {
        try {
        bundle.generateUniqueBundle();
        
        submitCluster(bundle);
        //now submit an input dataset
        String feed=Util.getInputFeedFromBundle(bundle);
        
        ServiceResponse response=feedHelper.submitEntity(URLS.SUBMIT_URL,feed);
        Util.assertSucceeded(response);
        
        response=feedHelper.submitEntity(URLS.SUBMIT_URL, feed); 
        Util.assertFailed(response);
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
    
    
    
    
    @DataProvider(name="DP")
    public static Object[][] getData(Method m) throws Exception
    {
        return Util.readBundles();
    }
    
    
    
}
