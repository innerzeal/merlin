package com.inmobi.qa.falcon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.generated.dependencies.Frequency.TimeUnit;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.response.ProcessInstancesResult;
import com.inmobi.qa.ivory.response.ProcessInstancesResult.WorkflowStatus;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;
import com.inmobi.qa.ivory.util.instanceUtil;
/**
 * 
 * @author samarth.gupta
 *
 */
public class ProcessInstanceRunningTest {

	PrismHelper prismHelper=new PrismHelper("prism.properties");
	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");

	//@BeforeClass(alwaysRun=true)
	public void createTestData() throws Exception
	{

		Util.print("in @BeforeClass");

		System.setProperty("java.security.krb5.realm", "");
		System.setProperty("java.security.krb5.kdc", "");


		Bundle b = new Bundle();
		b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();
		b = new Bundle(b,ivoryqa1.getEnvFileName());

		String startDate = "2010-01-01T20:00Z";
		String endDate = "2010-01-03T01:04Z";

		b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
		String prefix = b.getFeedDataPathPrefix();
		Util.HDFSCleanup(ivoryqa1,prefix.substring(1));

		DateTime startDateJoda = new DateTime(instanceUtil.oozieDateToDate(startDate));
		DateTime endDateJoda = new DateTime(instanceUtil.oozieDateToDate(endDate));

		List<String> dataDates = Util.getMinuteDatesOnEitherSide(startDateJoda,endDateJoda,20);

		for(int i = 0 ; i < dataDates.size(); i++)
			dataDates.set(i, prefix + dataDates.get(i));

		ArrayList<String> dataFolder = new ArrayList<String>();

		for(int i = 0 ; i < dataDates.size(); i++)
			dataFolder.add(dataDates.get(i));

		instanceUtil.putDataInFolders(ivoryqa1,dataFolder);
	}


	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}

	@Test(groups = { "0.1","0.2"})
	public void getResumedProcessInstance() throws Exception
	{
		Bundle b = new Bundle();

		try{

			b = (Bundle)Util.readELBundles()[0][0];
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessValidity("2010-01-02T01:00Z","2010-01-02T02:30Z");
			b.setProcessPeriodicity(5,TimeUnit.minutes);
			b.setOutputFeedPeriodicity(5,TimeUnit.minutes);
			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessConcurrency(3);
			b.submitAndScheduleBundle(prismHelper);
			Thread.sleep(15000);
			prismHelper.getProcessHelper().suspend(URLS.SUSPEND_URL,b.getProcessData());
			Thread.sleep(15000);
			prismHelper.getProcessHelper().resume(URLS.RESUME_URL, b.getProcessData());
			Thread.sleep(15000);
			ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readEntityName(b.getProcessData()));
			instanceUtil.validateSuccess(r,b,WorkflowStatus.RUNNING);		
		}
		finally{
			b.deleteBundle(prismHelper);
		}
	}


	@Test(groups = { "0.1","0.2"})
	public void getSuspendedProcessInstance() throws Exception
	{
		Bundle b = new Bundle();

		try{

			b = (Bundle)Util.readELBundles()[0][0];
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessValidity("2010-01-02T01:00Z","2010-01-02T02:30Z");
			b.setProcessPeriodicity(5,TimeUnit.minutes);
			b.setOutputFeedPeriodicity(5,TimeUnit.minutes);
			b.setOutputFeedLocationData("/examples/output-data/aggregator/aggregatedLogs/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessConcurrency(3);
			b.submitAndScheduleBundle(prismHelper);
			prismHelper.getProcessHelper().suspend(URLS.SUSPEND_URL,b.getProcessData());
			Thread.sleep(5000);
			ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readEntityName(b.getProcessData()));
			instanceUtil.validateSuccessWOInstances(r);
		}
		finally{
			b.deleteBundle(prismHelper);
		}
	}


	@Test(groups = { "0.1","0.2"})
	public void getRunningProcessInstance() throws Exception
	{
		Bundle b = new Bundle();
		try{

			b = (Bundle)Util.readELBundles()[0][0];
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b.setCLusterColo("ua2");
			b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessValidity("2010-01-02T01:00Z","2010-01-02T02:30Z");
			b.setProcessPeriodicity(5,TimeUnit.minutes);
			b.submitAndScheduleBundle(prismHelper);
			Thread.sleep(5000);
			ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readEntityName(b.getProcessData()));
			instanceUtil.validateSuccess(r,b,WorkflowStatus.RUNNING);		}
		finally{
			b.deleteBundle(prismHelper);
		}
	}

	@Test(groups = { "0.1","0.2"})
	public void getNonExistenceProcessInstance() throws Exception
	{
		ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,"invalidName");
		if(!(r.getStatusCode() == 777))
			Assert.assertTrue(false);
	}


	@Test(groups = { "0.1","0.2"})
	public void getKilledProcessInstance() throws Exception
	{
		Bundle b = new Bundle();

		try{

			b = (Bundle)Util.readELBundles()[0][0];
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.submitAndScheduleBundle(prismHelper);
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			Thread.sleep(5000);
			ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readEntityName(b.getProcessData()));
			if(!(r.getStatusCode() == 777))
				Assert.assertTrue(false);		}
		finally{
			b.deleteBundle(prismHelper);
		}
	}




	@Test(groups = { "0.1","0.2"})
	public void getSucceededProcessInstance() throws Exception
	{
		Bundle b = new Bundle();

		try{

			b = (Bundle)Util.readELBundles()[0][0];
			b = new Bundle(b,ivoryqa1.getEnvFileName());
			b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
			b.setProcessValidity("2010-01-02T01:00Z","2010-01-02T01:11Z");
			b.submitAndScheduleBundle(prismHelper);
			org.apache.oozie.client.Job.Status s  = null ; 
			for(int i = 0 ; i < 45 ; i ++)
			{
				s = instanceUtil.getDefaultCoordinatorStatus(ivoryqa1,Util.getProcessName(b.getProcessData()), 0);
				if(s.equals(org.apache.oozie.client.Job.Status.SUCCEEDED))
					break;
				Thread.sleep(45000);
			}

			if(!s.equals(org.apache.oozie.client.Job.Status.SUCCEEDED))
				Assert.assertTrue(false,"The job did not succeeded even in long time");

			ProcessInstancesResult r  = prismHelper.getProcessHelper().getRunningInstance(URLS.INSTANCE_RUNNING,Util.readEntityName(b.getProcessData()));
			instanceUtil.validateSuccessWOInstances(r);
		}
		finally{
			b.deleteBundle(prismHelper);
		}
	}


	@AfterClass(alwaysRun=true)
	public void deleteData() throws Exception
	{
		Util.print("in @AfterClass");

		System.setProperty("java.security.krb5.realm", "");
		System.setProperty("java.security.krb5.kdc", "");


		Bundle b = new Bundle();
		b = (Bundle)Util.readELBundles()[0][0];
		b = new Bundle(b,ivoryqa1.getEnvFileName());
		b.setInputFeedDataPath("/samarthData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");
		String prefix = b.getFeedDataPathPrefix();
		Util.HDFSCleanup(ivoryqa1,prefix.substring(1));
	}

}
