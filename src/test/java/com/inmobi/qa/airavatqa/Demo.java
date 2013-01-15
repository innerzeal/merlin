package com.inmobi.qa.airavatqa;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.inmobi.qa.airavatqa.core.APIResult;
import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.ServiceResponse;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.core.Util.URLS;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;

public class Demo {

	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);

/*

	@Test(dataProvider="demo-DP") 
	public void demoBundle(Bundle bundle) throws Exception {

		try{
			bundle.submitAndScheduleBundle();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}

		finally
		{
			bundle.deleteBundle();
		}
	}
	
	@DataProvider(name="demo-DP")
	public static Object[][] getTestData(Method m) throws Exception
	{

		return Util.readDemoBundle();
	}*/
}
