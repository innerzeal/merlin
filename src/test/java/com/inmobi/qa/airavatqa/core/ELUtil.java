package com.inmobi.qa.airavatqa.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.log4testng.Logger;

/**
 * 
 * @author samarth.gupta
 * 
 */
public class ELUtil {
        
        static Logger logger=Logger.getLogger(ELUtil.class);

    	static PrismHelper prismHelper=new PrismHelper("prism.properties");
    	static ColoHelper ivoryqa1 = new ColoHelper("ivoryqa-1.config.properties");
    	
        
	public static String testWith(String feedStart, String feedEnd,String processStart, String processend,String startInstance, String endInstance,boolean isMatch) throws Exception
	{
		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
		bundle  = new Bundle(bundle,ivoryqa1.getEnvFileName());

		bundle.setFeedValidity(feedStart,feedEnd,Util.getInputFeedNameFromBundle(bundle));
		bundle.setProcessValidity(processStart,processend);
		//System.out.println("processData is: "+ bundle.getProcessData());
		try{

			bundle.setInvalidData();
			bundle.setDatasetInstances(startInstance,endInstance);
			String submitResponse=bundle.submitAndScheduleBundle(prismHelper);
			logger.info("processData in try is: "+bundle.getProcessData());
			if(isMatch)
				getAndMatchDependencies(prismHelper,bundle);

			return submitResponse ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}
		finally
		{
			logger.info("deleting entity:");
			bundle.deleteBundle(prismHelper);
		}

	}
	
	public static String testWith(String processStart, String processend,String startInstance, String endInstance,boolean isMatch) throws Exception
	{
		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
		bundle  = new Bundle(bundle,ivoryqa1.getEnvFileName());

		bundle.setProcessValidity(processStart,processend);
		//System.out.println("processData is: "+ bundle.getProcessData());
		try{

			bundle.setInvalidData();
			bundle.setDatasetInstances(startInstance,endInstance);
			String submitResponse=bundle.submitAndScheduleBundle(prismHelper);
			logger.info("processData in try is: "+bundle.getProcessData());
			if(isMatch)
				getAndMatchDependencies(prismHelper,bundle);

			return submitResponse ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}
		finally
		{
			logger.info("deleting entity:");
			bundle.deleteBundle(prismHelper);
		}

	}
	public static String testWith(String startInstance, String endInstance,boolean isMatch) throws Exception
	{
		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
		bundle  = new Bundle(bundle,ivoryqa1.getEnvFileName());

		try{
			//start="2010-01-02T01:00Z"

			bundle.setInvalidData();
			bundle.setDatasetInstances(startInstance,endInstance);
			String submitResponse=bundle.submitAndScheduleBundle(prismHelper);
			if(isMatch)
				getAndMatchDependencies(prismHelper,bundle);

			return submitResponse ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}
		finally
		{
			logger.info("deleting entity:");
			bundle.deleteBundle(prismHelper);
		}
	}
	/*public static String testWith(String startInstance, String endInstance) throws Exception
	{
		Bundle bundle = (Bundle)Util.readELBundles()[0][0];
		try{
			//start="2010-01-02T01:00Z"

			bundle.setInvalidData();
			bundle.setDatasetInstances(startInstance,endInstance);
			String submitResponse=bundle.submitAndScheduleBundle();

			getAndMatchDependencies(bundle);
			return submitResponse ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}
		finally
		{
			System.out.println("deleting entity:");
			bundle.deleteBundle();
		}
	}*/
	public static void getAndMatchDependencies(PrismHelper prismHelper,Bundle bundle) throws Exception
	{
		try{
			String coordID = Util.getCoordID(Util.getOozieJobStatus(prismHelper,Util.getProcessName(bundle.getProcessData()),"NONE").get(0));
			Util.print("coord id: "+ coordID);
			Thread.sleep(30000);
			ArrayList<String> missingDependencies = Util.getMissingDependencies(prismHelper,coordID);
			for(String dependency : missingDependencies)
			{
				Util.print("dependency from job: "+dependency);
			}
			Date jobNominalTime =  Util.getNominalTime(prismHelper,coordID);
			
			Calendar time = Calendar.getInstance();
			//time.setTimeZone(TimeZone.getTimeZone("GMT"));
			time.setTime(jobNominalTime);
			Util.print("nominalTime:"+jobNominalTime);
			Util.print("nominalTime in GNT string: "+jobNominalTime.toGMTString());

			TimeZone z = time.getTimeZone();
			int offset = z.getRawOffset();
			int offsetHrs = offset / 1000 / 60 / 60;
			int offsetMins = offset / 1000 / 60 % 60;

			System.out.println("offset: " + offsetHrs);
			System.out.println("offset: " + offsetMins);

			time.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
			time.add(Calendar.MINUTE, (-offsetMins));

			logger.info("GMT Time: "+time.getTime());

			int frequency = bundle.getInitialDatasetFrequency();

			ArrayList<String> qaDependencyList = getQADepedencyList(time,bundle.getStartInstanceProcess(time), bundle.getEndInstanceProcess(time), frequency,bundle);
			for(String qaDependency : qaDependencyList)	
				Util.print("qa qaDependencyList: "+qaDependency );


			Assert.assertTrue(matchDependencies(missingDependencies,qaDependencyList));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}
	}

