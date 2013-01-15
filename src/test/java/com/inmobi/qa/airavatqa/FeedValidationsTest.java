//package com.inmobi.qa.airavatqa;
//
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.xmlUtil;
//
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.generated.feed.ActionType;
//import com.inmobi.qa.airavatqa.generated.feed.Cluster;
//import com.inmobi.qa.airavatqa.generated.feed.ClusterType;
//import com.inmobi.qa.airavatqa.generated.feed.Partition;
//import com.inmobi.qa.airavatqa.generated.feed.Feed;
//import com.inmobi.qa.airavatqa.generated.feed.Retention;
//import com.inmobi.qa.airavatqa.generated.feed.TimezoneType;
//import com.inmobi.qa.airavatqa.generated.feed.Validity;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//import org.testng.log4testng.Logger;
//
///**
// * 
// * @author samarth.gupta
// *
// */
//
//public class FeedValidationsTest {
//
//        Logger logger=Logger.getLogger(this.getClass());
//	@BeforeMethod
//	public void testName(Method method)
//	{
//		Util.print("test name: "+method.getName());
//	}
//
//	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//	IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//	String cluster2Data = null;
//
//
//	@Test(groups={"0.1","0.2"},dataProvider="clusterTest-DP")
//	public void feedClusterTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitFeedFailuer(bundle,expectedError);
//	} 
//	
//	@Test(groups={"0.1","0.2"},dataProvider="pos-clusterTest-DP")
//	public void pos_feedClusterTest(Bundle bundle) throws Exception
//	{
//		submitFeedSuccess(bundle);
//	} 
//
//
//
//	@Test(groups={"0.1","0.2"},dataProvider="stringTest-DP")
//	public void feedNameTest(Bundle bundle) throws Exception
//	{
//		submitFeedFailuer(bundle,"test");
//	} 
//
//	@Test(groups={"0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void noPartitions(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeFeedPartition(bundle);
//		submitFeedFailuer(bundle,"Invalid content was found starting with element 'groups'. One of '{\"uri:ivory:feed:0.1\":partitions}' is expected.");
//	}
//
//	@Test(groups={"0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void noClustersElement(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeFeedClusters(bundle);
//		submitFeedFailuer(bundle,"Invalid content was found starting with element 'locations'. One of '{\"uri:ivory:feed:0.1\":clusters}' is expected");
//	}
//
//	@Test(groups={"0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedPeriodicityMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeFeedPeriodicity(bundle);
//		submitFeedFailuer(bundle,"Invalid content was found starting with element 'late-arrival'. One of '{\"uri:ivory:feed:0.1\":periodicity}' is expected.");
//	}
//
//	@Test(groups={"0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedDescriptionMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeFeedDescription(bundle);
//		submitFeedSuccess(bundle);
//	}
//
//
//	@Test(groups={"0.1","0.2"},dataProvider="partitionTest-DP")
//	public void feedPartitionTest(Bundle bundle) throws Exception
//	{
//		submitFeedFailuer(bundle,"test");
//	} 
//
//	
//
//	@Test(groups={"0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void feedFrequencyMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeFeedFrequency(bundle);
//		submitFeedFailuer(bundle," Invalid content was found starting with element 'periodicity'. One of '{\"uri:ivory:feed:0.1\":availabilityFlag, \"uri:ivory:feed:0.1\":frequency}' is expected");
//	}							 
//	
//	public void submitFeedFailuer(Bundle bundle,String expectedError) throws Exception
//	{
//		try{
//			if(cluster2Data!=null)
//				clusterHelper.submitEntity(URLS.SUBMIT_URL,cluster2Data);
//			clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//			logger.info("dataset is: "+bundle.getDataSets().get(0));
//			ServiceResponse dataSubmission=dataHelper.submitEntity(URLS.SUBMIT_URL,bundle.getDataSets().get(0));
//			if(!dataSubmission.getMessage().contains(expectedError) && !expectedError.equals("test"))
//				Assert.assertTrue(false,"errorMessage not as Expected");
//			Util.assertFailed(dataSubmission,"should have not been submitted");
//		}
//		finally{
//			clusterHelper.delete(Util.URLS.DELETE_URL,bundle.getClusterData());
//			if(cluster2Data != null)
//				clusterHelper.delete(Util.URLS.DELETE_URL,cluster2Data);
//		}
//	}
//
//	public void submitFeedSuccess(Bundle bundle) throws Exception
//	{
//		try{
//			if(cluster2Data!=null)
//				clusterHelper.submitEntity(URLS.SUBMIT_URL,cluster2Data);
//			clusterHelper.submitEntity(URLS.SUBMIT_URL,bundle.getClusterData());
//			logger.info("dataset is: "+bundle.getDataSets().get(0));
//			ServiceResponse dataSubmission=dataHelper.submitEntity(URLS.SUBMIT_URL,bundle.getDataSets().get(0));
//			Util.assertSucceeded(dataSubmission,"should have been submitted");
//		}
//		finally{
//			clusterHelper.delete(Util.URLS.DELETE_URL,bundle.getClusterData());
//			if(cluster2Data != null)
//				clusterHelper.delete(Util.URLS.DELETE_URL,cluster2Data);
//		}
//	}
//
//
//	public Bundle setFeedName(String newName) throws Exception
//	{
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setName(newName);
//		writeFeedElement(bundle,feedElement);
//		return bundle;
//	}
//
//	public Bundle setFeedPartition(String partName1, String partName2) throws Exception
//	{
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Util.print("partition 1: "+partName1 + "partition 2: "+partName2);
//		Partition part1 = new Partition();
//		part1.setName(partName1);
//		Partition part2 = new Partition();
//		part2.setName(partName2);
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.getPartitions().getPartition().set(0,part1);
//		feedElement.getPartitions().getPartition().set(1,part2);
//		writeFeedElement(bundle,feedElement); 
//		return bundle;
//	}
//
//	public Bundle setFeedCluster(Validity v1,Retention r1,String n1,ClusterType t1,Validity v2,Retention r2,String n2,ClusterType t2) throws Exception
//	{
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Cluster c1 = new Cluster();
//		c1.setName(n1);
//		c1.setRetention(r1);
//		c1.setType(t1);
//		c1.setValidity(v1);
//
//		Cluster c2 = new Cluster();
//		c2.setName(n2);
//		c2.setRetention(r2);
//		c2.setType(t2);
//		c2.setValidity(v2);
//
//		if(v1.getStart().equals("allNull"))
//			c1=c2=null;
//
//		com.inmobi.qa.airavatqa.generated.cluster.Cluster clusterElement = getClusterElement(bundle);
//		clusterElement.setName(n1);
//		writeClusterElement(bundle, clusterElement);
//
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.getClusters().getCluster().set(0, c1);
//		feedElement.getClusters().getCluster().add(c2);
//		writeFeedElement(bundle,feedElement);
//
//
//		clusterElement.setName(n2);
//		JAXBContext jc=JAXBContext.newInstance(com.inmobi.qa.airavatqa.generated.cluster.Cluster.class); 
//		java.io.StringWriter sw = new StringWriter();
//		Marshaller marshaller = jc.createMarshaller();
//		marshaller.marshal(clusterElement,sw);
//		logger.info("modified cluster 2 is: "+sw);
//		cluster2Data = sw.toString();
//		return bundle;
//	}
//
//	private void writeClusterElement(Bundle bundle,
//			com.inmobi.qa.airavatqa.generated.cluster.Cluster clusterElement) throws Exception{
//		JAXBContext jc=JAXBContext.newInstance(com.inmobi.qa.airavatqa.generated.cluster.Cluster.class); 
//		java.io.StringWriter sw = new StringWriter();
//		Marshaller marshaller = jc.createMarshaller();
//		marshaller.marshal(clusterElement,sw);
//		logger.info("modified cluster is: "+sw);
//		bundle.setClusterData(sw.toString());
//	}
//
//	private com.inmobi.qa.airavatqa.generated.cluster.Cluster getClusterElement(
//			Bundle bundle) throws Exception {
//		JAXBContext jc=JAXBContext.newInstance(com.inmobi.qa.airavatqa.generated.cluster.Cluster.class); 
//		Unmarshaller u=jc.createUnmarshaller();
//		return (com.inmobi.qa.airavatqa.generated.cluster.Cluster)u.unmarshal((new StringReader(bundle.getClusterData())));
//	}
//
//
//	public void removeFeedDescription(Bundle bundle) throws Exception
//	{
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setDescription(null);
//		writeFeedElement(bundle,feedElement);
//	}
//
//	public void removeFeedPartition(Bundle bundle) throws Exception
//	{
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setPartitions(null);
//		writeFeedElement(bundle,feedElement);
//	}
//
//	public void removeFeedClusters(Bundle bundle) throws Exception
//	{
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setClusters(null);
//		writeFeedElement(bundle,feedElement);
//	}
//	private void removeFeedFrequency(Bundle bundle) throws Exception{
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setFrequency(null);
//		writeFeedElement(bundle,feedElement);		
//	}
//
//
//	private void removeFeedPeriodicity(Bundle bundle) throws Exception{
//		Feed feedElement = getFeedElement(bundle);
//		feedElement.setPeriodicity(null);
//		writeFeedElement(bundle,feedElement);		
//	}
//	public Feed getFeedElement(Bundle bundle) throws Exception
//	{
//		JAXBContext jc=JAXBContext.newInstance(Feed.class); 
//		Unmarshaller u=jc.createUnmarshaller();
//		return (Feed)u.unmarshal((new StringReader(bundle.getDataSets().get(0))));
//	}
//
//	public void writeFeedElement(Bundle bundle, Feed feedElement) throws Exception
//	{
//		JAXBContext jc=JAXBContext.newInstance(Feed.class); 
//		java.io.StringWriter sw = new StringWriter();
//		Marshaller marshaller = jc.createMarshaller();
//		marshaller.marshal(feedElement,sw);
//		List<String> bundleData = new ArrayList<String>();
//		logger.info("modified dataset is: "+sw);
//		bundleData.add(sw.toString());
//		bundleData.add(bundle.getDataSets().get(1));
//		bundle.setDataSets(bundleData);
//	}
//
//	@DataProvider(name="partitionTest-DP")
//	public Object[][] getELFeedPartitionTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setFeedPartition(null,null)},
//				{setFeedPartition("***",null)},
//				{setFeedPartition(null,"***")}
//		};
//	}
//
//	@DataProvider(name="clusterTest-DP")
//	public Object[][] getELFeedClusterTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				 {setFeedCluster(xmlUtil.createValidity("2009-04-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"Invalid start date: 2009-04-31T00:00Z for cluster: clustName"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00T","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"for type 'date-time-type'"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"for type 'date-time-type'"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:abZ","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"for type 'date-time-type'"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T 00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"for type 'date-time-type'"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00ZZ","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"for type 'date-time-type'"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",null),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)," Attribute 'timezone' must appear on element 'validity'."}
//			//	,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"Value 'invalidTimeZone' is not facet-valid with respect to enumeration '[UTC]'. It must be a value from the enumeration."}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("years(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)," for type 'validity-type'."}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),null,ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"Attribute 'name' must appear on element 'cluster'."}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"***clustName***",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)," Value '***clustName***' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET),"Feed should have atleast one source cluster"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Feed should not have more than one source cluster"}
//			//	,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value 'GMT' is not facet-valid with respect to enumeration '[UTC]'."}
//			//	,{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value 'GMT' is not facet-valid with respect to enumeration '[UTC]'."}
//				,{setFeedCluster(xmlUtil.createValidity("2009-02-30T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Invalid start date: 2009-02-30T00:00Z for cluster:"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-03-32T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-03-32T00:00Z' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-04-31T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Invalid end date: 2012-04-31T00:00Z for cluster: corp99"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-04-00T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-04-00T00:00Z' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-4-1T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-4-1T00:00Z' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-40-10T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-40-10T00:00Z' is not facet-valid with respect to pattern"}			,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-04-24:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-04-24:00Z' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-02-10T24:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-02-10T24:00Z' is not facet-valid with respect to pattern"}
//				,{setFeedCluster(xmlUtil.createValidity("2009-03-31T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.TARGET,xmlUtil.createValidity("2009-02-01T00:00Z","2012-02-10T00:60Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.SOURCE),"Value '2012-02-10T00:60Z' is not facet-valid with respect to pattern"}
//		};
//	}
//
//	@DataProvider(name="pos-clusterTest-DP")
//	public Object[][] getELFeedClusterPosTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)},
//				{setFeedCluster(xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("minutes(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)},
//				{setFeedCluster(xmlUtil.createValidity("2009-12-31T23:59Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"clustName",ClusterType.SOURCE,xmlUtil.createValidity("2009-02-01T00:00Z","2012-01-01T00:00Z",TimezoneType.UTC),xmlUtil.createRtention("hours(10)",ActionType.DELETE),"corp99",ClusterType.TARGET)},
//
//		};
//	}
//
//	@DataProvider(name="stringTest-DP")
//	public Object[][] getELTestData(Method m) throws Exception
//	{
//		return new Object[][] {
//
//				{setFeedName(null)},
//				{setFeedName("***")},
//				{setFeedName("  ")},
//				{setFeedName(" .adbsch")},
//				{setFeedName("name***")}
//		};
//	}
//}
//
