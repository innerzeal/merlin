package com.inmobi.qa.falcon;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.generated.feed.ActionType;
import com.inmobi.qa.ivory.generated.feed.ClusterType;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.response.ProcessInstancesResult;
import com.inmobi.qa.ivory.response.ServiceResponse;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;
import com.inmobi.qa.ivory.util.instanceUtil;
import com.inmobi.qa.ivory.util.xmlUtil;



public class FeedInstanceStatusTest {

	PrismHelper prismHelper=new PrismHelper("prism.properties");

	ColoHelper mkqa=new ColoHelper("mk-qa.config.properties");

	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");

	ColoHelper gs1001 = new ColoHelper("gs1001.config.properties");
	
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method) throws Exception
	{
		Util.print("test name: "+method.getName());
		Util.restartService(mkqa.getClusterHelper());
		Util.restartService(ivoryqa1.getClusterHelper());
 
	}
	
	
	
	@Test
	public void feedInstanceStatus_running() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,mkqa.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			b3  = new Bundle(b3,gs1001.getEnvFileName());


			
			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b3.setCLusterColo("ua3");
			Util.print("cluster b3: "+b3.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
			String startTime = instanceUtil.getTimeWrtSystemTime(-50);


			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,instanceUtil.addMinsToTime(startTime,65)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,20),instanceUtil.addMinsToTime(startTime,85)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,40),instanceUtil.addMinsToTime(startTime,110)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,"UK/${cluster.colo}");

			
			

			Util.print("feed: "+feed);
			
			//status before submit
			ProcessInstancesResult responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,100)+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 100));

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);
			
			// both replication instances
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime,100));

			
			
			// single instance at -30
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20));
			
			//single at -10
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40));
			
			
			//single at 10
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40));
			

			//single at 30
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40)); 
			
			
			String postFix = "/US/ua2" ;
			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ivoryqa1,prefix.substring(1));
			Util.lateDataReplenish(ivoryqa1,80,0,1,prefix,postFix);


			postFix = "/UK/ua3" ;
			prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(gs1001,prefix.substring(1));
			Util.lateDataReplenish(gs1001,80,0,1,prefix,postFix);

		
			// both replication instances
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime,100));

			// single instance at -30
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20));
			
			//single at -10
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40));
			
			
			//single at 10
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40));
			

			//single at 30
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,40)); 
			
			
			Util.print("Wait till feed goes into running ");

			
			
			
			//suspend instances -10
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceSuspend(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime, 40));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20)+"&end="+instanceUtil.addMinsToTime(startTime, 40));

			//resuspend -10 and suspend -30 source specific
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceSuspend(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20)+"&end="+instanceUtil.addMinsToTime(startTime, 40));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20)+"&end="+instanceUtil.addMinsToTime(startTime, 40));

			
			//resume -10 and -30
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceResume(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20)+"&end="+instanceUtil.addMinsToTime(startTime, 40));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,20)+"&end="+instanceUtil.addMinsToTime(startTime, 40));

			//get running instances
			responseInstance  =  prismHelper.getFeedHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readDatasetName(feed));
			
			//rerun succeeded instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceRerun(Util.readDatasetName(feed), "?start="+startTime);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime,20));

			//kill instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceKill(Util.readDatasetName(feed), "?start="+instanceUtil.addMinsToTime(startTime,44));
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceKill(Util.readDatasetName(feed), "?start="+startTime);

			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			
			//rerun killed instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceRerun(Util.readDatasetName(feed), "?start="+startTime);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			
			//kill feed
			r = prismHelper.getFeedHelper().delete(URLS.DELETE_URL, feed);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			Util.print(responseInstance.getMessage());
			
			}
		finally{
			
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
		
}
	
	
/*
	@Test
	public void feedInstanceStatus_oneSourceTwoTarget() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,mkqa.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			b3  = new Bundle(b3,gs1001.getEnvFileName());

			Util.print("cluster b3: "+b3.getClusters().get(0));

			
			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b3.setCLusterColo("ua3");
			Util.print("cluster b3: "+b3.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
			String startTime = instanceUtil.getTimeWrtSystemTime(-100);


			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,instanceUtil.addMinsToTime(startTime,130)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,40),instanceUtil.addMinsToTime(startTime,150)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,60),instanceUtil.addMinsToTime(startTime,160)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.TARGET,null);

			
			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ivoryqa1,prefix.substring(1));
			Util.lateDataReplenish(ivoryqa1,120,0,1,prefix);



			Util.print("feed: "+feed);
			ProcessInstancesResult responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime);
			
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime);


			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			
			
			Util.print("Wait till feed goes into running ");

			r = prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			r = prismHelper.getFeedHelper().resume(URLS.RESUME_URL,feed);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			//non existent instance suspend
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceSuspend(Util.readDatasetName(feed), "?start="+instanceUtil.addMinsToTime(startTime,63));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

		
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceSuspend(Util.readDatasetName(feed), "?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			//suspend suspended
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceSuspend(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			responseInstance = prismHelper.getFeedHelper().getProcessInstanceResume(Util.readDatasetName(feed), "?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			//rerun succeeded instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceRerun(Util.readDatasetName(feed), "?start="+startTime);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			//kill instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceKill(Util.readDatasetName(feed), "?start="+instanceUtil.addMinsToTime(startTime,44));
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceKill(Util.readDatasetName(feed), "?start="+instanceUtil.addMinsToTime(startTime,40));

			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			
			//rerun killed instance
			responseInstance = prismHelper.getFeedHelper().getProcessInstanceRerun(Util.readDatasetName(feed), "?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			
			//kill feed
			r = prismHelper.getFeedHelper().delete(URLS.DELETE_URL, feed);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 120));

			Util.print(responseInstance.getMessage());
			
			}
		finally{
			
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
		
}
	


	
	@Test
	public void feedInstanceStatus_s1s_s1e_t1s_t1e_s2s_s2e() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,mkqa.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			b3  = new Bundle(b3,gs1001.getEnvFileName());


			
			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b3.setCLusterColo("ua3");
			Util.print("cluster b3: "+b3.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
		
			//set start time of first cluster
			String startTime = instanceUtil.getTimeWrtSystemTime(-50);


			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,instanceUtil.addMinsToTime(startTime,60)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,65),instanceUtil.addMinsToTime(startTime,100)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,110),instanceUtil.addMinsToTime(startTime,140)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,"UK/${cluster.colo}");

			
			Util.print("feed: "+feed);
			
			//status before submit
			ProcessInstancesResult responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,100)+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 100));

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);
			
			
			//check no replication coord should be created

			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"), 0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);

			}
		finally{
			
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
		
}
	
	
	@Test
	public void feedInstanceStatus_s1s_s1e_s2s_t1s_s2e_t1e() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,mkqa.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			b3  = new Bundle(b3,gs1001.getEnvFileName());


			
			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b3.setCLusterColo("ua3");
			Util.print("cluster b3: "+b3.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
		
			//set start time of first cluster
			String startTime = instanceUtil.getTimeWrtSystemTime(-50);


			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,instanceUtil.addMinsToTime(startTime,30)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,55),instanceUtil.addMinsToTime(startTime,150)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,40),instanceUtil.addMinsToTime(startTime,110)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,"UK/${cluster.colo}");

			
			Util.print("feed: "+feed);
			
			//status before submit
			ProcessInstancesResult responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,100)+"&end="+instanceUtil.addMinsToTime(startTime, 120));
			
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime, 100));

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);
			
			
			//check no replication coord should be created

			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);

			}
		finally{
			
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
		
}	
	
	
	@Test
	public void feedInstanceStatus_s1s_s1e_s2s_t1s_t1e_s2e() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,mkqa.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			b3  = new Bundle(b3,gs1001.getEnvFileName());


			
			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b3.setCLusterColo("ua3");
			Util.print("cluster b3: "+b3.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
		
			//set start time of first cluster
			String startTime = instanceUtil.getTimeWrtSystemTime(-50);


			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,instanceUtil.addMinsToTime(startTime,30)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,55),instanceUtil.addMinsToTime(startTime,110)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(instanceUtil.addMinsToTime(startTime,40),instanceUtil.addMinsToTime(startTime,150)),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,"UK/${cluster.colo}");

			
			Util.print("feed: "+feed);
			
			//status before submit
			ProcessInstancesResult responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime,110));
			
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+startTime+"&end="+instanceUtil.addMinsToTime(startTime,110));

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);
			
			//check status only replication validity
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,55)+"&end="+instanceUtil.addMinsToTime(startTime,110));
			Util.assertSucceeded(responseInstance);
			Assert.assertTrue(responseInstance.getInstances()!=null);
			
			//null instance
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,32)+"&end="+instanceUtil.addMinsToTime(startTime,39));
			Util.assertSucceeded(responseInstance);
			Assert.assertTrue(responseInstance.getInstances()==null);
			
			
			//null instance
			responseInstance  =  prismHelper.getFeedHelper().getProcessInstanceStatus(Util.readDatasetName(feed),"?start="+instanceUtil.addMinsToTime(startTime,113)+"&end="+instanceUtil.addMinsToTime(startTime,120));
			Util.assertSucceeded(responseInstance);
			Assert.assertTrue(responseInstance.getInstances()==null);
			
			
			//check no replication coord should be created

			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"REPLICATION"),0);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(gs1001.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(mkqa.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ivoryqa1.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"), 0);

			}
		finally{
			
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
		
}*/
	
	
}