	public static boolean  matchDependencies(ArrayList<String> fromJob, ArrayList<String> QAList)
	{
		if(fromJob.size() != QAList.size())
			return false ; 

		for(int index = 0 ; index < fromJob.size() ; index++)
		{
			if(!fromJob.get(index).contains(QAList.get(index)))
				return false ; 
		}

		return true;
	}


	public static ArrayList<String> getQADepedencyList(Calendar nominalTime, Date startRef, Date endRef, int frequency, Bundle bundle) throws Exception
	{
		//nominalTime.add(Calendar.MINUTE,-330);

		Util.print("start ref:"+ startRef);
		Util.print("end ref:"+ endRef);

		Calendar initialTime = Calendar.getInstance();
		initialTime.setTime(startRef);
		//initialTime.
		Calendar finalTime = Calendar.getInstance();


		finalTime.setTime(endRef);
		String path = Util.getDatasetPath(bundle);

		TimeZone tz = TimeZone.getTimeZone("GMT");
		nominalTime.setTimeZone(tz);
		Util.print("nominalTime: "+initialTime.getTime());


		Util.print("finalTime: "+ finalTime.getTime());

		ArrayList<String> returnList = new ArrayList<String>();

		while(!initialTime.getTime().equals(finalTime.getTime()))
		{
			Util.print("initialTime: "+initialTime.getTime());
			returnList.add(getPath(path,initialTime));
			initialTime.add(Calendar.MINUTE,frequency);
		}
		returnList.add(getPath(path,initialTime));
		Collections.reverse(returnList);
		return returnList;
	}


	public static String intToString(int num, int digits)
	{
		assert digits > 0 : "Invalid number of digits";

		// create variable length array of zeros
		char[] zeros = new char[digits];
		Arrays.fill(zeros, '0');
		// format number as String
		DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

		return df.format(num);
	}

	public static String getPath(String path, Calendar time)
	{
		//Util.print("function: getpath start");
		//Util.print("pathTime: "+time.getTime());
		if(path.contains("${YEAR}"))
		{
			//path.replace("${YEAR}",Integer.toString(initialTime.get(Calendar.YEAR));
			path = path.replaceAll("\\$\\{YEAR\\}",Integer.toString(time.get(Calendar.YEAR)));
		}

		if(path.contains("${MONTH}"))
		{
			path = path.replaceAll("\\$\\{MONTH\\}",intToString(time.get(Calendar.MONTH)+1, 2));
			//path = path.replaceAll("\\$\\{MONTH\\}",Integer.toString(initialTime.get(Calendar.MONTH)+1));
		}


		if(path.contains("${DAY}"))
		{
			path = path.replaceAll("\\$\\{DAY\\}",intToString(time.get(Calendar.DAY_OF_MONTH), 2));
			//path = path.replaceAll("\\$\\{DAY\\}",Integer.toString(initialTime.get(Calendar.DAY_OF_MONTH)));
		}

		if(path.contains("${HOUR}"))
		{
			path = path.replaceAll("\\$\\{HOUR\\}",intToString(time.get(Calendar.HOUR_OF_DAY), 2));
			//path = path.replaceAll("\\$\\{HOUR\\}",Integer.toString(initialTime.get(Calendar.HOUR_OF_DAY)));
		}

		if(path.contains("${MINUTE}"))
		{
			//Util.print("minutes before replacing path: "+intToString(time.get(Calendar.MINUTE), 2));
			path = path.replaceAll("\\$\\{MINUTE\\}",intToString(time.get(Calendar.MINUTE), 2));
			//path = path.replaceAll("\\$\\{MINUTE\\}",Integer.toString(initialTime.get(Calendar.MINUTE)));
		}

		//Util.print("new path: "+path);
		//Util.print("function: getpath end");
		return path;
	}


