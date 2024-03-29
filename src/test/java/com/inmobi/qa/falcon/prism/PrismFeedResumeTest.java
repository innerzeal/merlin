/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.falcon.prism;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.util.Util;

/**
 *
 * @author rishu.mehrotra
 */
public class PrismFeedResumeTest {
    
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
	
    PrismHelper prismHelper = new PrismHelper("prism.properties");
    ColoHelper UA1ColoHelper = new ColoHelper("mk-qa.config.properties");
    ColoHelper UA2ColoHelper = new ColoHelper("ivoryqa-1.config.properties");

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeSuspendedFeedOnBothColos(Bundle bundle) throws Exception {
        Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
        Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

        UA1Bundle.generateUniqueBundle();
        UA2Bundle.generateUniqueBundle();

        //schedule using colohelpers
        submitAndScheduleFeedUsingColoHelper(UA1ColoHelper, UA1Bundle);
        submitAndScheduleFeedUsingColoHelper(UA2ColoHelper, UA2Bundle);

        //suspend using prismHelper
        Util.assertSucceeded(prismHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA1Bundle.getDataSets().get(0)));
        //verify
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "SUSPENDED", UA1ColoHelper).get(0).contains("SUSPENDED"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"SUSPENDED");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        //suspend using prismHelper
        Util.assertSucceeded(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        //verify
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        //try using the colohelper                
        Util.assertSucceeded(UA1ColoHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA1Bundle.getDataSets().get(0)));
        //verify
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "SUSPENDED", UA1ColoHelper).get(0).contains("SUSPENDED"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"SUSPENDED");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        //suspend using prismHelper
        Util.assertSucceeded(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        //verify
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");

        //suspend on the other one
        Util.assertSucceeded(UA1ColoHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA1Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "SUSPENDED", UA1ColoHelper).get(0).contains("SUSPENDED"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"SUSPENDED");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        
        Util.assertSucceeded(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeDeletedFeedOnBothColos(Bundle bundle) throws Exception {
        Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
        Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

        UA1Bundle.generateUniqueBundle();
        UA2Bundle.generateUniqueBundle();

        //schedule using colohelpers
        submitAndScheduleFeedUsingColoHelper(UA1ColoHelper, UA1Bundle);
        submitAndScheduleFeedUsingColoHelper(UA2ColoHelper, UA2Bundle);

        //delete using coloHelpers
        Util.assertSucceeded(prismHelper.getFeedHelper().delete(Util.URLS.DELETE_URL, UA1Bundle.getDataSets().get(0)));


        //suspend using prismHelper
        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        //verify
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");

        Util.assertSucceeded(prismHelper.getFeedHelper().delete(Util.URLS.DELETE_URL, UA2Bundle.getDataSets().get(0)));
        //suspend on the other one
        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "KILLED", UA2ColoHelper).get(0).contains("KILLED"));
        
        Util.assertFailed(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL,UA1Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
        Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL,UA2Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "KILLED", UA2ColoHelper).get(0).contains("KILLED"));
    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeResumedFeedOnBothColos(Bundle bundle) throws Exception {
        Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
        Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

        UA1Bundle.generateUniqueBundle();
        UA2Bundle.generateUniqueBundle();

        //schedule using colohelpers
        submitAndScheduleFeedUsingColoHelper(UA1ColoHelper, UA1Bundle);
        submitAndScheduleFeedUsingColoHelper(UA2ColoHelper, UA2Bundle);

        Util.assertSucceeded(prismHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA1Bundle.getDataSets().get(0)));
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "SUSPENDED", UA1ColoHelper).get(0).contains("SUSPENDED"));
        compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"SUSPENDED");
        for (int i = 0; i < 2; i++) {
            //suspend using prismHelper
            Util.assertSucceeded(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            //verify
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        }
                 

        
        Util.assertSucceeded(prismHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA2Bundle.getDataSets().get(0)));        
        Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "SUSPENDED", UA2ColoHelper).get(0).contains("SUSPENDED"));
        compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"SUSPENDED");
        
        for (int i = 0; i < 2; i++) {
            Util.assertSucceeded(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            //verify
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "SUSPENDED", UA2ColoHelper).get(0).contains("SUSPENDED"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"SUSPENDED");
        }           
        
        
        for (int i = 0; i < 2; i++) {
            //suspend on the other one
            Util.assertSucceeded(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));        
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
        }
        
        for (int i = 0; i < 2; i++) {
            //suspend on the other one
            Util.assertSucceeded(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));        
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
        }        
    }

    @Test(dataProvider = "DP")
    public void testResumeNonExistentFeedOnBothColos(Bundle bundle) throws Exception {
        Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
        Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

        UA1Bundle.generateUniqueBundle();
        UA2Bundle.generateUniqueBundle();

        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));

        Util.assertFailed(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
    }

    @Test(dataProvider = "DP")
    public void testResumeSubmittedFeedOnBothColos(Bundle bundle) throws Exception {
        Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
        Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

        UA1Bundle.generateUniqueBundle();
        UA2Bundle.generateUniqueBundle();

        submitFeed(UA1Bundle);
        submitFeed(UA2Bundle);

        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));

        Util.assertFailed(UA1ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
        Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));


    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeScheduledFeedOnBothColosWhen1ColoIsDown(Bundle bundle) throws Exception {
        try {
            Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
            Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

            UA1Bundle.generateUniqueBundle();
            UA2Bundle.generateUniqueBundle();

            //schedule using colohelpers
            submitAndScheduleFeedUsingColoHelper(UA1ColoHelper, UA1Bundle);
            submitAndScheduleFeedUsingColoHelper(UA2ColoHelper, UA2Bundle);
            Util.assertSucceeded(UA1ColoHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA1Bundle.getDataSets().get(0)));
            Util.assertSucceeded(UA2ColoHelper.getFeedHelper().suspend(Util.URLS.SUSPEND_URL, UA2Bundle.getDataSets().get(0)));
            
            Util.shutDownService(UA1ColoHelper.getFeedHelper());

           
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            //verify
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "SUSPENDED", UA2ColoHelper).get(0).contains("SUSPENDED"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"SUSPENDED");
            //resume on the other one
            Util.assertSucceeded(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "SUSPENDED", UA1ColoHelper).get(0).contains("SUSPENDED"));
            
            
            Util.startService(UA1ColoHelper.getFeedHelper());
            compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"SUSPENDED");
            Util.assertSucceeded(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
            Util.assertSucceeded(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "RUNNING", UA1ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA1Bundle.getDataSets().get(0), UA1ColoHelper,"RUNNING");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new TestNGException(e.getCause());
        } finally {

            Util.restartService(UA1ColoHelper.getFeedHelper());
        }

    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeDeletedFeedOnBothColosWhen1ColoIsDown(Bundle bundle) throws Exception {
        try {
            Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
            Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

            UA1Bundle.generateUniqueBundle();
            UA2Bundle.generateUniqueBundle();

            //schedule using colohelpers
            submitAndScheduleFeedUsingColoHelper(UA1ColoHelper, UA1Bundle);
            submitAndScheduleFeedUsingColoHelper(UA2ColoHelper, UA2Bundle);

            //delete using prismHelper
            Util.assertSucceeded(prismHelper.getFeedHelper().delete(Util.URLS.DELETE_URL, UA1Bundle.getDataSets().get(0)));

            Util.shutDownService(UA1ColoHelper.getFeedHelper());

            //suspend using prismHelper
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            //verify
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");
            //suspend using prismHelper
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            //verify
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "RUNNING", UA2ColoHelper).get(0).contains("RUNNING"));            
            compareStatus(UA2Bundle.getDataSets().get(0), UA2ColoHelper,"RUNNING");

            Util.assertSucceeded(prismHelper.getFeedHelper().delete(Util.URLS.DELETE_URL, UA2Bundle.getDataSets().get(0)));
            //suspend on the other one
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "KILLED", UA2ColoHelper).get(0).contains("KILLED"));
            
            Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA1Bundle.getDataSets().get(0)), "KILLED", UA1ColoHelper).get(0).contains("KILLED"));
            Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(UA2Bundle.getDataSets().get(0)), "KILLED", UA2ColoHelper).get(0).contains("KILLED"));            
        } catch (Exception e) {
            e.printStackTrace();
            throw new TestNGException(e.getCause());
        } finally {
            Util.restartService(UA1ColoHelper.getFeedHelper());
        }
    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeNonExistentFeedOnBothColosWhen1ColoIsDown(Bundle bundle) throws Exception {
        try {
            Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
            Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

            UA1Bundle.generateUniqueBundle();
            UA2Bundle.generateUniqueBundle();

            Util.shutDownService(UA1ColoHelper.getFeedHelper());

            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));

        } catch (Exception e) {
            e.printStackTrace();
            throw new TestNGException(e.getCause());
        } finally {
            Util.restartService(UA1ColoHelper.getProcessHelper());
        }
    }

    @Test(dataProvider = "DP", groups = {"prism", "0.2"})
    public void testResumeSubmittedFeedOnBothColosWhen1ColoIsDown(Bundle bundle) throws Exception {
        try {
            Bundle UA1Bundle = new Bundle(bundle, UA1ColoHelper.getEnvFileName());
            Bundle UA2Bundle = new Bundle(bundle, UA2ColoHelper.getEnvFileName());

            UA1Bundle.generateUniqueBundle();
            UA2Bundle.generateUniqueBundle();

            submitFeed(UA1Bundle);
            submitFeed(UA2Bundle);

            Util.shutDownService(UA1ColoHelper.getFeedHelper());

            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA1Bundle.getDataSets().get(0)));
            Util.assertFailed(prismHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
            Util.assertFailed(UA2ColoHelper.getFeedHelper().resume(Util.URLS.RESUME_URL, UA2Bundle.getDataSets().get(0)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new TestNGException(e.getCause());
        } finally {
            Util.restartService(UA1ColoHelper.getProcessHelper());
        }

    }
        

    private void submitFeed(Bundle bundle) throws Exception {
        
        for(String cluster:bundle.getClusters())
        {
            Util.assertSucceeded(prismHelper.getClusterHelper().submitEntity(Util.URLS.SUBMIT_URL, cluster));
        }
        
        Util.assertSucceeded(prismHelper.getFeedHelper().submitEntity(Util.URLS.SUBMIT_URL, bundle.getDataSets().get(0)));
    }

    private void submitAndScheduleFeedUsingColoHelper(ColoHelper coloHelper, Bundle bundle) throws Exception {
        submitFeed(bundle);
        Util.assertSucceeded(coloHelper.getFeedHelper().schedule(Util.URLS.SCHEDULE_URL, bundle.getDataSets().get(0)));
    }

    @DataProvider(name = "DP")
    public Object[][] getData() throws Exception {
        return Util.readBundles("src/test/resources/LateDataBundles");
    }
    
    
    private void compareStatus(String entity,ColoHelper coloHelper,String expectedStatus) throws Exception
    {
        Assert.assertEquals(Util.parseResponse(prismHelper.getFeedHelper().getStatus(Util.URLS.STATUS_URL,entity)).getMessage(),coloHelper.getFeedHelper().getColo().split("=")[1] +"/"+expectedStatus);
        Assert.assertEquals(Util.parseResponse(prismHelper.getFeedHelper().getStatus(Util.URLS.STATUS_URL,entity)).getMessage(),coloHelper.getFeedHelper().getColo().split("=")[1] +"/"+Util.parseResponse(coloHelper.getFeedHelper().getStatus(Util.URLS.STATUS_URL,entity)).getMessage());  
    }
    
}
