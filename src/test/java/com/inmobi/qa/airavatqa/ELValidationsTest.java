package com.inmobi.qa.airavatqa;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.inmobi.qa.airavatqa.core.ELUtil;
import com.inmobi.qa.airavatqa.core.Util;


/**
 * 
 * 
 * @author samarth.gupta
 *
 */
public class ELValidationsTest {

	String response ; 
	// feed start <validity start="2009-02-01T00:00Z" end="2012-01-01T00:00Z"

	// process start <validity timezone="UTC" end="2011-01-03T03:00Z" start="2010-01-02T01:00Z" />

	//test for instance when process time line is subset of feed time 

	@BeforeMethod
	public void testName(Method method)
	{
		Util.print("test name: "+method.getName());
	}
	
	@Test(groups = { "0.1","0.2"}) 
	public void startInstBeforeFeedStart_today02() throws Exception	{
		//Start instance  now(-40,0) of feed raaw-logs16-ac91a90f-4a52-4b10-8541-4d63883a621e is before the start of feed 2009-02-02T20:00Z
		response = ELUtil.testWith("2009-02-02T20:00Z","2011-12-31T00:00Z","2009-02-02T20:00Z","2011-12-31T00:00Z","now(-40,0)","currentYear(20,30,24,20)",false);
		validate(response);
	}
	@Test(groups = { "0.1","0.2"}) 
	public void startInstAfterFeedEnd() throws Exception
	{
		response = ELUtil.testWith("currentYear(10,0,22,0)","now(4,20)",false);
		validate(response);
	}
	
	@Test(groups = { "0.1","0.2"}) 
	public void bothInstReverse() throws Exception
	{
		response = ELUtil.testWith("now(0,0)","now(-100,0)",false);
		validate(response);
	}
/*	
  
	
	@Test(groups = { "0.1","0.2"})(groups = {"sanity"})
	public void test() throws Exception
	{
		response = ELUtil.testWith("2009-02-02T20:00Z","2011-12-31T00:00Z","now(-40,0)","currentYear(20,30,24,20)",false);
		validate(response); 
	}

	
	@Test(groups = { "0.1","0.2"})(groups = {"sanity"})
	public void startInstBeforeFeedStart_mix() throws Exception
	{
		response = ELUtil.testWith("currentYear(-36,0,22,0)","now(4,20)",false);
		validate(response);
	}

	@Test(groups = { "0.1","0.2"}) 
	public void endInstBeforeStartTime() throws Exception
	{
		response = ELUtil.testWith("lastYear(-20,0,0,0)","lastYear(-10,0,0,0)",false);
		validate(response);
	}
		
	@Test(groups = { "0.1","0.2"}) 
	public void startInstBeforeFeedStar_today() throws Exception	{

		response = ELUtil.testWith("2009-02-01T20:00Z","2012-01-01T00:00Z","today(0,0)","today(4,20)",false);
		validate(response);
	}
	
	
	// test case when processtime is super set of feedtime line 

	@Test(groups = { "0.1","0.2"}) 
	public void startInstBeforeFeedStart_P() throws Exception
	{
		String response = ELUtil.testWith("2009-03-01T00:00Z","2012-02-01T00:00Z","today(144,0)","today(144,40)",false);
		validate(response);
	}

	
	@Test(groups = { "0.1","0.2"}) 
	public void bothInstAfterFeedEnd() throws Exception
	{
		response = ELUtil.testWith("currentYear(48,0,22,0)","currentYear(50,0,22,0)",false);
		validate(response);
	}
*/
	private void  validate(String response)
	{
		if(!
				(((response.contains("End instance ") || response.contains("Start instance"))
						&& (response.contains("for feed") || response.contains("of feed")) 
						&& (response.contains("is before the start of feed") || response.contains("is after the end of feed"))) || (response.contains("End instance") && response.contains("is before the start instance")))
		)
			Assert.assertTrue(false);
	}


}
