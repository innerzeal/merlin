package com.inmobi.qa.airavatqa.prism;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.inmobi.qa.airavatqa.core.APIResult;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ColoHelper;
import com.inmobi.qa.airavatqa.core.PrismHelper;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.core.prismUtil;
import com.jcraft.jsch.Logger;

public class PrismSubmitTest {


	@BeforeMethod(alwaysRun=true)
	public void testName(Method method) throws Exception
	{
		Util.print("test name: "+method.getName());
		//restart server as precaution
		Util.restartService(UA1coloHelper.getClusterHelper());
		Util.restartService(ivoryqa1.getClusterHelper());

	}



	public PrismSubmitTest() throws Exception{

	}

	PrismHelper prismHelper=new PrismHelper("prism.properties");

	ColoHelper UA1coloHelper=new ColoHelper("mk-qa.config.properties");

	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");

	

	@Test
	public void submitCluster_1prism1coloPrismdown() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(prismHelper.getClusterHelper());


			ArrayList<String> beforeSubmit =UA1coloHelper.getClusterHelper().getStoreInfo();

			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			ArrayList<String> afterSubmit = UA1coloHelper.getClusterHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit, afterSubmit,Util.readClusterName(b.getClusters().get(0)),0);

			Assert.assertTrue(r.getMessage().contains("FAILED"));

		}

		finally{
			Util.restartService(prismHelper.getClusterHelper());
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	
	
	@Test
	public void submitCluster_resubmitDiffContent() throws Exception
	{
		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();


		try{


			//submit unique entity
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			//b.setInputFeedAvailabilityFlag("hihello");
			b.setCLusterWorkingPath(b.getClusters().get(0),"/projects/ivory/someRandomPath");
			Util.print("modified cluster Data: " + b.getClusters().get(0));
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			
			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("already registered with configuration store. Can't be submitted again. Try removing before submitting"));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}
	
	
	@Test
	public void submitCluster_resubmitAlreadyPARTIALWithAllUp() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);

			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("PARTIAL"));

			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);

			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));


		}

		finally{

			Util.restartService(UA1coloHelper.getClusterHelper());

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	

	
	@Test
	public void submitProcess_1ColoDownAfter2FeedSubmitStartAfterProcessSubmitAnsDeleteProcess() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(12000);

			

			ArrayList<String>	 beforeSubmit_ua1 =UA1coloHelper.getProcessHelper().getStoreInfo();
			ArrayList<String>	 beforeSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			ArrayList<String>	 beforeSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();		
			
			r= prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());

			r =  prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL, b.getProcessData());
			Util.assertFailed(r);
			
			ArrayList<String>	afterSubmit_ua1 = UA1coloHelper.getProcessHelper().getStoreInfo();
			ArrayList<String>	afterSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			ArrayList<String>	afterSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.getProcessName(b.getProcessData()),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.getProcessName(b.getProcessData()),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.getProcessName(b.getProcessData()),0);		
			
			
			
			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(15000);
			
			
			 beforeSubmit_ua1 =UA1coloHelper.getProcessHelper().getStoreInfo();
			 beforeSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			 beforeSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();		
			

			r= prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			Assert.assertTrue(r.getMessage().contains("FAILED"));

			
			afterSubmit_ua1 = UA1coloHelper.getProcessHelper().getStoreInfo();
			afterSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			afterSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.getProcessName(b.getProcessData()),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.getProcessName(b.getProcessData()),-1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.getProcessName(b.getProcessData()),0);	
			
		}

		finally{
			Util.restartService(UA1coloHelper.getClusterHelper());
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));

		}
	}
	
	
	@Test
	public void submitProcess_ideal() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			
			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getFeedHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getFeedHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getFeedHelper().getStoreInfo();			
			
			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getFeedHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getFeedHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getFeedHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,2);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,2);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,0);			
			
			
			 beforeSubmit_ua1 =UA1coloHelper.getProcessHelper().getStoreInfo();
			 beforeSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			 beforeSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();			
			
			
			r =  prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL, b.getProcessData());
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			
			afterSubmit_ua1 = UA1coloHelper.getProcessHelper().getStoreInfo();
			afterSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			afterSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.getProcessName(b.getProcessData()),1);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.getProcessName(b.getProcessData()),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.getProcessName(b.getProcessData()),0);				

		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
		}
	}

	
	
	@Test
	public void submitCluster_1prism1coloColoDown() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(UA1coloHelper.getClusterHelper());

			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();			
			
			ServiceResponse	r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			

			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			//should be partial
			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readClusterName(b.getClusters().get(0)),1);			
			
			
			Util.startService(UA1coloHelper.getClusterHelper());

			Thread.sleep(10000);


			beforeSubmit_ua1 =UA1coloHelper.getClusterHelper().getStoreInfo();
			beforeSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			beforeSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();			
			
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			

			 afterSubmit_ua1 = UA1coloHelper.getClusterHelper().getStoreInfo();
			 afterSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			 afterSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			//should be succeeded
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readClusterName(b.getClusters().get(0)),0);			
			
		}

		finally{
			Util.startService(UA1coloHelper.getClusterHelper());
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}


	
	
	@Test
	public void submitCluster_1prism1coloSubmitDeleted() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());


			
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));

		
			
			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();			
			
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			

			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readClusterName(b.getClusters().get(0)),1);			
			

		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	

	

	@Test
	public void submitProcess_woClusterSubmit() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r =  prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL, b.getProcessData());

			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("is not registered"));

		}

		finally{
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
		}
	}

	@Test
	public void submitProcess_woFeedSubmit() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r =  prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL, b.getProcessData());

			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("is not registered"));

		}

		finally{

			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));
		}
	}


	

	
	@Test
	public void submitCluster_resubmitAlreadyPARTIAL() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);

			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("PARTIAL"));

			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("already registered with configuration store. Can't be submitted again. Try removing before submitting"));


		}

		finally{

			Util.restartService(UA1coloHelper.getClusterHelper());

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}




	

	@Test
	public void submitCluster_polarization() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			//shutdown one colo and submit 

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),1);



			//resubmit PARTIAL success 
			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);
			beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);




		}

		finally{
			Util.restartService(UA1coloHelper.getClusterHelper());
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	

	@Test
	public void submitCluster_resubmitDiffContentPARTIAL() throws Exception
	{
		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();


		try{


			//submit unique entity PARTIAL
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);

			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			//b.setInputFeedAvailabilityFlag("hihello");
			b.setCLusterWorkingPath(b.getClusters().get(0),"/projects/ivory/someRandomPath");
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("FAILED"));
			Assert.assertTrue(r.getMessage().contains("already registered with configuration store. Can't be submitted again. Try removing before submitting"));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	@Test
	public void submitCluster_PARTIALDeletedOfPARTIALSubmit() throws Exception
	{
		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();


		try{


			//submit unique entity PARTIAL
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			//Util.startService(UA1coloHelper.getClusterHelper());
			//Thread.sleep(30000);



			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			r =  prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),-1);

			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("PARTIAL"));

		}
		catch(Exception e){
			e.printStackTrace();
		}

		finally{
			Util.restartService(UA1coloHelper.getClusterHelper());

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}
	
	@Test
	public void submitCluster_submitPartialDeleted() throws Exception
	{
		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();


		try{


			//submit unique entity PARTIAL
			b = new Bundle(b,UA1coloHelper.getEnvFileName());
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			Thread.sleep(30000);

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);
			
			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			r =  prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("PARTIAL"));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),-1);

			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);
			
			
			
			beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),1);

		}
		catch(Exception e){
			e.printStackTrace();
		}

		finally{
			Util.restartService(UA1coloHelper.getClusterHelper());

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}
	

	@Test
	public void submitCluster_resubmitAlreadySucceeded() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			//submit unique entity
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			ArrayList<String> beforeSubmit_colo =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			ArrayList<String> afterSubmit_colo = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =ivoryqa1.getClusterHelper().getStoreInfo();
			prismUtil.compareDataStoreStates(beforeSubmit_colo, afterSubmit_colo,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);
		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}

	
	


	@Test
	public void submitCluster_1prism1coloAllUp() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			
			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			
			
			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readClusterName(b.getClusters().get(0)),1);

			
			
		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}


	@Test
	public void submitCluster_1prism1coloAlreadySubmitted() throws Exception
	{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());



			ServiceResponse r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));

			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();			
			
			r =  prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			

			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getClusterHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getClusterHelper().getStoreInfo();

			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));
			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readClusterName(b.getClusters().get(0)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readClusterName(b.getClusters().get(0)),0);

		}

		finally{
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL, b.getClusters().get(0));
		}
	}
	@Test
	public void submitProcess_1ColoDownAfter1FeedSubmitStartAfter2feed() throws Exception
	{
		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ServiceResponse r = prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL,b.getClusters().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			Assert.assertTrue(r.getMessage().contains("SUCCEEDED"));

			Util.shutDownService(UA1coloHelper.getClusterHelper());
			Thread.sleep(30000);

			
			ArrayList<String> beforeSubmit_ua1 =UA1coloHelper.getFeedHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_ua2 =ivoryqa1.getFeedHelper().getStoreInfo();
			ArrayList<String> beforeSubmit_prism =prismHelper.getFeedHelper().getStoreInfo();			
			
			r = prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));
			Assert.assertTrue(r.getMessage().contains("FAILED"));

			ArrayList<String> afterSubmit_ua1 = UA1coloHelper.getFeedHelper().getStoreInfo();
			ArrayList<String> afterSubmit_ua2 =ivoryqa1.getFeedHelper().getStoreInfo();
			ArrayList<String> afterSubmit_prism =prismHelper.getFeedHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.readDatasetName(b.getDataSets().get(1)),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.readDatasetName(b.getDataSets().get(1)),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.readDatasetName(b.getDataSets().get(1)),0);			
			
			
			Util.startService(UA1coloHelper.getClusterHelper());
			Thread.sleep(15000);


			 beforeSubmit_ua1 =UA1coloHelper.getProcessHelper().getStoreInfo();
			 beforeSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			 beforeSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();			
			
			r =  prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL, b.getProcessData());
			Assert.assertTrue(r.getMessage().contains("FAILED"));

			afterSubmit_ua1 = UA1coloHelper.getProcessHelper().getStoreInfo();
			afterSubmit_ua2 =ivoryqa1.getProcessHelper().getStoreInfo();
			afterSubmit_prism =prismHelper.getProcessHelper().getStoreInfo();

			prismUtil.compareDataStoreStates(beforeSubmit_ua1, afterSubmit_ua1,Util.getProcessName(b.getProcessData()),0);
			prismUtil.compareDataStoreStates(beforeSubmit_prism, afterSubmit_prism,Util.getProcessName(b.getProcessData()),1);
			prismUtil.compareDataStoreStates(beforeSubmit_ua2, afterSubmit_ua2,Util.getProcessName(b.getProcessData()),0);				

			
		}

		finally{
			Util.restartService(UA1coloHelper.getClusterHelper());
			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());

			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));

			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusters().get(0));
		}
	}
	

}
