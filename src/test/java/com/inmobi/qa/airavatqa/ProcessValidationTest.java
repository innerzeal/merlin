//package com.inmobi.qa.airavatqa;
//
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.lang.reflect.Method;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import java.math.BigInteger;
//import com.inmobi.qa.airavatqa.core.Bundle;
//import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
//import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
//import com.inmobi.qa.airavatqa.core.ServiceResponse;
//import com.inmobi.qa.airavatqa.core.Util;
//import com.inmobi.qa.airavatqa.core.instanceUtil;
//import com.inmobi.qa.airavatqa.core.xmlUtil;
//import com.inmobi.qa.airavatqa.generated.ExecutionType;
//import com.inmobi.qa.airavatqa.generated.FrequencyType;
//import com.inmobi.qa.airavatqa.generated.Input;
//import com.inmobi.qa.airavatqa.generated.PolicyType;
//import com.inmobi.qa.airavatqa.generated.Process;
//import com.inmobi.qa.airavatqa.generated.Cluster;
//import com.inmobi.qa.airavatqa.generated.Property;
//import com.inmobi.qa.airavatqa.generated.Retry;
//import com.inmobi.qa.airavatqa.generated.TimezoneType;
//import com.inmobi.qa.airavatqa.generated.Validity;
//import com.inmobi.qa.airavatqa.generated.Workflow;
//
//
//import com.inmobi.qa.airavatqa.generated.feed.Feed;
//
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//
//
//public class ProcessValidationTest {
//
//	@BeforeMethod
//	public void testName(Method method)
//	{
//		Util.print("test name: "+method.getName());
//	}
//
//	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
//	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
//	IEntityManagerHelper clusterHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.CLUSTER);
//
//	@Test(groups = { "0.1","0.2"},dataProvider="periodicity-DP")
//	public void processPeriodicityTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//	
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="clusterTest-DP")
//	public void processClusterTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="concurrencyTest-DP")
//	public void processConcurrencyTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="validityTestValid-DP")
//	public void processValidityTest_valid(Bundle bundle) throws Exception
//	{
//		submitProcessSuccess(bundle);
//	} 
//	
//	@Test(groups = { "0.1","0.2"},dataProvider="validityTest-DP")
//	public void processValidityTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processFrequencyMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessFrequency(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'periodicity'. One of '{frequency}' is expected.");
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="Workflow-DP")
//	public void processWorkflowTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processPeriodicityMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessPeriodicity(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'validity'. One of '{periodicity}' is expected.");
//	} 
//
//
//	//	@Test(dataProvider="retry-DP")
//	//	public void processRetryTest(Bundle bundle,String expectedError) throws Exception
//	//	{
//	//		submitProcessFailuer(bundle,expectedError);
//	//	} 
//
//	
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processWorkflowMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessWorkflow(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'retry'. One of '{workflow}' is expected.");
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="inputTest-DP")
//	public void processInputTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processInputsMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessInputs(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'outputs'. One of '{inputs}' is expected.");
//	} 
//
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processValidityMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessValidity(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'inputs'. One of '{validity}' is expected.");
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="executionTest-DP")
//	public void processExecutionTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processExecutionMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessExecution(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'frequency'. One of '{execution}' is expected");
//	}
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processConcurrencyMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessConcurrencyr(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'execution'. One of '{concurrency}' is expected.");
//	}
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void clusterTagMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removePrecessCluster(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'concurrency'. One of '{cluster}' is expected.");
//	}
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processNameMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessName(bundle);
//		submitProcessFailuer(bundle,"Attribute 'name' must appear on element 'process'.");
//	}
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processPropertiesMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessProperties(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'workflow'. One of '{properties}' is expected");
//	}
//
//
//	@Test(groups = { "0.1","0.2"},dataProvider="propertyTest-DP")
//	public void processPropertyTest(Bundle bundle,String expectedError) throws Exception
//	{
//		submitProcessFailuer(bundle,expectedError);
//	} 
//	
//	
///*	@Test(groups = { "0.1","0.2"},dataProvider="EL-DP", dataProviderClass = Bundle.class)
//	public void processRetryMissing(Bundle bundle) throws Exception
//	{
//		bundle.generateUniqueBundle();
//		removeProcessRetry(bundle);
//		submitProcessFailuer(bundle,"Invalid content was found starting with element 'late-process'. One of '{retry}' is expected.");
//	} */
//
//	private void removeProcessProperties(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setProperties(null);
//		instanceUtil.writeProcessElement(bundle,processElement);		
//	}
//
//	private void removeProcessPeriodicity(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setPeriodicity(null);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//	}
//
//
//	private void removeProcessFrequency(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setFrequency(null);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//	}
//
//	private void removeProcessRetry(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setRetry(null);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//	}
//
//	private void removeProcessWorkflow(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setWorkflow(null);
//		instanceUtil.writeProcessElement(bundle,processElement);		
//	}
//
//	private void removeProcessInputs(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setInputs(null);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//	}
//
//	private void removeProcessValidity(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setValidity(null);
//		instanceUtil.writeProcessElement(bundle,processElement);	
//
//	}
//
//	private void removeProcessExecution(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setExecution(null);
//		instanceUtil.writeProcessElement(bundle,processElement);		
//	}
//
//	private void removeProcessConcurrencyr(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setConcurrency(null);
//		instanceUtil.writeProcessElement(bundle,processElement);		
//	}
//
//	private void removePrecessCluster(Bundle bundle) throws Exception{
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setCluster(null);
//		instanceUtil.writeProcessElement(bundle,processElement);				
//	}
//
//	private void submitProcessFailuer(Bundle bundle,String expectedError) throws Exception {
//		try{
//
//			Util.print("process to be submitted: "+bundle.getProcessData());
//			ServiceResponse bundleResponse = bundle.submitBundle(false);
//			if(!bundleResponse.getMessage().contains(expectedError) && !expectedError.equals("test"))
//				Assert.assertTrue(false,"errorMessage not as Expected");
//			Util.assertFailed(bundleResponse,"should have not been submitted");
//		}
//		finally{
//			bundle.deleteBundle();
//		}
//
//	}
//
//	private void submitProcessSuccess(Bundle bundle) throws Exception {
//		try{
//
//			Util.print("process to be submitted: "+bundle.getProcessData());
//			ServiceResponse bundleResponse = bundle.submitBundle(false);
//			Util.assertSucceeded(bundleResponse,"should have been submitted");
//
//		}
//		finally{
//			bundle.deleteBundle();
//		}
//	}
//
//	private void removeProcessName(Bundle bundle) throws Exception {
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setName(null);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//	}
//
//	/*
//	private static void instanceUtil.writeFeedElement(Bundle bundle, Process processElement) throws Exception {
//		JAXBContext jc=JAXBContext.newInstance(Process.class); 
//		java.io.StringWriter sw = new StringWriter();
//		Marshaller marshaller = jc.createMarshaller();
//		marshaller.marshal(processElement,sw);
//		logger.info("modified process is: "+sw);
//		bundle.setProcessData(sw.toString());
//	}
//
//	public static Process instanceUtil.getProcessElement(Bundle bundle) throws Exception {
//		JAXBContext jc=JAXBContext.newInstance(Process.class); 
//		Unmarshaller u=jc.createUnmarshaller();
//		return (Process)u.unmarshal((new StringReader(bundle.getProcessData())));
//
//	}
//*/
//	@DataProvider(name="clusterTest-DP")
//	public Object[][] getELFeedClusterTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setFeedCluster("  "),"Value '  ' is not facet-valid with respect to pattern"},
//				{setFeedCluster("corpnotregisterd"),"is not registered"},
//				{setFeedCluster("corp_corp_corp_corp_corp_hahaha"),"is not registered"},
//				{setFeedCluster("...hai ye wonder full .. "),"is not facet-valid with respect to pattern"},
//				{setFeedCluster("hello\\hi"),"is not facet-valid with respect to pattern"},
//				{setFeedCluster("hello\\h/i"),"is not facet-valid with respect to pattern"},
//				{setFeedCluster("hello\\\\_asndchhi"),"is not facet-valid with respect to pattern"},
//				{setFeedCluster("999999999"),"is not facet-valid with respect to pattern"},
//				{setFeedCluster("a999999999"),"is not registered"},
//		};
//	}
//
//
//	@DataProvider(name="concurrencyTest-DP")
//	public Object[][] getELFeedConcurrencyTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setProcessConcurrency(BigInteger.valueOf(0)),"Value '0' is not facet-valid with respect to minInclusive '1' for type"},
//				{setProcessConcurrency(BigInteger.valueOf(-1)),"Value '-1' is not facet-valid with respect to minInclusive '1' for type"},
//				{setProcessConcurrency(null),"Invalid content was found starting with element 'execution'. One of '{concurrency}' is expected"},
//			//	{setProcessConcurrency(BigInteger.valueOf(11/2)),"is not facet-valid with respect to pattern"},
//
//		};
//	}
//	@DataProvider(name="propertyTest-DP")
//	public Object[][] getELFeedPropertyTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				///{setProcessProperty(null,null),"Value '0' is not facet-valid with respect to minInclusive '1' for type"},
//				{setProcessProperty("allNull",null),"must not appear on element 'property', because the {nillable} property of 'property' is false"},
//				{setProcessProperty("propName",null),"Attribute 'value' must appear on element 'property'"},
//				//{setProcessProperty(null,"propValue"),"Value '0' is not facet-valid with respect to minInclusive '1' for type"},
//
//		};
//	}
//	@DataProvider(name="inputTest-DP")
//	public Object[][] getELFeedInputTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setProcessInput("now(0,-60)","now(0,20)","  ",""),"Value '  ' is not facet-valid with respect to pattern"},
//				{setProcessInput("now(0,-60)","now(0,20)",null,null),"Attribute 'name' must appear on element 'input'."},
//				//{setProcessInput("now(0,-60)","now(0,20)","feedName","InvalidFeed"),"Attribute 'name' must appear on element 'input'."},
//				{setProcessInput(null,"now(0,20)","feedName","InvalidFeed"),"Attribute 'start-instance' must appear on element 'input'."},
//
//		};
//	}
//
//	private Object setProcessInput(String start, String end,String name,String part) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);		
//		Input i = new Input();
//		i.setEndInstance(end);
//		i.setFeed(getFeed1Name(bundle));
//		i.setName(name);
//		i.setPartition(part);
//		i.setStartInstance(start);
//		processElement.getInputs().getInput().set(0,i);
//		instanceUtil.writeProcessElement(bundle,processElement);
//		return bundle;
//	}
//
//private Object setProcessProperty(String name, String value) throws Exception
//{
//	Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//	bundle.generateUniqueBundle();
//	Process processElement = instanceUtil.getProcessElement(bundle);
//	Property p = new Property();
//	p.setName(name);
//	p.setValueAttribute(value);
//	if(name.equals("allNull"))
//		p = null;
//	processElement.getProperties().getProperty().set(0, p);
//	instanceUtil.writeProcessElement(bundle,processElement);
//	return bundle;
//}
//	private String getFeed1Name(Bundle bundle) throws Exception  {
//		JAXBContext jc=JAXBContext.newInstance(Feed.class); 
//		Unmarshaller u=jc.createUnmarshaller();
//		Feed dataElement=(Feed)u.unmarshal((new StringReader(bundle.dataSets.get(0))));
//		if(!dataElement.getName().contains("raaw-logs16"))
//		{
//			dataElement = (Feed)u.unmarshal(new StringReader(bundle.dataSets.get(1)));
//		}
//		return dataElement.getName();
//	}
//
//	@DataProvider(name="validityTest-DP")
//	public Object[][] getELFeedValidityTest(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setProcessValidity("","",TimezoneType.UTC),"Value '' is not facet-valid with respect to pattern"},
//				{setProcessValidity("2011-01-03T03:00","2011-01-03T03:00Z",TimezoneType.UTC),"Value '2011-01-03T03:00' is not facet-valid with respect to pattern"},
//				{setProcessValidity(null,"2011-01-03T03:00Z",TimezoneType.UTC),"Attribute 'start' must appear on element 'validity'."},
//				{setProcessValidity("2011-02-29T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC),"Invalid start date: 2011-02-29T03:00Z"},
//				{setProcessValidity("2011-02-31T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC),"Invalid start date: 2011-02-31T03:00Z"},
//				{setProcessValidity("2011-02-30T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC),"Invalid start date: 2011-02-30T03:00Z"},
//				{setProcessValidity("2011-02-32T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC),"Value '2011-02-32T03:00Z' is not facet-valid with respect to pattern"},
//				{setProcessValidity("2011-04-31T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC),"Invalid start date: 2011-04-31T03:00Z"},
//		};
//	}
//	@DataProvider(name="validityTestValid-DP")
//	public Object[][] getELFeedValidityTest_valid(Method m) throws Exception
//	{
//
//		return new Object[][] {
//
//				{setProcessValidity("2011-02-28T03:00Z","2012-01-03T03:00Z",TimezoneType.UTC)},
//				{setProcessValidity("2011-02-28T03:00Z","2011-02-28T03:00Z",TimezoneType.UTC)},
//				{setProcessValidity("2011-01-01T00:00Z","2011-12-31T23:59Z",TimezoneType.UTC)},
//				{setProcessValidity("2011-02-28T00:00Z","2012-02-29T23:59Z",TimezoneType.UTC)},
//
//
//		};
//	}
//	private Object setProcessValidity(String start, String end, TimezoneType timeZone) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		Validity v = new Validity();
//		v.setEnd(end);
//		v.setStart(start);
//		v.setTimezone(timeZone);
//		processElement.setValidity(v);
//		instanceUtil.writeProcessElement(bundle,processElement);
//		return bundle;
//	}
//
//	@DataProvider(name="periodicity-DP")
//	public Object[][] getELFeedRetryTest(Method m) throws Exception
//	{
//		return new Object[][] {
//
//				{setProcessPeriodicity(BigInteger.valueOf(-1)),"Value '-1' is not facet-valid with respect to minInclusive '1' for type"},
//				{setProcessPeriodicity(BigInteger.valueOf(0)),"Value '0' is not facet-valid with respect to minInclusive '1' for type"},
//			//	{setProcessPeriodicity(BigInteger.valueOf(0000001)),"Invalid content was found starting with element 'frequency'. One of '{execution}' is expected."},
//
//		};
//	}
//
//	private Object setProcessPeriodicity(BigInteger periodicity) throws Exception {
//
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);	
//		processElement.setPeriodicity(periodicity);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//
//	/*	@DataProvider(name="retry-DP")
//	public Object[][] getELFeedRetryTest(Method m) throws Exception
//	{
//		return new Object[][] {
//
//				{setProcessRetry(BigInteger.valueOf(-1),BigInteger.valueOf(10),),"Invalid content was found starting with element 'frequency'. One of '{execution}' is expected."},
//
//		};
//	}
//	 */
//	private Object setProcessRetry(BigInteger attempts, BigInteger delay, FrequencyType delayUnit) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);	
//		Retry r = new Retry();
//		r.setAttempts(attempts);
//		r.setDelay(delay);
//		r.setDelayUnit(delayUnit);
//		r.setPolicy(PolicyType.FINAL);
//		processElement.setRetry(r);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//
//	@DataProvider(name="executionTest-DP")
//	public Object[][] getELFeedExecutionTest(Method m) throws Exception
//	{
//		return new Object[][] {
//
//				{setProcessExecution(null),"Invalid content was found starting with element 'frequency'. One of '{execution}' is expected."},
//
//		};
//	}
//
//	@DataProvider(name="Workflow-DP")
//	public Object[][] getELFeedWorkflowTest(Method m) throws Exception
//	{
//		return new Object[][] {
//
//				{setProcessWorkflow("  "),"Workflow path:    does not exists in HDFS:"},
//				{setProcessWorkflow("invalid"),"Workflow path: invalid does not exists in HDFS: "},
//				{setProcessWorkflow("*****////"),"Workflow path: *****//// does not exists in HDFS:"},
//				{setProcessWorkflow(null),"Attribute 'path' must appear on element 'workflow'."},
//		};
//	}
//	private Object setProcessWorkflow(String workflow) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);	
//		Workflow f = new Workflow();
//		f.setPath(workflow);
//		//f.setLibpath("/examples/apps/aggregator/lib");
//		processElement.setWorkflow(f);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//
//	private Object setProcessExecution(ExecutionType executionType) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setExecution(executionType);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//
//	private Object setProcessConcurrency(BigInteger concurrency) throws Exception {
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setConcurrency(concurrency);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//
//	private Object setFeedCluster(String name) throws Exception {
//
//		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
//		bundle.generateUniqueBundle();
//		Cluster c = new Cluster();
//		c.setName(name);
//		Process processElement = instanceUtil.getProcessElement(bundle);
//		processElement.setCluster(c);
//		instanceUtil.writeProcessElement(bundle,processElement);			
//		return bundle;
//	}
//}
//
//
