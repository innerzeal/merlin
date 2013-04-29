package com.inmobi.qa.airavatqa.prism;

import java.lang.reflect.Method;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.generated.feed.ActionType;
import com.inmobi.qa.ivory.generated.feed.ClusterType;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.response.ServiceResponse;
import com.inmobi.qa.ivory.supportClasses.GetBundle;
import com.inmobi.qa.ivory.util.AssertUtil;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;
import com.inmobi.qa.ivory.util.instanceUtil;
import com.inmobi.qa.ivory.util.xmlUtil;



public class ClusterSpecificPathTest {

	
	@BeforeMethod(alwaysRun=true)
	public void printTestName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	
    PrismHelper prismHelper = new PrismHelper("prism.properties");
    ColoHelper ua1 = new ColoHelper("mk-qa.config.properties");
    ColoHelper ua2 = new ColoHelper("ivoryqa-1.config.properties");
    ColoHelper ua3 = new ColoHelper("gs1001.config.properties");
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd/HH/mm");
    
    @Test(enabled=true,timeOut=600000)
    public void Feed2Cluster2DiffPath() throws Exception   {
    	
    	/**
    	 * This test case will schedule a process on 2 clusters. The location of feed on both the colos will be different 
    	 */
    	Bundle b1 = (Bundle)Util.readBundle(GetBundle.RegularBundle)[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readBundle(GetBundle.RegularBundle)[0][0];
		b2.generateUniqueBundle();

		try{
			b1 = new Bundle(b1,ua1.getEnvFileName());
			b2  = new Bundle(b2,ua2.getEnvFileName());
			
			Bundle.submitCluster(b1,b2);
			
			instanceUtil.createDataWithinDatesAndPrefix(ua2, instanceUtil.oozieDateToDate("2012-10-01T10:00Z"), instanceUtil.oozieDateToDate("2012-10-01T13:00Z"),"/newLocaltion/testData/", 1);
			instanceUtil.createDataWithinDatesAndPrefix(ua1, instanceUtil.oozieDateToDate("2012-10-01T10:00Z"), instanceUtil.oozieDateToDate("2012-10-01T13:00Z"),"/samarth/input-data/rawLogs/", 1);

			
			String feedInput = b1.getDataSets().get(0);
			feedInput =  instanceUtil.setFeedCluster(feedInput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-01-01T00:00Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),null,ClusterType.SOURCE,null,null);
		
			feedInput = instanceUtil.setFeedCluster(feedInput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-10-01T12:10Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,"${cluster.colo}",null);
			feedInput = instanceUtil.setFeedCluster(feedInput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-10-01T12:25Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"${cluster.colo}","/newLocaltion/testData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			ServiceResponse r= prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feedInput);
			Thread.sleep(10000);
			AssertUtil.assertSucceeded(r);
			
			String feedOutput = b1.getDataSets().get(1);
			feedOutput =  instanceUtil.setFeedCluster(feedOutput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-01-01T00:00Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),null,ClusterType.SOURCE,null,null);
		
			feedOutput = instanceUtil.setFeedCluster(feedOutput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-10-01T12:10Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.SOURCE,"${cluster.colo}",null);
			feedOutput = instanceUtil.setFeedCluster(feedOutput,xmlUtil.createValidity("2010-10-01T12:00Z","2099-10-01T12:25Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"${cluster.colo}","/testOutput/testData/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}");

			r= prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feedOutput);
			Thread.sleep(10000);
			AssertUtil.assertSucceeded(r);
			

			String process = b1.getProcessData();
			process = instanceUtil.setProcessCluster(process, null, xmlUtil.createProcessValidity("2012-10-01T12:00Z","2012-10-01T12:10Z"));
			
			process = instanceUtil.setProcessCluster(process, Util.readClusterName(b1.getClusters().get(0)), xmlUtil.createProcessValidity("2012-10-01T12:05Z","2012-10-01T12:20Z"));
			process = instanceUtil.setProcessCluster(process, Util.readClusterName(b2.getClusters().get(0)), xmlUtil.createProcessValidity("2012-10-01T12:10Z","2012-10-01T12:30Z"));
			
			Util.print("process: "+process);
			
			
			r= prismHelper.getProcessHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, process);
			Thread.sleep(10000);
			AssertUtil.assertSucceeded(r);
			
			
			

		}
		
		finally{
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b1.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b1.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b1.getDataSets().get(1));
			Bundle.deleteCluster(b1,b2);
			
		}
    	
    }
    
    
}
