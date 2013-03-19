package com.inmobi.qa.airavatqa.prism;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.CoordinatorJob;
import org.apache.oozie.client.CoordinatorAction.Status;
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
import com.inmobi.qa.ivory.supportClasses.Consumer;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;
import com.inmobi.qa.ivory.util.instanceUtil;
import com.inmobi.qa.ivory.util.xmlUtil;



public class PrismFeedReplicationTest {


	@BeforeMethod(alwaysRun=true)
	public void testName(Method method) throws Exception
	{
		Util.print("test name: "+method.getName());
		//restart server as precaution
		Util.restartService(ua1.getClusterHelper());
		Util.restartService(ua2.getClusterHelper());
		Util.restartService(ua3.getClusterHelper());


	}

	public PrismFeedReplicationTest() throws Exception{

	}

	PrismHelper prismHelper=new PrismHelper("prism.properties");

	ColoHelper ua1=new ColoHelper("mk-qa.config.properties");

	ColoHelper ua2 = new ColoHelper("ivoryqa-1.config.properties");

	ColoHelper ua3 = new ColoHelper("gs1001.config.properties");

	@Test(enabled=false,timeOut=1200000)
	public void multipleSourceOneTarget_oneSourceWithPartition() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());


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

			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,null);


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feed);
			Thread.sleep(10000);
			Assert.assertTrue(r.getMessage().contains("FAILED"));

			Assert.assertTrue(r.getMessage().contains("as there are more than one source clusters"));

		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);

		}
	}



	
	
	@Test(enabled=false,timeOut=1200000)
	public void multipleTargetOneSource() throws Exception
	{

		Bundle source_bundle_ua1 = (Bundle)Util.readELBundles()[0][0];
		source_bundle_ua1.generateUniqueBundle();
		Bundle target01_bundle_ua2 = (Bundle)Util.readELBundles()[0][0];
		target01_bundle_ua2.generateUniqueBundle();
		Bundle target02_bundle_ua3 = (Bundle)Util.readELBundles()[0][0];
		target02_bundle_ua3.generateUniqueBundle();

		try{
			source_bundle_ua1 = new Bundle(source_bundle_ua1,ua1.getEnvFileName());
			target01_bundle_ua2  = new Bundle(target01_bundle_ua2,ua2.getEnvFileName());
			target02_bundle_ua3  = new Bundle(target02_bundle_ua3,ua3.getEnvFileName());

			//set cluster colos
			source_bundle_ua1.setCLusterColo("ua1");
			Util.print("cluster b1: "+source_bundle_ua1.getClusters().get(0));
			target01_bundle_ua2.setCLusterColo("ua2");
			Util.print("cluster b2: "+target01_bundle_ua2.getClusters().get(0));
			target02_bundle_ua3.setCLusterColo("ua3");
			Util.print("cluster b3: "+target02_bundle_ua3.getClusters().get(0));


			//submit 3 clusters
			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,source_bundle_ua1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,target01_bundle_ua2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,target02_bundle_ua3.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			//get input and output unique feed
			String inputFeed = Util.getInputFeedFromBundle(source_bundle_ua1);
			String outputFeed = Util.getOutputFeedFromBundle(source_bundle_ua1);

			//set source and target for the 2 feeds

			//set clusters to null;
			inputFeed =  instanceUtil.setFeedCluster(inputFeed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);
			outputFeed =  instanceUtil.setFeedCluster(outputFeed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);


			//set new feed input data
			inputFeed = 	Util.setFeedPathValue(inputFeed, "/samarthRetention/inputFeed/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			//generate data in colo ua1 
			String prefix = instanceUtil.getFeedPrefix(inputFeed);
			Util.HDFSCleanup(ua1,prefix.substring(1));
			Util.lateDataReplenish(ua1,90,0,1,prefix);

			//clean old data from target box
			Util.HDFSCleanup(ua2,prefix.substring(1));
			Util.HDFSCleanup(ua3,prefix.substring(1));


			//feed start validity
			String feedStartTime = instanceUtil.getTimeWrtSystemTime(-50);


			//set clusters for inputFeed 
			inputFeed = instanceUtil.setFeedCluster(inputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(source_bundle_ua1.getClusters().get(0)),ClusterType.SOURCE,null);
			inputFeed = instanceUtil.setFeedCluster(inputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(target01_bundle_ua2.getClusters().get(0)),ClusterType.TARGET,null);
			inputFeed = instanceUtil.setFeedCluster(inputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(target02_bundle_ua3.getClusters().get(0)),ClusterType.TARGET,null);

			//set clusters for output feed
			outputFeed = instanceUtil.setFeedCluster(outputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(source_bundle_ua1.getClusters().get(0)),ClusterType.SOURCE,null);
			outputFeed = instanceUtil.setFeedCluster(outputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(target01_bundle_ua2.getClusters().get(0)),ClusterType.TARGET,null);
			outputFeed = instanceUtil.setFeedCluster(outputFeed,xmlUtil.createValidity(feedStartTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(target02_bundle_ua3.getClusters().get(0)),ClusterType.TARGET,null);


			//submit feeds
			Util.print("feed01: "+inputFeed);
			Util.print("outputFeed: "+outputFeed);

			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, inputFeed);
			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, outputFeed);


			//create a process with 3 clusters 

			//get a process
			String process = source_bundle_ua1.getProcessData();


			//decide process validity
			String 	processStartTime = instanceUtil.getTimeWrtSystemTime(-11);
			String 	processEndTime = instanceUtil.getTimeWrtSystemTime(15);


			//add clusters to process
			process = instanceUtil.setProcessCluster(process,null,xmlUtil.createProcessValidity(processStartTime,"2099-01-01T00:00Z"));
			process = instanceUtil.setProcessCluster(process,Util.readClusterName(source_bundle_ua1.getClusters().get(0)),xmlUtil.createProcessValidity(processStartTime,processEndTime));
			process = instanceUtil.setProcessCluster(process,Util.readClusterName(target01_bundle_ua2.getClusters().get(0)),xmlUtil.createProcessValidity(processStartTime,processEndTime));
			process = instanceUtil.setProcessCluster(process,Util.readClusterName(target02_bundle_ua3.getClusters().get(0)),xmlUtil.createProcessValidity(processStartTime,processEndTime));

			//submit and schedule process
			Util.print("process: "+process);
			r = prismHelper.getProcessHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL,process);
			Thread.sleep(30000);

			//verify, process should be in waiting
			for(int i = 0 ; i < 30 ; i++)
			{
				Status sUa1 = 	instanceUtil.getInstanceStatus(ua1, Util.getProcessName(process),0, 0);
				Status sUa2 =	instanceUtil.getInstanceStatus(ua2, Util.getProcessName(process),0, 0);
				Status sUa3 =	instanceUtil.getInstanceStatus(ua3, Util.getProcessName(process),0, 0);

				if( (sUa1.toString().equals("RUNNING") || sUa1.toString().equals("SUCCEEDED"))  &&  sUa2.toString().equals("WAITING")  &&  sUa3.toString().equals("WAITING") )
					break;
				else
					Assert.assertTrue(false);
			}





			//schedule feed using submit and schedule
			r = prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, inputFeed);
			r = prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, outputFeed);

			Util.print("Wait till process goes into running ");


			for(int i = 0 ; i < 30 ; i++)
			{
				Status sUa1 = 	instanceUtil.getInstanceStatus(ua1, Util.getProcessName(process),0, 0);
				Status sUa2 =	instanceUtil.getInstanceStatus(ua2, Util.getProcessName(process),0, 0);
				Status sUa3 =	instanceUtil.getInstanceStatus(ua3, Util.getProcessName(process),0, 0);

				if((sUa1.toString().equals("RUNNING") || sUa1.toString().equals("SUCCEEDED") || sUa1.toString().equals("KILLED") )   &&  (sUa2.toString().equals("RUNNING") || sUa2.toString().equals("SUCCEEDED") || sUa2.toString().equals("KILLED")) &&  ( sUa3.toString().equals("RUNNING") || sUa3.toString().equals("SUCCEEDED") || sUa3.toString().equals("KILLED")) )
					break;

				if(i==29)
					Assert.assertTrue(false,"Expceted state for coordinatior was not reached after 30 attempts");

				Thread.sleep(20000);

			}


			String processName = Util.readEntityName(source_bundle_ua1.getProcessData());

			ProcessInstancesResult responseInstance  =  prismHelper.getProcessHelper().getProcessInstanceStatus(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  ua1.getProcessHelper().getProcessInstanceSuspend(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  prismHelper.getProcessHelper().getProcessInstanceStatus(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime,11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  prismHelper.getProcessHelper().getProcessInstanceResume(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  prismHelper.getProcessHelper().getProcessInstanceStatus(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  ua1.getProcessHelper().getProcessInstanceKill(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

			responseInstance  =  prismHelper.getProcessHelper().getProcessInstanceRerun(processName,"?start="+processStartTime+"&end="+instanceUtil.addMinsToTime(processStartTime, 11));
			Util.assertSucceeded(responseInstance);

		}

		finally{
			source_bundle_ua1.deleteBundle(prismHelper);
			target01_bundle_ua2.deleteBundle(prismHelper);
			target02_bundle_ua3.deleteBundle(prismHelper);


		}
	}



		@Test(enabled=false,timeOut=1200000)
	public void multipleSourceOneTarget() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());


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

			String postFix = "/US/ua2" ;
			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ua2,prefix.substring(1));
			Util.lateDataReplenish(ua2,120,0,1,prefix,postFix);


			postFix = "/UK/ua3" ;
			prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ua3,prefix.substring(1));
			Util.lateDataReplenish(ua3,120,0,1,prefix,postFix);

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"US/${cluster.colo}");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,"UK/${cluster.colo}");


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);

			Consumer consumer=new Consumer("IVORY."+Util.readDatasetName(Util.getInputFeedFromBundle(b1)),Util.readQueueLocationFromCluster(b1.getClusters().get(0)));
			consumer.start();




			String TargetBundleID = 	instanceUtil.getLatestBundleID(Util.readDatasetName(feed),"FEED",ua1.getFeedHelper());

			ArrayList<String> replicationCoordNameTarget  = instanceUtil.getReplicationCoordName(TargetBundleID,ua1.getFeedHelper());
			String retentionCoordNameTarget    = instanceUtil.getRetentionCoordName(TargetBundleID,ua1.getFeedHelper());
			ArrayList<String> replicationCoordIDTarget    = instanceUtil.getReplicationCoordID(TargetBundleID,ua1.getFeedHelper());
			String retentionCoordIDTarget      = instanceUtil.getRetentionCoordID(TargetBundleID,ua1.getFeedHelper());

			String SourceBundleID_01 = 	instanceUtil.getLatestBundleID(Util.readDatasetName(feed),"FEED",ua2.getFeedHelper());
			String retentionCoordNameSource_01 = instanceUtil.getRetentionCoordName(SourceBundleID_01,ua2.getFeedHelper());
			String retentionCoordIDSource_01 = instanceUtil.getRetentionCoordID(SourceBundleID_01,ua2.getFeedHelper());


			String SourceBundleID_02 = 	instanceUtil.getLatestBundleID(Util.readDatasetName(feed),"FEED",ua2.getFeedHelper());
			String retentionCoordNameSource_02 = instanceUtil.getRetentionCoordName(SourceBundleID_02,ua2.getFeedHelper());
			String retentionCoordIDSource_02 = instanceUtil.getRetentionCoordID(SourceBundleID_02,ua2.getFeedHelper());



			if(replicationCoordNameTarget.size()==0 || retentionCoordNameTarget==null || replicationCoordIDTarget.size()==0 || retentionCoordIDTarget==null || retentionCoordNameSource_01==null || retentionCoordIDSource_01==null || retentionCoordNameSource_02==null || retentionCoordIDSource_02==null)
				Assert.assertFalse(true,"correct retention and replication coords were not created on source on target machines");

			consumer.stop();
			List<HashMap<String,String>> data = consumer.getMessageData();

			instanceUtil.verifyDataInTarget(ua1.getFeedHelper(),feed);

			Util.print("counsumerData: "+ data.get(0).toString());


		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);

		}
	}


	@Test(enabled=false,timeOut=1200000)
	public void onlyOneTarget() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());

			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);


			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.removeFeedPartitionsTag(feed);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);

			Util.print("feed: "+feed);
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("should have atleast one source cluster defined"));

		}
		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
	}



	
	@Test(enabled=false,timeOut=1200000)
	public void onlyOneSource() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());

			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);


			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.removeFeedPartitionsTag(feed);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);
			Util.print("feed: "+feed);
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			Thread.sleep(20000);


		}
		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
	}

    @Test(enabled=false,timeOut=1200000)
	public void muultipeSourceNoTarget_noPartitionAtTop() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());

			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);


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

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.removeFeedPartitionsTag(feed);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,null);

			Util.print("feed: "+feed);
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			Thread.sleep(20000);

			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua2.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua3.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua1.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);

		}
		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
	}



	@Test(enabled=false,timeOut=1200000)
	public void muultipeSourceNoTarget_noPartition() throws Exception
	{
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		Bundle b3 = (Bundle)Util.readELBundles()[0][0];
		b3.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			b3  = new Bundle(b3,ua3.getEnvFileName());

			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);


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

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b3.getClusters().get(0)),ClusterType.SOURCE,null);

			Util.print("feed: "+feed);
			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			Thread.sleep(10000);

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(15000);

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			Thread.sleep(20000);

			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua2.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua3.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);
			Assert.assertEquals(instanceUtil.checkIfFeedCoordExist(ua1.getFeedHelper(),Util.readDatasetName(feed),"RETENTION"),1);

		}
		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
			b3.deleteBundle(prismHelper);
		}
	}



	@SuppressWarnings("deprecation")

	@Test(enabled=false,timeOut=1200000)
	public void oneSourceOneTarget_FeedResume() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());

			b2  = new Bundle(b2,ua2.getEnvFileName());

			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));
			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);

			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(prefix.substring(1));
			Util.lateDataReplenish(200,0,1,prefix);

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			Util.print("Source: ua1   Target: ua2");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);



			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);
			Thread.sleep(30000);

			r = prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL, feed);
			Thread.sleep(30000);

			r = prismHelper.getFeedHelper().resume(URLS.RESUME_URL,feed);


		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);

		}
	}

	@Test(enabled=false,timeOut=1200000)
	public void oneSourceOneTarget_AlalibilityFlag() throws Exception
	{

		String dependency = "depends.txt" ;
		Bundle b1 = (Bundle)Util.readAvailabilityBUndle()[0][0];
		b1.setInputFeedAvailabilityFlag(dependency);

		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readAvailabilityBUndle()[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());

			b2  = new Bundle(b2,ua2.getEnvFileName());

			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));
			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);

			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(prefix.substring(1));
			Util.lateDataReplenish(200,0,1,prefix);

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			Util.print("Source: ua1   Target: ua2");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);



			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);

		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);
		}
	}




	@Test(enabled=false,timeOut=1200000)
	public void oneSourceOneTarget_PrismTarget() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());

			b2  = new Bundle(b2,ua2.getEnvFileName());

			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));
			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);

			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ua1,prefix.substring(1));
			Util.lateDataReplenish(ua1,120,0,1,prefix);

			//clear target cluster
			Util.HDFSCleanup(ua2,prefix.substring(1));



			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			Util.print("Source: ua1   Target: ua2");
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.TARGET,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);




			Consumer consumer_mkqa=new Consumer("IVORY."+Util.readDatasetName(Util.getInputFeedFromBundle(b1)),ua1.getClusterHelper().getActiveMQ());
			consumer_mkqa.start();


			Consumer consumer_ivoryqa1=new Consumer("IVORY."+Util.readDatasetName(Util.getInputFeedFromBundle(b1)),ua2.getClusterHelper().getActiveMQ());
			consumer_ivoryqa1.start();

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);

			consumer_mkqa.stop();
			List<HashMap<String,String>> data_mkqa = consumer_mkqa.getMessageData();

			consumer_ivoryqa1.stop();
			List<HashMap<String,String>> data_ivoryqa1 = consumer_ivoryqa1.getMessageData();



			//mkqaJMS file
			for(int i = 0 ; i < data_mkqa.size() ; i++)
				Util.print("counsumerData from data_mkqa: "+ data_mkqa.get(i).toString());

			File mkqaJMS = new File("src/test/resources/mkqaJMS.txt");


		
				mkqaJMS.createNewFile();
		

			for(int i = 0 ; i < data_mkqa.size() ; i++)
			{
				Util.print("counsumerData from data_mkqa: "+ data_mkqa.get(i).toString());


				FileWriter fr = new FileWriter(mkqaJMS);
				fr.append(data_mkqa.get(i).toString());
				fr.close();

			}

			File ivoryJMS = new File("src/test/resources/ivoryJMS.txt");


				ivoryJMS.createNewFile();
	

			for(int i = 0 ; i < data_ivoryqa1.size() ; i++)
			{
				Util.print("counsumerData from data_ivoryqa1: "+ data_ivoryqa1.get(i).toString());


				FileWriter fr = new FileWriter(ivoryJMS);
				fr.append(data_ivoryqa1.get(i).toString());
				fr.close();

			}

		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);

		}
	}

	@Test(enabled=true,timeOut=1200000)
	public void oneSourceOneTarget_extraDataTargetDelete() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());

			b2  = new Bundle(b2,ua3.getEnvFileName());

			b1.setInputFeedDataPath("/replicationDeleteExtraTargetTest/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			b1.setCLusterColo("ua1");
			b2.setCLusterColo("ua3");
			
			Bundle.submitCluster(b1,b2);
		
			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);

			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ua1,prefix.substring(1));
			Util.HDFSCleanup(ua3,prefix.substring(1));
			
			instanceUtil.putFileInFolders(ua1,instanceUtil.createEmptyDirWithinDatesAndPrefix(ua1, instanceUtil.oozieDateToDate(instanceUtil.getTimeWrtSystemTime(-20)), instanceUtil.oozieDateToDate(instanceUtil.getTimeWrtSystemTime(10)), "/replicationDeleteExtraTargetTest/", 1),"log_01.txt","xmlFileName.xml","_SUCCESS");
			instanceUtil.putFileInFolders(ua3,instanceUtil.createEmptyDirWithinDatesAndPrefix(ua3, instanceUtil.oozieDateToDate(instanceUtil.getTimeWrtSystemTime(-20)), instanceUtil.oozieDateToDate(instanceUtil.getTimeWrtSystemTime(10)), "/replicationDeleteExtraTargetTest/", 1),"QABackLog.txt","xmlFileName.xml");


			String startTime = instanceUtil.getTimeWrtSystemTime(-10);

			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.TARGET,null);


			Util.print("feed: "+feed);

			ServiceResponse r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);
			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);

		

	/*		String bundleID = instanceUtil.getLatestBundleID(Util.readDatasetName(feed),"FEED",ua1.getClusterHelper());

			if(bundleID!=null)
			{
				String retentionCoord = instanceUtil.getRetentionCoordID(bundleID, ua1.getClusterHelper());
				if(retentionCoord== null)
					Assert.assertTrue(false);
			}
*/


		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);

		}
	}

	@Test(enabled=false,timeOut=1200000)
	public void oneSourceOneTarget() throws Exception
	{

		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());

			b1.setInputFeedDataPath("/samarthRetention/input-data/rawLogs/oozieExample/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			b1.setCLusterColo("ua1");
			Util.print("cluster b1: "+b1.getClusters().get(0));
			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b1.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			b2.setCLusterColo("ua2");
			Util.print("cluster b2: "+b2.getClusters().get(0));
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b2.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


			String feed = b1.getDataSets().get(0);
			feed =  instanceUtil.setFeedCluster(feed,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,null);

			String prefix = b1.getFeedDataPathPrefix();
			Util.HDFSCleanup(ua2,prefix.substring(1));
			Util.lateDataReplenish(ua2,120,0,1,prefix);

			String startTime = instanceUtil.getTimeWrtSystemTime(-100);

			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,null);
			feed = instanceUtil.setFeedCluster(feed,xmlUtil.createValidity(startTime,"2099-01-01T00:00Z"),xmlUtil.createRtention("hours(10)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,null);


			Util.print("feed: "+feed);

			r= prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL, feed);


			Util.shutDownService(ua1.getClusterHelper());
			Thread.sleep(5000);

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);

			Util.startService(ua1.getClusterHelper());
			Thread.sleep(15000);

			r= prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL, feed);

			String bundleID = instanceUtil.getLatestBundleID(Util.readDatasetName(feed),"FEED",ua1.getClusterHelper());

			if(bundleID!=null)
			{
				String retentionCoord = instanceUtil.getRetentionCoordID(bundleID, ua1.getClusterHelper());
				if(retentionCoord== null)
					Assert.assertTrue(false);
			}



		}

		finally{
			b1.deleteBundle(prismHelper);
			b2.deleteBundle(prismHelper);

		}
	}

	
}
