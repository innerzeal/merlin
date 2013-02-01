/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import java.lang.reflect.Method;

import com.inmobi.qa.airavatqa.core.APIResult;
import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ColoHelper;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.PrismHelper;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
import junit.framework.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author rishu.mehrotra
 */
public class FeedSubmitAndScheduleTest {

	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */

	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	

	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
	IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
	PrismHelper prismHelper=new PrismHelper("prism.properties");
	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");


	@Test(groups={"0.1","0.2","sanity"},dataProvider="DP" )
	public void snsNewFeed(Bundle bundle) throws Exception
	{
		try {
			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			Assert.assertEquals(Util.parseResponse(prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,bundle.getClusters().get(0))).getStatusCode(),200);
			//            for(String dataset:bundle.getDataSets())
			//            {
			//               Assert.assertEquals(Util.parseResponse(prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,dataset)).getStatusCode(),200); 
			//            }



			//ServiceResponse response=processHelper.submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getProcessData());
			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));



			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());


			Thread.sleep(5000);

			//Assert.assertTrue(Util.getOozieJobStatus(Util.readEntityName(bundle.getProcessData()),"RUNNING").get(0).contains("RUNNING"));
		}
		finally {
			bundle.deleteBundle(prismHelper);
		}

	}

	@Test(groups={"0.1","0.2"},dataProvider="DP" )
	public void snsExistingFeed(Bundle bundle) throws Exception
	{
		try {
			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());      
			Assert.assertEquals(Util.parseResponse(prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,bundle.getClusters().get(0))).getStatusCode(),200);

			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));



			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));

			//try to submitand schedule the same process again
			response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));
		}
		finally {
			bundle.deleteBundle(prismHelper);
		}

	}

	@Test(groups={"0.1","0.2"},dataProvider="DP" )
	public void snsFeedWithoutCluster(Bundle bundle) throws Exception
	{
		try {
			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),400);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.FAILED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());


		}
		finally {
			bundle.deleteBundle(prismHelper);
		}

	}



	@Test(groups={"0.1","0.2"},dataProvider="DP" )
	public void snsRunningProcess(Bundle bundle) throws Exception
	{
		try 
		{

			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			Assert.assertEquals(Util.parseResponse(prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,bundle.getClusters().get(0))).getStatusCode(),200);


			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));

			response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

		} 
		finally {
			bundle.deleteBundle(prismHelper);
		}

	}



	@Test(groups={"0.1","0.2"},dataProvider="DP" )
	public void snsDeletedFeed(Bundle bundle) throws Exception
	{
		try
		{
			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			Assert.assertEquals(Util.parseResponse(prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,bundle.getClusters().get(0))).getStatusCode(),200);


			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));

			Assert.assertEquals(Util.parseResponse(prismHelper.getFeedHelper().delete(URLS.DELETE_URL,bundle.getDataSets().get(0))).getStatusCode(),200);
			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"KILLED",ivoryqa1).get(0).contains("KILLED"));

			response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));
		}
		finally {
			bundle.deleteBundle(prismHelper);
		}
	}



	@Test(groups={"0.1","0.2"},dataProvider="DP" )
	public void snsSuspendedFeed(Bundle bundle) throws Exception
	{
		try {
			bundle = (Bundle)Util.readELBundles()[0][0];
			bundle = new Bundle(bundle,ivoryqa1.getEnvFileName());
			Assert.assertEquals(Util.parseResponse(prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,bundle.getClusters().get(0))).getStatusCode(),200);


			ServiceResponse response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());
			Thread.sleep(20000);

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"RUNNING",ivoryqa1).get(0).contains("RUNNING"));

			Assert.assertEquals(Util.parseResponse(prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL,bundle.getDataSets().get(0))).getStatusCode(),200);
			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"SUSPENDED",ivoryqa1).get(0).contains("SUSPEND"));

			response=prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,bundle.getDataSets().get(0));

			Assert.assertEquals(Util.parseResponse(response).getStatusCode(),200);
			Assert.assertEquals(Util.parseResponse(response).getStatus(),APIResult.Status.SUCCEEDED);
			Assert.assertNotNull(Util.parseResponse(response).getMessage());

			Assert.assertTrue(Util.getOozieFeedJobStatus(Util.readDatasetName(bundle.getDataSets().get(0)),"SUSPENDED",ivoryqa1).get(0).contains("SUSPEND"));
		}
		finally {
			bundle.deleteBundle(prismHelper);
		}

	}


	@DataProvider(name="DP")
	public Object[][] getBundleData() throws Exception
	{
		return Util.readELBundles();
	}
}



