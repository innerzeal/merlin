package com.inmobi.qa.airavatqa.prism;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.generated.feed.ActionType;
import com.inmobi.qa.ivory.generated.feed.ClusterType;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.instanceUtil;
import com.inmobi.qa.ivory.util.xmlUtil;
import com.inmobi.qa.ivory.util.Util.URLS;

public class Feed_Delay_Parallel_TimeoutTest {

	PrismHelper prismHelper=new PrismHelper("prism.properties");
	ColoHelper gs1001 = new ColoHelper("gs1001.config.properties");
    ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");


	
	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	@Test(enabled=true,timeOut=12000000)
	public void timeoutTest() throws Exception{
		
		Bundle b1 = (Bundle)Util.readELBundles()[0][0];
		b1.generateUniqueBundle();
		Bundle b2 = (Bundle)Util.readELBundles()[0][0];
		b2.generateUniqueBundle();
		
		try{
			b1 = new Bundle(b1,gs1001.getEnvFileName());
			b2  = new Bundle(b2,ivoryqa1.getEnvFileName());
			
			b1.setInputFeedDataPath("/feedTimeoutTest/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			Bundle.submitCluster(b1,b2);
			String feedOutput01 = b1.getDataSets().get(0);
			com.inmobi.qa.ivory.generated.dependencies.Frequency delay= new com.inmobi.qa.ivory.generated.dependencies.Frequency("hours(5)");
			
			feedOutput01 =  instanceUtil.setFeedCluster(feedOutput01,xmlUtil.createValidity("2010-10-01T12:00Z","2099-01-01T00:00Z"),xmlUtil.createRtention("days(10000)",ActionType.DELETE),null,ClusterType.SOURCE,null,null);
			feedOutput01 = instanceUtil.setFeedCluster(feedOutput01,xmlUtil.createValidity("2013-04-21T00:00Z","2099-10-01T12:10Z"),xmlUtil.createRtention("hours(15)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"",delay,"/feedTimeoutTest/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");
			feedOutput01 = instanceUtil.setFeedCluster(feedOutput01,xmlUtil.createValidity("2013-04-21T00:00Z","2099-10-01T12:25Z"),xmlUtil.createRtention("hours(15)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,"",delay,"/feedTimeoutTestTarget/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");
			//feedOutput01 = instanceUtil.setFeedCluster(feedOutput01,xmlUtil.createValidity("2013-04-21T00:00Z","2099-10-01T12:10Z"),xmlUtil.createRtention("hours(15)",ActionType.DELETE),Util.readClusterName(b2.getClusters().get(0)),ClusterType.SOURCE,"","/feedTimeoutTest/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");
			//feedOutput01 = instanceUtil.setFeedCluster(feedOutput01,xmlUtil.createValidity("2013-04-21T00:00Z","2099-10-01T12:25Z"),xmlUtil.createRtention("hours(15)",ActionType.DELETE),Util.readClusterName(b1.getClusters().get(0)),ClusterType.TARGET,"","/feedTimeoutTestTarget/${YEAR}/${MONTH}/${DAY}/${HOUR}/${MINUTE}/");

			feedOutput01 = Util.setFeedProperty(feedOutput01, "timeout","minutes(35)");
			feedOutput01 = Util.setFeedProperty(feedOutput01, "parallel","3");
			
			System.out.println(feedOutput01);
			prismHelper.getFeedHelper().submitAndSchedule(URLS.SUBMIT_AND_SCHEDULE_URL, feedOutput01);
			
		}
		finally{
			b1.deleteBundle(prismHelper);

		}
		
	}
	
	
	
}