	public static Date getMinutes(String expression,Calendar time)
	{
		int hr=0; 
		int mins=0 ; 
		int day = 0 ;
		int month=0;

		Calendar cal = Calendar.getInstance();
		cal.setTime(time.getTime());
		//cal.add(Calendar.MINUTE,-330);
		if(expression.contains("now"))
		{
			hr = Integer.parseInt(expression.substring(expression.indexOf('(')+1, expression.indexOf(',')));
			mins = Integer.parseInt(expression.substring(expression.indexOf(',')+1, expression.indexOf(')')));
			cal.add(Calendar.HOUR,hr);
			cal.add(Calendar.MINUTE,mins);
		}
		else if(expression.contains("today"))
		{
			hr = Integer.parseInt(expression.substring(expression.indexOf('(')+1, expression.indexOf(',')));
			mins = Integer.parseInt(expression.substring(expression.indexOf(',')+1, expression.indexOf(')')));
			cal.add(Calendar.HOUR,hr-(cal.get(Calendar.HOUR_OF_DAY)));
			cal.add(Calendar.MINUTE,mins);
		}
		else if(expression.contains("yesterday"))
		{
			hr = Integer.parseInt(expression.substring(expression.indexOf('(')+1, expression.indexOf(',')));
			mins = Integer.parseInt(expression.substring(expression.indexOf(',')+1, expression.indexOf(')')));
			//cal.add(Calendar.HOUR,-24);
			cal.add(Calendar.HOUR,hr-(cal.get(Calendar.HOUR_OF_DAY))-24);
			cal.add(Calendar.MINUTE,mins);
		}
		else if(expression.contains("currentMonth"))
		{
			expression = expression.substring(expression.indexOf("(")+1, expression.indexOf(")"));
			ArrayList<String> afterSplit = new ArrayList<String>(Arrays.asList(expression.split(",")));
			day = Integer.parseInt(afterSplit.get(0));
			hr = Integer.parseInt(afterSplit.get(1));
			mins = Integer.parseInt(afterSplit.get(2));
			cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),1,0,0);
			cal.add(Calendar.HOUR,24*day+hr);
			cal.add(Calendar.MINUTE,mins);

		}
		else if(expression.contains("lastMonth"))
		{
			expression = expression.substring(expression.indexOf("(")+1, expression.indexOf(")"));
			ArrayList<String> afterSplit = new ArrayList<String>(Arrays.asList(expression.split(",")));
			day = Integer.parseInt(afterSplit.get(0));
			hr = Integer.parseInt(afterSplit.get(1));
			mins = Integer.parseInt(afterSplit.get(2));
			cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)-1,1,0,0);
			cal.add(Calendar.HOUR,24*day+hr);
			cal.add(Calendar.MINUTE,mins);
		}
		else if(expression.contains("currentYear"))
		{
			expression = expression.substring(expression.indexOf("(")+1, expression.indexOf(")"));
			ArrayList<String> afterSplit = new ArrayList<String>(Arrays.asList(expression.split(",")));
			month = Integer.parseInt(afterSplit.get(0));
			day = Integer.parseInt(afterSplit.get(1));
			hr = Integer.parseInt(afterSplit.get(2));
			mins = Integer.parseInt(afterSplit.get(3));
			cal.set(cal.get(Calendar.YEAR),1,1,0,0);
			cal.add(Calendar.MONTH,month -1);
			cal.add(Calendar.HOUR,24*day+hr);
			cal.add(Calendar.MINUTE,mins);
		}
		else if(expression.contains("lastYear"))
		{
			expression = expression.substring(expression.indexOf("(")+1, expression.indexOf(")"));
			ArrayList<String> afterSplit = new ArrayList<String>(Arrays.asList(expression.split(",")));
			month = Integer.parseInt(afterSplit.get(0));
			day = Integer.parseInt(afterSplit.get(1));
			hr = Integer.parseInt(afterSplit.get(2));
			mins = Integer.parseInt(afterSplit.get(3));
			cal.set(cal.get(Calendar.YEAR)-1,1,1,0,0);
			cal.add(Calendar.MONTH,month -1);
			cal.add(Calendar.HOUR,24*day+hr);
			cal.add(Calendar.MINUTE,mins);
		}
		return cal.getTime();

	}
}


