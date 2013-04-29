package com.inmobi.qa.airavatqa.prism;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.inmobi.qa.ivory.bundle.Bundle;
import com.inmobi.qa.ivory.helpers.ColoHelper;
import com.inmobi.qa.ivory.helpers.PrismHelper;
import com.inmobi.qa.ivory.response.ServiceResponse;
import com.inmobi.qa.ivory.supportClasses.Brother;
import com.inmobi.qa.ivory.supportClasses.ENTITY_TYPE;
import com.inmobi.qa.ivory.util.Util;
import com.inmobi.qa.ivory.util.Util.URLS;



public class PrismConcurrentRequest {

	@BeforeMethod(alwaysRun=true)
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}



	public PrismConcurrentRequest() throws Exception{

	}

	PrismHelper prismHelper=new PrismHelper("prism.properties");

	ColoHelper UA1coloHelper=new ColoHelper("mk-qa.config.properties");

	ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");

	

	@Test
	public void submitSameFeedParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());



			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"submit", ENTITY_TYPE.DATA, brotherGrimm,b,prismHelper,URLS.SUBMIT_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);

			Thread.sleep(60000);

		}

		finally{

		}
	}

	@Test
	public void submitSameProcessParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));


			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"submit", ENTITY_TYPE.PROCESS, brotherGrimm,b,prismHelper,URLS.SUBMIT_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);

			Thread.sleep(60000);

		}

		finally{

		}
	}


	@Test
	public void deleteSameProcessParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));
			prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL,b.getProcessData());


			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"delete", ENTITY_TYPE.PROCESS, brotherGrimm,b,prismHelper,URLS.SUBMIT_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);

			Thread.sleep(60000);

		}

		finally{

			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}



	@Test
	public void schedulePrismParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(1));
			prismHelper.getProcessHelper().submitEntity(URLS.SUBMIT_URL,b.getProcessData());


			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"schedule", ENTITY_TYPE.PROCESS, brotherGrimm,b,prismHelper,URLS.SCHEDULE_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);

			Thread.sleep(60000);

		}

		finally{

			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}
	 


	@Test
	public void resumeAnsSuspendParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[4];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			ServiceResponse r = prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL,b.getDataSets().get(0));
			Thread.sleep(15000);
			prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL,b.getDataSets().get(0));
			Thread.sleep(15000);

			for(int i=1;i<=2;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"resume", ENTITY_TYPE.DATA, brotherGrimm,b,prismHelper,URLS.RESUME_URL);

			}

			for(int i=3;i<=4;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"suspend", ENTITY_TYPE.DATA, brotherGrimm,b,prismHelper,URLS.SUSPEND_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 3);


			Thread.sleep(60000);

		}

		finally{

			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}

	@Test
	public void resumeParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{
			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;

			prismHelper.getClusterHelper().submitEntity(URLS.SUBMIT_URL, b.getClusterData());
			prismHelper.getFeedHelper().submitEntity(URLS.SUBMIT_URL,b.getDataSets().get(0));
			ServiceResponse r = prismHelper.getFeedHelper().schedule(URLS.SCHEDULE_URL,b.getDataSets().get(0));
			Thread.sleep(15000);
			
			
			prismHelper.getFeedHelper().resume(URLS.RESUME_URL, b.getDataSets().get(0));
			Thread.sleep(5000);
			prismHelper.getFeedHelper().suspend(URLS.SUSPEND_URL,b.getDataSets().get(0));
			Thread.sleep(15000);
			
			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"resume", ENTITY_TYPE.DATA, brotherGrimm,b,prismHelper,URLS.RESUME_URL);

			}


			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);


			Thread.sleep(60000);

		}

		finally{

			prismHelper.getProcessHelper().delete(URLS.DELETE_URL,b.getProcessData());
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(0));
			prismHelper.getFeedHelper().delete(URLS.DELETE_URL, b.getDataSets().get(1));
			prismHelper.getClusterHelper().delete(URLS.DELETE_URL,b.getClusterData());
		}
	}

	
	@Test
	public void submitSameClusterParallel() throws Exception{

		Bundle b = (Bundle)Util.readELBundles()[0][0];
		b.generateUniqueBundle();

		try{


			b = new Bundle(b,UA1coloHelper.getEnvFileName());

			ThreadGroup brotherGrimm=new ThreadGroup("mixed");

			Brother brothers[]=new Brother[10];
			int failedResponse=0;
			int succeedeResponse=0 ;


			for(int i=1;i<=brothers.length;i++)
			{
				brothers[i-1]=new Brother("brother"+i,"submit", ENTITY_TYPE.CLUSTER, brotherGrimm,b,prismHelper,URLS.SUBMIT_URL);

			}

			for(Brother brother:brothers)
			{
				brother.start();

			}

			Thread.sleep(60000);

			for(Brother brother:brothers){

				if(brother.getOutput().getMessage().contains("SUCCEEDED"))
					succeedeResponse++;
				else if (brother.getOutput().getMessage().contains("FAILED"))
					failedResponse++;
			}

			Assert.assertEquals(succeedeResponse, 1);
			Assert.assertEquals(failedResponse, 9);

			Thread.sleep(60000);

		}

		finally{

		}
	}

}
